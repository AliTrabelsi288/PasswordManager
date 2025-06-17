package com.alitrabelsi.passwordmanager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.json.JSONObject;

public class VaultIO {
    protected static JSONObject readVault(Path path) throws IOException {
        if (!Files.exists(path)) {
            return new JSONObject();
        }

        String content = Files.readString(path);
        return new JSONObject(content);
    }

    protected static void writeVault(Path path, JSONObject vaultData) throws IOException {
        Files.write(path, vaultData.toString(4).getBytes());
    }
    
    protected static String masterPassword(Path path) throws IOException {
    	String content = Files.readString(path);
    	JSONObject vault = new JSONObject(content);
    	return vault.getString("masterPassword");
    }
    
    
}