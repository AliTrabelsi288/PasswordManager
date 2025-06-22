package com.alitrabelsi.passwordmanager;

import java.io.Console;
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
		
		sc.close();
	}
	
	private static void enterPassword(Scanner sc, Path path) {
		Hash hasher = new Hash(65535, 256);
		Console console = System.console();
		char[] pwdArray = console.readPassword("Enter the Password for Your Vault: ");
		String enteredMasterPassword = new String(pwdArray);	
		
		try{
			String storedMasterPassword = VaultIO.masterPassword(path);
			Boolean passwordMatch = hasher.comparePasswords(storedMasterPassword, enteredMasterPassword);
			
			if(passwordMatch) {
				System.out.println("Password Correct, Unlocking Vault ...");
				vaultUnlocked(sc, path, enteredMasterPassword);
			}
			else {
				System.out.println("ERROR : Password Incorrect");
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
		
		Console console = System.console();
		char[] pwdArray = console.readPassword("Create Pasword for Your Vault: ");
		String createMasterPassword = new String(pwdArray);	
		
		try{
			hashedCreateMasterPassword = hasher.hashAndSaltPassword(createMasterPassword);
			
			JSONObject vault = new JSONObject();
		    vault.put("masterPassword", hashedCreateMasterPassword);

		    VaultIO.writeVault(path, vault);
		    System.out.println("Vault Created and Master Password Saved, Unlocking Vault ...");
		    vaultUnlocked(sc, path, createMasterPassword);
		} 
		catch (Exception e) {
			System.out.println("ERROR : Hashing or Vault Error");
			e.printStackTrace();
		}
	}
	
	private static void vaultUnlocked(Scanner sc, Path path, String password) throws IOException{
		final String options = "\n 1. View Saved Passwords\n 2. Add New Password\n 3. Delete Saved Password\n 4. Quit";
		Boolean quit = false;
		
		do{
			System.out.println(options);
			String choice = sc.next();
			
			switch(choice) {
				case "1":
					viewPasswords(sc, path, password);
					break;
				case "2":
					addPassword(sc, path, password);
					break;
				case "3":
					deletePassword(sc, path);
					break;
				case "4":
					quit = true;
					break;
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
	
	private static void viewPasswords(Scanner sc, Path path, String password) throws IOException {
		JSONObject vault = VaultIO.readVault(path);
		Encryption decrypt = new Encryption(password);
		
		vault.remove("masterPassword");
		int count = 1;
		System.out.println("\nSaved Passwords: ");
		
		Iterator<String> keys = vault.keys();
		   while (keys.hasNext()) {
		       String key = keys.next();
		       Object value = vault.get(key);
		       
		       try {
		    	   String decryptedValue = decrypt.decrypt(value);
		    	   
		    	   System.out.println(count + ". " + "Account: " + key + ", Password: " + decryptedValue);
			       count++;
		       } 
		       catch(Exception e) {
					System.out.println("ERROR : Fatal Decryption or Vault Error");
					e.printStackTrace();
		       }
		   }
		   
	}
	
	private static void addPassword(Scanner sc, Path path, String password) throws IOException {
		JSONObject vault = VaultIO.readVault(path);
		Encryption encrypt = new Encryption(password);
		
		System.out.println("\nAdd Password: ");
		
		System.out.println("Name of Account: ");
		String account = sc.next();
		
		Console console = System.console();
		char[] pwdArray = console.readPassword("Account Password: ");
		String pass = new String(pwdArray);	
		
		try {
			String encryptedPass = encrypt.encrypt(pass);
			
			vault.put(account, encryptedPass);
			VaultIO.writeVault(path, vault);
			
			System.out.println("\nNew Account Successfully Added");
		}
		catch (Exception e) {
			System.out.println("ERROR : Fatal Encryption or Vault Error");
			e.printStackTrace();
			
		}
		
	}
	
	private static void deletePassword(Scanner sc, Path path) throws IOException {
		JSONObject vault = VaultIO.readVault(path);
		System.out.println("\nDelete Password");
		
		System.out.println("Name of Account: ");
		String name = sc.next();
		
		System.out.println("Confirm Removal of Account: " + name + " ? (y/n)");
		String confirm = sc.next();
		
		switch (confirm) {
			case "y":
				try {
					Object check = vault.get(name);
				}
				catch (Exception e){
					System.out.println("ERROR : Account Does Not Exist");
					break;
				}
					
				vault.remove(name);
				VaultIO.writeVault(path, vault);
				
				System.out.println("Account Successfully Removed");
				break;
			default:
				System.out.println("Delete Cancelled");
				break;
				
		}	
		
	}
}