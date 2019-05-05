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
        System.out.println("Type the port number");
        input = scanner.nextLine();
        ClientAgent client = new ClientAgent(Integer.parseInt(input));
        client.start();
        
        try {            
            PDU pdu = new PDU();
            do {
                //System.out.print("JOE > ");
                input = scanner.nextLine();
                pdu.sendProtocolar(input);
                client.setPacket(pdu);
                client.send();
            } while (!input.equals("exit"));
            client.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
