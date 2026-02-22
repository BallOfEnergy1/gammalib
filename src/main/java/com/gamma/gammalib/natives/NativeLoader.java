package com.gamma.gammalib.natives;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class NativeLoader {

    static {
        // clear existing dlls
        Path tempDir = Path.of(System.getProperty("java.io.tmpdir"), "gammalib-natives");
        File root = tempDir.toFile();
        File[] files = root.listFiles();
        if (root.isDirectory() && files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }

    public static void load(String s) {
        try {
            loadBundledNative(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadBundledNative(String baseName) throws IOException {
        String osName = System.getProperty("os.name")
            .toLowerCase();
        String arch = System.getProperty("os.arch")
            .toLowerCase();

        String folder;
        String suffix;
        if (osName.contains("windows") && (arch.contains("64") || arch.contains("amd64") || arch.contains("x86_64"))) {
            folder = "windows_64";
            suffix = ".dll";
        } else {
            throw new UnsupportedOperationException("No bundled native library for os=" + osName + ", arch=" + arch);
        }

        String resourcePath = "natives/" + folder + "/" + baseName + suffix;
        try (InputStream in = BasicSIMD.class.getClassLoader()
            .getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IOException("Missing native resource: " + resourcePath);
            }
            Path tempDir = Path.of(System.getProperty("java.io.tmpdir"), "gammalib-natives");
            Files.createDirectories(tempDir);
            Path tempFile = tempDir.resolve(baseName + suffix);
            if (Files.notExists(tempFile)) {
                Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
                tempFile.toFile()
                    .deleteOnExit();
            }
            Runtime.getRuntime()
                .load(
                    tempFile.toAbsolutePath()
                        .toString());
        }
    }
}
