package cn.hncj.or.http;

import java.io.File;
import java.util.List;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import cn.hncj.or.config.Const;
import cn.hncj.or.db.BookDb;
import cn.hncj.or.function.CheckNetwork;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class DownBookHttpUtils {
	/**
	 * 下载小书
	 * 
	 * @param bar
	 * @param context
	 * @param bookId
	 */
	public static void downloadbook(final ProgressBar bar,
			final Context context, String bookId, final Button button,
			final String bookname) {
		if (CheckNetwork.isNetworkAvailable(context)) {
			RequestParams params = new RequestParams();
			params.addBodyParameter("bookId", bookId);
			HttpUtils httpUtils = new HttpUtils();
			httpUtils.download(Const.DOWNBOOKPATH + "?bookId=" + bookId,
					Const.LocalFile + "/books/" + bookname + ".txt", params,
					new RequestCallBack<File>() {
						@Override
						public void onSuccess(ResponseInfo<File> arg0) {
							// TODO Auto-generated method stub
							BookDb bookdb = new BookDb(context);// 加载数据库
							SQLiteDatabase database = bookdb
									.getWritableDatabase();
							File file = new File(Const.LocalFile + "/books/"
									+ bookname + ".txt");
							insert(file.getParent(), file.getPath(),database);
						}

						@Override
						public void onLoading(long total, long current,
								boolean isUploading) {
							int progress = (int) (current * 100 / total);
							bar.setProgress(progress);
							button.setTextSize(10);
							button.setText(progress + "%");
							if (progress == 100) {
								button.setTextSize(10);
								button.setText("完成");
								bar.setVisibility(View.INVISIBLE);
							}
						}

						@Override
						public void onStart() {
							// TODO Auto-generated method stub
							bar.setVisibility(View.VISIBLE);
						}

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							// TODO Auto-generated method stub
							bar.setVisibility(View.INVISIBLE);
							Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT)
									.show();
						}
					});

		} else {
			return;
		}
	}

	public static void insert(String parent, String path, SQLiteDatabase db) {
		String str[] = { "path" };
		Cursor cursor = db.query(Const.DB_TNAME, str, "path=?",
				new String[] { path }, null, null, null);
		if (cursor.getCount() != 0) {
			return;
		} else {
			String sql = "insert into " + Const.DB_TNAME
					+ " (parent,path, type,now,ready) values('" + parent
					+ "','" + path + "',1,0,null" + ");";
			db.execSQL(sql);
		}
		// cursor.close();
		// db.close();
	}
}
