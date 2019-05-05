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
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 * @author joanacruz
 */
public class Out extends Thread{
    private Resources connResources;
    private PDU packet;
    private byte[] dataFile;
    
    public Out(Resources connection) throws UnknownHostException, SocketException {
        connResources = connection;
        packet = new PDU();
        dataFile = new byte[256];
    }
    
    @Override
    public void run(){
        try {
            FileInputStream fis = null;
            boolean transfer = true;
            int readData = 0;
            fis = new FileInputStream(new File("./src/Server/file.txt"));
            while (transfer) {
                packet = connResources.getPacket();
                int seqNumber = packet.getSeqNumber();
                int ackNumber = packet.getAckNumber();
                System.out.println("SEQ NUM " + seqNumber);
                if (seqNumber == 1) {
                    readData = fis.read(dataFile, 0, dataFile.length);
                    packet.setAckNumber(ackNumber + packet.getLengthData());
                    System.out.println("ACK NUM " + ackNumber);
                }
                else{
                    fis.skip(readData);
                    readData = fis.read(dataFile, 0, dataFile.length);
                    packet.setAckNumber(seqNumber);
                    System.out.println("READ DATA" + readData);
                    // se não existirem mais dados para ler do ficheiro
                    if (readData == -1) {
                        transfer = false;
                    }
                }
                packet.setFlagType(2);
                packet.setSeqNumber(ackNumber);
                packet.setMessagePacket(new String(dataFile));
                packet.setLengthData(readData);
                connResources.send(packet);
                sleep(5000);
            }
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } catch (InterruptedException ex) {
        }
    }
}
