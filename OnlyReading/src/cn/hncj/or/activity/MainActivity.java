package cn.hncj.or.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.hncj.or.config.Const;
import cn.hncj.or.db.BookDb;
import cn.hncj.or.ui.CircleImageView;
import cn.hncj.or.ui.MyGridView;
import cn.hncj.or.ui.SlideMenu;
import cn.hncj.or.utils.MyApplication;

import com.hncj.activity.R;

public class MainActivity extends Activity implements OnClickListener {
	private SlideMenu slideMenu;
	private RadioGroup group;
	private GridView menugridView;
	private CircleImageView cricleview;
	private View reanview;// 移除动画
	private ArrayList<HashMap<String, Object>> bookItem = null;// 本地书籍信息
	private HashMap<String, Object> map;
	private MyGridView gridView;
	private BookDb bookdb;// 创建数据库
	private BookAdapter bookadapter;
	private Dialog builder;
	private TextView debutton;
	private static String[] names = { "收藏", "下载", "账户", "设置" };
	private static int[] ids = { R.drawable.item1, R.drawable.item2,
			R.drawable.item3, R.drawable.item4 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		slideMenu = (SlideMenu) findViewById(R.id.slide_menu);
		cricleview = (CircleImageView) findViewById(R.id.title_bar_menu_btn);
		menugridView = (GridView) findViewById(R.id.menu_grid);
		menugridView.setAdapter(new Myadpter());
		cricleview.setOnClickListener(this);
		initView();
		gridView = (MyGridView) findViewById(R.id.book_gridview);
		bookdb = new BookDb(MyApplication.context, Const.DB_TNAME);
		loadBookdata();
		gridView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Animation animation = AnimationUtils.loadAnimation(
						MyApplication.getcontext(), R.anim.shake);
				reanview = view;
				view.setAnimation(animation);
				debutton = (TextView) view.findViewById(R.id.del_book);
				debutton.setVisibility(View.VISIBLE);
				debutton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						showdeletDialog();
					}
				});
				return true;
			}
		});
		// 单次点击事件
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// 修改数据库中图书的最近阅读状态为1
				String s = (String) bookItem.get(position).get("path");
				SQLiteDatabase db = bookdb.getWritableDatabase();
				File f = new File(s);
				if (f.length() == 0) {
					Toast.makeText(MainActivity.this, "该文件为空文件",
							Toast.LENGTH_SHORT).show();

				} else {
					ContentValues values = new ContentValues();
					values.put("now", 1);// key为字段名，value为值
					db.update(Const.DB_TNAME, values, "path=?",
							new String[] { s });// 修改状态为图书被已被导入
					db.close();
					Intent intent = new Intent();
					String path = (String) bookItem.get(position).get("path");
					// sintent.setClass(MainActivity.this, Read.class);
					intent.putExtra("path", path);
					startActivity(intent);
				}
			}
		});
	}

	/**
	 * 删除书的Dialog
	 */
	public void showdeletDialog() {
		builder = new Dialog(this, R.style.dialog);
		builder.setCanceledOnTouchOutside(false);
		View view = View.inflate(MainActivity.this, R.layout.dialog_deletebook,
				null);
		builder.setContentView(view);
		TextView cencel = (TextView) view.findViewById(R.id.cencel_del);
		TextView okdelet = (TextView) view.findViewById(R.id.ok_del);
		cencel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				debutton.setVisibility(View.INVISIBLE);
				reanview.clearAnimation();// 移除动画
				builder.dismiss();
			}
		});
		okdelet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 删除操作
			}
		});
		// 捕捉回退键
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				debutton.setVisibility(View.INVISIBLE);
				reanview.clearAnimation();
			}
		});

		builder.show();
	}

	/**
	 * 菜单适配
	 */
	class Myadpter extends BaseAdapter {
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return names.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(MainActivity.this,
					R.layout.list_item_menu, null);
			ImageView iv_item = (ImageView) view.findViewById(R.id.iv_item);
			TextView tv_item = (TextView) view.findViewById(R.id.tv_item);
			tv_item.setText(names[position]);
			iv_item.setImageResource(ids[position]);
			return view;
		}
	}

	/**
	 * 加载侧边菜单
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_bar_menu_btn:
			if (slideMenu.isMainScreenShowing()) {
				slideMenu.openMenu();
			} else {
				slideMenu.closeMenu();
			}
			break;
		}
	}

	/**
	 * 查找图书按钮
	 */
	public void query(View view) {
		Intent intent = new Intent();
		intent.setClass(this, QueryBookActivity.class);
		startActivity(intent);
		this.finish();
	}

	/**
	 * 加载Fragment布局
	 */
	public void initView() {
		group = (RadioGroup) findViewById(R.id.tab_menu);
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.book_jia:
					break;
				case R.id.book_cheng:
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, BookstoreActivity.class);
					startActivity(intent);
					break;
				case R.id.book_history:
					Intent intn = new Intent(MainActivity.this,
							BookhistoryActivity.class);
					startActivity(intn);
					break;
				default:
					break;
				}

			}
		});
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
			if (name.length() > 20) {
				name = name.substring(0, 8) + "...";
			}
			map = new HashMap<String, Object>();
			map.put("BookName", name != null ? name : "未知");
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
			bname.setText(bookItem.get(position).get("BookName").toString());
			return contentView;
		}
	}

	/**
	 * 监听回退键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

		}
		return super.onKeyDown(keyCode, event);
	}
}
