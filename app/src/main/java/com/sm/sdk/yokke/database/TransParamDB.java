package com.sm.sdk.yokke.database;

import android.util.Log;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.sm.sdk.yokke.models.transData.TransParam;

import java.sql.SQLException;

public class TransParamDB {
    private static final String TAG = "TransParamDB";
    private RuntimeExceptionDao<TransParam, Integer> transParamDao = null;
    private static TransParamDB instance;

    private BaseDbHelper dbHelper;

    public TransParamDB() {
        dbHelper = BaseDbHelper.getInstance();
    }

    public static synchronized TransParamDB getInstance() {
        if (instance == null) {
            instance = new TransParamDB();
        }

        return instance;
    }

    private RuntimeExceptionDao<TransParam, Integer> getTransParamDao() {
        if(transParamDao == null) {
            transParamDao = dbHelper.getRuntimeExceptionDao(TransParam.class);
        }
        return transParamDao;
    }

    /**
     * insert trans parameter
     */
    public boolean insertTransParam(TransParam param) {
        try{
            RuntimeExceptionDao<TransParam, Integer> dao = getTransParamDao();
            dao.create(param);
            return true;
        } catch(RuntimeException e) {
            Log.e(TAG,"ERROR INSERT TRANSPARAM",e);
        }
        return false;
    }

    public boolean updateTransParam(TransParam param) {
        try {
            RuntimeExceptionDao<TransParam,Integer> dao = getTransParamDao();
            dao.update(param);
            return true;
        } catch(RuntimeException e) {
            Log.e(TAG,"ERROR UPDATE TRANSPARAM",e);
        }
        return false;
    }

    public TransParam findTransParam(int id) {
        try {
            RuntimeExceptionDao<TransParam, Integer> dao = getTransParamDao();
            return dao.queryForId(id);
        } catch (RuntimeException e) {
            Log.e(TAG,"ERROR FIND TRANSPARAM by id",e);
        }

        return null;
    }

    public TransParam findTransParamByParamName(String paramName) {
        try {
            RuntimeExceptionDao<TransParam, Integer> dao = getTransParamDao();
            QueryBuilder<TransParam, Integer> queryBuilder = dao.queryBuilder();
            queryBuilder.where().eq(TransParam.PARAM_NAME_FIELD, paramName);
            return queryBuilder.queryForFirst();
        } catch (SQLException e) {
            Log.e(TAG, "ERROR findTransParamByParamName", e);
        }

        return null;
    }
}
