/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Common.In;
import Common.Out;
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
public class ClientHandler extends Thread{
    private Resources connResources;
    private PDU packet;
    
    public ClientHandler(int port, PDU packet) throws UnknownHostException, SocketException {
        connResources = new Resources(port, InetAddress.getByName("localhost"));
        connResources.setPacket(packet);
    }
    
    @Override
    public void run(){
        boolean running = true;
        String receivedMessage = null;
        try {
            connResources.receive();
            packet = connResources.getPacket();

            while(running){                
                receivedMessage = packet.getMessagePacket();           
                if(receivedMessage.matches("download [A-Za-z0-9.]+")){
                    In in = new In(connResources);
                    Out out = new Out(connResources);
                    //in.start();
                    out.start();
                }
                else if(receivedMessage.matches("exit")){
                    System.out.println("Socket closed with ports " + packet.getPort() + " " + connResources.getPortSend() 
                    + " " + connResources.getSocket().getPort());
                    connResources.close();
                    running = false;
                }
            }
        } catch (IOException ex) {
        }
    }
}
