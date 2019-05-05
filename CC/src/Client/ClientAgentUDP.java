/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Common.PDU;
import java.io.File;
import java.io.FileOutputStream;
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
    private final DatagramSocket socket;
    private final InetAddress address;
    private final PDU packet;
    private int port = 7777;
    private byte[] sendBuf;
    private byte[] receiveBuf;
    
    public ClientAgentUDP(int port) throws SocketException, UnknownHostException {
        this.socket = new DatagramSocket(port);
        this.address = InetAddress.getByName("localhost");
        this.packet = new PDU();
        this.sendBuf = new byte[256];
        this.receiveBuf = new byte[256];
    }
    
    private void sendSynPacket() throws IOException{
        this.packet.setMessagePacket("S");
        send(this.packet.PDUToByte());
    }
    
    public void send(byte[] buffer) throws IOException{
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, this.address, port);
        this.socket.send(packet);
    }
    
    public void receive() throws IOException{
        DatagramPacket packet = new DatagramPacket(this.receiveBuf, this.receiveBuf.length);
        socket.receive(packet);    
        this.packet.ByteToPDU(this.receiveBuf);
        System.out.println("RECEBI:\nNúmero de sequência " + this.packet.getSeqNumber() +
                           "    Número de ACK " + this.packet.getAckNumber() +
                           "    Flag Type " + this.packet.getFlagType() +
                           "    PDU Type " + this.packet.getTypeOfPDU() +
                           "    Mensagem do segmento " + this.packet.getMessagePacket() + 
                           "    Total Bytes Mensagem " + this.packet.getLengthData() + 
                           "    Port " + this.packet.getPort()
        );
        if(this.packet.getFlagType() == 4){
            this.port = this.packet.getPort();
        }
        if(this.packet.getFlagType() == 2){
            FileOutputStream fos = null;
            if(this.packet.getSeqNumber() == 1){
                File file = new File("./src/Client/file.txt");
                file.createNewFile();
                fos = new FileOutputStream(file);
                fos.write(this.receiveBuf, 0, this.receiveBuf.length);
            }
        }
    }
    
    public void close() {
        socket.close();
    }
    
    public void run(){
        try{
            boolean running = true;
            sendSynPacket();
            while(running){
                this.receiveBuf = new byte[256];
                this.sendBuf = new byte[256];
                receive();
                if(this.packet.getFlagType() == 2 || this.packet.getFlagType() == 4){
                    System.out.println("OLA");
                    this.packet.sendACK();
                    send(this.packet.PDUToByte());
                }
            }
        
        }
        catch (IOException ex){
        }
    }
}
