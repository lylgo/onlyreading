package cn.hncj.or.db;

import cn.hncj.or.config.Const;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 *建立书籍数据库
 * @author Administrator
 *
 */
public class BookDb extends SQLiteOpenHelper {
	private static final int DATA_VERSION = 1;

	public BookDb(Context context) {
		super(context, Const.DATABASE_NAME, null, DATA_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String msql = "CREATE TABLE " + Const.DB_TMARK + " ( path text not null, "
				+ "begin int not null default 0,"
				+ " word text not null , time text not null);";
		String sql = "CREATE TABLE " + Const.DB_TNAME + " ( parent text not null, "
				+ "path text not null, "+"type  text not null"
				+ ", now  text not null, ready);";
		db.execSQL(sql);
		db.execSQL(msql);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}
}
