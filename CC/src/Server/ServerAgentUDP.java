/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Common.Out;
import Common.PDU;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


/**
 *
 * @author joanacruz
 */
public class ServerAgentUDP extends Thread{
    private DatagramSocket socket;
    private boolean running;
    private PDU packet;
    private byte[] receiveBuf;
    private byte[] sendBuf;
   
    public ServerAgentUDP() throws SocketException {
        this.socket = new DatagramSocket(7777);
        this.running = true;
        this.packet = new PDU();
        this.receiveBuf = new byte[256];
        this.sendBuf  = new byte[256];
    }
    
    public void receive(){
        this.packet.ByteToPDU(this.receiveBuf);
    }
    
    public void send(byte[] buffer, InetAddress address, int port) throws IOException{
        buffer = this.packet.PDUToByte();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
        this.socket.send(packet);
    }
    
    @Override
    public void run() {
        try {
            while (running) {
                this.receiveBuf = new byte[256];
                DatagramPacket packetReceveid = new DatagramPacket(this.receiveBuf, this.receiveBuf.length);
                socket.receive(packetReceveid);
                
                InetAddress address = packetReceveid.getAddress();
                int port = packetReceveid.getPort();
                   
                receive();
                //Início de conexão
                if(this.packet.getFlagType() == 0){
                    System.out.println("Aceitei conexão do cliente na porta " + port + " e o número de sequência é " + this.packet.getSeqNumber() +
                    " , o tipo de flag é " + this.packet.getFlagType() + " e a flag usada é " + this.packet.getTypeOfPDU());
                    System.out.println("O cliente disse " + this.packet.getMessagePacket());
                }
                
                String receivedMessage = this.packet.getMessagePacket();
                System.out.println(receivedMessage);
                if(receivedMessage.matches("download [A-Za-z.]+")){
                    //System.out.println(System.getProperty("user.dir"));
                    Out out = new Out(this.socket, port, this.packet);
                    out.start();
                    /*
                    if(this.packet.getSeqNumber() == 0){
                        file.read(this.sendBuf, 0, this.sendBuf.length);
                        this.packet.setFlagType(2);
                        this.packet.setMessagePacket(new String(this.sendBuf));
                        send(this.packet.PDUToByte(), address, port);
                    }
                    else{
                    }*/
                }
                if(receivedMessage.matches("upload [A-Za-z.]+")){
                    
                }
                if(receivedMessage.equals("exit")){
                    running = false;
                    socket.close();
                }
            }
        } catch (IOException ex) {
        }
    }
}
