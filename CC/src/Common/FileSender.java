/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joanacruz
 */
public class FileSender extends Thread {

    private Resources connResources;
    private PDU packet;
    private byte[] dataFile;
    private Map<Integer, PDU> packetsList;
    private final AtomicBoolean finished;

    public FileSender(Resources connection, Map<Integer, PDU> packets) throws UnknownHostException, SocketException {
        connResources = connection;
        packet = new PDU();
        dataFile = new byte[256];
        packetsList = packets;
        finished = new AtomicBoolean(true);
    }
    
    @Override
    public void run() {
        try {
            FileInputStream fis = null;
            int readData = 0;
            int seqNumber = 0;
            fis = new FileInputStream(new File("./src/Server/file.txt"));
            while (finished.get()) {
                //packet = connResources.getPacket();
                //int ackNumber = packet.getAckNumber();
                if (seqNumber == 1) {
                    readData = fis.read(dataFile, 0, dataFile.length);
                    //packet.setAckNumber(ackNumber + packet.getLengthData());
                } else {
                    fis.skip(readData);
                    readData = fis.read(dataFile, 0, dataFile.length);
                    //packet.setAckNumber(seqNumber);

                }
                // se não existirem mais dados para ler do ficheiro
                if (readData == -1) {
                    finished.set(false);
                }

                packet.setFlagType(2);
                packet.setSeqNumber(++seqNumber);
                packet.setMessagePacket(new String(dataFile));
                packet.setLengthData(readData);
                packetsList.put(seqNumber, packet.clone());
                connResources.send(packet);
                                System.out.println("SeqNumber " + seqNumber);
            }
            sleep(5000);
            while(packetsList.size()>0){
                for(PDU pdu : packetsList.values())
                    connResources.send(pdu);
            }
            packet.setFlagType(3);
            connResources.send(packet);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
