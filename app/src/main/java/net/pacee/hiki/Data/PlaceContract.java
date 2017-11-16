package net.pacee.hiki.Data;

import android.net.Uri;

/**
 * Created by mupac_000 on 11-11-17.
 */

public class PlaceContract {

    public static String AUTHORITY = "net.pacee.hiki";
    public static String PATH = "place";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+AUTHORITY);
    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();



    public static String TABLE_NAME = "Place";
    public static String TABLE_ID = "_ID";
    public static String TABLE_PLACEID = "placeId";
    public static String COL_NAME="name";
    public static String COL_ADRESS="adress";
    public static String COL_LAT="lat";
    public static String COL_LNG="lng";
    public static String COL_COMMENT="comment";
    public static String COL_DATE="date";
    public static String COL_DONE="done";


    public static String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+"("
            + TABLE_ID  +" INTEGER PRIMARY KEY AUTOINCREMENT,"
            + TABLE_PLACEID + "  INTEGER,"
            + COL_NAME + " TEXT NOT NULL,"
            + COL_ADRESS + " TEXT NOT NULL,"
            + COL_LAT + "REAL NOT NULL,"
            + COL_LNG + "REAL NOT NULL"
            + COL_COMMENT + "TEXT NOT NULL,"
            + COL_DATE + " TEXT NOT NULL,"
            + COL_DONE + "INTEGER)";

    public static String UPGRADE_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME+";";
}
