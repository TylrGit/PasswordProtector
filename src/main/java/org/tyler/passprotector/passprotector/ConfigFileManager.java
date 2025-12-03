package org.tyler.passprotector.passprotector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Scanner;


public class ConfigFileManager {
    // Setting list:
    // true = light, false = dark
    public static boolean theme;

    // Potentially later change so application can also work on linux/mac
    private static final String appDataFilePath = "C:\\Users\\"+System.getProperty("user.name")+"\\AppData\\Local";

    // Call on load, Create config file if possible
    public static void createConfigFile() {
        try {
            boolean fileCreated = new File(appDataFilePath+"\\PasswordProtector\\AppConfig.json").createNewFile();

            if(fileCreated) {
                // Initialize & save default settings
                theme = true;
                saveSettings();
            } else {
                // Get stored settings
                getSettings();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    // Manage json writing and reading for settings read/write

    // Store setting data, each parameter is a setting
    public static void saveSettings() {
        ArrayList<Boolean> settings = new ArrayList<>();
        settings.add(theme);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(settings);

        try {
            FileWriter writer = new FileWriter(appDataFilePath+"\\PasswordProtector\\AppConfig.json");
            writer.write(json);
            writer.close();

        } catch (IOException e) {
            System.out.println("Error");
        }
    }

    // Read the settings
    public static void getSettings() throws FileNotFoundException {
        Gson gson = new Gson();

        Scanner reader = new Scanner(new File(appDataFilePath+"\\PasswordProtector\\AppConfig.json"));
        StringBuilder json = new StringBuilder();
        while (reader.hasNextLine()) {
            json.append(reader.nextLine());
        }
        reader.close();

        // Define class
        Type listType = new TypeToken<ArrayList<Boolean>>(){}.getType();

        // Deserialized
        ArrayList<Boolean> settings = gson.fromJson(String.valueOf(json), listType);
        theme = settings.getFirst();
    }
}
