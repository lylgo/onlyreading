package cn.hncj.or.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.hncj.or.config.Const;
import cn.hncj.or.db.BookDb;
import cn.hncj.or.ui.CircleImageView;
import cn.hncj.or.ui.MyGridView;
import cn.hncj.or.ui.MyImageView;
import cn.hncj.or.ui.SlideMenu;
import cn.hncj.or.utils.MyApplication;

import com.hncj.activity.R;

public class MainActivity extends BaseActivity implements OnClickListener {
	private SlideMenu slideMenu;
	private RadioGroup group;
	private GridView menugridView;
	private CircleImageView cricleview;
	private View reanview;// �Ƴ�����
	private ArrayList<HashMap<String, Object>> bookItem = null;// �����鼮��Ϣ
	private HashMap<String, Object> map;
	private MyGridView gridView;
	private BookDb bookdb;// �������ݿ�
	private BookAdapter bookadapter;
	private SharedPreferences sp;
	private Dialog builder,userDiag;
	private TextView debutton, nickname;
	private CircleImageView userImage;
	private RadioButton rabutton;
	private int local;// ɾ����־λ
	private static String[] names = { "�ղ�", "����", "�˻�", "����" };
	private static int[] ids = { R.drawable.item1, R.drawable.item2,
			R.drawable.item3, R.drawable.item4 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		slideMenu = (SlideMenu) findViewById(R.id.slide_menu);
		cricleview = (CircleImageView) findViewById(R.id.title_bar_menu_btn);
		menugridView = (GridView) findViewById(R.id.menu_grid);
		rabutton = (RadioButton) findViewById(R.id.book_jia);
		nickname = (TextView) findViewById(R.id.nickname);// �ǳ�
		userImage=(CircleImageView) findViewById(R.id.title_Image);
		sp = getSharedPreferences("land", MODE_PRIVATE);
		String name=sp.getString("name", "��δ��¼");
		if(name!=null){
			nickname.setText(name);
		}
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
				local = position;
				debutton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						showdeletDialog(local);
					}
				});
				return true;
			}
		});
		// ���ε���¼�
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// �޸����ݿ���ͼ�������Ķ�״̬Ϊ1
				String path = (String) bookItem.get(position).get("path");
				SQLiteDatabase db = bookdb.getWritableDatabase();
				File f = new File(path);
				if (f.length() == 0) {
					Toast.makeText(MainActivity.this, "���ļ�Ϊ���ļ�",
							Toast.LENGTH_SHORT).show();

				} else {
					ContentValues values = new ContentValues();
					values.put("now", 1);// keyΪ�ֶ�����valueΪֵ
					db.update(Const.DB_TNAME, values, "path=?",
							new String[] { path });// �޸�״̬Ϊ�Ѷ�
					db.close();
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, ReadBookActivity.class);
					Log.i("LL", bookItem.get(position).get("BookName")
							.toString()
							+ ".txt");
					intent.putExtra("path",
							bookItem.get(position).get("BookName").toString()
									+ ".txt");
					startActivity(intent);
				}
			}
		});
		/**
		 * �˵�����
		 */
		menugridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					Log.i("FF", "�����1");
					break;
				case 1:
					Log.i("FF", "�����2");
					break;
				case 2:
					Intent intent = new Intent(MainActivity.this,
							RegisterActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
					break;
				case 3:
					break;
				default:
					break;
				}
			}
		});
		/**
		 * ͷ�����
		 */
		userImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showUerImage();
			}
		});
	}

	/**
	 * ɾ�����Dialog
	 */
	public void showdeletDialog(final int local) {
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
				reanview.clearAnimation();// �Ƴ�����
				builder.dismiss();
			}
		});
		okdelet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String path = bookItem.get(local).get("path").toString();
				deleteBook(path);
				reanview.clearAnimation();
				loadBookdata();
				builder.dismiss();
			}
		});
		// ��׽���˼�
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
	 * �˵�����
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
	 * ���ز�߲˵�
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
	 * ����ͼ�鰴ť
	 */
	public void query(View view) {
		Intent intent = new Intent();
		intent.setClass(this, QueryBookActivity.class);
		startActivity(intent);
		this.finish();
	}

	/**
	 * �˵���ת
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
					sp = getSharedPreferences("history", MODE_PRIVATE);
					Editor ed = sp.edit();
					ed.putString("flag", "flag");
					ed.commit();
					break;
				default:
					break;
				}

			}
		});
	}

	/**
	 * ɾ��ͼ��
	 * 
	 * @return
	 */
	public void deleteBook(String path) {
		String dewhere[] = { path };
		SQLiteDatabase database = bookdb.getWritableDatabase();
		database.delete(Const.DB_TNAME, "path=?", dewhere);
		database.close();
	}

	/**
	 * ���ر������
	 */
	public void loadBookdata() {
		String colname[] = { "path" };// ���ݿ��ֶ�ָ��������
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
		if (bookItem == null)// ��ղ���
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
			map.put("BookName", name != null ? name : "δ֪");
			map.put("path", file.getPath());
			map.put("com", 0 + file.getName());
			bookItem.add(map);
		}
		bookadapter = new BookAdapter();
		gridView.setAdapter(bookadapter);
	}

	/**
	 * �鱾������
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
   * ͷ��Dialog
   */
	public void showUerImage(){
		AlertDialog.Builder builder=new Builder(this);
		View view=View.inflate(this, R.layout.dialog_showuser, null);
		builder.setView(view);
		userDiag=builder.show();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		sp = getSharedPreferences("history", MODE_PRIVATE);
		String flag = sp.getString("flag", null);
		if (flag != null) {
			rabutton.setChecked(true);
		}
		super.onPause();
	}

	/**
	 * �������˼�
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

		}
		return super.onKeyDown(keyCode, event);
	}
}
