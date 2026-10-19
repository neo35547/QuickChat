/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.quickchatpoepart2;

import java.util.Scanner;
import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author neose
 */
public class QuickChatPOEPART2 {

    static Scanner scanner = new Scanner(System.in);
    static String registeredUsername;
    static String registeredPassword;
    static String registeredPhone;
    static int totalMessagesSent = 0;
    static int messageNumber = 0;

    public static void main(String[] args) {

        System.out.println("Welcome to QuickChat!");

        registerUser();

        if (loginUser()) {
            System.out.println("Login successful. Welcome to QuickChat!");
            runMessageSystem();
        } else {
            System.out.println("Login failed. Exiting application.");
        }
    }

    // Register a new user
    public static void registerUser() {

        boolean valid = false;

        while (!valid) {

            // Validate username
            System.out.println("\nCreate a username (must contain an underscore and must not exceed 5 characters):");
            registeredUsername = scanner.nextLine();

            if (!(registeredUsername.contains("_")
                    && registeredUsername.length() <= 5)) {
                System.out.println("Invalid username. Please try again.");
                continue;
            }

            // Validate password
            System.out.println("Create a password (more than 8 characters, "
                    + "a capital letter, a number and a special character):");
            registeredPassword = scanner.nextLine();

            if (!registeredPassword.matches(
                    "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+=]).{9,}$")) {
                System.out.println("Invalid password. Please try again.");
                continue;
            }

            // Validate cellphone number
            System.out.println("Enter your cellphone number (+27xxxxxxxxx):");
            registeredPhone = scanner.nextLine();

            if (!registeredPhone.matches("^\\+27[6-8]\\d{8}$")) {
                System.out.println("Invalid cellphone number. Please try again.");
                continue;
            }

            System.out.println("Registration successful!");
            valid = true;
        }
    }

    // Check login details
    public static boolean loginUser() {

        System.out.println("\nLOGIN");

        System.out.println("Enter your username:");
        String user = scanner.nextLine();

        System.out.println("Enter your password:");
        String pass = scanner.nextLine();

        return user.equals(registeredUsername)
                && pass.equals(registeredPassword);
    }

    // Display the main menu
    public static void runMessageSystem() {

        boolean keepRunning = true;

        while (keepRunning) {

            System.out.println("\n===== QuickChat Main Menu =====");
            System.out.println("1. Send Messages");
            System.out.println("2. Coming Soon");
            System.out.println("3. Quit");
            System.out.print("Choose an option: ");

            int choice = readInt();

            switch (choice) {
                case 1:
                    sendMessages();
                    break;

                case 2:
                    System.out.println("Coming soon.");
                    break;

                case 3:
                    keepRunning = false;
                    System.out.println("Goodbye!");
                    break;

                default:
                    System.out.println("Invalid option. Please choose 1, 2 or 3.");
            }
        }
    }

    // Allow users to create and manage multiple messages
    public static void sendMessages() {

        System.out.println("\nHow many messages would you like to create?");
        int count = readInt();

        if (count <= 0) {
            System.out.println("Please enter a number greater than zero.");
            return;
        }

        for (int i = 0; i < count; i++) {

            String messageID = generateMessageID();

            // Validate recipient's South African number
            System.out.println("Enter recipient's number (+27xxxxxxxxx):");
            String recipient = scanner.nextLine();

            if (!recipient.matches("^\\+27\\d{9}$")) {
                System.out.println("Invalid number format. Please try again.");
                i--;
                continue;
            }

            // Validate message length
            System.out.println("Enter your message (maximum 250 characters):");
            String message = scanner.nextLine();

            if (message.length() > 250) {
                System.out.println("Message too long by "
                        + (message.length() - 250) + " characters.");
                i--;
                continue;
            }

            if (message.trim().isEmpty()) {
                System.out.println("Message cannot be empty.");
                i--;
                continue;
            }

            messageNumber++;

            String hash = generateMessageHash(
                    messageID, messageNumber, message);

            // Allow the user to choose an action
            System.out.println("\nWhat would you like to do?");
            System.out.println("1. Send");
            System.out.println("2. Disregard");
            System.out.println("3. Store");
            System.out.print("Choose an option: ");

            int action = readInt();

            switch (action) {

                case 1:
                    totalMessagesSent++;
                    displayMessageDetails(
                            messageID, hash, recipient, message);
                    System.out.println("Message successfully sent.");
                    break;

                case 2:
                    System.out.println("Message disregarded.");
                    break;

                case 3:
                    writeMessageToJSON(
                            messageID, hash, recipient, message);
                    System.out.println("Message stored.");
                    break;

                default:
                    System.out.println("Invalid option. Message disregarded.");
                    break;
            }
        }

        System.out.println("\nTotal messages sent: " + totalMessagesSent);
    }

    // Read an integer safely from the console
    public static int readInt() {

        while (true) {
            String input = scanner.nextLine();

            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Enter a whole number: ");
            }
        }
    }

    // Generate a random 10-digit message ID
    public static String generateMessageID() {
        return String.format(
                "%010d", new Random().nextInt(1_000_000_000));
    }

    // Generate a message hash
    public static String generateMessageHash(
            String id, Integer num, String msg) {

        String[] words = msg.trim().split("\\s+");

        String first = words[0];
        String last = words[words.length - 1];

        return (id.substring(0, 2) + ":" + num + ":"
                + first + last).toUpperCase();
    }

    // Display message details in the console
    public static void displayMessageDetails(
            String id, String hash, String recipient, String msg) {

        System.out.println("\n===== Message Details =====");
        System.out.println("Message ID: " + id);
        System.out.println("Message Hash: " + hash);
        System.out.println("Recipient: " + recipient);
        System.out.println("Message: " + msg);
    }

    // Store message details in a file
    public static void writeMessageToJSON(
            String messageID, String hash,
            String recipient, String message) {

        StringBuilder jsonEntry = new StringBuilder();

        jsonEntry.append("{\n");
        jsonEntry.append("  \"MessageID\":\"")
                .append(escapeJSON(messageID)).append("\",\n");
        jsonEntry.append("  \"MessageHash\":\"")
                .append(escapeJSON(hash)).append("\",\n");
        jsonEntry.append("  \"Recipient\":\"")
                .append(escapeJSON(recipient)).append("\",\n");
        jsonEntry.append("  \"Message\":\"")
                .append(escapeJSON(message)).append("\"\n");
        jsonEntry.append("}\n");

        try (FileWriter file = new FileWriter("Jsonnew.json", true)) {
            file.write(jsonEntry.toString());
            file.write("\n");

        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }

    // Escape special characters for JSON strings
    public static String escapeJSON(String value) {

        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
