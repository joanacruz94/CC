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
    private String messagePacket;
    private byte[] dataFile;

    public PDU() {
        this.seqNumber = 0;
        this.ackNumber = 0;
        this.flagType = 0;
        this.messagePacket = "";
        this.dataFile = new byte[256];
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
            default:
                result = "NOTHING";
        }
        return result;
    }
    
    public byte[] PDUToByte(){
        byte[] seqNum = ByteBuffer.allocate(4).putInt(this.getSeqNumber()).array();
        byte[] ackNum = ByteBuffer.allocate(4).putInt(this.getAckNumber()).array();
        byte[] flag = ByteBuffer.allocate(4).putInt(this.getFlagType()).array();
        ByteBuffer buffer = ByteBuffer.allocate(12 + this.getMessagePacket().getBytes().length);
        buffer.put(seqNum);
        buffer.put(ackNum);
        buffer.put(flag);
        buffer.put(this.getMessagePacket().getBytes());
        
        return buffer.array();
    }
    
    public void ByteToPDU(byte[] dataPacket){
        this.setSeqNumber(ByteBuffer.wrap(Arrays.copyOfRange(dataPacket, 0, 4)).getInt());
        this.setAckNumber(ByteBuffer.wrap(Arrays.copyOfRange(dataPacket, 4, 8)).getInt());
        this.setFlagType(ByteBuffer.wrap(Arrays.copyOfRange(dataPacket, 8, 12)).getInt());
        byte[] message = trim(dataPacket);
        this.setMessagePacket(new String(ByteBuffer.wrap(Arrays.copyOfRange(message, 12, message.length)).array()));
    }
}
