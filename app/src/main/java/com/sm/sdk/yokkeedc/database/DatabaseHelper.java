package com.sm.sdk.yokkeedc.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import androidx.annotation.RequiresApi;

import com.sm.sdk.yokkeedc.models.AIDList;
import com.sm.sdk.yokkeedc.models.EDCParam;
import com.sm.sdk.yokkeedc.models.EMVTag;
import com.sm.sdk.yokkeedc.models.JspeedyParam;
import com.sm.sdk.yokkeedc.models.PaypassParam;
import com.sm.sdk.yokkeedc.models.PaywaveParam;
import com.sm.sdk.yokkeedc.models.TransDataDB;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "mti_edc.db";
    public  static final int DB_VERSION = 2;

    static SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(EDCParam.SQL_CREATE);
        database.execSQL(AIDList.SQL_CREATE);
        database.execSQL(EMVTag.SQL_CREATE);
        database.execSQL(PaywaveParam.SQL_CREATE);
        database.execSQL(PaypassParam.SQL_CREATE);
        database.execSQL(JspeedyParam.SQL_CREATE);
        database.execSQL(TransDataDB.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i1) {
        database.execSQL(EDCParam.SQL_DELETE);
        database.execSQL(AIDList.SQL_DELETE);
        database.execSQL(EMVTag.SQL_DELETE);
        database.execSQL(PaywaveParam.SQL_DELETE);
        database.execSQL(PaypassParam.SQL_DELETE);
        database.execSQL(JspeedyParam.SQL_DELETE);
        database.execSQL(TransDataDB.SQL_DELETE);
        onCreate(database);
    }

    /*
     *   Insert Parameter to table TransDataDB
     *
     * */

    public void insertTransData(List<TransDataDB> lstTrans) {
        int i;
        String paramName, paramVal;
        TransDataDB param;

        db = getWritableDatabase();
        ContentValues values = new ContentValues();

        for (TransDataDB trans : lstTrans){
            values.put(TransDataDB.COL_AMOUNT,trans.getAmount());
            values.put(TransDataDB.COL_APP_CD,trans.getAppCD());
            values.put(TransDataDB.COL_BATCH_NO,trans.getBatchNo());
            values.put(TransDataDB.COL_CARD_BRAND,trans.getCardBrand());
            values.put(TransDataDB.COL_CARD_HOLDER_NAME,trans.getCardHolderName());
            values.put(TransDataDB.COL_CARD_NO,trans.getCardNo());
            values.put(TransDataDB.COL_CARD_TYPE,trans.getCardType());
            values.put(TransDataDB.COL_DATE,trans.getDate());
            values.put(TransDataDB.COL_Exp_Date,trans.getExpDate());
            values.put(TransDataDB.COL_id,trans.getId());
            values.put(TransDataDB.COL_MID,trans.getMID());
            values.put(TransDataDB.COL_TIP,trans.getTip());
            values.put(TransDataDB.COL_ONOFF,trans.getONOFF());
            values.put(TransDataDB.COL_TRACE_NO,trans.getTraceNo());
            values.put(TransDataDB.COL_TIME,trans.getTime());
            values.put(TransDataDB.COL_REFF_NO,trans.getReffNo());
            values.put(TransDataDB.COL_POS_ENTRY_MODE,trans.getPosEntryMode());
            values.put(TransDataDB.COL_TID,trans.getTID());
        }

        db.close();
    }

    /**
     * Get all parameter data from table tbTransData
     * @return merchantInfo
     */
    public List<TransDataDB> getTransData() {
        db = getReadableDatabase();
        List<TransDataDB> transDatumDBS = new ArrayList<>();

        transDatumDBS.clear();

        Cursor cursor = db.rawQuery("select * from "+ TransDataDB.TABLE_NAME+
                " order by "+ TransDataDB._ID, null);

        TransDataDB newTransDataDB;
        try{
            while (cursor.moveToNext()){
                newTransDataDB = new TransDataDB( cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10),
                        cursor.getString(11),
                        cursor.getString(12),
                        cursor.getString(13),
                        cursor.getString(14),
                        cursor.getString(15),
                        cursor.getString(16),
                        cursor.getString(17),
                        cursor.getString(18));
                transDatumDBS.add(newTransDataDB);
            }
        } finally {
            cursor.close();
        }
        db.close();
        return transDatumDBS;

    }

    /** INSERT EDC PARAM TO TABLE **/
    public void insertEDCParam(List<EDCParam> lstParam) {
        int i;
        String paramCode, paramVal;
        EDCParam param;
        db = getWritableDatabase();
        ContentValues values = new ContentValues();

        for(i=0; i<lstParam.size(); i++) {
            param = lstParam.get(i);
            paramCode = param.getParamCode();
            paramVal = param.getParamVal();
            values.put(EDCParam.COL_PARAM_CODE, paramCode);
            values.put(EDCParam.COL_PARAM_VAL, paramVal);

            db.insert(EDCParam.TABLE_NAME, null, values);
        }

        db.close();
    }

    /** DELETE EDC PARAM **/
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void deleteEDCParam() {
        int i, lstSize = 0;
        //EDCParam param;

        List<EDCParam> newListParam = getEDCParam();
        lstSize = newListParam.size();

        db = getWritableDatabase();

        if(lstSize > 0) {
            newListParam.forEach((param) -> {
                db.delete(EDCParam.TABLE_NAME, EDCParam._ID+" = ?",
                        new String[]{param.getId()});
            });
        }

        //db.execSQL(EDCParam.SQL_DELETE);
        db.close();

    }

    /** GET EDC PARAM FROM TABLE **/
    public List<EDCParam> getEDCParam() {
        db = getReadableDatabase();
        List<EDCParam> edcParam = new ArrayList<>();

        Cursor cursor = db.rawQuery("select * from "+EDCParam.TABLE_NAME+
                " order by "+EDCParam._ID, null);

        EDCParam newEdcParam;
        try{
            while (cursor.moveToNext()){
                newEdcParam = new EDCParam(Long.toString(cursor.getLong(0)), cursor.getString(1),
                        cursor.getString(2));
                edcParam.add(newEdcParam);
            }
        } finally {
            cursor.close();
        }
        db.close();
        return edcParam;

    }

    /** INSERT AID LIST TO TABLE **/
    public void insertAIDList(String aid) {
        db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(AIDList.COL_EMV_APP_ID, aid);

        db.insert(AIDList.TABLE_NAME, null, values);
        db.close();
    }

    /** GET AID LIST FROM TABLE **/
    public List<AIDList> getAIDLists() {
        db = getReadableDatabase();
        List<AIDList> aidLists = new ArrayList<>();

        Cursor cursor = db.rawQuery("select * from "+AIDList.TABLE_NAME+
                " order by "+AIDList._ID, null);

        AIDList newAidLst;
        try{
            while (cursor.moveToNext()){
                newAidLst = new AIDList(Long.toString(cursor.getLong(0)), cursor.getString(1));
                aidLists.add(newAidLst);
            }
        } finally {
            cursor.close();
        }
        db.close();
        return aidLists;

    }

    /** INSERT EMV TAG TO TABLE **/
    public void insertEMVTag(String tagCode, String tagVal) {
        db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(EMVTag.COL_TAG_CD, tagCode);
        values.put(EMVTag.COL_TAG_VAL, tagVal);

        db.insert(EMVTag.TABLE_NAME, null, values);
        db.close();
    }

    /** GET EMV TAG FROM TABLE **/
    public List<EMVTag> getEMVTagLists() {
        db = getReadableDatabase();
        List<EMVTag> emvTagList = new ArrayList<>();

        Cursor cursor = db.rawQuery("select * from "+EMVTag.TABLE_NAME+
                " order by "+EMVTag._ID, null);

        EMVTag newEmvTag;
        try{
            while (cursor.moveToNext()){
                newEmvTag = new EMVTag(Long.toString(cursor.getLong(0)),
                        cursor.getString(1),
                        cursor.getString(2));
                emvTagList.add(newEmvTag);
            }
        } finally {
            cursor.close();
        }
        db.close();
        return emvTagList;

    }

    /** INSERT PAYPASS PARAM TO TABLE **/
    public void insertPaypassParam(String tagCode, String tagVal) {
        db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(PaypassParam.COL_TAG_CD, tagCode);
        values.put(PaypassParam.COL_TAG_VAL, tagVal);

        db.insert(PaypassParam.TABLE_NAME, null, values);
        db.close();
    }

    /** GET PAYPASS PARAM FROM TABLE **/
    public List<PaypassParam> getPaypassParamLists() {
        db = getReadableDatabase();
        List<PaypassParam> paypassParamList = new ArrayList<>();

        Cursor cursor = db.rawQuery("select * from "+PaypassParam.TABLE_NAME+
                " order by "+PaypassParam._ID, null);

        PaypassParam newPaypassParamList;
        try{
            while (cursor.moveToNext()){
                newPaypassParamList = new PaypassParam(Long.toString(cursor.getLong(0)),
                        cursor.getString(1),
                        cursor.getString(2));
                paypassParamList.add(newPaypassParamList);
            }
        } finally {
            cursor.close();
        }
        db.close();
        return paypassParamList;

    }

    /** INSERT JSPEEDY PARAM TO TABLE **/
    public void insertJspeedyParam(String tagCode, String tagVal) {
        db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(JspeedyParam.COL_TAG_CD, tagCode);
        values.put(JspeedyParam.COL_TAG_VAL, tagVal);

        db.insert(JspeedyParam.TABLE_NAME, null, values);
        db.close();
    }

    /** GET JSPEEDY PARAM FROM TABLE **/
    public List<JspeedyParam> getJspeedyParamLists() {
        db = getReadableDatabase();
        List<JspeedyParam> jspeedyParamList = new ArrayList<>();

        Cursor cursor = db.rawQuery("select * from "+JspeedyParam.TABLE_NAME+
                " order by "+JspeedyParam._ID, null);

        JspeedyParam newJspeedyParamList;
        try{
            while (cursor.moveToNext()){
                newJspeedyParamList = new JspeedyParam(Long.toString(cursor.getLong(0)),
                        cursor.getString(1),
                        cursor.getString(2));
                jspeedyParamList.add(newJspeedyParamList);
            }
        } finally {
            cursor.close();
        }
        db.close();
        return jspeedyParamList;

    }
}
