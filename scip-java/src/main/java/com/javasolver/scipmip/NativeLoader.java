package com.javasolver;

import jnr.ffi.LibraryLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

final class NativeLoader {
    private static final String RESOURCE_ROOT = "scip-10.1.0/";
    private static final String LIBRARY_LIST = "libraries.txt";
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
        boolean x64 = arch.equals("amd64") || arch.equals("x86_64");
        boolean arm64 = arch.equals("aarch64") || arch.equals("arm64");

        if (os.startsWith("win") && arch.contains("64"))
            return loadBundled("win-x64", WINDOWS_LIBRARIES, "libscip.dll", "libscip");
        if (os.startsWith("linux") && x64)
            return loadBundled("linux-x86_64", null, "libscip.so", "scip");
        if (os.startsWith("linux") && arm64)
            return loadBundled("linux-aarch64", null, "libscip.so", "scip");
        if ((os.startsWith("mac") || os.startsWith("darwin")) && arm64)
            return loadBundled("macos-aarch64", null, "libscip.dylib", "scip");

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

    /**
     * Extracts the bundled libraries of a platform into a temporary directory and loads them.
     * When {@code libraries} is null, the load order is read from the platform's libraries.txt.
     */
    private static SCIPNative loadBundled(String platform, String[] libraries, String mainLibrary, String jnrName) {
        String root = RESOURCE_ROOT + platform + "/";
        try {
            String[] files = libraries != null ? libraries : readLibraryList(root);
            File directory = Files.createTempDirectory("scip-java-").toFile();
            directory.deleteOnExit();
            for (String library : files) extract(root, directory, library);
            for (String library : files) {
                try {
                    System.load(new File(directory, library).getAbsolutePath());
                } catch (UnsatisfiedLinkError error) {
                    if (mainLibrary.equals(library)) throw error;
                }
            }
            return LibraryLoader.create(SCIPNative.class)
                    .search(directory.getAbsolutePath()).load(jnrName);
        } catch (IOException error) {
            throw new IllegalStateException("Unable to extract bundled SCIP libraries for " + platform, error);
        }
    }

    private static String[] readLibraryList(String root) throws IOException {
        InputStream input = NativeLoader.class.getClassLoader().getResourceAsStream(root + LIBRARY_LIST);
        if (input == null) throw new IOException("Missing native resource " + root + LIBRARY_LIST);
        List<String> names = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) names.add(line);
            }
        }
        return names.toArray(new String[0]);
    }

    private static void extract(String root, File directory, String name) throws IOException {
        InputStream input = NativeLoader.class.getClassLoader().getResourceAsStream(root + name);
        if (input == null) throw new IOException("Missing native resource " + root + name);
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
