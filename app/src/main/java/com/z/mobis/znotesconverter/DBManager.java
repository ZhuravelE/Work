package com.z.mobis.znotesconverter;

import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.atomic.AtomicInteger;

public class DBManager {
	final static String LOG_TAG = "DatabaseManager.";
	
	private AtomicInteger mOpenCounter = new AtomicInteger();
	
	private static DBManager instance;
    private OpenDbHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;

    public DBManager() {
    	
    }
    
    private DBManager(OpenDbHelper helper) {
        mDatabaseHelper = helper;
    }
    
    public synchronized void initializeInstance(OpenDbHelper helper) {
        /*if (instance == null) {*/
            instance = new DBManager(helper);
            //mDatabaseHelper = helper;
        /*}else{
        	if (!instance.mDatabaseHelper.dbName.equals(helper.dbName)){
        		instance = new DBManager(helper);
        	}
        }*/
    }

    public synchronized DBManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(DBManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        	//instance = new DatabaseManager(helper);
        }

        return instance;
    }

    public synchronized SQLiteDatabase openReadableDatabase() {
    	//Log.i(LOG_TAG + "openReadableDatabase", "dbName: " + mDatabaseHelper.dbName);
        if(mOpenCounter.incrementAndGet() == 1) {
            // Opening new database
        	//Log.i(LOG_TAG + "openReadableDatabase", "Opening new database");
            mDatabase = mDatabaseHelper.getReadableDatabase();
        }
        return mDatabase;
    }

    synchronized SQLiteDatabase openWritableDatabase(){
        if(mOpenCounter.incrementAndGet() == 1){
            mDatabase = mDatabaseHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    public synchronized void closeDatabase() {
    	//Log.i(LOG_TAG + "closeDatabase", "dbName: " + mDatabaseHelper.dbName);
        if(mOpenCounter.decrementAndGet() == 0) {
            // Closing database
        	//Log.i(LOG_TAG + "closeDatabase", "Closing database");
            mDatabase.close();
        }
    }
}
