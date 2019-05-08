/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Common.AckReceiver;
import Common.FileSender;
import Common.PDU;
import Common.Resources;
import static Common.Resources.trim;

import static Common.Resources.FILES_FOLDER;
import java.io.File;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;


/*
 *
 * @author joanacruz
 */
public class ClientHandler extends Thread {

    private Resources connResources;
    private PDU packet;
    
    ClientHandler(InetAddress addressSend, int port) throws UnknownHostException, SocketException {
        connResources = new Resources(port, addressSend);
    }

    @Override
    public void run() {
        try {
            boolean running = true;
            try {
                while (running) {
                    connResources.receive();
                    packet = connResources.getPacketReceive();
                    Map<Integer, PDU> packetsList = new ConcurrentHashMap<>();
                    AtomicBoolean endOfTransfer = new AtomicBoolean(true);
                    switch (packet.getFlagType()) {
                        case 5:
                            String fileName = new String(packet.getFileData()).split(" ")[1];
                            AckReceiver in = new AckReceiver(connResources, packetsList, endOfTransfer);
                            FileSender out = new FileSender(connResources, packetsList, endOfTransfer, fileName);
                            in.start();
                            out.start();
                            in.join();
                            out.join();
                            break;
                        //TODO: Enviar lista de ficheiros
                        case 8:
                            File folder = new File(FILES_FOLDER);
                            File[] listOfFiles = folder.listFiles();
                            String filesList = "";
                            for (int i = 0; i < listOfFiles.length; i++) {
                                if (listOfFiles[i].isFile()) {
                                    filesList += listOfFiles[i].getName() + ";";
                                }
                            }
                            packet.setFileData(filesList.getBytes());
                            connResources.send(packet);
                            break;
                        case 7:
                            packet.ackPacket();
                            connResources.send(packet);
                            packet.setFlagType(3);
                            connResources.send(packet);
                            System.out.println("Socket closed with ports " + packet.getPort() + " " + connResources.getPortSend()
                                    + " " + connResources.getSocket().getPort());
                            connResources.close();
                            running = false;
                            break;
                        case 3:
                            System.out.println("\n\n A TUA BELHA\n");
                            break;
                        default:
                            break;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
