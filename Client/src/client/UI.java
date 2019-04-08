/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author joanacruz
 */
public class UI {

    private static Scanner scanner = new Scanner(System.in);

    public static String showWelcomeMenu() {
        List<Option> options = new ArrayList<>();
        options.add(new Option("Download", "1"));
        options.add(new Option("Upload", "2"));
        options.add(new Option("Exit", "0"));
        Menu welcomeMenu = new Menu(options, "Welcome");
        welcomeMenu.show();
        String selectedOption = readLine();
        String res = null;
        switch (selectedOption) {
            case "1":
                res = "download";
                break;
            case "0":
                res = "upload";
                break;
            case "2":
                res = "exit";
                break;
            default:
                System.out.println("Please select one of the available options");
                waitForEnter();
                res = showWelcomeMenu();
        }
        return res;
    }
    
    public static String showDownloadMenu() {
        showHeader("Download");
        System.out.println("Insert the name of the file ");
        String file = scanner.nextLine();
        return file;
    }
    
    private static String readLine() {
        return scanner.nextLine();
    }
    
    public static void waitForEnter() {
        System.out.println("Press enter to continue");
        scanner.nextLine();
    }
    
    private static void showHeader(String name) {
        System.out.println("--------------------------------------------------------------------");
        System.out.println("                          " + name);
        System.out.println("--------------------------------------------------------------------");
    }
    
}
