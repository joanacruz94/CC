/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author joanacruz
 */
public class AckReceiver extends Thread{
    private Resources connResources;
    private PDU packet;
    private Map<Integer, PDU> packetsList;
    
    public AckReceiver(Resources connection, Map<Integer, PDU> packets) throws UnknownHostException, SocketException{
        connResources = connection;
        packet = new PDU();
        packetsList = packets;
    }
    
    @Override
    public void run(){
        boolean running = true;
        try {
            while(running){
                connResources.receive();
                packet = connResources.getPacketReceive();
                int ackNumber = packet.getAckNumber();
                packetsList.remove(ackNumber);
                if(packetsList.isEmpty()) running = false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
