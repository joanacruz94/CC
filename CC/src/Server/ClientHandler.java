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
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/*
 *
 * @author joanacruz
 */
public class ClientHandler extends Thread{
    private Resources connResources;
    private PDU packet;
    private Map<Integer, PDU> packetsList;
    
    public ClientHandler(int port) throws UnknownHostException, SocketException {
        connResources = new Resources(port, InetAddress.getByName("localhost"));
        packetsList = new ConcurrentHashMap<>();
    }
    
    @Override
    public void run(){
        try {
            boolean running = true;
            try {
                while(running){
                    connResources.receive();
                    packet = connResources.getPacketReceive();
                    switch (packet.getFlagType()) {
                        case 5:
                            AckReceiver in = new AckReceiver(connResources, packetsList);
                            FileSender out = new FileSender(connResources, packetsList);
                            in.start();
                            out.start();
                            in.join();
                            out.join();
                            break;
                        //TODO: Enviar lista de ficheiros
                        case 8:
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