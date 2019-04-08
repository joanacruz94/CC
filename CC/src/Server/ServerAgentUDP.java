/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joanacruz
 */
public class ServerAgentUDP extends Thread {
 
    private DatagramSocket socket;
    private boolean running;
    private byte[] receiveBuf;
    private byte[] sendBuf;

 
    public ServerAgentUDP() throws SocketException {
        socket = new DatagramSocket(4445);
    }
 
    public void run() {
        running = true;
        try {
            while (running) {
                this.receiveBuf = new byte[256];
                this.sendBuf  = new byte[256];
                DatagramPacket packetReceveid = new DatagramPacket(this.receiveBuf, this.receiveBuf.length);
                socket.receive(packetReceveid);
                int message = ByteBuffer.wrap(packetReceveid.getData()).getInt();
                InetAddress address = packetReceveid.getAddress();
                int port = packetReceveid.getPort();
                this.sendBuf = ByteBuffer.allocate(15).putInt(message).array();
                DatagramPacket packetSent = new DatagramPacket(this.sendBuf, this.sendBuf.length, address, port);
                String received = new String(packetSent.getData(), 0, packetSent.getLength());
                if(received.equals("exit")){
                    running = false;
                    continue;
                }
                System.out.println(received);
                socket.send(packetSent);
            }
        } catch (IOException ex) {
        }
        socket.close();
        System.out.println("Acabou");
    }
}