/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author joanacruz
 */
public class Out extends Thread{
    
    private DatagramSocket socket;
    private int port;
    private InetAddress address;
    private PDU packet;
    private byte[] sendBuf;

    public Out(DatagramSocket socket, int port, PDU packet) throws UnknownHostException {
        this.socket = socket;
        this.port = port;
        this.address = InetAddress.getByName("localhost");
        this.packet = packet;
        this.sendBuf = new byte[256];
    }
    
    @Override
    public void run(){
        FileInputStream file = null;
        try {
            file = new FileInputStream(new File("./src/Server/file.txt"));
        } catch (FileNotFoundException ex) {
        }
        if(this.packet.getSeqNumber() == 0){
            try {
                file.read(this.sendBuf, 0, this.sendBuf.length);
            } catch (IOException ex) {
            }
            this.packet.setFlagType(2);
            this.packet.setMessagePacket(new String(this.sendBuf));
            byte[] buffer = this.packet.PDUToByte();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
            try {
                this.socket.send(packet);
            } catch (IOException ex) {
            }
        }
        else{
            int dataLength = 0;
            try {
                dataLength = file.read(this.sendBuf, 0, this.sendBuf.length);
            } catch (IOException ex) {
            }
            // se não existe mais dados para ler
            if (dataLength == -1){
            }
            // envia o resto dos dados
            else{
                this.packet.setFlagType(2);
                this.packet.setMessagePacket(new String(this.sendBuf));
                byte[] buffer = this.packet.PDUToByte();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
            }
        }
    }
    
}
