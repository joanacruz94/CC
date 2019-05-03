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

/**
 *
 * @author joanacruz
 */
public class ClientAgentUDP extends Thread{
    private DatagramSocket socket;
    private InetAddress address;
    private PDU packet;
    private byte[] sendBuf;
    private byte[] receiveBuf;
    
    public ClientAgentUDP(int port) throws SocketException, UnknownHostException {
        this.socket = new DatagramSocket(port);
        this.address = InetAddress.getByName("localhost");
        this.packet = new PDU();
        this.sendBuf = new byte[256];
        this.receiveBuf = new byte[256];
    }
    
    public void send(byte[] buffer) throws IOException{
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, this.address, 7777);
        this.socket.send(packet);
    }
    
    public void receive() throws IOException{
        DatagramPacket packet = new DatagramPacket(this.receiveBuf, this.receiveBuf.length);
        socket.receive(packet);    
        this.packet.ByteToPDU(this.receiveBuf);
        System.out.println("Número de sequência " + this.packet.getSeqNumber() +
                           "    Número de ACK " + this.packet.getAckNumber() +
                           "    Flag Type " + this.packet.getFlagType() +
                           "    PDU Type " + this.packet.getTypeOfPDU() +
                           "    Mensagem do segmento" + this.packet.getMessagePacket());
    }
    
    public void close() {
        socket.close();
    }
    
    public void run(){
        try{
            boolean running = true;
            while(running){
                receive();
                this.packet.setSeqNumber(this.packet.getSeqNumber() + 1);
                send(this.packet.PDUToByte());
            }
        
        }
        catch (IOException ex){
        }
    }
}
