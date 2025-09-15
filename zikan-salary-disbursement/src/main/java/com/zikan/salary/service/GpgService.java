package com.zikan.salary.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * Simple wrapper around the system GPG CLI to encrypt files with a given public key file.
 * Requires gpg to be installed and available on PATH.
 */
@Service
public class GpgService {

    public File encryptFile(File input, File publicKeyFile) throws IOException, InterruptedException {
        File out = new File(input.getAbsolutePath() + ".gpg");
        ProcessBuilder pb = new ProcessBuilder(
                "gpg",
                "--batch", "--yes",
                "--output", out.getAbsolutePath(),
                "--encrypt",
                "--recipient-file", publicKeyFile.getAbsolutePath(),
                input.getAbsolutePath()
        );
        pb.inheritIO();
        Process p = pb.start();
        int code = p.waitFor();
        if (code != 0) {
            throw new IOException("gpg encryption failed, exit code " + code);
        }
        return out;
    }
}
