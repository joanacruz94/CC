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
public class ClientAgentUDP {

    private final int serverPort = 7777;
    private PDU packet;
    private Resources connResources;
    private Map<Integer, PDU> packetsList;

    public ClientAgentUDP(String serverHost, int port) throws UnknownHostException, SocketException {
        packet = new PDU();
        connResources = new Resources(port, InetAddress.getByName(serverHost));
        connResources.setPortSend(serverPort);
    }

    public void setPacket(PDU pdu) {
        packet = pdu;
    }

    public PDU getPacket() {
        return packet;
    }

    public void send() throws IOException {
        connResources.send(packet);
    }

    public void receive() throws IOException {
        connResources.receive();
        packet = connResources.getPacketReceive();
    }

    public void closeConnection() {
        connResources.close();
    }

    public void downloadFile(String fileName) throws UnknownHostException, SocketException, InterruptedException {
        packetsList = new ConcurrentHashMap<>();
        AtomicBoolean transfer = new AtomicBoolean(true);
        FileReceiver fileReceiver = new FileReceiver(connResources, packetsList, transfer, fileName);
        AckSender ackSender = new AckSender(connResources, packetsList, transfer);
        fileReceiver.start();
        ackSender.start();
        fileReceiver.join();
        ackSender.join();
        for (int i = 0; i < 3; i++) {
            connResources.receive(500);
        }
    }

    /*public void uploadFile() throws UnknownHostException, InterruptedException, SocketException{
        packetsList = new ConcurrentHashMap<>();
        FileSender fileSender = new FileSender(connResources, packetsList);
        AckReceiver ackReceiver = new AckReceiver(connResources, packetsList);
        fileSender.start();
        ackReceiver.start();
        fileSender.join();
        ackReceiver.join();      
    }*/
    public boolean connect() {
        try {
            int attempts = 5;
            while (attempts > 0) {
                send();
                if (connResources.receive(5000)) {
                    break;
                }
                attempts--;

            }
            if (attempts == 0) {
                return false;
            }
            packet.ackPacket();
            connResources.send(packet);
            connResources.send(packet);
            connResources.send(packet);
            packet = connResources.getPacketReceive();
            int newPort = packet.getPort();
            connResources.setPortSend(newPort);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
