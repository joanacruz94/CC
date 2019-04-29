/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

/**
 *
 * @author joanacruz
 */
public class PDU {
    private int seqNumber;
    private int ackNumber;
    private boolean ackFlag;
    private byte[] dataPacket;

    public PDU(int seqNumber, int ackNumber, boolean ackFlag, byte[] dataPacket) {
        this.seqNumber = seqNumber;
        this.ackNumber = ackNumber;
        this.ackFlag = ackFlag;
        this.dataPacket = dataPacket;
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

    public boolean isAckFlag() {
        return ackFlag;
    }

    public void setAckFlag(boolean ackFlag) {
        this.ackFlag = ackFlag;
    }

    public byte[] getDataPacket() {
        return dataPacket;
    }

    public void setDataPacket(byte[] dataPacket) {
        this.dataPacket = dataPacket;
    }
}
