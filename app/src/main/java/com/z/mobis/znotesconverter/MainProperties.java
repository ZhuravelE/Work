package com.z.mobis.znotesconverter;

import android.content.SharedPreferences;

import java.util.List;
import java.util.Locale;

public class MainProperties {
	public static int theme;
	public static boolean always_show_date;
	public static String date_time_format;
	public static Locale locale;
	public static int childsLines = 2;
	public static int FontSizeName = 18;
	public static int FontSizeDesc = 14;
	public static int colorBackg;
	public static int colorText = -1;
	public static int link_Show_mask;
	public static boolean link_Clickable;
	public static int actionType;
	static List<Long> lockedPunks;
	public static long root_parentid  = 0L;
	public static int imagesize;
	public static int sortBy;
	public static String sortByStr;
	public static int pathLenght;
	public static boolean imageLeft;
	public static final int listSize = 300;
	public static boolean ShowID;
	public static int parentLines;
	public static boolean imageClicable;
	public static int punktpos;
	private static SharedPreferences prefs;
	public static boolean doubleLinkMode;

	public static int[] limits = new int[]{0, 0};    //{было, стало}
	
	public MainProperties(){
	}

	public static SharedPreferences getPrefs(){
		return prefs;
	}
}
