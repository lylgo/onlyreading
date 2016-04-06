package com.hncj.or.frament;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.hncj.or.config.Const;
import cn.hncj.or.db.BookDb;
import cn.hncj.or.ui.MyGridView;
import cn.hncj.or.utils.MyApplication;

import com.hncj.activity.R;

public class FragmentBookstack extends Fragment {
	private ArrayList<HashMap<String, Object>> bookItem = null;// 本地书籍信息
	private HashMap<String, Object> map;
	private Map<String, Integer[]> mapImage;// 存放本地推荐目录的小封面图片引用
	private MyGridView gridView;
	private BookDb bookdb;// 创建数据库
	private BookAdapter bookadapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frament_bookstack, null);
		gridView = (MyGridView) view.findViewById(R.id.book_gridview);
		bookdb = new BookDb(MyApplication.context, Const.DB_TNAME);
		loadBookdata();
		return view;
	}

	/**
	 * 加载本地书库
	 */
	public void loadBookdata() {
		String colname[] = { "path" };// 数据库字段指定返回行
		SQLiteDatabase database = bookdb.getReadableDatabase();
		Cursor cur = database.query(Const.DB_TNAME, colname, null, null, null,
				null, null);
		Integer num = cur.getCount();
		ArrayList<String> arraylist = new ArrayList<String>();
		while (cur.moveToNext()) {
			String path = cur.getString(cur.getColumnIndex("path"));
			arraylist.add(path);
		}
		database.close();
		cur.close();
		if (bookItem == null)// 清空操作
			bookItem = new ArrayList<HashMap<String, Object>>();
		bookItem.clear();
		Map<String, String[]> maps = new HashMap<String, String[]>();
		for (int i = 0; i < num; i++) {
				File file = new File(arraylist.get(i));
				String name = file.getName().substring(0,
						file.getName().length() - 4);
				if (name.length() >20) {
					name= name.substring(0, 8) + "...";
				}
				map = new HashMap<String, Object>();
				map.put("BookName", name != null ? name :"未知");
				map.put("path", file.getPath());
				map.put("com", 0 + file.getName());// 单独用于排序
				bookItem.add(map);
		}
		bookadapter = new BookAdapter();
		gridView.setAdapter(bookadapter);
	}

	/**
	 * 书本设配器
	 */
	class BookAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return bookItem.size();
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View contentView, ViewGroup arg2) {
			contentView = View.inflate(MyApplication.context,
					R.layout.book_item, null);
			TextView bname = (TextView) contentView
					.findViewById(R.id.book_name);
			Log.i("TTT", bookItem.get(position).get("BookName").toString());
			bname.setText(bookItem.get(position).get("BookName").toString());
			return contentView;
		}
	}
}
