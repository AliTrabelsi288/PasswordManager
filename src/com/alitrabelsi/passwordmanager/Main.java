package com.alitrabelsi.passwordmanager;

import java.io.IOException;
import java.util.Scanner;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Iterator;

/**
 * Handles all password manager logic
 * Allows user to create or access their vault, view saved passwords and add more
 * 
 * @author Ali Trabelsi
 * @version 1.0.0
 */
public class Main {
	public static void main (String[] args) {
		Scanner sc = new Scanner(System.in);
		
		// path to vault.json file in .passwordmanager folder in user home directory
		Path path = Paths.get(System.getProperty("user.home"), ".passwordmanager", "vault.json");

		try {
		    // Create the .passwordmanager directory if it doesn't exist
		    Files.createDirectories(path.getParent());
		} catch (IOException e) {
		    // Throw runtime exception if directory creation fails
		    throw new RuntimeException("ERROR : Failed to Create Directory", e);
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
				System.out.println("Password Correct, Unlocking Vault ...");
				vaultUnlocked(sc, path);
			}
			else {
				System.out.println("Password Incorrect");
				System.exit(0);
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
		    System.out.println("Vault Created and Master Password Saved, Unlocking Vault ...");
		    vaultUnlocked(sc, path);
		} 
		catch (Exception e) {
			System.out.println("ERROR : Hashing or Vault Error");
			e.printStackTrace();
		}
	}
	
	private static void vaultUnlocked(Scanner sc, Path path) throws IOException{
		final String options = "\n 1. View Saved Passwords\n 2. Add New Password\n 3. Delete Saved Password\n 4. Quit";
		Boolean quit = false;
		
		do{
			System.out.println(options);
			String choice = sc.next();
			
			switch(choice) {
				case "1":
					viewPasswords(sc, path);
					continue;
				case "2":
					addPassword(sc, path);
					continue;
				case "3":
					deletePassword(sc, path);
					continue;
				case "4":
					quit = true;
					continue;
				default:
					System.out.println("Choose from 1-4");
					continue;
			}
		}
		while(quit != true);
		
		if(quit) {
			System.out.println("Quitting ...");
			System.exit(0);
		}
		
	}
	
	private static void viewPasswords(Scanner sc, Path path) throws IOException {
		JSONObject vault = VaultIO.readVault(path);
		vault.remove("masterPassword");
		int count = 1;
		System.out.println("\nSaved Passwords: ");
		
		Iterator<String> keys = vault.keys();
		   while (keys.hasNext()) {
		       String key = keys.next();
		       Object value = vault.get(key);
		  
		       System.out.println(count + ". " + "Account: " + key + ", Password: " + value);
		       count++;
		   }
		   
	}
	
	private static void addPassword(Scanner sc, Path path) throws IOException {
		JSONObject vault = VaultIO.readVault(path);
		vault.remove("masterPassword");
		System.out.println("\nAdd Password: ");
		
		System.out.println("Name of Account: ");
		String name = sc.next();
		
		System.out.println("Account Password: ");
		String password = sc.next();
		
		vault.put(name, password);
		VaultIO.writeVault(path, vault);
		
		System.out.println("\nNew Account Successfully Added");
		
	}
	
	private static void deletePassword(Scanner sc, Path path) {
		
	}
}