/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author joanacruz
 */
public class FileReceiver extends Thread{   
    private Resources connResources;
    private PDU packet;
    private Map<Integer, PDU> packetsList;
    private Map<Integer, PDU> packetsFinal;
    private byte[] buffer;
    private AtomicBoolean transferComplete;
    
    public FileReceiver(Resources connection, Map<Integer, PDU> packets, AtomicBoolean transfer) throws UnknownHostException, SocketException{
        connResources = connection;
        packet = new PDU();
        packetsList = packets;
        packetsFinal = new ConcurrentHashMap<>();
        transferComplete = transfer;
        buffer = new byte[256];
    }
    
    @Override
    public void run(){
        int dataFile = 0;
        FileOutputStream fos = null;
        try {
            while(transferComplete.get()){
                connResources.receive();
                packet = connResources.getPacketReceive();
                packetsList.put(packet.getSeqNumber(), packet.clone());
                packetsFinal.put(packet.getSeqNumber(), packet.clone());
                if(packet.getFlagType() == 3){
                    transferComplete.set(false);
                }
            }
            for(PDU pdu : packetsFinal.values()){
                System.out.println("SEQ NUMB PDU" + pdu.getSeqNumber());
                /*if(pdu.getSeqNumber() == 1){
                    File file = new File("./src/Client/file.txt");
                    file.createNewFile();
                    fos = new FileOutputStream(file);
                }
                buffer = pdu.getMessagePacket().getBytes();
                fos.write(buffer, dataFile, buffer.length);
                dataFile += buffer.length;*/
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
