package com.sm.sdk.yokke.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "reversal_data")
public class ReversalData {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(dataType = DataType.BYTE_ARRAY)
    private byte[] message;


    public ReversalData(byte[] message) {
        this.message = message;
    }

    public ReversalData() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getMessage() {
        return message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }

}
