package cn.hncj.or.db;

import cn.hncj.or.config.Const;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 书签数据库的建立
 */
public class BookmarkDb extends SQLiteOpenHelper {
	private static int DATABASE_VERSION = 1;

	public BookmarkDb(Context context) {
		super(context, Const.DATABASE_NAME, null, DATABASE_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE " + Const.DB_TMARK + " ( path text not null, "
				+ "begin int not null default 0,"
				+ " word text not null , time text not null);";
		db.execSQL(sql);

	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
