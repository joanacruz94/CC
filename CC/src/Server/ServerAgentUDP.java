/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Common.PDU;
import Common.Resources;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 * @author joanacruz
 */
public class ServerAgentUDP extends Thread{
    private Resources connResources;
    private int clientPort = 7771;
    private PDU packet;
    
    public ServerAgentUDP() throws SocketException, UnknownHostException {
        connResources = new Resources(7777, InetAddress.getByName("localhost"));
        packet = new PDU();
    }
    
    public void run() {
        boolean running = true;
        try {
            while(running) {
                connResources.receive();
                
                packet = connResources.getPacket();
                        
                /* Método que declara o início de uma conexão
                 * Recebe um segmento do tipo SYN de um cliente
                 * Envia ao cliente um segmento do tipo SYN + ACK
                 */
                if(packet.getFlagType() == 0){
                    System.out.println("Accepted conection from client in port " + connResources.getPortSend() + " !");
                    packet.sendSYNACK(clientPort);
                    connResources.send(packet);
                    System.out.println("The server now will answer to client in port " + clientPort);
                    ClientHandler handler = new ClientHandler(clientPort++, packet);
                    handler.start(); 
                }                                                
            }
        } catch (IOException ex) {
        }
    }
}
