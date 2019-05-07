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
    private InetAddress addressSend;
    private PDU packetReceive;
    private PDU packetSend;
    private int portSend;
    private byte[] buffer;
    
    public Resources(int port, InetAddress address) throws SocketException{
        this.socket = new DatagramSocket(port);
        this.address = address;
        this.packetReceive = new PDU();
        this.packetSend = new PDU();
        this.portSend = 0;
        this.buffer = new byte[276];
        //this.socket.setSoTimeout(60000);
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

    public InetAddress getAddressSend() {
        return addressSend;
    }

    public void setAddressSend(InetAddress addressSend) {
        this.addressSend = addressSend;
    }
   
    public PDU getPacketReceive() {
        return packetReceive;
    }

    public void setPacketReceive(PDU packet) {
        this.packetReceive = packet;
    }

    public PDU getPacketSend() {
        return packetSend;
    }

    public void setPacketSend(PDU packetSend) {
        this.packetSend = packetSend;
    }

    public void receive() throws IOException{
        DatagramPacket packet = new DatagramPacket(this.buffer, this.buffer.length);
        this.socket.receive(packet);      
        this.address = packet.getAddress();
        this.portSend = packet.getPort();
        this.addressSend = packet.getAddress();
        this.packetReceive.ByteToPDU(this.buffer);
        this.buffer = new byte[276];
        printStateReceive();
    }
    
    public void send(PDU pdu) throws IOException{
        setPacketSend(pdu);
        this.buffer = this.packetSend.PDUToByte();
        DatagramPacket packet = new DatagramPacket(this.buffer, this.buffer.length, this.address, this.portSend);
        this.socket.send(packet);
        this.buffer = new byte[276];
        printStateSend();
    }
    
    public void close(){
        this.socket.close();
    }
    
    public void printStateReceive(){
        System.out.println("RECEIVE:\nSequence Number " + this.packetReceive.getSeqNumber() +
                           "    ACK Number " + this.packetReceive.getAckNumber() +
                           "    Flag Type " + this.packetReceive.getFlagType() +
                           "    PDU Type " + this.packetReceive.getTypeOfPDU() +
                           "    Message Total Bytes " + this.packetReceive.getLengthData() +
                           "    Port Receive " + portSend +
                           "    Current Thread Working " + Thread.currentThread().toString());    
    }
    public void printStateSend(){
        System.out.println("SEND:\nSequence Number " + this.packetSend.getSeqNumber() +
                           "    ACK Number " + this.packetSend.getAckNumber() +
                           "    Flag Type " + this.packetSend.getFlagType() +
                           "    PDU Type " + this.packetSend.getTypeOfPDU() +
                           "    Message Total Bytes " + this.packetSend.getLengthData() +
                           "    Port Send " + portSend +
                           "    Current Thread Working " + Thread.currentThread().toString());    
    }
    
}
