package com.sm.sdk.yokke.models.transData;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "trans_param")
public class TransParam implements Serializable {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = PARAM_VAL_FIELD)
    private String paramVal;
    @DatabaseField(columnName = PARAM_NAME_FIELD)
    private String paramName;

    /**
     * field name
     */
    public static final String PARAM_NAME_FIELD = "param_name";
    public static final String PARAM_VAL_FIELD = "param_val";

    /**
     * Param Name
     */
    public static final String TRACE_AUDIT_NUM = "trace audit num";
    public static final String INVOICE_NUM = "invoice num";
    public static final String BATCH_NUM = "batch num";

    public TransParam() {

    }

    public TransParam(TransParam param) {
        this.id = param.id;
        this.paramVal = param.paramVal;
        this.paramName = param.paramName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getParamVal() {
        return paramVal;
    }

    public void setParamVal(String paramVal) {
        this.paramVal = paramVal;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }
}
