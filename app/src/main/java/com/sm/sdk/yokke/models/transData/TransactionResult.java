package com.sm.sdk.yokke.models.transData;

public class TransactionResult {

    private int transStatus;
    private byte[] recvMessage;

    public TransactionResult(int transStatus, byte[] recvMessage) {
        this.transStatus = transStatus;
        this.recvMessage = recvMessage;
    }

    public int getTransStatus() {
        return transStatus;
    }

    public void setTransStatus(int transStatus) {
        this.transStatus = transStatus;
    }

    public byte[] getRecvMessage() {
        return recvMessage;
    }

    public void setRecvMessage(byte[] recvMessage) {
        this.recvMessage = recvMessage;
    }
}
