# Password Manager 

A lightweight and secure command-line password manager built in Java.  
It allows you to store, view, and manage your passwords locally with encryption and authentication.

---

##  Features

- AES-256 encryption (AES/GCM/NoPadding) for password storage
- Master password authentication using PBKDF2 with SHA-256
- Passwords stored securely in an encrypted local `vault.json` file
- CLI interface for adding, viewing, and deleting credentials
- Salt and IV are randomly generated per encryption to ensure ciphertext uniqueness
- Cross-platform compatibility (Windows, macOS, Linux)

---

##  Security Overview

- **Encryption:** All stored credentials are encrypted using AES in GCM mode with a 256-bit key.
- **Key Derivation:** The encryption key is not stored. It is derived from the user's master password using PBKDF2 with SHA-256, 65,536 iterations, and a random salt (16 bytes) per encryption.
- **Decryption:** During decryption, the same master password is used with the stored salt to regenerate the key and decrypt the password.
- **Storage Format:** Encrypted entries are stored in the format `salt:iv:ciphertext`, all Base64 encoded.
- **Authentication:** The master password is hashed and salted using PBKDF2. The hash is verified on every login and never stored in plain text.

---

##  Installation

> **Note:** All commands below should be run from your terminal or command prompt depending on your operating system.

1. Clone the repository:

```bash
git clone https://github.com/AliTrabelsi288/PasswordManager.git
```

> **Note:** Ensure you are within the cloned repository folder 'PasswordManager' before compiling and running.

2. Compile the source code with the required JSON library (`json-20250517.jar`):

**Linux/macOS:**

```bash
javac -d bin -cp lib/json-20250517.jar src/com/alitrabelsi/passwordmanager/*.java
```

**Windows (PowerShell or CMD):**

```cmd
javac -d bin -cp lib\json-20250517.jar src\com\alitrabelsi\passwordmanager\*.java
```

3. Run the application:

**Linux/macOS:**

```bash
java -cp bin:lib/json-20250517.jar com.alitrabelsi.passwordmanager.Main
```

**Windows (PowerShell or CMD):**

```cmd
java -cp "bin;lib\json-20250517.jar" com.alitrabelsi.passwordmanager.Main
```

---

##  Usage

1. On first run, you’ll be prompted to create a master password. This will be hashed and used to authenticate future sessions.

2. On subsequent launches, you must enter the correct master password to unlock your vault.

3. From the CLI menu, you can:
   - Add a new credential (site, username, password)
   - View stored credentials (passwords are decrypted only when viewed)
   - Delete credentials
   - Exit the vault

Your data is encrypted and stored locally in a file called `vault.json`.

---

##  Notes

- This project is for educational and personal use.
- Be sure to remember your master password — there is no recovery mechanism.
- Do not store the compiled binary or `vault.json` in unsecured environments.

---

##  Author

Developed by **Ali Trabelsi**

