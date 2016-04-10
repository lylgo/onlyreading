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
	private String table;

	public BookDb(Context context, String name) {
		super(context, Const.DATABASE_NAME, null, DATA_VERSION);
		this.table=name;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "CREATE TABLE " + table + " ( parent text not null, "
				+ "path text not null, "+"type  text not null"
				+ ", now  text not null, ready);";
		db.execSQL(sql);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}
}
