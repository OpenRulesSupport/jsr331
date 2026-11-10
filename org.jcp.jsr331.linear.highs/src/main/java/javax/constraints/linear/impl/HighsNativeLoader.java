package javax.constraints.linear.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

import com.sun.jna.Native;

/** Loads the bundled HiGHS native library or a user-specified library. */
final class HighsNativeLoader {

    static final String LIBRARY_PROPERTY = "jsr331.highs.library";

    private static final HighsLibrary INSTANCE = loadLibrary();

    private HighsNativeLoader() {
    }

    static HighsLibrary get() {
        return INSTANCE;
    }

    private static HighsLibrary loadLibrary() {
        String configured = System.getProperty(LIBRARY_PROPERTY);
        if (configured != null && !configured.isBlank()) {
            return Native.load(configured, HighsLibrary.class);
        }

        NativeResource nativeResource = nativeResource();
        try (InputStream input = HighsNativeLoader.class.getClassLoader()
                .getResourceAsStream(nativeResource.resourcePath())) {
            if (input == null) {
                throw new IllegalStateException(
                        "HiGHS native library is not available for this platform: "
                        + System.getProperty("os.name") + " / "
                        + System.getProperty("os.arch"));
            }

            Path directory = Files.createTempDirectory("jsr331-highs-");
            Path library = directory.resolve(nativeResource.fileName());
            Files.copy(input, library, StandardCopyOption.REPLACE_EXISTING);
            directory.toFile().deleteOnExit();
            library.toFile().deleteOnExit();
            return Native.load(library.toAbsolutePath().toString(), HighsLibrary.class);
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot extract the bundled HiGHS library", exception);
        }
    }

    private static NativeResource nativeResource() {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        String arch = System.getProperty("os.arch", "").toLowerCase(Locale.ROOT);

        if (os.contains("win") && isX64(arch)) {
            return new NativeResource("natives/windows_64/highs.dll", "highs.dll");
        }
        if (os.contains("linux") && isX64(arch)) {
            return new NativeResource("natives/linux_64/libhighs.so", "libhighs.so");
        }
        if ((os.contains("mac") || os.contains("darwin")) && isArm64(arch)) {
            return new NativeResource("natives/osx_arm64/libhighs.dylib", "libhighs.dylib");
        }
        throw new IllegalStateException(
                "Unsupported HiGHS platform: " + System.getProperty("os.name")
                + " / " + System.getProperty("os.arch")
                + ". Set -D" + LIBRARY_PROPERTY
                + "=<path> to use a compatible installed HiGHS library.");
    }

    private static boolean isX64(String arch) {
        return arch.equals("amd64") || arch.equals("x86_64") || arch.equals("x64");
    }

    private static boolean isArm64(String arch) {
        return arch.equals("aarch64") || arch.equals("arm64");
    }

    private record NativeResource(String resourcePath, String fileName) {
    }
}
