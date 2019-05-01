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
    private int flag;
    private byte[] dataPacket;
    private int lengthPacket;
    private int totalSize;

    public PDU() {
        this.seqNumber = 0;
        this.ackNumber = 0;
        this.flag = 0;
        this.dataPacket = new byte[1040];
        this.lengthPacket = this.dataPacket.length;
        this.totalSize = this.lengthPacket + 9;
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

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public byte[] getDataPacket() {
        return dataPacket;
    }

    public void setDataPacket(byte[] dataPacket) {
        this.dataPacket = dataPacket;
    }
    
    public int intfromByte(byte[] sizebytes){
        ByteBuffer wrapped = ByteBuffer.wrap(sizebytes);
        return wrapped.getInt(); 
    }
	 
    public byte[] bytefromInt(int integer){
        ByteBuffer dbuf = ByteBuffer.allocate(4);
        dbuf.putInt(integer);
        return  dbuf.array();
    }
    
    public byte[] PDUtoByte(){
        byte[] arrayFinal = new byte[this.lengthPacket + 9];
        byte[] seqNum = bytefromInt(this.seqNumber);
        byte[] ack = bytefromInt(this.ackNumber);
        byte[] flag = bytefromInt(this.flag);
        
        for(int i = 0; i < 4; i++){
            arrayFinal[i] = seqNum[i];
        }
        
        for(int i = 0; i < 4; i++){
            arrayFinal[i + 4] = ack[i];
        }
        
        for(int i = 0; i < 4; i++){
            arrayFinal[i + 8] = flag[i];
        }
        
        for(int i = 0; i < 4; i++){
            arrayFinal[i+9] = this.dataPacket[i];
        }
        
        return arrayFinal;
    }
    
    public void byteToPDU(byte[] array){
        this.setSeqNumber(intfromByte(Arrays.copyOfRange(array, 0, 4)));
        System.out.println(this.getSeqNumber());
        this.setAckNumber(intfromByte(Arrays.copyOfRange(array, 4, 8)));
        this.setDataPacket(Arrays.copyOfRange(array, 8, array.length));
        
    }
}
