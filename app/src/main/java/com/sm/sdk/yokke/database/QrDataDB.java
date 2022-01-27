package com.sm.sdk.yokke.database;

import android.util.Log;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.sm.sdk.yokke.models.QrTransData;

import java.util.List;

public class QrDataDB {
    private static final String TAG = "QRDataDB";
    private RuntimeExceptionDao<QrTransData, Integer> qrDataDao = null;
    private static QrDataDB instance;

    private BaseDbHelper dbHelper;

    public QrDataDB() {
        dbHelper = BaseDbHelper.getInstance();
    }

    public static synchronized QrDataDB getInstance() {
        if (instance == null) {
            instance = new QrDataDB();
        }

        return instance;
    }

    private RuntimeExceptionDao<QrTransData, Integer> getQrDatadao() {
        if(qrDataDao == null) {
            qrDataDao = dbHelper.getRuntimeExceptionDao(QrTransData.class);
        }
        return qrDataDao;
    }

    /**
     * insert trans parameter
     */
    public boolean insertQrtransData(QrTransData qrTransData) {
        try{
            RuntimeExceptionDao<QrTransData, Integer> dao = getQrDatadao();
            dao.create(qrTransData);
            return true;
        } catch(RuntimeException e) {
            Log.e(TAG,"ERROR INSERT QR DATA",e);
        }
        return false;
    }

    /**
     * select semua data dari reversal dataabase
     * untuk di cek apakah perlu dilakukan send reversal atau tidak
     */
    public List<QrTransData> selectAllQRDataDb() {
        try{
            RuntimeExceptionDao<QrTransData, Integer> dao = getQrDatadao();
            return dao.queryForAll();
        } catch(RuntimeException e) {
            Log.e(TAG,"ERROR SELECT ALL DATA QR",e);
        }
        return null;
    }

    public void deleteAllQrDataDb() {
        try{
            RuntimeExceptionDao<QrTransData, Integer> dao = getQrDatadao();
            dao.delete(selectAllQRDataDb());
        } catch(RuntimeException e) {
            Log.e(TAG,"ERROR DELETE ALL DATA QR",e);
        }
    }
}
