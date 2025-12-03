package org.tyler.passprotector.passprotector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bson.Document;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class FileManager {
    // Potentially later change so application can also work on linux/mac
    private static final String appDataFilePath = "C:\\Users\\"+System.getProperty("user.name")+"\\AppData\\Local";
    public static ArrayList<File> passwordFiles;


    // Call on load, Create the "passwords" folder for storing password jsons
    public static void createProjectDirectories() {
        new File(appDataFilePath+"\\PasswordProtector").mkdir();
        new File(appDataFilePath+"\\PasswordProtector\\passwords").mkdir();
    }

    // Call on load, Get password files from local storage folder
    public static void getFilesInFolder() {
        File folder = new File(appDataFilePath+"\\PasswordProtector\\passwords");
        passwordFiles = new ArrayList<>(Arrays.asList(Objects.requireNonNull(folder.listFiles())));
    }

    // Create json file for storing user passwords
    public static void createPasswordJson(String fileName) {
        try {
            File passwordJson = new File(appDataFilePath+"\\PasswordProtector\\passwords\\"+fileName+".json");
            passwordJson.createNewFile();
        } catch (IOException e) {
            System.out.println("Could not create PW file");
        }
    }

    // Gets data from a file, encrypt it, store back in file, change file format
    public static void encryptFileData(String fileName, String password) {
        File file = getFileByName(fileName);
        String fileNameWithExtension = Objects.requireNonNull(file).getName();

        try {
            // Read file
            Scanner reader = new Scanner(file);
            StringBuilder fileData = new StringBuilder();
            while (reader.hasNextLine()) {
                fileData.append(reader.nextLine());
            }
            reader.close();

            Document fileEncryptionData = DatabaseAccess.getFileDataByName(fileName);
            // Encrypt
            String encryptedData = AES.encrypt(fileData.toString(), password, (String) fileEncryptionData.get("salt"), (String) fileEncryptionData.get("iv")); // CHANGE later to get salt / iv from DB

            // Write to file
            try {
                FileWriter writer = new FileWriter(appDataFilePath+"\\PasswordProtector\\passwords\\"+fileNameWithExtension);
                writer.write(encryptedData);
                writer.close();

                // Renames with .enc
                file.renameTo(new File(appDataFilePath+"\\PasswordProtector\\passwords\\"+fileName+".enc"));
            } catch (IOException e) {
                System.out.println("Could not write or rename file");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Could not read file.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    // Get data from file, decrypt, store back in
    public static void decryptFileData(String fileName, String password) {
        File file = getFileByName(fileName);
        String fileNameWithExtension = Objects.requireNonNull(file).getName();

        try {
            // Read file
            Scanner reader = new Scanner(file);
            StringBuilder fileData = new StringBuilder();
            while (reader.hasNextLine()) {
                fileData.append(reader.nextLine());
            }
            reader.close();

            Document fileEncryptionData = DatabaseAccess.getFileDataByName(fileName);
            // Decrypt
            String decryptedData = AES.decrypt(fileData.toString(), password, (String) fileEncryptionData.get("salt"), (String) fileEncryptionData.get("iv")); // CHANGE later to get salt / iv from DB

            // Write to file
            try {
                FileWriter writer = new FileWriter(appDataFilePath+"\\PasswordProtector\\passwords\\"+fileNameWithExtension);
                writer.write(decryptedData);
                writer.close();

                // Renames with .json
                file.renameTo(new File(appDataFilePath+"\\PasswordProtector\\passwords\\"+fileName+".json"));
            } catch (IOException e) {
                System.out.println("An error occurred.");
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    // Delete file using filename
    public static void deleteFile(String fileName) {
        File file = getFileByName(fileName);

        // Delete the file with the matching name
        Objects.requireNonNull(file).delete();
    }

    // Rename file using fileName
    public static void renameFile(String oldFileName, String newFileName) {
        File file = getFileByName(oldFileName);

        String oldFileNameWithExtension = Objects.requireNonNull(file).getName();
        String newFileNameWithExtension = newFileName+oldFileNameWithExtension.substring(oldFileNameWithExtension.indexOf("."));
        file.renameTo(new File(appDataFilePath+"\\PasswordProtector\\passwords\\"+newFileNameWithExtension));
    }

    // Get and return file by file name
    public static File getFileByName(String fileName) {
        ArrayList<String> passwordFileNames = new ArrayList<>();

        // If there's no files yet
        if (passwordFiles.isEmpty()) {
            return null;
        }

        // Convert file list to file name list
        for(File file : passwordFiles) {
            passwordFileNames.add(file.getName().substring(0, file.getName().indexOf('.')));
        }

        // Checking if the file exists
        if (passwordFileNames.contains(fileName)) {
            return passwordFiles.get(passwordFileNames.indexOf(fileName));
        } else {
            return null;
        }
    }

    public static boolean getFileExists(String fileName) {
        ArrayList<String> passwordFileNames = new ArrayList<>();

        // If there's no files yet
        if (passwordFiles.isEmpty()) {
            return false;
        }

        // Convert file list to file name list
        for(File file : passwordFiles) {
            passwordFileNames.add(file.getName().substring(0, file.getName().indexOf('.')));
        }

        // Checking if the file exists
        return passwordFileNames.contains(fileName);
    }

    public static ArrayList<String> getUnlockedFileList() {
        ArrayList<String> unlockedFiles = new ArrayList<>();

        for(File file : passwordFiles) {
            String fileNameWithExtension = file.getName();

            if(fileNameWithExtension.endsWith(".json")) {
                // Cuts off .json and adds
                unlockedFiles.add(fileNameWithExtension.substring(0,fileNameWithExtension.indexOf(".")));
            }
        }

        return unlockedFiles;
    }

    // Store json data in file by taking the arraylist and filename
    public static void storeJson(String fileName, ArrayList<PasswordTableRow> passwords) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(passwords);

        try {
            FileWriter writer = new FileWriter(appDataFilePath+"\\PasswordProtector\\passwords\\"+fileName+".json");
            writer.write(json);
            writer.close();

        } catch (IOException e) {
            System.out.println("Error");
        }
    }

    // Read and deserialize, "decode", the json data and turn it into an arraylist use the filename
    public static ArrayList<PasswordTableRow> getPasswordsFromJson(String fileName) {
        Gson gson = new Gson();

        try {
            Scanner reader = new Scanner(Objects.requireNonNull(getFileByName(fileName)));
            StringBuilder json = new StringBuilder();
            while (reader.hasNextLine()) {
                json.append(reader.nextLine());
            }
            reader.close();

            // Define class
            Type listType = new TypeToken<ArrayList<PasswordTableRow>>(){}.getType();

            // Deserialized
            return gson.fromJson(String.valueOf(json), listType);

        } catch (FileNotFoundException e) {
            System.out.println("Could not read file.");
        }

        return null;
    }

}
