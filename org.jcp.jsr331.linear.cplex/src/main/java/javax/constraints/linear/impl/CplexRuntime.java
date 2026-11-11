package javax.constraints.linear.impl;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import ilog.cplex.IloCplex;

/** Locates and loads the native library matching the active CPLEX Java API. */
public final class CplexRuntime {

    public static final String LIBRARY_PROPERTY = "jsr331.cplex.library";
    public static final String HOME_PROPERTY = "jsr331.cplex.home";

    private static volatile boolean loaded;
    private static volatile Path loadedLibrary;

    private CplexRuntime() {
    }

    /** Ensures that the native CPLEX library is loaded before IloCplex starts. */
    public static void ensureLoaded() {
        if (loaded) {
            return;
        }
        synchronized (CplexRuntime.class) {
            if (loaded) {
                return;
            }

            String version = javaApiVersion();
            String libraryBaseName = libraryBaseName(version);
            UnsatisfiedLinkError pathFailure;
            try {
                System.loadLibrary(libraryBaseName);
                initializeCplexLoader(false);
                loaded = true;
                return;
            } catch (UnsatisfiedLinkError error) {
                pathFailure = error;
            }

            Path library = locateLibrary(libraryBaseName);
            if (library == null) {
                throw unavailable(version, libraryBaseName, pathFailure);
            }

            try {
                System.load(library.toAbsolutePath().normalize().toString());
                initializeCplexLoader(true);
                loadedLibrary = library.toAbsolutePath().normalize();
                loaded = true;
            } catch (UnsatisfiedLinkError error) {
                IllegalStateException failure = new IllegalStateException(
                        "Found CPLEX " + version + " native library at " + library
                        + " but Java could not load it. Verify that its architecture matches "
                        + "the running JVM and that all dependent libraries are installed.",
                        error);
                failure.addSuppressed(pathFailure);
                throw failure;
            }
        }
    }

    /** Returns the native library explicitly loaded by discovery, if any. */
    public static Path getLoadedLibrary() {
        return loadedLibrary;
    }

    static String libraryBaseName(String version) {
        if (version == null || version.isBlank()) {
            throw new IllegalStateException(
                    "The CPLEX Java API JAR does not declare an implementation version");
        }
        String[] parts = version.split("\\.");
        if (parts.length < 3
                || !isDigits(parts[0])
                || !isDigits(parts[1])
                || !isDigits(parts[2])) {
            throw new IllegalStateException(
                    "Unsupported CPLEX Java API version format: " + version);
        }
        return "cplex" + parts[0] + parts[1] + parts[2];
    }

    private static String javaApiVersion() {
        Package cplexPackage = IloCplex.class.getPackage();
        String version = cplexPackage == null
                ? null
                : cplexPackage.getImplementationVersion();
        return version == null ? "" : version;
    }

    private static Path locateLibrary(String libraryBaseName) {
        String mappedName = System.mapLibraryName(libraryBaseName);
        String explicit = System.getProperty(LIBRARY_PROPERTY);
        if (explicit != null && !explicit.isBlank()) {
            Path configured = Paths.get(explicit).toAbsolutePath().normalize();
            if (Files.isRegularFile(configured)) {
                return configured;
            }
            if (Files.isDirectory(configured)) {
                Path candidate = configured.resolve(mappedName);
                if (Files.isRegularFile(candidate)) {
                    return candidate;
                }
            }
            throw new IllegalStateException(
                    "System property " + LIBRARY_PROPERTY
                    + " does not identify a CPLEX native library: " + configured);
        }

        for (Path root : discoveryRoots()) {
            Path found = findLibrary(root, mappedName);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    private static Set<Path> discoveryRoots() {
        Set<Path> roots = new LinkedHashSet<>();
        addRoot(roots, System.getProperty(HOME_PROPERTY));

        Map<String, String> environment = System.getenv();
        addRoot(roots, environment.get("CPLEX_HOME"));
        environment.entrySet().stream()
                .filter(entry -> entry.getKey().toUpperCase(Locale.ROOT)
                        .startsWith("CPLEX_STUDIO_DIR"))
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> addRoot(roots, entry.getValue()));

        if (isWindows()) {
            addIlogRoot(roots, environment.get("ProgramW6432"));
            addIlogRoot(roots, environment.get("ProgramFiles"));
            addIlogRoot(roots, environment.get("ProgramFiles(x86)"));
            addRoot(roots, "C:\\Program Files\\IBM\\ILOG");
        } else {
            addRoot(roots, "/opt/ibm/ILOG");
            addRoot(roots, "/opt/IBM/ILOG");
            if (System.getProperty("os.name", "").toLowerCase(Locale.ROOT)
                    .contains("mac")) {
                addRoot(roots, "/Applications");
            }
        }
        return roots;
    }

    private static void addIlogRoot(Set<Path> roots, String programFiles) {
        if (programFiles != null && !programFiles.isBlank()) {
            addRoot(roots, Paths.get(programFiles, "IBM", "ILOG").toString());
        }
    }

    private static void addRoot(Set<Path> roots, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        Path root = Paths.get(value).toAbsolutePath().normalize();
        if (Files.isDirectory(root)) {
            roots.add(root);
        }
    }

    private static Path findLibrary(Path root, String mappedName) {
        List<Path> matches = new ArrayList<>();
        try (Stream<Path> paths = Files.find(root, 7,
                (path, attributes) -> attributes.isRegularFile()
                        && path.getFileName().toString().equalsIgnoreCase(mappedName))) {
            paths.forEach(matches::add);
        } catch (Exception ignored) {
            return null;
        }
        return matches.stream()
                .sorted(Comparator.comparing(Path::toString).reversed())
                .findFirst()
                .orElse(null);
    }

    private static void initializeCplexLoader(boolean suppressMissingPathMessage) {
        PrintStream originalOut = System.out;
        PrintStream sink = null;
        try {
            if (suppressMissingPathMessage) {
                sink = new PrintStream(OutputStream.nullOutputStream());
                System.setOut(sink);
            }
            Class.forName("ilog.cplex.CplexInitializer", true,
                    IloCplex.class.getClassLoader());
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException(
                    "The CPLEX Java API is incomplete: CplexInitializer is missing",
                    exception);
        } finally {
            if (suppressMissingPathMessage) {
                System.setOut(originalOut);
                sink.close();
            }
        }
    }

    private static IllegalStateException unavailable(
            String version, String libraryBaseName, UnsatisfiedLinkError cause) {
        return new IllegalStateException(
                "CPLEX Java API " + version + " is present, but native library "
                + System.mapLibraryName(libraryBaseName) + " was not found. Install the "
                + "matching CPLEX Optimization Studio release, or set -D"
                + LIBRARY_PROPERTY + "=<full-library-path> or -D" + HOME_PROPERTY
                + "=<CPLEX-Studio-directory>.",
                cause);
    }

    private static boolean isDigits(String value) {
        if (value.isEmpty()) {
            return false;
        }
        for (int index = 0; index < value.length(); index++) {
            if (!Character.isDigit(value.charAt(index))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isWindows() {
        return System.getProperty("os.name", "")
                .toLowerCase(Locale.ROOT).contains("win");
    }
}
