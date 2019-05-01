/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Common.PDU;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ClientAgentUDP extends Thread{
    private DatagramSocket socket;
    private InetAddress address;
    private PDU packet;
    private byte[] sendbuf;
    private byte[] receivebuf;
 
    public ClientAgentUDP(int port) throws SocketException, UnknownHostException {
        this.socket = new DatagramSocket(port);
        this.address = InetAddress.getByName("localhost");
        this.sendbuf = new byte[256];
        this.receivebuf = new byte[256];
    }
    
    public void sendPDU(PDU pdu) throws IOException{
        this.sendbuf = pdu.PDUtoByte();
        DatagramPacket packet = new DatagramPacket(this.sendbuf, this.sendbuf.length, this.address, 7777);
        this.socket.send(packet);
    }
    
    public void sendMessage(String msg) throws IOException{
        this.sendbuf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(this.sendbuf, this.sendbuf.length, this.address, 7777);
        this.socket.send(packet);
    }
    
    public String receiveMessage() throws IOException{
        DatagramPacket packet = new DatagramPacket(receivebuf, receivebuf.length);
        socket.receive(packet);
        String received = new String(packet.getData(), 0, packet.getLength());
        return received;
    }
 
    public void close() {
        socket.close();
    }
    
    public void run(){
        try{
            boolean running = true;
            while(running){
                String receivedMessage = receiveMessage();
                System.out.println(receivedMessage);
            }
        
        }
        catch (IOException ex){
        }
    }
}