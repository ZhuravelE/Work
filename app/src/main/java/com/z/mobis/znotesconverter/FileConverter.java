package com.z.mobis.znotesconverter;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.z.mobis.znotesconverter.FileSaver.FileSaver;
import com.z.mobis.znotesconverter.FileSaver.HtmlWriter;
import com.z.mobis.znotesconverter.FileSaver.TxtWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Евгений on 07.12.2016.
 */

public class FileConverter {
    private final String LOG_TAG = "FileConv.";
    private final String LINK_TABLE = OpenDbHelper.LINK_TABLE;
    private final String LINK_PARENT_ID = OpenDbHelper.LINK_PARENT_ID;
    private final String LINK_CHILD_ID = OpenDbHelper.LINK_CHILD_ID;
    private final String LINK_ORD = OpenDbHelper.LINK_ORD;


    private final String PUNKT_TABLE = OpenDbHelper.PUNKT_TABLE;
    private final String PARENT_ID = OpenDbHelper.PARENT_ID;
    private final String _ID = OpenDbHelper._ID;
    private final String PUNKT_NAME = OpenDbHelper.PUNKT_NAME;
    private final String PUNKT_DESC = OpenDbHelper.PUNKT_DESC;

    private final String SELECT_PUNKTS = "SELECT * FROM " + PUNKT_TABLE + " WHERE ";

    private final DBManager dbManager;
    private final OpenDbHelper dbHelper;
    private FileSaver fileSaver;

    private FileConverter(Context context, String dbPath, String dbName){
        this.dbHelper = OpenDbHelper.newInstance(context, dbPath, dbName);
        this.dbManager = new DBManager();
        dbManager.initializeInstance(dbHelper);
    }

    public static FileConverter getConverterToTxt(Context context, String dbPath, String dbName){
        FileConverter instance = new FileConverter(context, dbPath, dbName);
        try {
            instance.fileSaver = new TxtWriter(dbPath + dbName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return instance;
    }

    public static FileConverter getConverterToHtml(Context context, String dbPath, String dbName){
        FileConverter instance = new FileConverter(context, dbPath, dbName);
        try {
            instance.fileSaver = new HtmlWriter(dbPath + dbName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return instance;
    }

    public void convert(){
        if (fileSaver != null && fileSaver.isFileAvailable()) {
            try {
                SQLiteDatabase sqliteDB = dbManager.getInstance().openReadableDatabase();
                int dbVersion = sqliteDB.getVersion();

                sqliteDB.beginTransaction();
                try {
                    getItems(sqliteDB, getChildrenID(sqliteDB, 0L), "");
                    fileSaver.closeWritableFile();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    sqliteDB.endTransaction();
                }
                dbManager.getInstance().closeDatabase();
            } catch (SQLException sqlerror) {
                Log.e(LOG_TAG + "convert error", sqlerror.getMessage());
            }
        }
    }

    void saveToDbz(){
        if (fileSaver != null && fileSaver.isFileAvailable()){
            try{
                SQLiteDatabase sqLiteDB = dbManager.getInstance().openWritableDatabase();
            }catch (SQLException sqlError){
                Log.e(LOG_TAG + "convert error", sqlError.getMessage());
            }
        }
    }

    private void getItems(SQLiteDatabase sqliteDB, List<Long> ids, String indent) throws IOException {
        String name = "";
        String desc = "";
        indent = indent + fileSaver.getTab();
        Cursor crsr;
        for (Long id: ids){
            crsr = sqliteDB.query(PUNKT_TABLE, new String[]{PUNKT_NAME, PUNKT_DESC}, _ID + " = " + Long.toString(id), null, null, null, null);
            if (crsr.moveToFirst()){
                do {
                    name = indent + crsr.getString(0);
                    desc = indent + crsr.getString(1);
                    fileSaver.write(name, desc);
                }while (crsr.moveToNext());
                crsr.close();
            }
            getItems(sqliteDB, getChildrenID(sqliteDB, id), indent);
        }
    }

    private List<Long> getChildrenID(SQLiteDatabase sqliteDB, long parentId){
        List<Long> childrenId = new ArrayList<>();
        Cursor crsr = sqliteDB.query(LINK_TABLE, new String[]{LINK_CHILD_ID}, LINK_PARENT_ID + " = " + Long.toString(parentId), null, null, null, null);
        if (crsr.moveToFirst()){
            do {
                childrenId.add(crsr.getLong(0));
            }while (crsr.moveToNext());
            crsr.close();
        }
        return childrenId;
    }
}
