/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import static Common.Resources.FILES_FOLDER;
import java.io.File;
import java.io.FileInputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.CRC32;

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
    private AtomicBoolean endOfTransfer;
    private String fileName;

    public FileSender(Resources connection, Map<Integer, PDU> packets, AtomicBoolean end, String fName) throws UnknownHostException, SocketException {
        connResources = connection;
        packet = new PDU();
        dataFile = new byte[1024];
        packetsList = packets;
        finished = new AtomicBoolean(false);
        endOfTransfer = end;
        fileName = fName;
    }

    @Override
    public void run() {
        try {
            FileInputStream fis = null;
            int readData = 0;
            int seqNumber = 0;
            fis = new FileInputStream(new File(FILES_FOLDER + fileName));

            while (!finished.get()) {
                Arrays.fill(dataFile, (byte)0);
                if (seqNumber == 0) {
                    packet.setFileName(fileName.getBytes());
                    readData = fis.read(dataFile);
                } else {
                    readData = fis.read(dataFile);
                    // se não existirem mais dados para ler do ficheiro
                    if (readData == -1) {
                        finished.set(true);
                        break;
                    }
                }
                packet.setFlagType(2);
                packet.setSeqNumber(++seqNumber);
                packet.setFileData(dataFile);
                CRC32 checksum = new CRC32();
                checksum.update(packet.getSeqNumber());
                checksum.update(packet.getFileData());
                packet.setChecksum(checksum.getValue());
                packetsList.put(seqNumber, packet.clone());
                connResources.send(packet);
                
            }
            sleep(500);
            while (packetsList.size() > 0) {
                for (PDU pdu : packetsList.values()) {
                    connResources.send(pdu);
                }
            }
            packet = new PDU();
            while (endOfTransfer.get()) {
                packet.setFlagType(3);
                connResources.send(packet);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
