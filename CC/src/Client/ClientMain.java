/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Common.PDU;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author joanacruz
 */
public class ClientMain {

    private static Scanner scanner = new Scanner(System.in);
    private static String input;

    public static void main(String[] args) throws IOException {
        String serverHost = "localhost";
        int clientPort = 5000;
        if(args.length > 1){
            serverHost = args[1];
        }
        if(args.length > 2){
            clientPort = Integer.parseInt(args[2]);
        }
        ClientAgentUDP client = new ClientAgentUDP(serverHost, clientPort);
        
        if (!client.connect()) {
            System.out.println("Could not connect to server.");
            return;
        }
        try {
            PDU pdu = new PDU();
            do {
                System.out.print("JOE > ");
                input = scanner.nextLine();
                pdu.protocolarPacket(input);
                client.setPacket(pdu);
                client.send();
                if(input.matches("download [A-Za-z0-9._]+")) {
                    client.downloadFile();
                }
                if(input.equals("list")){
                    client.receive();
                    pdu = client.getPacket();
                    String[] filesList = new String(pdu.getFileData()).split(";");
                    System.out.println("Files List");
                    for(String file : filesList) System.out.println(file);
                }
                
                //if(input.matches("upload [A-Za-z0-9.]+"))
                //client.uploadFile();
            } while (!input.equals("exit"));
            pdu = client.getPacket();
            pdu.setFlagType(3);
            client.setPacket(pdu);
            client.send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
