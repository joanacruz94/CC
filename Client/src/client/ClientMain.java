/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 * @author joanacruz
 */
public class ClientMain {
        /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        ClientAgentUDP client = new ClientAgentUDP();
        
        try {            
            String input, serverAnswer;
            do {
                input = UI.showWelcomeMenu();
                switch (input) {
                    case "download":
                        String file = UI.showDownloadMenu();
                        System.out.println("TO SERVER");
                        System.out.println("download " + file);
                        client.sendMessage("download " + file);
                        break;
                    case "upload":
                        break;
                    case "exit":
                        System.out.println("ByeBye");
                        break;
                }
            } while (!input.equals("exit"));
            client.sendMessage("exit");
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
