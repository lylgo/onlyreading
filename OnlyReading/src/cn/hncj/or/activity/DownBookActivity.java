package cn.hncj.or.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.hncj.or.config.Const;
import cn.hncj.or.db.BookDb;

import com.hncj.activity.R;

public class DownBookActivity extends BaseActivity {
	private ListView booklist;
	private ImageButton button;
	private List<Map<String, String>> filebooklist;
	private MyBookAdapter adapter;
	private Dialog builder;
	protected BookDb bookdb;
	protected SQLiteDatabase database;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		filebooklist = new ArrayList<Map<String, String>>();
		adapter = new MyBookAdapter();
		LoadFilebook();
		setContentView(R.layout.activity_filebook);
		bookdb = new BookDb(this);// 加载数据库
		database = bookdb.getWritableDatabase();
		booklist = (ListView) findViewById(R.id.filebook_list);
		button = (ImageButton) findViewById(R.id.back_home);
		button.setOnClickListener(new MyClickListener());
		booklist.setAdapter(adapter);
		booklist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				showdeletDialog(position, filebooklist.get(position)
						.get("name"), "T");
			}
		});
		booklist.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				showdeletDialog(position, filebooklist.get(position)
						.get("name"), "D");
				return false;
			}
		});
	}

	/**
	 * 添加书的Dialog
	 */
	public void showdeletDialog(final int position, final String bookname,
			String flag) {
		builder = new Dialog(this, R.style.dialog);
		builder.setCanceledOnTouchOutside(false);
		View view = View.inflate(DownBookActivity.this,
				R.layout.dialog_deletebook, null);
		builder.setContentView(view);
		TextView toptext = (TextView) view.findViewById(R.id.toptext);
		TextView cencel = (TextView) view.findViewById(R.id.cencel_del);
		TextView okdelet = (TextView) view.findViewById(R.id.ok_del);
		if (flag.equals("T")) {
			toptext.setText("点击确定将该书导入书库");
			cencel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					builder.dismiss();
				}
			});
			okdelet.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					File file = new File(Const.LocalFile + "/books/" + bookname
							+ ".txt");
					insert(file.getParent(), file.getPath(), database);
					builder.dismiss();
				}
			});
		} else {
			toptext.setText("你确定删除本书");
			cencel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					builder.dismiss();
				}
			});
			okdelet.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					boolean flag = delecbook(bookname);
					if (flag) {
						Toast.makeText(DownBookActivity.this, "删除本地书籍成功",
								Toast.LENGTH_SHORT).show();
						filebooklist.remove(position);
						adapter.notifyDataSetChanged();

					} else {
						Toast.makeText(DownBookActivity.this, "删除本地书籍失败",
								Toast.LENGTH_SHORT).show();

					}
					builder.dismiss();
				}
			});
		}
		builder.show();
	}

	class MyClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(DownBookActivity.this,
					MainActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
			finish();
		}

	}

	/**
	 * 适配器
	 */
	class MyBookAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return filebooklist.size();
		}

		@Override
		public Object getItem(int position) {
			return filebooklist.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if (convertView == null) {
				view = View.inflate(DownBookActivity.this,
						R.layout.item_file_book, null);
			} else {
				view = convertView;
			}
			TextView bookname = (TextView) view.findViewById(R.id.bookname);
			TextView booktime = (TextView) view.findViewById(R.id.booktime);
			bookname.setText(filebooklist.get(position).get("name"));
			booktime.setText(filebooklist.get(position).get("time"));
			final int pos = position;
			return view;
		}
	}

	/**
	 * 加载本地数据
	 */
	@SuppressLint("SimpleDateFormat")
	private void LoadFilebook() {
		File file = new File(Const.LocalFile + "/books");
		File[] filelist = file.listFiles();
		for (File fileitem : filelist) {
			Map<String, String> map = new HashMap<String, String>();
			String name = fileitem.getName().substring(0,
					(fileitem.getName().length() - 4));
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			@SuppressWarnings("deprecation")
			String time = format.format(new Date(fileitem.lastModified()));
			map.put("name", name);
			map.put("time", time);
			filebooklist.add(map);
		}
		adapter.notifyDataSetChanged();
	}

	/**
	 * 导入数据库
	 */
	public void insert(String parent, String path, SQLiteDatabase db) {
		try {
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
			cursor.close();
			db.close();
			Toast.makeText(DownBookActivity.this, "导入书籍成功", Toast.LENGTH_SHORT)
					.show();
		} catch (SQLException e) {
			Toast.makeText(DownBookActivity.this, "导入书籍失败", Toast.LENGTH_SHORT)
					.show();
			e.printStackTrace();
		}
	}

	/**
	 * 删除书籍
	 */
	public boolean delecbook(String bookname) {
		try {
			File file = new File(Const.LocalFile + "/books/" + bookname
					+ ".txt");
			file.delete();
			SQLiteDatabase data = bookdb.getWritableDatabase();
			data.delete(Const.DB_TNAME, "path=?",
					new String[] { file.getPath() });
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(DownBookActivity.this,
					MainActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
