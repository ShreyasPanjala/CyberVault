CyberVault – Secure Password Management System

CyberVault is a simple yet functional and secure password-management console application built in Java.
It allows users to securely store, retrieve, and manage passwords using a custom encryption method, user authentication system, and admin controls. 
This project is designed for beginners exploring cybersecurity concepts like caesar cipher, file handling, encryption, and modular Java design.

🚀 Features
🔐 User Authentication

Predefined user accounts and an admin account

Password-based login using Scanner input

Verification before accessing the vault

🧩 Password Manager

Add new password entries

Store up to 200 password records

Caesar-cipher-based encryption (+3 shift)

Decryption on retrieval

Edit and delete entries

Search entries by platform/site name

🛡️ Admin Module

View all users

Reset user passwords

Audit stored password entries

Perform maintenance operations

📂 Project Structure
CyberVault/
│
├── CyberVault.java        # Main driver program (user login + UI flow)
├── PasswordManager.java   # Handles encryption, storage, and management
├── PasswordEntry.java     # Data model for password records
└── README.md              # Documentation

🧠 How It Works

Program starts → User is prompted to log in.

If user login successful → User gets access to the Password Manager features.

If admin login → Admin dashboard opens with elevated privileges.

PasswordManager →

Encryption method shifts characters by +3

Entries stored in an array of PasswordEntry objects

Retrieval decrypts the stored password

All actions loop until user exits.

🛠️ Tech Stack

Language: Java

Libraries: java.util.Scanner

Paradigm: OOP + modular design

📈 Future Enhancements

Replace Caesar cipher with AES-256 encryption

Implement file-based or database-based persistent storage

Role-based access control

GUI interface with JavaFX or Swing

Auto-generation of strong passwords

Two-factor authentication for login
