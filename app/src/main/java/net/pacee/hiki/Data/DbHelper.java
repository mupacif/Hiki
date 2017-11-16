package net.pacee.hiki.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static net.pacee.hiki.Data.PlaceContract.CREATE_TABLE;
import static net.pacee.hiki.Data.PlaceContract.UPGRADE_TABLE;

/**
 * Created by mupac_000 on 11-11-17.
 */

public class DbHelper extends SQLiteOpenHelper {
    public static String DB_NAME = "insect.database";
    public static int DB_VERSION=1;

    public DbHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(UPGRADE_TABLE);
    }


}
