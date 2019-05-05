/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Common.PDU;
import Common.Resources;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joanacruz
 */
public class ClientAgent extends Thread{
    private final int portSend = 7777;
    private PDU packet;
    private Resources connectionResources;
    
    public ClientAgent(int port) throws UnknownHostException, SocketException{
        packet = new PDU();
        connectionResources = new Resources(port, InetAddress.getByName("localhost"));
        connectionResources.setPortSend(portSend);
    }
    
    public void setPacket(PDU pdu){
        packet = pdu;
    }
    
    public void send() throws IOException{
        connectionResources.send(packet);
    }
    
    private void sendSynPacket() throws IOException{
        packet.setMessagePacket("S");
        connectionResources.send(packet);
    }
    
    public void closeConnection(){
        connectionResources.close();
    }
      
    @Override
    public void run(){
        try {
            boolean running = true;
            sendSynPacket();
            while(running){
                connectionResources.receive();
                packet = connectionResources.getPacket();
                if(packet.getFlagType() == 4){
                    int newPort = packet.getPort();
                    connectionResources.setPortSend(newPort);
                    packet.sendACK();
                }
                sleep(5);
            }
        } catch (IOException ex) {
        } catch (InterruptedException ex) {
        }
    }
}
