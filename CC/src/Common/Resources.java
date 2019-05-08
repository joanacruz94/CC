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
import java.net.SocketTimeoutException;
import java.util.Arrays;

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
    
    static public final String FILES_FOLDER = "./src/Server/Files/";

    public Resources(int port, InetAddress address) throws SocketException {
        this.socket = new DatagramSocket(port);
        this.address = address;
        this.packetReceive = new PDU();
        this.packetSend = new PDU();
        this.portSend = 0;
        this.buffer = new byte[1064];
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

    static public byte[] trim(byte[] bytes) {
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0) {
            --i;
        }
        return Arrays.copyOf(bytes, i + 1);
    }

    public void receive() throws IOException {
        DatagramPacket packet = new DatagramPacket(this.buffer, this.buffer.length);
        this.socket.receive(packet);
        this.address = packet.getAddress();
        this.portSend = packet.getPort();
        this.addressSend = packet.getAddress();
        this.packetReceive.ByteToPDU(this.buffer);
        Arrays.fill(this.buffer, (byte) 0);
        printStateReceive();
    }

    public boolean receive(int timeout) {
        int prevTimeout = -1;
        try {
            DatagramPacket packet = new DatagramPacket(this.buffer, this.buffer.length);
            prevTimeout = this.socket.getSoTimeout();
            this.socket.setSoTimeout(timeout);
            try {
                this.socket.receive(packet);
            } catch (SocketTimeoutException ex) {
                this.socket.setSoTimeout(prevTimeout);
                return false;
            }
            this.socket.setSoTimeout(prevTimeout);
            this.address = packet.getAddress();
            this.portSend = packet.getPort();
            this.addressSend = packet.getAddress();
            this.packetReceive.ByteToPDU(this.buffer);
            Arrays.fill(this.buffer, (byte) 0);
            printStateReceive();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public void send(PDU pdu) throws IOException {
        setPacketSend(pdu);
        this.buffer = this.packetSend.PDUToByte();
        DatagramPacket packet = new DatagramPacket(this.buffer, this.buffer.length, this.address, this.portSend);
        this.socket.send(packet);
        Arrays.fill(this.buffer, (byte) 0);
        printStateSend();
    }

    public void close() {
        this.socket.close();
    }

    public void printStateReceive() {
        System.out.println("RECEIVE:\nSequence Number " + this.packetReceive.getSeqNumber()
                + "    ACK Number " + this.packetReceive.getAckNumber()
                + "    Flag Type " + this.packetReceive.getFlagType()
                + "    PDU Type " + this.packetReceive.getTypeOfPDU()
                + "    Message Total Bytes " + trim(this.packetReceive.getFileData()).length
                + "    Port Receive " + portSend
                + "    Current Thread" + Thread.currentThread().getName());
    }

    public void printStateSend() {
        System.out.println("SEND:\nSequence Number " + this.packetSend.getSeqNumber()
                + "    ACK Number " + this.packetSend.getAckNumber()
                + "    Flag Type " + this.packetSend.getFlagType()
                + "    PDU Type " + this.packetSend.getTypeOfPDU()
                + "    Message Total Bytes " + trim(this.packetSend.getFileData()).length
                + "    Port Send " + portSend
                + "    Current Thread" + Thread.currentThread().getName());
    }

}
