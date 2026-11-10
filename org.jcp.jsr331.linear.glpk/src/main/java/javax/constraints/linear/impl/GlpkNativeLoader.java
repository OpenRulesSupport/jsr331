package javax.constraints.linear.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import com.sun.jna.Native;

/** Locates the GLPK callable library installed on the host. */
final class GlpkNativeLoader {

    static final String LIBRARY_PROPERTY = "jsr331.glpk.library";

    private static final GlpkLibrary INSTANCE = loadLibrary();

    private GlpkNativeLoader() {
    }

    static GlpkLibrary get() {
        return INSTANCE;
    }

    private static GlpkLibrary loadLibrary() {
        String configured = System.getProperty(LIBRARY_PROPERTY);
        if (configured != null && !configured.isBlank()) {
            return Native.load(configured, GlpkLibrary.class);
        }

        String executable = System.getProperty(
                javax.constraints.linear.LinearSolver.LP_SOLVER_EXE);
        if (executable != null && !executable.isBlank()) {
            Path directory = Path.of(executable).toAbsolutePath().getParent();
            Path adjacent = platformLibrary(directory);
            if (adjacent != null) {
                return Native.load(adjacent.toString(), GlpkLibrary.class);
            }
        }

        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        String libraryName = os.contains("win") ? "glpk_4_65" : "glpk";
        try {
            return Native.load(libraryName, GlpkLibrary.class);
        } catch (UnsatisfiedLinkError error) {
            throw new IllegalStateException(
                    "Cannot load the GLPK callable library. Add it to the native library path "
                    + "or set -D" + LIBRARY_PROPERTY + "=<absolute library path>.", error);
        }
    }

    private static Path platformLibrary(Path directory) {
        if (directory == null) {
            return null;
        }
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        String fileName;
        if (os.contains("win")) {
            fileName = "glpk_4_65.dll";
        } else if (os.contains("mac") || os.contains("darwin")) {
            fileName = "libglpk.dylib";
        } else {
            fileName = "libglpk.so";
        }
        Path candidate = directory.resolve(fileName);
        return Files.isRegularFile(candidate) ? candidate.toAbsolutePath() : null;
    }
}
