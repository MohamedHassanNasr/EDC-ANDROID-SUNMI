package com.sm.sdk.yokke.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.sm.sdk.yokke.activities.MtiApplication;
import com.sm.sdk.yokke.database.upgrade.DbUpgrader;
import com.sm.sdk.yokke.models.QrTransData;
import com.sm.sdk.yokke.models.ReversalData;
import com.sm.sdk.yokke.models.BatchRecord;
import com.sm.sdk.yokke.models.transData.TransParam;

import java.sql.SQLException;

public class BaseDbHelper extends OrmLiteSqliteOpenHelper {
    protected static final String TAG = "BaseDbHelper";
    private static BaseDbHelper instance;

    private static String DATABASE_NAME = "mti_edc_db";
    private static int DATABASE_VERSION = 1;
    private static final String UPGRADER_PATH = "com.sm.sdk.yokkeedc.database.upgrade.db1";

    private BaseDbHelper() {
        super(MtiApplication.getInstance(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, TransParam.class);
            TableUtils.createTableIfNotExists(connectionSource, BatchRecord.class);
            TableUtils.createTableIfNotExists(connectionSource, ReversalData.class);
            TableUtils.createTableIfNotExists(connectionSource, QrTransData.class);
        } catch (SQLException throwables) {
            Log.e(TAG, "ERROR CREATE TABLE");
            throwables.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            for (int i = oldVersion; i < newVersion; ++i) {
                DbUpgrader.upgrade(sqliteDatabase, connectionSource, i, i + 1, UPGRADER_PATH);
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "", e);
        }
    }

    public static synchronized BaseDbHelper getInstance() {
        if (instance == null) {
            instance = new BaseDbHelper();
        }
        return instance;
    }

}
