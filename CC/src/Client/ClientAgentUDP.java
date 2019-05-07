/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Common.PDU;
import Common.FileReceiver;
import Common.Resources;
import Common.AckSender;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author joanacruz
 */
public class ClientAgentUDP extends Thread{
    private final int portSend = 7777;
    private PDU packet;
    private Resources connResources;
    private byte[] buffer;
    private Map<Integer, PDU> packetsList;
    
    public ClientAgentUDP(int port) throws UnknownHostException, SocketException{
        packet = new PDU();
        connResources = new Resources(port, InetAddress.getByName("localhost"));
        connResources.setPortSend(portSend);
        buffer = new byte[256];
        packetsList = new ConcurrentHashMap<>();
    }
    
    public void setPacket(PDU pdu){
        packet = pdu;
    }

    public PDU getPacket() {
        return packet;
    }
    
    public void send() throws IOException{
        connResources.send(packet);
    }
    
    private void sendSynPacket() throws IOException{
        packet.setMessagePacket("S");
        connResources.send(packet);
    }
    
    public void closeConnection(){
        connResources.close();
    }
    
    public void corram() throws UnknownHostException, SocketException{
        AtomicBoolean transfer = new AtomicBoolean(true);
        FileReceiver receiver = new FileReceiver(connResources, packetsList, transfer);
        AckSender sender = new AckSender(connResources, packetsList, transfer);
        receiver.start();
        sender.start();

    }
      
    @Override
    public void run(){
        try {
            sendSynPacket();
            connResources.receive();
            packet = connResources.getPacketReceive();
            int newPort = packet.getPort();
            System.out.println(newPort);

                        System.out.println(connResources.getPortSend());

            connResources.setPortSend(newPort);
                        System.out.println(connResources.getPortSend());

            packet.ackPacket();
            connResources.send(packet);
            System.out.println(connResources.getPortSend());
            } catch (Exception ex) {
                ex.printStackTrace();
        } 
    }
}
