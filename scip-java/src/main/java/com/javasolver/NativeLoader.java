package com.javasolver;

import jnr.ffi.LibraryLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

final class NativeLoader {
    private static final String RESOURCE_ROOT = "scip-10.1.0/win-x64/";
    private static final String[] WINDOWS_LIBRARIES = {
        "libiomp5md.dll", "libmmd.dll", "svml_dispmd.dll", "libifcoremd.dll",
        "tbb12.dll", "gmp-10.dll", "mpfr-6.dll", "z.dll", "jansson.dll",
        "coinmumps-3.dll", "ipopt-3.dll", "highs.dll", "libsoplexshared.dll",
        "libscip.dll"
    };

    private NativeLoader() {}

    static SCIPNative load() {
        String configured = System.getenv("SCIP_LIBRARY_PATH");
        if (configured != null && !configured.trim().isEmpty())
            return loadConfigured(new File(configured.trim()));

        String os = System.getProperty("os.name", "").toLowerCase();
        String arch = System.getProperty("os.arch", "").toLowerCase();
        if (os.startsWith("win") && arch.contains("64")) return loadBundledWindows();

        try {
            return LibraryLoader.create(SCIPNative.class).load("scip");
        } catch (UnsatisfiedLinkError error) {
            throw new UnsatisfiedLinkError("SCIP native library was not found. Set SCIP_LIBRARY_PATH " +
                    "to the SCIP shared library or its directory. Original error: " + error.getMessage());
        }
    }

    private static SCIPNative loadConfigured(File configured) {
        File directory = configured.isDirectory() ? configured : configured.getAbsoluteFile().getParentFile();
        if (configured.isFile()) System.load(configured.getAbsolutePath());
        String name = configured.isFile() ? baseName(configured.getName()) : "scip";
        return LibraryLoader.create(SCIPNative.class).search(directory.getAbsolutePath()).load(name);
    }

    private static SCIPNative loadBundledWindows() {
        try {
            File directory = Files.createTempDirectory("scip-java-").toFile();
            directory.deleteOnExit();
            for (String library : WINDOWS_LIBRARIES) extract(directory, library);
            for (String library : WINDOWS_LIBRARIES) {
                try {
                    System.load(new File(directory, library).getAbsolutePath());
                } catch (UnsatisfiedLinkError error) {
                    if ("libscip.dll".equals(library)) throw error;
                }
            }
            return LibraryLoader.create(SCIPNative.class)
                    .search(directory.getAbsolutePath()).load("libscip");
        } catch (IOException error) {
            throw new IllegalStateException("Unable to extract bundled SCIP libraries", error);
        }
    }

    private static void extract(File directory, String name) throws IOException {
        InputStream input = NativeLoader.class.getClassLoader().getResourceAsStream(RESOURCE_ROOT + name);
        if (input == null) throw new IOException("Missing native resource " + RESOURCE_ROOT + name);
        File outputFile = new File(directory, name);
        outputFile.deleteOnExit();
        try (InputStream in = input; FileOutputStream out = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[64 * 1024];
            int count;
            while ((count = in.read(buffer)) >= 0) out.write(buffer, 0, count);
        }
    }

    private static String baseName(String name) {
        String lower = name.toLowerCase();
        if (lower.endsWith(".dll")) name = name.substring(0, name.length() - 4);
        else if (lower.endsWith(".dylib")) name = name.substring(0, name.length() - 6);
        int so = lower.indexOf(".so");
        if (so >= 0) name = name.substring(0, so);
        return name;
    }
}
