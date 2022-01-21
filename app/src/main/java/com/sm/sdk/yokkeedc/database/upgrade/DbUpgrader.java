package com.sm.sdk.yokkeedc.database.upgrade;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.IntDef;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

public abstract class DbUpgrader {
    private static final String TAG = "DB Upgrader";

    @IntDef({ADD, DELETE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OperationType {
    }

    public static final int ADD = 0;
    public static final int DELETE = 1;

    public static void upgrade(SQLiteDatabase db, ConnectionSource cs, int oldVersion, int newVersion, String packagePath) {
        try {
            Class<?> c1 = Class.forName(packagePath + ".Upgrade" + oldVersion + "To" + newVersion);
            DbUpgrader upgrader = (DbUpgrader) c1.newInstance();
            Log.i("DbUpgrader", "upgrading from version(" + oldVersion +
                    ") to version(" + newVersion + ")");
            upgrader.upgrade(db, cs);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Log.w(TAG, "", e);
            throw new IllegalArgumentException("No Upgrader for " + db.toString() +
                    " from version(" + oldVersion +
                    ") to version(" + newVersion + ")");
        } catch (SQLException e) {
            Log.w(TAG, "", e);
        }
    }

    private static void createTable(SQLiteDatabase db, ConnectionSource cs, Class clazz) throws SQLException {
        try {
            String sql = (String) TableUtils.getCreateTableStatements(cs, clazz).get(0);
            db.execSQL(sql);
        } catch (Exception e) {
            Log.w(TAG, e);
            TableUtils.createTable(cs, clazz);
        }
    }

    protected static void upgradeTable(SQLiteDatabase db, ConnectionSource cs, Class clazz, @OperationType int type, Map<String, Object> columnWithValue) throws SQLException {
        String tableName = extractTableName(clazz);

        db.beginTransaction();
        try {
            //Rename table
            String tempTableName = tableName + "_temp";
            String sql = "ALTER TABLE " + tableName + " RENAME TO " + tempTableName;
            db.execSQL(sql);

            //Create table
            createTable(db, cs, clazz);

            //Load data
            String columns;
            if (type == ADD) {
                columns = Arrays.toString(getColumnNames(db, tempTableName));
            } else if (type == DELETE) {
                columns = Arrays.toString(getColumnNames(db, tableName));
            } else {
                throw new IllegalArgumentException("OperationType error");
            }

            columns = columns.replace("[", "").replace("]", "");
            sql = "INSERT INTO " + tableName + "(" + columns + ") " + "SELECT " + columns + " FROM " + tempTableName;
            db.execSQL(sql);

            // update default value
            if (type == ADD && columnWithValue != null && columnWithValue.size() > 0) {
                String[] sets = columnWithValue.keySet().toArray((new String[columnWithValue.size()]));
                for (int i = 0; i < sets.length; ++i) {
                    sets[i] += ("='" + columnWithValue.get(sets[i]).toString() + "'");
                }
                String newColumn = Arrays.toString(sets).replace("[", "").replace("]", "");
                sql = "UPDATE " + tableName + " SET " + newColumn;
                db.execSQL(sql);
            }

            //Drop temp table
            sql = "DROP TABLE IF EXISTS " + tempTableName;
            db.execSQL(sql);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }

    private static String[] getColumnNames(SQLiteDatabase db, String tableName) {
        String[] columnNames = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * from " + tableName, null);
            if (cursor != null) {
                columnNames = cursor.getColumnNames();
            }
        } catch (Exception e) {
            Log.w(TAG, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return columnNames;
    }

    private static <T> String extractTableName(Class<T> clazz) {
        DatabaseTable databaseTable = clazz.getAnnotation(DatabaseTable.class);
        String name = null;
        if (databaseTable != null && databaseTable.tableName() != null && !databaseTable.tableName().isEmpty()) {
            name = databaseTable.tableName();
        }

        if (name == null) {
            name = clazz.getSimpleName().toLowerCase();
        }

        return name;
    }

    protected abstract void upgrade(SQLiteDatabase db, ConnectionSource cs) throws SQLException;
}
