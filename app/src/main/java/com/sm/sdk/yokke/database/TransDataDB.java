package com.sm.sdk.yokke.database;

import android.util.Log;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.sm.sdk.yokke.models.transData.TransData;

import java.sql.SQLException;

public class TransDataDB {
    private static final String TAG = "TransDataDB";
    private RuntimeExceptionDao<TransData, Integer> transDataDao = null;
    private static TransDataDB instance;

    private BaseDbHelper dbHelper;

    public TransDataDB() {
        dbHelper = BaseDbHelper.getInstance();
    }

    public static synchronized TransDataDB getInstance() {
        if (instance == null) {
            instance = new TransDataDB();
        }

        return instance;
    }

    private RuntimeExceptionDao<TransData, Integer> getTransDataDao() {
        if(transDataDao == null) {
            transDataDao = dbHelper.getRuntimeExceptionDao(TransData.class);
        }
        return transDataDao;
    }

    /**
     * insert trans parameter
     */
    public boolean insertTransData(TransData transData) {
        try{
            RuntimeExceptionDao<TransData, Integer> dao = getTransDataDao();
            dao.create(transData);
            return true;
        } catch(RuntimeException e) {
            Log.e(TAG,"ERROR INSERT TRANSDATA",e);
        }
        return false;
    }

    public boolean updateTransData(TransData transData) {
        try {
            RuntimeExceptionDao<TransData,Integer> dao = getTransDataDao();
            dao.update(transData);
            return true;
        } catch(RuntimeException e) {
            Log.e(TAG,"ERROR UPDATE TRANSDATA",e);
        }
        return false;
    }

    public TransData findTransData(int id) {
        try {
            RuntimeExceptionDao<TransData, Integer> dao = getTransDataDao();
            return dao.queryForId(id);
        } catch (RuntimeException e) {
            Log.e(TAG,"ERROR FIND TRANSDATA by id",e);
        }

        return null;
    }

    public TransData findTransDataByTraceNo(String traceNo) {
        try {
            RuntimeExceptionDao<TransData, Integer> dao = getTransDataDao();
            QueryBuilder<TransData, Integer> queryBuilder = dao.queryBuilder();
            queryBuilder.where().eq(TransData.TRACE_NO_FIELD, traceNo);
            return queryBuilder.queryForFirst();
        } catch (SQLException e) {
            Log.e(TAG, "ERROR findTransDataByTraceNo", e);
        }

        return null;
    }
}
