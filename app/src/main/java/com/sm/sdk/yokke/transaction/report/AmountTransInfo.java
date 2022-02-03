package com.sm.sdk.yokke.transaction.report;

public class AmountTransInfo {
    private long saleAmount;
    private long saleCounter;
    private long voidAmount;
    private long voidCounter;
    private long refundAmount;
    private long refundCounter;
    private long qrisSaleAmount;
    private long qrisSaleCounter;
    private long qrisRefundAmount;
    private long qrisRefundCounter;

    public AmountTransInfo() {
        this.saleAmount = 0;
        this.saleCounter = 0;
        this.voidAmount = 0;
        this.voidCounter = 0;
        this.refundAmount = 0;
        this.refundCounter = 0;
        this.qrisSaleAmount = 0;
        this.qrisSaleCounter = 0;
        this.qrisRefundAmount = 0;
        this.qrisRefundCounter = 0;
    }

    public long getSaleAmount() {
        return saleAmount;
    }

    public void setSaleAmount(long saleAmount) {
        this.saleAmount = saleAmount;
    }

    public long getSaleCounter() {
        return saleCounter;
    }

    public void setSaleCounter(long saleCounter) {
        this.saleCounter = saleCounter;
    }

    public long getVoidAmount() {
        return voidAmount;
    }

    public void setVoidAmount(long voidAmount) {
        this.voidAmount = voidAmount;
    }

    public long getVoidCounter() {
        return voidCounter;
    }

    public void setVoidCounter(long voidCounter) {
        this.voidCounter = voidCounter;
    }

    public long getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(long refundAmount) {
        this.refundAmount = refundAmount;
    }

    public long getRefundCounter() {
        return refundCounter;
    }

    public void setRefundCounter(long refundCounter) {
        this.refundCounter = refundCounter;
    }

    public long getQrisSaleAmount() {
        return qrisSaleAmount;
    }

    public void setQrisSaleAmount(long qrisSaleAmount) {
        this.qrisSaleAmount = qrisSaleAmount;
    }

    public long getQrisSaleCounter() {
        return qrisSaleCounter;
    }

    public void setQrisSaleCounter(long qrisSaleCounter) {
        this.qrisSaleCounter = qrisSaleCounter;
    }

    public long getQrisRefundAmount() {
        return qrisRefundAmount;
    }

    public void setQrisRefundAmount(long qrisRefundAmount) {
        this.qrisRefundAmount = qrisRefundAmount;
    }

    public long getQrisRefundCounter() {
        return qrisRefundCounter;
    }

    public void setQrisRefundCounter(long qrisRefundCounter) {
        this.qrisRefundCounter = qrisRefundCounter;
    }
}
