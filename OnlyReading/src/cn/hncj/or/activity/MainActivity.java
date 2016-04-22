package cn.hncj.or.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.hncj.or.config.Const;
import cn.hncj.or.db.BookDb;
import cn.hncj.or.http.ImageHttpUtils;
import cn.hncj.or.ui.CircleImageView;
import cn.hncj.or.ui.MyGridView;
import cn.hncj.or.ui.SlideMenu;
import cn.hncj.or.utils.CompressBitmapUtils;
import cn.hncj.or.utils.MyApplication;

import com.hncj.activity.R;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

public class MainActivity extends BaseActivity implements OnClickListener {
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
	private SharedPreferences sp;
	private Dialog builder, ImageDiag;
	private TextView debutton, nickname;
	private CircleImageView userImage, title_Image;
	private RadioButton rabutton;
	private int local;// 删除标志位
	private Bitmap bitmap;// 剪切后bitmap
	protected File tempFile;
	private String name, email;
	private static String[] names = { "收藏", "下载", "账户", "设置" };
	private static int[] ids = { R.drawable.item1, R.drawable.item2,
			R.drawable.item3, R.drawable.item4 };
	@SuppressLint("CutPasteId")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SpeechUtility.createUtility(this, SpeechConstant.APPID+"=5713667d");//初始化
		slideMenu = (SlideMenu) findViewById(R.id.slide_menu);
		cricleview = (CircleImageView) findViewById(R.id.title_bar_menu_btn);
		menugridView = (GridView) findViewById(R.id.menu_grid);
		rabutton = (RadioButton) findViewById(R.id.book_jia);
		nickname = (TextView) findViewById(R.id.nickname);// 昵称
		userImage = (CircleImageView) findViewById(R.id.title_Image);
		title_Image = (CircleImageView) findViewById(R.id.title_bar_menu_btn);
		sp = getSharedPreferences("land", MODE_PRIVATE);
		name = sp.getString("name", "尚未登录");
		email = sp.getString("email", "");
		nickname.setText(name);
		if (!email.equals("")) {
			CompressBitmapUtils.getLocalBitmap(
					Const.LocalFile + email + ".png", email, userImage,
					title_Image);
		}
		menugridView.setAdapter(new Myadpter());
		cricleview.setOnClickListener(this);
		initView();
		gridView = (MyGridView) findViewById(R.id.book_gridview);
		bookdb = new BookDb(MainActivity.this);
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
		// 单次点击事件
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// 修改数据库中图书的最近阅读状态为1
				String path = (String) bookItem.get(position).get("path");
				SQLiteDatabase db = bookdb.getWritableDatabase();
				File f = new File(path);
				if (f.length() == 0) {
					Toast.makeText(MainActivity.this, "该文件为空文件",
							Toast.LENGTH_SHORT).show();

				} else {
					ContentValues values = new ContentValues();
					values.put("now", 1);// key为字段名，value为值
					db.update(Const.DB_TNAME, values, "path=?",
							new String[] { path });// 修改状态为已读
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, ReadBookActivity.class);
					intent.putExtra("path", path);
					startActivity(intent);
				}
			}
		});
		/**
		 * 菜单监听
		 */
		menugridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
//					sp = getSharedPreferences("land", MODE_PRIVATE);
//					Editor editor = sp.edit();
//					editor.putString("name", "尚未登陆");
//					editor.putString("email", "");
//					editor.putString("date", "");
//					editor.commit();
//					Toast.makeText(getApplication(), "s", 1).show();
					Intent intent=new Intent(MainActivity.this,CollecActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
					break;
				case 1:
					break;
				case 2:
					break;
				case 3:
					break;
				default:
					break;
				}
			}
		});
		/**
		 * 头像监听
		 */
		userImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (email.equals("")) {
					Intent intent = new Intent(MainActivity.this,
							LoginActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
				} else {
					showUerImage();
				}

			}
		});
	}

	/**
	 * 删除书的Dialog
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
				reanview.clearAnimation();// 移除动画
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
	}

	/**
	 * 菜单跳转
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
					overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
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
	 * 删除图书
	 * 
	 * @return
	 */
	public void deleteBook(String path) {
		String dewhere[] = { path };
		SQLiteDatabase database = bookdb.getWritableDatabase();
		database.delete(Const.DB_TNAME, "path=?", dewhere);
		database.close();
		bookadapter.notifyDataSetChanged();
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
			// if (name.length() > 20) {
			// name = name.substring(0, 8) + "...";
			// }
			map = new HashMap<String, Object>();
			map.put("BookName", name != null ? name : "未知");
			map.put("path", file.getPath());
			map.put("com", 0 + file.getName());
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
	 * 头像Dialog
	 */
	public void showUerImage() {
		ImageDiag = new Dialog(this, R.style.dialog);
		View view = View.inflate(this, R.layout.dialog_showuser, null);
		Button markbutton = (Button) view.findViewById(R.id.mak_image);
		Button gabutton = (Button) view.findViewById(R.id.ga_image);
		Button cancelbutton = (Button) view.findViewById(R.id.cancel_button);
		markbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ImageDiag.dismiss();
				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				startActivityForResult(intent, Const.PHOTO_REQUEST_CAMERA);
			}
		});
		
		gabutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ImageDiag.dismiss();
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setType("image/*");
				startActivityForResult(intent, Const.PHOTO_REQUEST_GALLERY);
			}
		});
		cancelbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ImageDiag.dismiss();
			}
		});
		ImageDiag.setContentView(view);
		ImageDiag.setCanceledOnTouchOutside(false);
		ImageDiag.show();
	}

	/**
	 * 回调方法
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (hasSdcard()) {
			tempFile = new File(Environment.getExternalStorageDirectory()
					.getPath() + "/onlyreading");
			if (!tempFile.exists())
				tempFile.mkdir();
		}
		if (requestCode == Const.PHOTO_REQUEST_GALLERY) {
			if (data != null) {
				Uri uri = data.getData();
				crop(uri);
			}
		} else if (requestCode == Const.PHOTO_REQUEST_CAMERA) {
			if(data!=null){
				Uri uri = data.getData();
				crop(uri);
			}
		} else if (requestCode == Const.PHOTO_REQUEST_CUT) {
			try {
				bitmap = data.getParcelableExtra("data");
				userImage.setImageBitmap(bitmap);
				Bitmap bit = CompressBitmapUtils.compBitmap(bitmap);
				title_Image.setImageBitmap(bit);// 压缩处理
				ImageHttpUtils.uploadMethod(tempFile, bitmap, this, email);
				ImageDiag.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 图像剪切
	 */
	private void crop(Uri uri) {
		// 裁剪图片意图
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// 裁剪框的比例，1：1
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// 裁剪后输出图片的尺寸大小
		intent.putExtra("outputX", 250);
		intent.putExtra("outputY", 250);
		// 图片格式
		intent.putExtra("outputFormat", "JPEG");
		intent.putExtra("noFaceDetection", false);// 取消人脸识别
		intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
		startActivityForResult(intent, Const.PHOTO_REQUEST_CUT);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		loadBookdata();
		sp = getSharedPreferences("history", MODE_PRIVATE);
		String flag = sp.getString("flag", null);
		if (flag != null) {
			rabutton.setChecked(true);
		}
		super.onPause();
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

	/**
	 * 验证SD卡是否存在
	 */
	private boolean hasSdcard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
}
