<p align="center">
  <img alt="logo: JavaFX" src="./src/main/resources/org/tyler/passprotector/passprotector/images/PassProtectorTitle.png" />
</p>

<p align="center">
  <img alt="Framework: JavaFX" src="https://img.shields.io/badge/JavaFX-%23ED8B00.svg?logo=openjdk&logoColor=white" />
  <img alt="Database: MongoDB" src="https://img.shields.io/badge/MongoDB-%234ea94b.svg?logo=mongodb&logoColor=white" />
  <img alt="Version: 1.0.0" src="https://img.shields.io/badge/version-1.0.0-blue" />
  <img alt="License: MIT" src="https://img.shields.io/badge/License-All Rights Reserved-red" />
  <img alt="Platform: Windows" src="=https://img.shields.io/badge/Windows-10%20&%2011-blue" />
</p>

# Password Protector

&nbsp;&nbsp;&nbsp;&nbsp;An extra secure password manager for those worried about their passwords being stolen. 
Keeps passwords safely encrypted on device through a single password given by the user. Stores a hashed version of the password, 
salt, and initialization vector (IV) within a database for password verification. Sends back the salt and IV for decryption when the 
correct password is given for the particular encrypted file.

## 🖼️Preview

### Screenshots

<p align="center">
  <img alt="" src="./src/main/resources/org/tyler/passprotector/passprotector/images/PassProt1.png" />
  <img alt="" src="./src/main/resources/org/tyler/passprotector/passprotector/images/PassProt2.png" />
  <img alt="" src="./src/main/resources/org/tyler/passprotector/passprotector/images/PassProt3.png" />
</p>

### Video Demo

[![Watch the video](https://img.youtube.com/vi/lbKZQDCEckI/0.jpg)](https://youtu.be/lbKZQDCEckI)

## 🛠️Tech Stack

**Framework:** JavaFX

**Database:** MongoDB

### Features:

- Store passwords locally
- Encrypt password files with a key
- File password verification with database


## 📥Installation

1. Download the project
2. Unzip/extract the project folder
3. Open in an IDE (IntelliJ, Eclipse)
4. Get a MongoDB URI
4. Set up environment variable
5. Run Main.java

#### Environment Variables

Add the following environment variables to your .env file from `MongoDB`

`MONGO_URI`
