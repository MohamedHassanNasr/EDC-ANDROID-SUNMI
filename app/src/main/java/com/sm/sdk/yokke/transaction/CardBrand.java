package com.sm.sdk.yokke.transaction;

public enum CardBrand {
    MASTER_CARD("M","MASTER"),
    VISA("V","VISA"),
    JCB("J","JCB"),
    CUP("C","UNIONPAY"),
    DFS("D","DFS"),
    LOCAL("E","LOCAL"),
    ARTAJASA("1","ARTAJASA"),
    RINTIS("2","RINTIS"),
    ALTO("3","ALTO"),
    LINK("4","LINK");

    private String symbol;
    private String brand;

    private CardBrand(String symbol, String brand) {
        this.symbol = symbol;
        this.brand = brand;
    }

    public String getBrandName() {
        return brand;
    }

    public String getSymbol() {
        return symbol;
    }
}
