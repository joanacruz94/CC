/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import static Common.Resources.trim;
import java.io.File;
import java.io.FileOutputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.CRC32;

/**
 *
 * @author joanacruz
 */
public class FileReceiver extends Thread {

    private Resources connResources;
    private PDU packet;
    private Map<Integer, PDU> packetsList;
    private Map<Integer, PDU> packetsFinal;
    private byte[] buffer;
    private AtomicBoolean transferComplete;

    public FileReceiver(Resources connection, Map<Integer, PDU> packets, AtomicBoolean transfer) throws UnknownHostException, SocketException {
        connResources = connection;
        packet = new PDU();
        packetsList = packets;
        packetsFinal = new ConcurrentHashMap<>();
        transferComplete = transfer;
        buffer = new byte[1024];
    }

    @Override
    public void run() {
        int dataFile = 0;
        FileOutputStream fos = null;
        try {
            while (transferComplete.get()) {
                connResources.receive();
                packet = connResources.getPacketReceive();

                if (packet.getFlagType() == 2) {
                    long checksumReceived = packet.getChecksum();
                    CRC32 checksum = new CRC32();
                    checksum.update(packet.getSeqNumber());
                    checksum.update(packet.getFileData());
                    long calculatedChecksum = checksum.getValue();
                    if (checksumReceived == calculatedChecksum) {
                        packetsList.put(packet.getSeqNumber(), packet.clone());
                        packetsFinal.put(packet.getSeqNumber(), packet.clone());
                    } else {
                        System.out.println("Falha no checksum");
                    }
                }
                if (packet.getFlagType() == 3) {
                    transferComplete.set(false);
                }

            }
            
            for (PDU pdu : packetsFinal.values()) {
                if (pdu.getSeqNumber() == 1) {
                    String fileName = new String(pdu.getFileName());
                    File file = new File("./src/Client/" + fileName);
                    file.createNewFile();
                    fos = new FileOutputStream(file);
                }
                buffer = pdu.getFileData();
                fos.write(buffer, 0, trim(buffer).length);
                buffer = new byte[1024];
                //dataFile += buffer.length;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
