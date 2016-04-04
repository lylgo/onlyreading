package com.hncj.or.frament;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
		String colname[] = { "path" };
		SQLiteDatabase database = bookdb.getReadableDatabase();
		Cursor cur = database.query(Const.DB_TNAME, colname, "type=1", null,
				null, null, null);
		Cursor curtow = database.query(Const.DB_TNAME, colname, "type=2", null,
				null, null, null);
		Integer num = cur.getCount();
		Integer numtow = curtow.getCount();
		ArrayList<String> arraylist = new ArrayList<String>();
		while (curtow.moveToNext()) {
			String s = curtow.getString(curtow.getColumnIndex("path"));
			arraylist.add(s);
		}
		while (cur.moveToNext()) {
			String s = cur.getString(cur.getColumnIndex("path"));
			arraylist.add(s);
		}
		database.close();
		cur.close();
		curtow.close();
		if (bookItem == null)
			bookItem = new ArrayList<HashMap<String, Object>>();
		bookItem.clear();
		//------------------------------------------------------------
		Map<String, String[]> maps = new HashMap<String, String[]>();
		String[] bookids = getResources().getStringArray(R.array.bookid);
		String[] booknames = getResources().getStringArray(R.array.bookname);
		String[] bookauthors = getResources().getStringArray(R.array.bookauthor);
		for (int i = 0; i < bookids.length; i++) {
			String[] value = new String[2];
			value[0] = booknames[i];
			value[1] = bookauthors[i];
			maps.put(bookids[i], value);
		}
		//------------------------------------------------------------
		for (int i = 0; i < num + numtow; i++) {
			if (i < numtow) {
				File file1 = new File(arraylist.get(i));
				String m = file1.getName().substring(0,
						file1.getName().length() - 4);
				if (m.length() > 8) {
					m = m.substring(0, 8) + "...";
				}
				String id = arraylist.get(i).substring(
						arraylist.get(i).lastIndexOf("/") + 1);
				String[] array = maps.get(id);
				String auther = array != null && array[1] == null ? "未知"
						: array[1];
				String name = array[0] == null ? m : array[0];
				map = new HashMap<String, Object>();

				if (i == 0) {
					map.put("itemback", R.drawable.itemback);
				} else if ((i % 2) == 0) {
					map.put("itemback", R.drawable.itemback);
				}
				map.put("ItemImage",
						mapImage != null ? mapImage.get(file1.getName())[0]
								: R.drawable.cover);
				map.put("BookName", name == null ? m : name);
				map.put("ItemTitle1", "作者：" + auther);
				map.put("LastImage", "推荐书目");
				map.put("path", file1.getPath());
				map.put("com", 0 + file1.getName());// 单独用于排序
				bookItem.add(map);
			} else {
				map = new HashMap<String, Object>();

				File file1 = new File(arraylist.get(i));
				String m = file1.getName().substring(0,
						file1.getName().length() - 4);
				if (m.length() > 8) {
					m = m.substring(0, 8) + "...";
				}
				if (i == 0) {
					map.put("itemback", R.drawable.itemback);
				} else if ((i % 2) == 0) {
					map.put("itemback", R.drawable.itemback);
				}
				map.put("ItemImage", R.drawable.cover);
				map.put("BookName", m);
				map.put("ItemTitle", m);
				map.put("ItemTitle1", "作者：未知");
				map.put("LastImage", "本地导入");
				map.put("path", file1.getPath());
				map.put("com", "1");
				bookItem.add(map);

			}
		}
		for(int i=1;i<8;i++){
		HashMap<String, Object> mm=new HashMap<String, Object>();
		//----------------------------------------------------------------------
		mm.put("BookName", "ss");
		mm.put("path", "dd");
		bookItem.add(mm);}
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
			contentView = View.inflate(MyApplication.context, R.layout.book_item, null);
			TextView bname = (TextView) contentView
					.findViewById(R.id.book_name);
			bname.setText(bookItem.get(position).get("BookName").toString());
			return contentView;
		}
	}
}
