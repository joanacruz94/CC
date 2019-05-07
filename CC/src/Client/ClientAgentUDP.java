/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Common.AckReceiver;
import Common.PDU;
import Common.FileReceiver;
import Common.Resources;
import Common.AckSender;
import Common.FileSender;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Scanner;
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
    private static Scanner scanner = new Scanner(System.in);
    private static String input;
    
    public ClientAgentUDP(int port) throws UnknownHostException, SocketException{
        packet = new PDU();
        connResources = new Resources(port, InetAddress.getByName("localhost"));
        connResources.setPortSend(portSend);
        buffer = new byte[256];
        input = null;
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
    
    public void downloadFile() throws UnknownHostException, SocketException, InterruptedException{
        packetsList = new ConcurrentHashMap<>();
        AtomicBoolean transfer = new AtomicBoolean(true);
        FileReceiver fileReceiver = new FileReceiver(connResources, packetsList, transfer);
        AckSender ackSender = new AckSender(connResources, packetsList, transfer);
        fileReceiver.start();
        ackSender.start();
        fileReceiver.join();
        ackSender.join();
    }
    
    public void uploadFile() throws UnknownHostException, InterruptedException, SocketException{
        packetsList = new ConcurrentHashMap<>();
        FileSender fileSender = new FileSender(connResources, packetsList);
        AckReceiver ackReceiver = new AckReceiver(connResources, packetsList);
        fileSender.start();
        ackReceiver.start();
        fileSender.join();
        ackReceiver.join();      
    }
      
    @Override
    public void run(){
        try {
            sendSynPacket();
            connResources.receive();
            packet = connResources.getPacketReceive();
            int newPort = packet.getPort();
            connResources.setPortSend(newPort);
            //packet.ackPacket();
            //connResources.send(packet);
            } catch (Exception ex) {
                ex.printStackTrace();
        } 
    }
}
