package javax.constraints.impl;

import java.lang.reflect.Field;
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

import ilog.cp.IloCP;

/** Locates and loads the native library matching the CP Optimizer Java API. */
public final class CpOptimizerRuntime {
    public static final String LIBRARY_PROPERTY = "jsr331.cpoptimizer.library";
    public static final String HOME_PROPERTY = "jsr331.cpoptimizer.home";

    private static volatile boolean loaded;
    private static volatile Path loadedLibrary;

    private CpOptimizerRuntime() {
    }

    public static void ensureLoaded() {
        if (loaded) {
            return;
        }
        synchronized (CpOptimizerRuntime.class) {
            if (loaded) {
                return;
            }
            String version = javaApiVersion();
            String baseName = libraryBaseName(version);
            try {
                System.loadLibrary(baseName);
                loaded = true;
                return;
            } catch (UnsatisfiedLinkError pathFailure) {
                Path library = locateLibrary(baseName);
                if (library == null) {
                    throw unavailable(version, baseName, pathFailure);
                }
                try {
                    loadCplexDependency(version, library);
                    System.load(library.toAbsolutePath().normalize().toString());
                    markCpWrapperLoaded();
                    loadedLibrary = library.toAbsolutePath().normalize();
                    loaded = true;
                } catch (ReflectiveOperationException | UnsatisfiedLinkError error) {
                    IllegalStateException failure = new IllegalStateException(
                            "Found CP Optimizer " + version + " native library at "
                            + library + " but Java could not initialize it. Verify that "
                            + "its architecture matches the running JVM.", error);
                    failure.addSuppressed(pathFailure);
                    throw failure;
                }
            }
        }
    }

    public static Path getLoadedLibrary() {
        return loadedLibrary;
    }

    static String libraryBaseName(String version) {
        String[] parts = version == null ? new String[0] : version.split("\\.");
        if (parts.length < 3 || !digits(parts[0]) || !digits(parts[1])
                || !digits(parts[2])) {
            throw new IllegalStateException(
                    "Unsupported CP Optimizer Java API version: " + version);
        }
        return "cp_wrap_cpp_java" + parts[0] + parts[1] + parts[2];
    }

    private static String javaApiVersion() {
        Package apiPackage = IloCP.class.getPackage();
        return apiPackage == null ? null : apiPackage.getImplementationVersion();
    }

    private static void markCpWrapperLoaded() throws ReflectiveOperationException {
        Class<?> loader = Class.forName("ilog.concert.IloJNILoader", false,
                IloCP.class.getClassLoader());
        Field loadedLibraryIndex = loader.getDeclaredField("_loadedLib");
        loadedLibraryIndex.setAccessible(true);
        loadedLibraryIndex.setInt(null, 3);
    }

    private static Path locateLibrary(String baseName) {
        String mappedName = System.mapLibraryName(baseName);
        String explicit = System.getProperty(LIBRARY_PROPERTY);
        if (explicit != null && !explicit.isBlank()) {
            Path configured = Paths.get(explicit).toAbsolutePath().normalize();
            if (Files.isRegularFile(configured)) {
                return configured;
            }
            if (Files.isDirectory(configured)
                    && Files.isRegularFile(configured.resolve(mappedName))) {
                return configured.resolve(mappedName);
            }
            throw new IllegalStateException(
                    "System property " + LIBRARY_PROPERTY
                    + " does not identify a CP Optimizer native library: " + configured);
        }
        for (Path root : discoveryRoots()) {
            Path match = findLibrary(root, mappedName);
            if (match != null) {
                return match;
            }
        }
        return null;
    }

    private static void loadCplexDependency(String version, Path wrapper) {
        String cplexBaseName = "cplex" + numericVersion(version);
        try {
            System.loadLibrary(cplexBaseName);
            return;
        } catch (UnsatisfiedLinkError ignored) {
            // Discover the matching CPLEX engine next to the Studio installation.
        }
        Path studio = wrapper;
        while (studio != null && (studio.getFileName() == null
                || !studio.getFileName().toString().toLowerCase(Locale.ROOT)
                        .startsWith("cplex_studio"))) {
            studio = studio.getParent();
        }
        Path engine = studio == null ? null
                : findLibrary(studio, System.mapLibraryName(cplexBaseName));
        if (engine != null) {
            System.load(engine.toAbsolutePath().normalize().toString());
        }
    }

    private static String numericVersion(String version) {
        String[] parts = version.split("\\.");
        return parts[0] + parts[1] + parts[2];
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
                .findFirst().orElse(null);
    }

    private static IllegalStateException unavailable(
            String version, String baseName, UnsatisfiedLinkError cause) {
        return new IllegalStateException(
                "CP Optimizer Java API " + version + " is present, but native library "
                + System.mapLibraryName(baseName) + " was not found. Install the matching "
                + "CPLEX Optimization Studio release, or set -D" + LIBRARY_PROPERTY
                + "=<full-library-path> or -D" + HOME_PROPERTY
                + "=<CPLEX-Studio-directory>.", cause);
    }

    private static boolean digits(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        for (int index = 0; index < text.length(); index++) {
            if (!Character.isDigit(text.charAt(index))) {
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
