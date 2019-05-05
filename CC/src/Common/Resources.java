/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 *
 * @author joanacruz
 */
public class Resources {
    private DatagramSocket socket;
    private InetAddress address;
    private PDU packet;
    private int portSend;
    private byte[] buffer;
    
    public Resources(int port, InetAddress address) throws SocketException{
        this.socket = new DatagramSocket(port);
        this.address = address;
        this.packet = new PDU();
        this.portSend = 0;
        this.buffer = new byte[276];
    }
        
    public DatagramSocket getSocket() {
        return socket;
    }

    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
    }
    
    public int getPortSend() {
        return portSend;
    }
    
    public void setPortSend(int portSend) {
        this.portSend = portSend;
    }
    
    public PDU getPacket() {
        return packet;
    }

    public void setPacket(PDU packet) {
        this.packet = packet;
    }
    
    public void receive() throws IOException{
        DatagramPacket packet = new DatagramPacket(this.buffer, this.buffer.length);
        this.socket.receive(packet);      
        this.address = packet.getAddress();
        this.portSend = packet.getPort();
        this.packet.ByteToPDU(this.buffer);
        System.out.println("COMP BUFFER " + buffer.length);
        this.buffer = new byte[276];
        printState();
    }
    
    public void send(PDU pdu) throws IOException{
        setPacket(pdu);
        this.buffer = this.packet.PDUToByte();
        DatagramPacket packet = new DatagramPacket(this.buffer, this.buffer.length, this.address, this.portSend);
        this.socket.send(packet);
        this.buffer = new byte[276];
        printState1();
    }
    
    public void close(){
        this.socket.close();
    }
    
    public void printState(){
        System.out.println("RECEBI:\nNúmero de sequência " + this.packet.getSeqNumber() +
                           "    Número de ACK " + this.packet.getAckNumber() +
                           "    Flag Type " + this.packet.getFlagType() +
                           "    PDU Type " + this.packet.getTypeOfPDU() +
                           "    Mensagem do segmento " + this.packet.getMessagePacket() +
                           "    Total Bytes Mensagem " + this.packet.getLengthData() +
                           "    PORTA " + portSend);    
    }
    public void printState1(){
        System.out.println("ENVIEI:\nNúmero de sequência " + this.packet.getSeqNumber() +
                           "    Número de ACK " + this.packet.getAckNumber() +
                           "    Flag Type " + this.packet.getFlagType() +
                           "    PDU Type " + this.packet.getTypeOfPDU() +
                           "    Mensagem do segmento " + this.packet.getMessagePacket() +
                           "    Total Bytes Mensagem " + this.packet.getLengthData() +
                           "    PORTA " + portSend);    
    }
    
}
