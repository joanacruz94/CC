/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 *
 * @author joanacruz
 */
public class PDU {
    private int seqNumber;
    private int ackNumber;
    private int flagType;
    private int port;
    private String messagePacket;
    private int lengthData;

    public PDU() {
        this.seqNumber = 0;
        this.ackNumber = 0;
        this.flagType = 0;
        this.port = 0;
        this.messagePacket = "";
        this.lengthData = 0;
    }
    
    /*
    public PDU(byte[] dataPacket) {
        this.seqNumber = ByteBuffer.wrap(Arrays.copyOfRange(dataPacket, 0, 4)).getInt();
        this.ackNumber = ByteBuffer.wrap(Arrays.copyOfRange(dataPacket, 4, 8)).getInt();
        this.flagType = ByteBuffer.wrap(Arrays.copyOfRange(dataPacket, 8, 12)).getInt();
        byte[] message = trim(dataPacket);
        this.messagePacket = new String(ByteBuffer.wrap(Arrays.copyOfRange(message, 12, message.length)).array());
    }*/

    public PDU(int seqNumber, int ackNumber, int flagType, String messagePacket) {
        this.seqNumber = seqNumber;
        this.ackNumber = ackNumber;
        this.flagType = flagType;
        this.messagePacket = messagePacket;
        this.lengthData = messagePacket.getBytes().length;
        this.port = 0;
    }
    
    public PDU(PDU packet){
        this.seqNumber = packet.getSeqNumber();
        this.ackNumber = packet.getAckNumber();
        this.flagType = packet.getFlagType();
        this.messagePacket = packet.getMessagePacket();
        this.lengthData = packet.getLengthData();
        this.port = packet.getPort();
    }
    
    static byte[] trim(byte[] bytes) {
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0){
            --i;
        }
        return Arrays.copyOf(bytes, i + 1);
    }

    public int getSeqNumber() {
        return seqNumber;
    }

    public void setSeqNumber(int seqNumber) {
        this.seqNumber = seqNumber;
    }

    public int getAckNumber() {
        return ackNumber;
    }

    public void setAckNumber(int ackNumber) {
        this.ackNumber = ackNumber;
    }

    public int getFlagType() {
        return flagType;
    }

    public void setFlagType(int flagType) {
        this.flagType = flagType;
    }

    public String getMessagePacket() {
        return messagePacket;
    }

    public void setMessagePacket(String messagePacket) {
        this.messagePacket = messagePacket;
    }

    public int getLengthData() {
        return lengthData;
    }

    public void setLengthData(int lengthData) {
        this.lengthData = lengthData;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
    public String getTypeOfPDU(){
        String result;
        switch(this.flagType){
            case 0:
                result = "SYN";
                break;
            case 1:
                result = "ACK";
                break;
            case 2:
                result = "PSH";
                break;
            case 3:
                result = "FIN";
                break;
            case 4:
                result = "SYN + ACK";
                break;
            case 5:
                result = "JOE DOWN";
                break;
            case 6:
                result = "JOE UP";
                break;
            case 7:
                result = "EXIT";
                break;
            case 8:
                result = "LIST";
                break;
            default:
                result = "NOTHING";
        }
        return result;
    }
    
    public void protocolarPacket(String message){
        String m = message.split(" ")[0];
        
        if(m.equals("download"))
            this.setFlagType(5);
        else if(m.equals("upload"))
            this.setFlagType(6);
        else 
            this.setFlagType(7);

        this.setAckNumber(1);
        this.setSeqNumber(1);
        this.setMessagePacket(message);
        this.setLengthData(message.getBytes().length);
    }
    
    /* Ack like TCP
    public void sendACK(){
        int previousAck = this.ackNumber;
        this.setAckNumber(this.seqNumber + this.lengthData);
        this.setSeqNumber(previousAck);
        this.setFlagType(1);
        this.setMessagePacket("A");
        this.setLengthData(1);
    }*/
    
    public void ackPacket(){
        this.setAckNumber(this.seqNumber);
        this.setFlagType(1);
        this.setMessagePacket("A");
        this.setLengthData(1);
    }
    
    public void synPacket(int clientPort){
        this.setSeqNumber(0);
        this.setAckNumber(1);
        this.setFlagType(4);
        this.setMessagePacket("S");
        this.setPort(clientPort);
    }
        
    public byte[] PDUToByte(){
        ByteBuffer buffer = ByteBuffer.allocate(20 + this.getMessagePacket().getBytes().length);
        buffer.put(ByteBuffer.allocate(4).putInt(this.getSeqNumber()).array());
        buffer.put(ByteBuffer.allocate(4).putInt(this.getAckNumber()).array());
        buffer.put(ByteBuffer.allocate(4).putInt(this.getFlagType()).array());
        buffer.put(ByteBuffer.allocate(4).putInt(this.getLengthData()).array());
        buffer.put(ByteBuffer.allocate(4).putInt(this.getPort()).array());
        buffer.put(this.getMessagePacket().getBytes());
        
        return buffer.array();
    }
    
    public void ByteToPDU(byte[] dataPacket){
        this.setSeqNumber(ByteBuffer.wrap(Arrays.copyOfRange(dataPacket, 0, 4)).getInt());
        this.setAckNumber(ByteBuffer.wrap(Arrays.copyOfRange(dataPacket, 4, 8)).getInt());
        this.setFlagType(ByteBuffer.wrap(Arrays.copyOfRange(dataPacket, 8, 12)).getInt());
        this.setLengthData(ByteBuffer.wrap(Arrays.copyOfRange(dataPacket, 12, 16)).getInt());
        this.setPort(ByteBuffer.wrap(Arrays.copyOfRange(dataPacket, 16, 20)).getInt());
        byte[] message = trim(dataPacket);
        if(message.length >= 20)
        this.setMessagePacket(new String(ByteBuffer.wrap(Arrays.copyOfRange(message, 20, message.length)).array()));
    }
    
    public PDU clone(){
        return new PDU(this);
    }
}
