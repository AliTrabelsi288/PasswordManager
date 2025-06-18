package com.alitrabelsi.passwordmanager;

public class Encryption {
	private final int key;
	private final String encryptionAlgorithm;
	
	public Encryption(int key, String encryptionAlgorithm) {
		this.key = key;
		this.encryptionAlgorithm = encryptionAlgorithm;
	}
}
