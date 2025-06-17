package com.alitrabelsi.passwordmanager;

import java.io.IOException;
import java.util.Scanner;
import org.json.JSONObject;
import org.json.JSONArray;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
	public static void main (String[] args) {
		Scanner sc = new Scanner(System.in);
		
		// Build path to vault.json in a hidden folder inside the user's home directory
		Path path = Paths.get(System.getProperty("user.home"), ".passwordmanager", "vault.json");

		try {
		    // Create the .passwordmanager directory if it doesn't exist
		    Files.createDirectories(path.getParent());
		} catch (IOException e) {
		    // Throw runtime exception if directory creation fails
		    throw new RuntimeException("Failed to Create Directory", e);
		}
		
		System.out.println("*** Password Manager ***");
		
		if(Files.exists(path)) {
			enterPassword(sc, path);
		}
		
		else {
			createPassword(sc, path);
		}
	}
	
	private static void enterPassword(Scanner sc, Path path) {
		Hash hasher = new Hash(65535, 256);
		System.out.println("Please Enter the Password for Your Vault:");
		String enteredMasterPassword = sc.next();
		
		try{
			String storedMasterPassword = VaultIO.masterPassword(path);
			Boolean passwordMatch = hasher.comparePasswords(storedMasterPassword, enteredMasterPassword);
			
			if(passwordMatch) {
				System.out.println("Password Correct");
			}
			else {
				System.out.println("Password Incorrect");
			}
		} 
		catch (IOException e) {
			System.out.println("ERROR : Vault Error");
			e.printStackTrace();
		} 
		catch (Exception e) {
			System.out.println("ERROR : Hashing Error");
			e.printStackTrace();
		}
		
	}
	
	private static void createPassword(Scanner sc, Path path) {
		Hash hasher = new Hash(65535, 256);
		String hashedCreateMasterPassword = null;
		
		System.out.println("Please Create a Password for Your Vault:");
		String createMasterPassword = sc.next();	
		
		try{
			hashedCreateMasterPassword = hasher.hashAndSaltPassword(createMasterPassword);
			
			JSONObject vault = new JSONObject();
		    vault.put("masterPassword", hashedCreateMasterPassword);

		    VaultIO.writeVault(path, vault);
		    System.out.println("Vault Created and Master Password Saved.");
		} 
		catch (Exception e) {
			System.out.println("ERROR : Hashing or Vault Error");
			e.printStackTrace();
		}
	}
}