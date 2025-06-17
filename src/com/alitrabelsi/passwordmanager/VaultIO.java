package com.alitrabelsi.passwordmanager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.json.JSONObject;

public class VaultIO {
    public static JSONObject readVault(Path path) throws IOException {
        if (!Files.exists(path)) {
            return new JSONObject();
        }

        String content = Files.readString(path);
        return new JSONObject(content);
    }

    public static void writeVault(Path path, JSONObject vaultData) throws IOException {
        Files.write(path, vaultData.toString(4).getBytes());
    }
}