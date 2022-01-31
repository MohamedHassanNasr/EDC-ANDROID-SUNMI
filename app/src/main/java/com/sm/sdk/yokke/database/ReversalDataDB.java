package com.sm.sdk.yokke.database;

import android.util.Log;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.sm.sdk.yokke.models.ReversalData;

import java.util.List;

public class ReversalDataDB {
    private static final String TAG = "ReversalDataDB";
    private RuntimeExceptionDao<ReversalData, Integer> reversalDataDao = null;
    private static ReversalDataDB instance;

    private BaseDbHelper dbHelper;

    public ReversalDataDB() {
        dbHelper = BaseDbHelper.getInstance();
    }

    public static synchronized ReversalDataDB getInstance() {
        if (instance == null) {
            instance = new ReversalDataDB();
        }

        return instance;
    }

    private RuntimeExceptionDao<ReversalData, Integer> getReversalDataDao() {
        if(reversalDataDao == null) {
            reversalDataDao = dbHelper.getRuntimeExceptionDao(ReversalData.class);
        }
        return reversalDataDao;
    }

    /**
     * insert trans parameter
     */
    public boolean insertReversalDataDb(ReversalData reversalData) {
        try{
            RuntimeExceptionDao<ReversalData, Integer> dao = getReversalDataDao();
            dao.create(reversalData);
            return true;
        } catch(RuntimeException e) {
            Log.e(TAG,"ERROR INSERT REVERSAL DATA",e);
        }
        return false;
    }

    /**
     * select semua data dari reversal dataabase
     * untuk di cek apakah perlu dilakukan send reversal atau tidak
     */
    public List<ReversalData> selectAllReversalDataDb() {
        try{
            RuntimeExceptionDao<ReversalData, Integer> dao = getReversalDataDao();
            return dao.queryForAll();
        } catch(RuntimeException e) {
            Log.e(TAG,"ERROR SELECT ALL DATA REVERSAL",e);
        }
        return null;
    }

    public void deleteAllReversalDataDb() {
        try{
            RuntimeExceptionDao<ReversalData, Integer> dao = getReversalDataDao();
            dao.delete(selectAllReversalDataDb());
        } catch(RuntimeException e) {
            Log.e(TAG,"ERROR DELETE ALL DATA REVERSAL",e);
        }
    }
}
