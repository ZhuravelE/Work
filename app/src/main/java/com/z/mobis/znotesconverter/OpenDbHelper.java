package com.z.mobis.znotesconverter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OpenDbHelper extends SQLiteOpenHelper implements BaseColumns {
    private static final int DATABASE_VERSION = 29;
    
    public static String DB_DIRECTORY = null;
    //public static String dbName = "booksapp.db";
    public static String DB_FULL_PATH = null;

	private String dbPath;
	private String dbName;
	private static String dbType = FileFragment.DB_TYPE;

	public static String DEFAULT_DB_NAME = "zNotes";

	public static final String LINK_TABLE = "LNKS";
    private static final String LINK_ID = "_id";
	public static final String LINK_PARENT_ID = "parent_id";
    private static final String LINK_ISLINK = "islink";
	public static final String LINK_CHILD_ID = "child_id";
	public static final String LINK_ORD = "ord";
    private static final String LINK_DATE_CHANGE = "date_change";
    static final String LINK_FIELDS = LINK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
    									LINK_PARENT_ID + " INTEGER, " +
    									LINK_ISLINK + " INTEGER, " +
    									LINK_CHILD_ID + " INTEGER, " +
    									LINK_ORD + " INTEGER, " + 
    									LINK_DATE_CHANGE + " INTEGER";
    
    static final String PUNKT_TABLE = "PUNKTS";
    public static final String _ID = "_id";
    public static final String PUNKT_NAME = "name";
    public static final String PUNKT_DESC = "desc";
    //static final String PUNKT_ICO = "ico";
    //static final String PUNKT_ICO_TYPE = "ico_type";
    static final String PUNKT_ATTACHMENT = "attachment";
    public static final String PUNKT_ATTACHMENTS_QTY = "attachments_qty";
    public static final String PUNKT_STATUS = "status";
    static final String PUNKT_CHILDSQTY = "childs_qty";
    public static final String PUNKT_CLR_TEXT = "clr_text";
    public static final String PUNKT_CLR_BACKGROUND = "clr_background";
    static final String DATE_CHANGE = "date_change";
    static final String DATE_CREATE = "date_create";
    public static final String PARENT_ID = "parent_id";
    public static final String PUNKT_SOURCE_ID = "source_id";
    static final String ORD = "ord";
    public static final String USER_ID = "user_id";
    
    private static final String PUNKT_FIELDS = _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
    									PUNKT_NAME + " TEXT NOT NULL DEFAULT (''), "	+
    									PUNKT_DESC + " TEXT NOT NULL DEFAULT (''), " +
    									//PUNKT_ICO + " TEXT NOT NULL, " +
    									//PUNKT_ICO_TYPE + " INTEGER DEFAULT (0), " +
    									PUNKT_ATTACHMENT + " TEXT NOT NULL DEFAULT (''), " +
    									PUNKT_ATTACHMENTS_QTY + " INTEGER DEFAULT (0), " +
    									PUNKT_STATUS + " INTEGER DEFAULT (-1), " +
    									PUNKT_CHILDSQTY + " INTEGER DEFAULT (0), " +
    	    							PUNKT_CLR_TEXT  + " INTEGER DEFAULT (0), " +
    	    	    					PUNKT_CLR_BACKGROUND  + " INTEGER DEFAULT (0), " +
    									DATE_CHANGE + " INTEGER, " +
    									DATE_CREATE + " INTEGER, " +
    									PARENT_ID + " INTEGER DEFAULT (0), " +
    									PUNKT_SOURCE_ID + " INTEGER DEFAULT (0), " +
    									ORD + " INTEGER, " +
    									USER_ID + " TEXT NOT NULL DEFAULT ('')";
    public static final long SOURCE_IS_FILTER = -1;
    public static final String STATEMNT_SEPARATOR = " <Stat/Str> ";
    
    static final String ATTACH_TABLE = "ATTACHMENTS";
    //static final String _ID = "_id";
    //static final String PARENT_ID = "parent_id";
    static final String ATTACH_URL = "url";
    static final String ATTACH_TYPE = "type";
    //static final String ATTACH_ORD = "ord";
    //static final String DATE_CHANGE = "date_change";
    private static final String ATTACH_FIELDS = _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
							    		PARENT_ID + " INTEGER, " +
							    		ATTACH_URL + " TEXT NOT NULL DEFAULT (''), " +
							    		ATTACH_TYPE + " INTEGER DEFAULT (0), " + 
							    		ORD + " INTEGER DEFAULT (0), " +
							    		DATE_CHANGE + " INTEGER, " +
							    		DATE_CREATE + " INTEGER";
    
    public static final int ICO_T_ICO = 1;	//Иконка
	public static final int ICO_T_DB = 2; //База
	public static final int ICO_T_CONTACT = 3;	//Контакт
	public static final int ICO_T_PICTURE = 4;	//Изображение
	public static final int ICO_T_AUDIO = 5; //Аудио
	public static final int ICO_T_VIDEO = 6; //Видео
	public static final int ICO_T_FILE = 7; //Другой файл
	
	public static final String FILTER_TABLE = "FILTERS";
    //static final String FILTER_ID = "_id";
    //static final String FILTER_PARENT_ID = "parent_id";
    
    //static final String FILTER_ORD = "ord";	//порядок
    private static final String FILTER_OPEN_PAR  = "open_parentheses";	//открытые скобки (""(0), "("(1), "(("(2)...)
    private static final String FILTER_CRITERIA = "criteria";	//критерий (название, описание, статус...)
    private static final String FILTER_COMPARISON = "comparison_operator";	//оператор сравнения для критерия (=(0), <>(1), >(2), <(3)...)
    private static final String FILTER_STR_VAL = "value";	//строковое значение
   // static final String FILTER_INT_VAL = "integer_value";	//числовое значение
    private static final String FILTER_CLOSE_PAR  = "close_parentheses";	//закрытые скобки (""(0), ")"(1), "))"(2)...)
    private static final String FILTER_AND_OR  = "boolean_operator";	//and(0)/or(1) к следующему фильтру	//
    
    public static final String[] F_CRITERIAS = new String[]{PUNKT_NAME, PUNKT_DESC, "Progressbar", "Checkbox",
    	PUNKT_CLR_BACKGROUND, PUNKT_CLR_TEXT, USER_ID, PARENT_ID, "NOT " + PARENT_ID};
    public static final String[] F_CRITERIAS_ID = new String[]{PUNKT_NAME, PUNKT_DESC, PUNKT_STATUS, PUNKT_STATUS,
    	PUNKT_CLR_BACKGROUND, PUNKT_CLR_TEXT, USER_ID, PARENT_ID, "NOT " + PARENT_ID};
    public static final String[] F_COMPARISON = new String[]{"=", "<>", ">", "<", "LIKE", "NOT LIKE"};
    
    public static final String F_NOT_PROGRESS = " AND " + PUNKT_STATUS + ">100";
    public static final String F_NOT_CHECKBOX = " AND " + PUNKT_STATUS + "<=100" + " AND " + PUNKT_STATUS + ">=0";

	private static OpenDbHelper mInstance = null;
    private SQLiteDatabase myDataBase;
    
    private Context u_context;
    SQLiteDatabase u_db;
    int u_oldVersion, u_newVersion;
    
    private final static String LOG_TAG = "ODbH.";
    
    public static synchronized OpenDbHelper newInstance(Context context, String dbPath, String dbName) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
    	
    	//Log.i(LOG_TAG + "newInstance", "mInstance.dbName: " + mInstance.dbName);
        if (mInstance == null || !mInstance.dbName.equals(dbName) || !mInstance.dbPath.equals(dbPath)) {
        	Log.e(LOG_TAG + "newInstance", "обновить mInstance");
          //mInstance = new OpenDbHelper(ctx.getApplicationContext());
        	if (mInstance != null) {
        		mInstance.close();
        	}
        	mInstance = new OpenDbHelper(context, dbPath, dbName);
        	//mInstance.getWritableDatabase().execSQL("create table if not exists "+ FILTER_TABLE +" ("+ FILTER_FIELDS + ");");
        }else{
        	Log.i(LOG_TAG + "newInstance", "mInstance != null");
        }
        return mInstance;
	}

	public static synchronized OpenDbHelper getInstance() {
		return mInstance;
	}

    private OpenDbHelper(Context context, String sdpath, String db_NAME) {
    	super(context, sdpath + db_NAME + dbType, null, DATABASE_VERSION);
    	dbPath = sdpath;
    	dbName = db_NAME;
    	Log.e(LOG_TAG + "OpenDbHelper.START", dbName + dbType);
    }
    
    public OpenDbHelper(Context context, String path) {	//Подключения к базе для импорта или сравнения.
    	super(context, path, null, DATABASE_VERSION);
    	Log.e(LOG_TAG + "START 2", path);
    	u_context = context;
    }

	public String getDbName() {
		return dbName;
	}

	public String getDbPath() {
		return dbPath;
	}

	@Override
    public void onCreate(SQLiteDatabase db) {
    	Log.e(LOG_TAG + "onCreate", "START");

        createTables(db, PUNKT_TABLE, PUNKT_FIELDS);
        createTables(db, ATTACH_TABLE, ATTACH_FIELDS);
    }

    @Override
    public synchronized void close() {
    	Log.e(LOG_TAG + "close","START");
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	Log.e(LOG_TAG + "onUpgradeDB", " --- Upgrade database from " + oldVersion + " to " + newVersion + " version --- ");
    	/*if (newVersion == DATABASE_VERSION) {
	    	db.beginTransaction();
	        try {
	        	switch (oldVersion) {
	        	case 24:
	            	Log.e(LOG_TAG + "onUpgradeDB", "24");
	            	update24_25(db);
	        	case 25:
	            	Log.e(LOG_TAG + "onUpgradeDB", "25");
	            	update25_26(db);
	        	case 26:
	            	Log.e(LOG_TAG + "onUpgradeDB", "26");
	            	update26_27(db);
	        	case 27:
	            	Log.e(LOG_TAG + "onUpgradeDB", "27");
	            	update27_28(db);
	        	case 28:
	            	Log.e(LOG_TAG + "onUpgradeDB", "28");
	            	update28_29(db);
	        	case 29:
		            Log.e(LOG_TAG + "onUpgradeDB", "29");
		            update29_30(db);
	        	case 30:	//Для обновления уже созданных без вложений баз.
		            Log.e(LOG_TAG + "onUpgradeDB", "30");
		            update30_31(db);
	        	case 31:	//Удалить связи. Добовить в пункты parent_id, reference_id и ord
		            Log.e(LOG_TAG + "onUpgradeDB", "31");
		            update31_32(db);
	        	case 32:	//Добовить date_create в каждую таблицу
		            Log.e(LOG_TAG + "onUpgradeDB", "32");
		            update32_33(db);
	        	case 33:	//Удалить фильтры. Добовить user_id в PUNKTS
		            Log.e(LOG_TAG + "onUpgradeDB", "33");
		            //update33_34(db);
	        	}
	        	db.setTransactionSuccessful();
	    	} finally {
	    	db.endTransaction();
	    	}
        }*/
        Log.e(LOG_TAG + "onUpgradeDB", "Fin");
    }

    private void update24_25(SQLiteDatabase db){
    	Log.e(LOG_TAG + "update24_25", "24");
    	String PUNKT_ICO = "ico";
    	//db.beginTransaction();
        //try {
        	//db.execSQL("alter table "+ LINK_TABLE +" add column "+ LINK_LINKTO +" TEXT NOT NULL DEFAULT '';");
        	//Создать временную таблицу
            db.execSQL("create temporary table PUNKS_TMP (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					PUNKT_NAME + " TEXT NOT NULL, "	+
					PUNKT_DESC + " TEXT NOT NULL, " +
					PUNKT_ICO + " TEXT NOT NULL, " +
					"ico_type" + " INTEGER DEFAULT (0), " +
					PUNKT_STATUS + " INTEGER DEFAULT (-1), " +
					PUNKT_CHILDSQTY + " INTEGER);");
							
            String FIELDS_old = _ID + ", " +
            		PUNKT_NAME + ", " +
            		PUNKT_DESC + ", " +
            		PUNKT_ICO + ", " +
            		"ico_type" + ", " +
            		PUNKT_STATUS + ", " +
            		PUNKT_CHILDSQTY;
            
            String FIELDS_new = _ID + ", " +
            		PUNKT_NAME + ", " +
            		PUNKT_DESC + ", " +
            		PUNKT_ICO + ", " +
            		"ico_type" + ", " +
            		PUNKT_STATUS + ", " +
            		PUNKT_CHILDSQTY + ", " +
                    DATE_CHANGE;
            
            String PUNKT_FIELDSnew = _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					PUNKT_NAME + " TEXT NOT NULL, "	+
					PUNKT_DESC + " TEXT NOT NULL, " +
					PUNKT_ICO + " TEXT NOT NULL, " +
					"ico_type" + " INTEGER DEFAULT (0), " +
					PUNKT_STATUS + " INTEGER DEFAULT (-1), " +
					PUNKT_CHILDSQTY + " INTEGER, " +
					DATE_CHANGE + " TIMESTAMP";
            
            db.execSQL("insert into PUNKS_TMP select "+ FIELDS_old +" from "+ PUNKT_TABLE +";"); //Скопировать данные из старой во временную
            db.execSQL("drop table if exists "+ PUNKT_TABLE +";"); //Удалить старую таблицу
            db.execSQL("alter table PUNKS_TMP add " + DATE_CHANGE +";"); //Добавить новый столбец во временную таблицу
            db.execSQL("create table "+ PUNKT_TABLE +" ("+ PUNKT_FIELDSnew +");"); //Создать новую таблицу с новым набором столбцов
            //Log.e("OpenDbHelper onUpgrade", "create table "+ PUNKT_TABLE);
            
            db.execSQL("insert into "+ PUNKT_TABLE +" select "+ FIELDS_new +" from PUNKS_TMP;"); //Скопировать данные из временной в новую
            db.execSQL("drop table if exists PUNKS_TMP;"); //Удалить временную таблицу
    }
    
    private void update25_26(SQLiteDatabase db){
    	Log.e(LOG_TAG + "update25_26", "25");
    	String PUNKT_ICO = "ico";
    	/*db.beginTransaction();
        try {*/
        	//db.execSQL("alter table "+ LINK_TABLE +" add column "+ LINK_LINKTO +" TEXT NOT NULL DEFAULT '';");
        	//Создать временную таблицу
            db.execSQL("create temporary table PUNKS_TMP (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					PUNKT_NAME + " TEXT NOT NULL, "	+
					PUNKT_DESC + " TEXT NOT NULL, " +
					PUNKT_ICO + " TEXT NOT NULL, " +
					"ico_type" + " INTEGER DEFAULT (0), " +
					PUNKT_STATUS + " INTEGER DEFAULT (-1), " +
					PUNKT_CHILDSQTY + " INTEGER, " +
					DATE_CHANGE + " TIMESTAMP);");
							
            String FIELDS_old = _ID + ", " +
            		PUNKT_NAME + ", " +
            		PUNKT_DESC + ", " +
            		PUNKT_ICO + ", " +
            		"ico_type" + ", " +
            		PUNKT_STATUS + ", " +
            		PUNKT_CHILDSQTY + ", " +
                    DATE_CHANGE;
            
            String FIELDS_new = _ID + ", " +
            		PUNKT_NAME + ", " +
            		PUNKT_DESC + ", " +
            		PUNKT_ICO + ", " +
            		"ico_type" + ", " +
            		PUNKT_STATUS + ", " +
            		PUNKT_CHILDSQTY + ", " +
                    DATE_CHANGE + ", " +
                    PUNKT_CLR_TEXT + ", " +
                    PUNKT_CLR_BACKGROUND;
            
            String PUNKT_FIELDSnew = _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					PUNKT_NAME + " TEXT NOT NULL, "	+
					PUNKT_DESC + " TEXT NOT NULL, " +
					PUNKT_ICO + " TEXT NOT NULL, " +
					"ico_type" + " INTEGER DEFAULT (0), " +
					PUNKT_STATUS + " INTEGER DEFAULT (-1), " +
					PUNKT_CHILDSQTY + " INTEGER, " +
					DATE_CHANGE + " TIMESTAMP, " +
					PUNKT_CLR_TEXT  + " INTEGER DEFAULT (0), " +
					PUNKT_CLR_BACKGROUND  + " INTEGER DEFAULT (0)";
            
            db.execSQL("insert into PUNKS_TMP select "+ FIELDS_old +" from "+ PUNKT_TABLE + ";"); //Скопировать данные из старой во временную
            db.execSQL("drop table if exists "+ PUNKT_TABLE +";"); //Удалить старую таблицу
            db.execSQL("alter table PUNKS_TMP add " + PUNKT_CLR_TEXT  + " INTEGER DEFAULT 0" + ";"); //Добавить новый столбец во временную таблицу
            db.execSQL("alter table PUNKS_TMP add " + PUNKT_CLR_BACKGROUND  + " INTEGER DEFAULT 0" + ";"); //Добавить новый столбец во временную таблицу
            db.execSQL("create table "+ PUNKT_TABLE +" ("+ PUNKT_FIELDSnew +");"); //Создать новую таблицу с новым набором столбцов
            
            db.execSQL("insert into "+ PUNKT_TABLE +" select "+ FIELDS_new +" from PUNKS_TMP;"); //Скопировать данные из временной в новую
            db.execSQL("drop table if exists PUNKS_TMP;"); //Удалить временную таблицу

            /*db.setTransactionSuccessful();
        } finally {
          db.endTransaction();
        }*/
    }
    
    private void update26_27(SQLiteDatabase db){
    	Log.e(LOG_TAG + "update26_27", "26");
        	//Создать временную таблицу
        	String LINK_FIELDSold = LINK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					LINK_PARENT_ID + " INTEGER, " +
					LINK_ISLINK + " INTEGER, " +
					LINK_CHILD_ID + " INTEGER, " +
					LINK_ORD + " INTEGER";
        			
            db.execSQL("create temporary table LINK_TMP (" + LINK_FIELDSold + ");");
							
            String FIELDS_old = LINK_ID + ", " +
            		LINK_PARENT_ID + ", " +
            		LINK_ISLINK + ", " +
            		LINK_CHILD_ID + ", " +
            		LINK_ORD;
            
            String FIELDS_new = LINK_ID + ", " +
            		LINK_PARENT_ID + ", " +
            		LINK_ISLINK + ", " +
            		LINK_CHILD_ID + ", " +
            		LINK_ORD + ", " +
            		LINK_DATE_CHANGE;
            
            String LINK_FIELDSnew = LINK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					LINK_PARENT_ID + " INTEGER, " +
					LINK_ISLINK + " INTEGER, " +
					LINK_CHILD_ID + " INTEGER, " +
					LINK_ORD + " INTEGER, " + 
					LINK_DATE_CHANGE + " TIMESTAMP";
            
            db.execSQL("insert into LINK_TMP select "+ FIELDS_old +" from "+ LINK_TABLE +";"); //Скопировать данные из старой во временную
            db.execSQL("drop table if exists "+ LINK_TABLE +";"); //Удалить старую таблицу
            db.execSQL("alter table LINK_TMP add " + LINK_DATE_CHANGE +";"); //Добавить новый столбец во временную таблицу
            db.execSQL("create table "+ LINK_TABLE +" ("+ LINK_FIELDSnew +");"); //Создать новую таблицу с новым набором столбцов
            //Log.e("OpenDbHelper onUpgrade", "create table "+ PUNKT_TABLE);
            
            db.execSQL("insert into "+ LINK_TABLE +" select "+ FIELDS_new +" from LINK_TMP;"); //Скопировать данные из временной в новую
            db.execSQL("drop table if exists LINK_TMP;"); //Удалить временную таблицу

        	Cursor crsr = db.rawQuery("SELECT * FROM " + PUNKT_TABLE, null);
        	if (crsr.moveToFirst()) {
        		int ind_id = crsr.getColumnIndex(_ID);
            	int ind_date_change = crsr.getColumnIndex(DATE_CHANGE);
            	do{
            		long id = crsr.getLong(ind_id);
            		String date_change = crsr.getString(ind_date_change);
            		long dateChange = 0;
            		//String dateChang = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()); //Так дата записывалась
            		if (date_change != null && !date_change.equals("")){
            			try {
    						Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", u_context.getResources().getConfiguration().locale).parse(date_change);
    						dateChange = date.getTime();
    					} catch (ParseException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
            		}

            		ContentValues cv = new ContentValues();
        			cv.put(DATE_CHANGE, dateChange);
        			db.update(PUNKT_TABLE, cv, _ID + "=?", new String[] {Long.toString(id)});	//update(parent_id, cv);
            	}while (crsr.moveToNext());
            	
            	if (!crsr.isClosed()){
	    			crsr.close();
	    		}
        	}
    }
    
    private void update27_28(SQLiteDatabase db){
    	Log.e(LOG_TAG + "update27_28", "27");
    	String PUNKT_ICO = "ico";
    	String PUNKT_FIELDSold = _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				PUNKT_NAME + " TEXT NOT NULL, "	+
				PUNKT_DESC + " TEXT NOT NULL, " +
				PUNKT_ICO + " TEXT NOT NULL, " +
				"ico_type" + " INTEGER DEFAULT (0), " +
				PUNKT_STATUS + " INTEGER DEFAULT (-1), " +
				PUNKT_CHILDSQTY + " INTEGER, " +
				DATE_CHANGE + " TIMESTAMP, " +
				PUNKT_CLR_TEXT  + " INTEGER DEFAULT (0), " +
				PUNKT_CLR_BACKGROUND  + " INTEGER DEFAULT (0)";
    	
    	//Создать временную таблицу
    	db.execSQL("create temporary table PUNKS_TMP (" + PUNKT_FIELDSold + ");");
						
        String FIELDS_old = _ID + ", " +
        		PUNKT_NAME + ", " +
        		PUNKT_DESC + ", " +
        		PUNKT_ICO + ", " +
        		"ico_type" + ", " +
        		PUNKT_STATUS + ", " +
        		PUNKT_CHILDSQTY + ", " +
                DATE_CHANGE + ", " +
                PUNKT_CLR_TEXT + ", " +
                PUNKT_CLR_BACKGROUND;
        
        String FIELDS_new = _ID + ", " +
        		PUNKT_NAME + ", " +
        		PUNKT_DESC + ", " +
        		PUNKT_ICO + ", " +
        		"ico_type" + ", " +
        		PUNKT_STATUS + ", " +
        		PUNKT_CHILDSQTY + ", " +
                PUNKT_CLR_TEXT + ", " +
                PUNKT_CLR_BACKGROUND + ", " +
                DATE_CHANGE + ", " +
                DATE_CREATE;
        
        String PUNKT_FIELDSnew = _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				PUNKT_NAME + " TEXT NOT NULL, "	+
				PUNKT_DESC + " TEXT NOT NULL, " +
				PUNKT_ICO + " TEXT NOT NULL, " +
				"ico_type" + " INTEGER DEFAULT (0), " +
				PUNKT_STATUS + " INTEGER DEFAULT (-1), " +
				PUNKT_CHILDSQTY + " INTEGER, " +
				PUNKT_CLR_TEXT  + " INTEGER DEFAULT (0), " +
				PUNKT_CLR_BACKGROUND  + " INTEGER DEFAULT (0), " +
				DATE_CHANGE + " INTEGER, " +
				DATE_CREATE + " INTEGER";
        
        db.execSQL("insert into PUNKS_TMP select "+ FIELDS_old +" from "+ PUNKT_TABLE + ";"); //Скопировать данные из старой во временную
        db.execSQL("drop table if exists "+ PUNKT_TABLE +";"); //Удалить старую таблицу
        db.execSQL("alter table PUNKS_TMP add " + DATE_CREATE  + " INTEGER;"); //Добавить новый столбец во временную таблицу
        db.execSQL("create table "+ PUNKT_TABLE +" ("+ PUNKT_FIELDSnew +");"); //Создать новую таблицу с новым набором столбцов
        
        db.execSQL("insert into "+ PUNKT_TABLE +" select "+ FIELDS_new +" from PUNKS_TMP;"); //Скопировать данные из временной в новую
        db.execSQL("drop table if exists PUNKS_TMP;"); //Удалить временную таблицу

    	String LINK_FIELDSold = LINK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				LINK_PARENT_ID + " INTEGER, " +
				LINK_ISLINK + " INTEGER, " +
				LINK_CHILD_ID + " INTEGER, " +
				LINK_ORD + " INTEGER, " + 
				LINK_DATE_CHANGE + " TIMESTAMP";
    	
    	//Создать временную таблицу
        db.execSQL("create temporary table LINK_TMP (" + LINK_FIELDSold + ");");
						
        FIELDS_old = LINK_ID + ", " +
        		LINK_PARENT_ID + ", " +
        		LINK_ISLINK + ", " +
        		LINK_CHILD_ID + ", " +
        		LINK_ORD + ", " +
                LINK_DATE_CHANGE;
        
        FIELDS_new = LINK_ID + ", " +
        		LINK_PARENT_ID + ", " +
        		LINK_ISLINK + ", " +
        		LINK_CHILD_ID + ", " +
        		LINK_ORD + ", " +
        		LINK_DATE_CHANGE;
        
        String LINK_FIELDSnew = LINK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				LINK_PARENT_ID + " INTEGER, " +
				LINK_ISLINK + " INTEGER, " +
				LINK_CHILD_ID + " INTEGER, " +
				LINK_ORD + " INTEGER, " + 
				LINK_DATE_CHANGE + " INTEGER";
        
        db.execSQL("insert into LINK_TMP select "+ FIELDS_old +" from "+ LINK_TABLE +";"); //Скопировать данные из старой во временную
        db.execSQL("drop table if exists "+ LINK_TABLE +";"); //Удалить старую таблицу
        //db.execSQL("alter table LINK_TMP add " + LINK_DATE_CHANGE +";"); //Добавить новый столбец во временную таблицу
        db.execSQL("create table "+ LINK_TABLE +" ("+ LINK_FIELDSnew +");"); //Создать новую таблицу с новым набором столбцов
        //Log.e("OpenDbHelper onUpgrade", "create table "+ PUNKT_TABLE);
        
        db.execSQL("insert into "+ LINK_TABLE +" select "+ FIELDS_new +" from LINK_TMP;"); //Скопировать данные из временной в новую
        db.execSQL("drop table if exists LINK_TMP;"); //Удалить временную таблицу
    }
    
    private void update28_29(SQLiteDatabase db){	//Удаление двусторонних связей
    	Log.e(LOG_TAG + "update28_29", "28");
    	Cursor crsr = db.rawQuery("SELECT * FROM " + LINK_TABLE + " WHERE " + LINK_ISLINK + " =2", null);
    	if (crsr.moveToFirst()) {
    		int ind_id = crsr.getColumnIndex(LINK_ID);
        	int ind_parent_id = crsr.getColumnIndex(LINK_PARENT_ID);
        	//int ind_islink = crsr.getColumnIndex(LINK_ISLINK);
        	int ind_child_id = crsr.getColumnIndex(LINK_CHILD_ID);
        	int ind_ord = crsr.getColumnIndex(LINK_ORD);
        	//int ind_dateChange = crsr.getColumnIndex(LINK_DATE_CHANGE);
        	do{
        		long id = crsr.getLong(ind_id);
        		long parent_id = crsr.getLong(ind_parent_id);
        		int islink = 1;
        		long child_id = crsr.getLong(ind_child_id);
        		int order = crsr.getInt(ind_ord);
        		long dateChange = new Date().getTime(); //crsr.getLong(ind_dateChange);
        		
        		ContentValues cv = new ContentValues();
        		cv.put(LINK_ISLINK, islink);
    			cv.put(LINK_DATE_CHANGE, dateChange);
    			db.update(LINK_TABLE, cv, LINK_ID + "=?", new String[] {Long.toString(id)});	//update(parent_id, cv);
        		
        		cv.put(LINK_PARENT_ID, child_id);
        		//cv.put(LINK_ISLINK, islink);
        		cv.put(LINK_CHILD_ID, parent_id);
        		cv.put(LINK_ORD, order);
    			//cv.put(LINK_DATE_CHANGE, dateChange);
    			db.insert(LINK_TABLE, null, cv);
    			cv.clear();

        	}while (crsr.moveToNext());
        	
        	if (!crsr.isClosed()){
    			crsr.close();
    		}
    	}
    }
    
    private void update29_30(SQLiteDatabase db){	//Мульти вложения
    	Log.e(LOG_TAG + "update29_30", "29");
    	String PUNKT_ICO = "ico";
    	String PUNKT_ICOTYPE = "ico_type";
    	String SOURCE_FIELDSnew = _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
    			PARENT_ID + " INTEGER, " +
    			ATTACH_URL + " TEXT NOT NULL, " +
    			ATTACH_TYPE + " INTEGER DEFAULT (0), " + 
    			ORD + " INTEGER DEFAULT (0), " + 
    			DATE_CHANGE + " INTEGER";
    	
    	db.execSQL("create table "+ ATTACH_TABLE +" ("+ SOURCE_FIELDSnew +");"); //Создать новую таблицу с новым набором столбцов
        
        String FIELDS_new = _ID + ", " +
        		PUNKT_NAME + ", " +
        		PUNKT_DESC + ", " +
        		//PUNKT_ICO + ", " +
        		//"ico_type" + ", " +
        		PUNKT_ATTACHMENT + ", " +
        		PUNKT_ATTACHMENTS_QTY + ", " +
        		PUNKT_STATUS + ", " +
        		PUNKT_CHILDSQTY + ", " +
                PUNKT_CLR_TEXT + ", " +
                PUNKT_CLR_BACKGROUND + ", " +
                DATE_CHANGE + ", " +
                DATE_CREATE;
        
        String PUNKT_FIELDSnew = _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				PUNKT_NAME + " TEXT NOT NULL, "	+
				PUNKT_DESC + " TEXT NOT NULL, " +
				//PUNKT_ICO + " TEXT NOT NULL, " +
				//"ico_type" + ", " +
				PUNKT_ATTACHMENT + " TEXT NOT NULL, " +
				PUNKT_ATTACHMENTS_QTY + " INTEGER, " +
				PUNKT_STATUS + " INTEGER DEFAULT (-1), " +
				PUNKT_CHILDSQTY + " INTEGER, " +
				PUNKT_CLR_TEXT  + " INTEGER DEFAULT (0), " +
				PUNKT_CLR_BACKGROUND  + " INTEGER DEFAULT (0), " +
				DATE_CHANGE + " INTEGER, " +
				DATE_CREATE + " INTEGER";
        
        String PUNKT_FIELDS_1 = _ID + ", " +
				PUNKT_NAME + ", "	+
				PUNKT_DESC + ", " +
				PUNKT_ICO + ", " +
				PUNKT_STATUS + ", " +
				PUNKT_CHILDSQTY + ", " +
				PUNKT_CLR_TEXT  + ", " +
				PUNKT_CLR_BACKGROUND + ", " +
		        DATE_CHANGE + ", " +
		        DATE_CREATE;
        
        String PUNKT_FIELDS_2 = _ID + ", " +
				PUNKT_NAME + ", "	+
				PUNKT_DESC + ", " +
				PUNKT_ATTACHMENT + ", " +
				PUNKT_STATUS + ", " +
				PUNKT_CHILDSQTY + ", " +
				PUNKT_CLR_TEXT  + ", " +
				PUNKT_CLR_BACKGROUND + ", " +
		        DATE_CHANGE + ", " +
		        DATE_CREATE;
        
    	db.execSQL("create temporary table PUNKS_TMP (" + PUNKT_FIELDSnew + ");");	//Создать временную таблицу
        db.execSQL("insert into PUNKS_TMP(" + PUNKT_FIELDS_2 + ") select "+ PUNKT_FIELDS_1 +" from "+ PUNKT_TABLE + ";"); //Скопировать данные из старой во временную
        //db.execSQL("alter table PUNKS_TMP add " + PUNKT_ATTACHMENTS + " TEXT NOT NULL DEFAULT '';"); //Добавить новый столбец во временную таблицу
    	//db.execSQL("alter table PUNKS_TMP add " + DATE_CHANGE + " INTEGER;"); //Добавить новый столбец во временную таблицу
    	//db.execSQL("alter table PUNKS_TMP add " + DATE_CREATE + " INTEGER;"); //Добавить новый столбец во временную таблицу
    	
    	//db.execSQL("update PUNKS_TMP set " + PUNKT_ATTACHMENT + " = " + "( select " + DATE_CHANGE + " from " + PUNKT_TABLE + ") " + ";"); //Добавить новый столбец во временную таблицу
    	//db.execSQL("update PUNKS_TMP set " + PUNKT_ATTACHMENTS_QTY + " = " + "( select " + DATE_CREATE + " from " + PUNKT_TABLE + ") " + ";"); //Добавить новый столбец во временную таблицу
    	
        Cursor crsr = db.rawQuery("SELECT * FROM " + PUNKT_TABLE + " WHERE " + PUNKT_ICOTYPE + " >0", null);
    	if (crsr.moveToFirst()) {
        	int ind_parent_id = crsr.getColumnIndex(_ID);
        	int ind_ico = crsr.getColumnIndex(PUNKT_ICO);
        	int ind_ico_type = crsr.getColumnIndex(PUNKT_ICOTYPE);
        	
        	do{
        		String ico = crsr.getString(ind_ico);
        		if (ico != null && ico.length()>0){
	        		long parent_id = crsr.getLong(ind_parent_id);
	        		
	        		int ico_type = crsr.getInt(ind_ico_type);
	        		long dateChange = new Date().getTime();
	        		
	        		ContentValues cv = new ContentValues();
		        	cv.put(PARENT_ID, parent_id);
		    		cv.put(ATTACH_URL, ico);
		        	cv.put(ATTACH_TYPE, ico_type);
		        	cv.put(DATE_CHANGE, dateChange);
		    		db.insert(ATTACH_TABLE, null, cv);
		    		cv.clear();
		    			
		    		cv.put(PUNKT_ATTACHMENTS_QTY, 1);
		    		cv.put(DATE_CHANGE, dateChange);
		    		db.update("PUNKS_TMP", cv, _ID + "=?", new String[] {Long.toString(parent_id)});
        		}
        	}while (crsr.moveToNext());
        	
        	if (!crsr.isClosed()){
    			crsr.close();
    		}
    	}
    	
        db.execSQL("drop table if exists "+ PUNKT_TABLE + ";"); //Удалить старую таблицу
        db.execSQL("create table "+ PUNKT_TABLE +" ("+ PUNKT_FIELDSnew + ");"); //Создать новую таблицу с новым набором столбцов
        db.execSQL("insert into "+ PUNKT_TABLE +" select "+ FIELDS_new + " from PUNKS_TMP;"); //Скопировать данные из временной в новую

        db.execSQL("drop table if exists PUNKS_TMP;"); //Удалить временную таблицу
    }
    
    private void update30_31(SQLiteDatabase db){
    	Log.e(LOG_TAG + "update30_31", "30");
    	if (!isTableExists(db)){
    		String ATTACH_FIELDS = _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		    		PARENT_ID + " INTEGER, " +
		    		ATTACH_URL + " TEXT NOT NULL, " +
		    		ATTACH_TYPE + " INTEGER DEFAULT (0), " + 
		    		ORD + " INTEGER DEFAULT (0), " +
		    		DATE_CHANGE + " INTEGER";
    		createTables(db, ATTACH_TABLE, ATTACH_FIELDS);
    	}
    }
    
    private void update31_32(SQLiteDatabase db){
    	Log.e(LOG_TAG + "update31_32", "31");

        	String FILTER_FIELDS = _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		    		PARENT_ID + " INTEGER, " +
		    		ORD + " INTEGER DEFAULT (0), " +
		    		FILTER_OPEN_PAR + " TEXT NOT NULL, " +
		    		FILTER_CRITERIA + " TEXT NOT NULL, " + 
		    		FILTER_COMPARISON + " INTEGER DEFAULT (0), " + 
		    		FILTER_STR_VAL + " TEXT NOT NULL, " +
		    		//FILTER_INT_VAL + " INTEGER DEFAULT (0), " +
		    		FILTER_CLOSE_PAR + " TEXT NOT NULL, " +
		    		FILTER_AND_OR + " TEXT DEFAULT ('AND'), " +
		    		DATE_CHANGE + " INTEGER";
        	db.execSQL("create table "+ FILTER_TABLE +" ("+ FILTER_FIELDS + ");"); //Создать новую таблицу с новым набором столбцов
            //db.execSQL("insert into PUNKS_TMP select "+ FIELDS_old +" from " + PUNKT_TABLE + ";"); //Скопировать данные из старой во временную
            //db.execSQL("drop table if exists "+ PUNKT_TABLE +";"); //Удалить старую таблицу
            db.execSQL("alter table " + PUNKT_TABLE + " add " + PARENT_ID + " INTEGER;"); //Добавить новый столбец во временную таблицу
            db.execSQL("alter table " + PUNKT_TABLE + " add " + PUNKT_SOURCE_ID + " INTEGER;"); //Добавить новый столбец во временную таблицу
            db.execSQL("alter table " + PUNKT_TABLE + " add " + ORD + " INTEGER;"); //Добавить новый столбец во временную таблицу
            
            //db.execSQL("create table "+ PUNKT_TABLE +" ("+ PUNKT_FIELDSnew +");"); //Создать новую таблицу с новым набором столбцов
            //Log.e("OpenDbHelper onUpgrade", "create table "+ PUNKT_TABLE);
            //db.execSQL("insert into "+ PUNKT_TABLE +" select "+ FIELDS_new +" from PUNKS_TMP;"); //Скопировать данные из временной в новую
            //db.execSQL("drop table if exists PUNKS_TMP;"); //Удалить временную таблицу
           
            String LINK_TABLE = "LNKS";
            String LINK_PARENT_ID = "parent_id";
            String LINK_ISLINK = "islink";
            String LINK_CHILD_ID = "child_id";
            String LINK_ORD = "ord";
            String LINK_DATE_CHANGE = "date_change";
            
            Cursor crsr = db.rawQuery("SELECT * FROM " + LINK_TABLE, null);
        	if (crsr.moveToFirst()) {
            	int ind_parent_id = crsr.getColumnIndex(LINK_PARENT_ID);
            	int ind_islink = crsr.getColumnIndex(LINK_ISLINK);
            	int ind_child_id = crsr.getColumnIndex(LINK_CHILD_ID);
            	int ind_ord = crsr.getColumnIndex(LINK_ORD);
            	int ind_dateChange = crsr.getColumnIndex(LINK_DATE_CHANGE);
            	int refs = 0;
            	ContentValues cv = new ContentValues();
            	do{
            		long parent_id = crsr.getLong(ind_parent_id);
            		int islink = crsr.getInt(ind_islink);
            		long child_id = crsr.getLong(ind_child_id);
            		int ord = crsr.getInt(ind_ord);
            		long dateChange = crsr.getLong(ind_dateChange);
            		
            		if (islink == 0){	//Если не связь.
            			/*Cursor crsr2 = db.rawQuery("SELECT * FROM " + PUNKT_TABLE + " WHERE " + _ID + " =? ",
            					new String[] {Long.toString(child_id)});*/
            			cv.put(PARENT_ID, parent_id);
            			cv.put(ORD, ord);
            			db.update(PUNKT_TABLE, cv, _ID + "=?", new String[] {Long.toString(child_id)});
            		}else{
            			/*Cursor crsr2 = db.rawQuery("SELECT * FROM " + PUNKT_TABLE + " WHERE " + _ID + " =? ",
            					new String[] {Long.toString(child_id)});*/
            			cv.put(PUNKT_NAME, "");
            			cv.put(PUNKT_DESC, "");
            			cv.put(PUNKT_ATTACHMENT, "");
            			cv.put(DATE_CHANGE, dateChange);
            			cv.put(DATE_CREATE, dateChange);
            			cv.put(PARENT_ID, parent_id);
            			cv.put(PUNKT_SOURCE_ID, child_id);
            			cv.put(ORD, ord);
            			
            			long nref = db.insert(PUNKT_TABLE, null, cv);
            			if (nref > 0)
            				refs++;
            			else
            				Log.e("OpenDbHelper onUpgrade", "Ссылка не создана: " + Long.toString(child_id));
            		}
            		cv.clear();
            	}while (crsr.moveToNext());
            	Log.d("OpenDbHelper onUpgrade", "Создано ссылок: " + PUNKT_TABLE);
        	}
        	
        	crsr = db.rawQuery("SELECT * FROM " + PUNKT_TABLE + " WHERE " + PUNKT_ATTACHMENT + "='null' OR "
        			+ PUNKT_ATTACHMENT + "='0' OR " + PUNKT_ATTACHMENT + "='logo_noimage'", null);
        	if (crsr.moveToFirst()) {
        		int ind_id = crsr.getColumnIndex(_ID);
        		
				ContentValues cv = new ContentValues();
            	do{
            		long _id = crsr.getLong(ind_id);
            		cv.put(PUNKT_ATTACHMENT, "");
            		db.update(PUNKT_TABLE, cv, _ID + "=?", new String[] {Long.toString(_id)});
            	}while (crsr.moveToNext());
        	}
        	
        	if (!crsr.isClosed()){
    			crsr.close();
    		}
        	
            db.execSQL("drop table if exists LNKS"); //Удалить старую таблицу
    }
    
    private void update32_33(SQLiteDatabase db){
    	Log.e(LOG_TAG + "update32_33", "32");
    	try {
	            //db.execSQL("insert into PUNKS_TMP select "+ FIELDS_old +" from " + PUNKT_TABLE + ";"); //Скопировать данные из старой во временную
	            //db.execSQL("drop table if exists "+ PUNKT_TABLE +";"); //Удалить старую таблицу
	        	Cursor crsr = db.rawQuery("SELECT * FROM " + ATTACH_TABLE + " LIMIT 1", null);
	        	if (crsr.getColumnIndex(DATE_CREATE) == -1){
	        		db.execSQL("alter table " + ATTACH_TABLE + " add " + DATE_CREATE + " INTEGER;"); //Добавить новый столбец во временную таблицу
	        	}
	        	crsr = db.rawQuery("SELECT * FROM " + FILTER_TABLE + " LIMIT 1", null);
	        	if (crsr.getColumnIndex(DATE_CREATE) == -1){
	        		db.execSQL("alter table " + FILTER_TABLE + " add " + DATE_CREATE + " INTEGER;"); //Добавить новый столбец во временную таблицу
	        	}
	        	
	    		if (crsr != null && !crsr.isClosed()) {
	    			crsr.close();
	    		}
	
	            db.execSQL("update " + ATTACH_TABLE + " set " + DATE_CREATE + " = " + DATE_CHANGE + ";");
	            db.execSQL("update " + FILTER_TABLE + " set " + DATE_CREATE + " = " + DATE_CHANGE + ";");
    	} catch (SQLException sqlerror) {
			Log.e("refreshPunkts error", sqlerror.getMessage());
		}
    }

    
    private boolean isTableExists(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+ OpenDbHelper.ATTACH_TABLE +"'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
            	cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }
    
    private void createTables(SQLiteDatabase db, String tablename, String fields) {
    	Log.e(LOG_TAG + "createTables" , "CREATE TABLE " + tablename + " ("+ fields + ");");
    	db.execSQL("CREATE TABLE " + tablename + " ("+ fields + ");");
    }
}

    
