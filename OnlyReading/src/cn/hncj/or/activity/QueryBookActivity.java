package cn.hncj.or.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import cn.hncj.or.config.Const;
import cn.hncj.or.db.BookDb;

import com.hncj.activity.R;

public class QueryBookActivity extends BaseActivity {
	private ListView booklist;
	private TextView pathtext;
	private ProgressDialog dialog;
	private int filename = 0;// 文件个数
	private String fileEndsname;// 文件后缀名
	private List<String> mFileName = null;
	private List<String> mFilePaths = null;
	private String mSDCard = "/";
	private String mCurrentFilePath = mSDCard;
	private PopupWindow popupWindow;
	private View popview;
	private Button button;
	private Button okButton;
	private BookDb bookdb;//加载数据库
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.query_book_activity);
		booklist = (ListView) findViewById(R.id.booklist);
		pathtext = (TextView) findViewById(R.id.flsh);
		showDialog();
		Thread thread = new Thread(runnable);
		thread.start();//开启线程查找sd卡
		listListenter(); //listView的监听
		button = (Button) findViewById(R.id.btn_leftTop);
		pathtext.setText("路径：" + mCurrentFilePath);
		bookdb=new BookDb(this, Const.DB_TNAME);//加载数据库
		//加载弹窗
		popview = this.getLayoutInflater().inflate(R.layout.query_book_activity,
				null);
		popupWindow = new PopupWindow(popview, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(QueryBookActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
			}
		});

	}

	/**
	 * 添加的弹窗
	 */
	public void showpop() {
		popupWindow.showAtLocation(findViewById(R.id.querybook), Gravity.BOTTOM,
				0, 0);
		okButton = (Button) popview.findViewById(R.id.add_book);// 确认导入按钮
		okButton.setText("确认导入");
		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	/**
	 * 监听listview
	 */
	public void listListenter() {
		booklist.setOnItemClickListener(new ListView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				final File mFile = new File(mFilePaths.get(arg2));
				fileEndsname = mFile.getName().substring(mFile.getName().lastIndexOf(".") + 1,mFile.getName().length()).toLowerCase();// 取出文件后缀名并转成小写
				if (mFile.canRead()) {// 如果该文件是可读的，我们进去查看文件
					if (mFile.isDirectory()) {// 如果是文件夹，则直接进入该文件夹，查看文件目录
						initFileListInfo(mFilePaths.get(arg2));
						booklist.setAdapter(new FileAdapter(
								QueryBookActivity.this, mFileName, mFilePaths));
						pathtext.setText("路径：" + mCurrentFilePath);
					} else if (fileEndsname.equals("txt")) {
						// // 导入到书架
						// import_bookName = mFile.getName();
						// bookThread.state = BookFinal.BOOK_IMPORT;
						if(!popupWindow.isShowing()){
							showpop();
						}
					} else {// 如果不是文件夹
						openFile(mFile);
					}
				}
			}
		});
	}

	/**
	 * 遍历文件
	 */
	private void initFileListInfo(String filePath) {
		mCurrentFilePath = filePath;
		mFileName = new ArrayList<String>();
		mFilePaths = new ArrayList<String>();
		File mFile = new File(filePath);
		File[] mFiles = mFile.listFiles();// 遍历出该文件夹路径下的所有文件/文件夹
		if (!mCurrentFilePath.equals(mSDCard)) {
			initAddBackUp(filePath, mSDCard);
		}

		/* 将所有文件信息添加到集合中 */
		for (File mCurrentFile : mFiles) {
			if (mCurrentFile.canRead()) {// 判断是否可读 判断是否可读 判断是否可读
				mFileName.add(mCurrentFile.getName());
				mFilePaths.add(mCurrentFile.getPath());
			}
		}
		/* 适配数据 */

	}

	/**
	 * 加载Dialog
	 */
	public void showDialog() {
		dialog = new ProgressDialog(this);
		dialog.show();
		dialog.setCanceledOnTouchOutside(false);// 点击外部不取消
		dialog.setContentView(R.layout.myprogressdialog);
	}

	/**
	 * 开启线程
	 */
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message mag = QueryBookActivity.this.handler.obtainMessage();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			initFileListInfo(mCurrentFilePath);
			mag.what = 1;
			mag.sendToTarget();
		}
	};
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				dialog.dismiss();
				booklist.setAdapter(new FileAdapter(QueryBookActivity.this,
						mFileName, mFilePaths));
				pathtext.setText("路径：" + mCurrentFilePath);
				break;
			default:
				break;
			}

		}
	};

	/**
	 * 自定义listView设配器
	 */
	private Bitmap mImage;
	private Bitmap mAudio;
	private Bitmap mRar;
	private Bitmap mVideo;
	private Bitmap mFolder;
	private Bitmap mApk;
	private Bitmap mOthers;
	private Bitmap mTxt;
	private Bitmap mWeb;
	private Bitmap backImage;
	private Bitmap sanjiaoImage;
	private Context mContext;
	private List<String> mFileNameList;
	private List<String> mFilePathList;

	class FileAdapter extends BaseAdapter {
		public FileAdapter(Context context, List<String> fileName,
				List<String> filePath) {
			mContext = context;
			mFileNameList = fileName;
			mFilePathList = filePath;
			mImage = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.image);
			mAudio = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.audio);
			mVideo = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.video);
			mApk = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.apk);
			mTxt = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.txt);
			mOthers = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.othersother);
			mFolder = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.folder);
			mRar = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.zip_icon);
			mWeb = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.web_browser);
			backImage = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.file_return_sel);
			sanjiaoImage = BitmapFactory.decodeResource(
					mContext.getResources(), R.drawable.right_more_unsel);
		}

		public int getCount() {
			return mFilePathList.size();
		}

		public Object getItem(int position) {
			return mFileNameList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup viewgroup) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				LayoutInflater mLI = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = mLI.inflate(R.layout.wenjian_listview, null);
				viewHolder.mIV = (ImageView) convertView
						.findViewById(R.id.image_list_childs);
				viewHolder.mTV = (TextView) convertView
						.findViewById(R.id.text_list_childs);
				viewHolder.backImage = (ImageView) convertView
						.findViewById(R.id.backImage);
				viewHolder.wenjianSum = (TextView) convertView
						.findViewById(R.id.text2_list_childs);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			final File mFile = new File(mFilePathList.get(position).toString());
			String fileName = mFile.getName();
			String wenjianLength = Datachange((int) mFile.length());// 文件大小

			if (mFileNameList.get(position).toString().equals("BacktoUp")) {
				viewHolder.wenjianSum.setVisibility(View.GONE);
				viewHolder.mIV.setVisibility(View.GONE);
				viewHolder.backImage.setImageBitmap(backImage);
				viewHolder.mTV.setTextSize(20);
				viewHolder.mTV.setText("上一级");

			} else {

				viewHolder.mIV.setVisibility(View.VISIBLE);
				viewHolder.wenjianSum.setVisibility(View.VISIBLE);
				viewHolder.backImage.setImageBitmap(sanjiaoImage);
				viewHolder.mTV.setText(fileName);

				if (mFile.isDirectory()) {

					try {// 防止异常
						File[] fi = mFile.listFiles();
						// 看 本文件夹下 有几个 可读文件
						for (File mCurrentFile : fi) {
							if (mCurrentFile.canRead()) {// 判断是否可读 判断是否可读 判断是否可读
								filename++;
							}
						}

					} catch (Exception e) {
					}

					viewHolder.mIV.setImageBitmap(mFolder);
					viewHolder.wenjianSum.setText(filename + "个文件");
					filename = 0;
				} else {

					fileEndsname = fileName.substring(
							fileName.lastIndexOf(".") + 1, fileName.length())
							.toLowerCase();// 取出文件后缀名并转成小写
					if (fileEndsname.equals("m4a")
							|| fileEndsname.equals("mp3")
							|| fileEndsname.equals("mid")
							|| fileEndsname.equals("xmf")
							|| fileEndsname.equals("ogg")
							|| fileEndsname.equals("wav")) {
						viewHolder.mIV.setImageBitmap(mVideo);
						viewHolder.wenjianSum.setText(fileEndsname + "文件"
								+ "  " + wenjianLength);
					} else if (fileEndsname.equals("3gp")
							|| fileEndsname.equals("mp4")) {
						viewHolder.mIV.setImageBitmap(mAudio);
						viewHolder.wenjianSum.setText(fileEndsname + "文件"
								+ "  " + wenjianLength);
					} else if (fileEndsname.equals("jpg")
							|| fileEndsname.equals("gif")
							|| fileEndsname.equals("png")
							|| fileEndsname.equals("jpeg")
							|| fileEndsname.equals("bmp")) {
						viewHolder.mIV.setImageBitmap(mImage);
						viewHolder.wenjianSum.setText(fileEndsname + "文件"
								+ "  " + wenjianLength);
					} else if (fileEndsname.equals("apk")) {
						viewHolder.mIV.setImageBitmap(mApk);
						viewHolder.wenjianSum.setText(fileEndsname + "文件"
								+ "  " + wenjianLength);
					} else if (fileEndsname.equals("txt")) {
						viewHolder.mIV.setImageBitmap(mTxt);
						viewHolder.wenjianSum.setText(fileEndsname + "文件"
								+ "  " + wenjianLength);
					} else if (fileEndsname.equals("zip")
							|| fileEndsname.equals("rar")) {
						viewHolder.mIV.setImageBitmap(mRar);
						viewHolder.wenjianSum.setText(fileEndsname + "文件"
								+ "  " + wenjianLength);
					} else if (fileEndsname.equals("html")
							|| fileEndsname.equals("htm")
							|| fileEndsname.equals("mht")) {
						viewHolder.mIV.setImageBitmap(mWeb);
						viewHolder.wenjianSum.setText(fileEndsname + "文件"
								+ "  " + wenjianLength);
					} else {
						viewHolder.mIV.setImageBitmap(mOthers);
						viewHolder.wenjianSum.setText("未知文件" + "  "
								+ wenjianLength);

					}
				}

			}
			return convertView;
		}

		class ViewHolder {
			ImageView mIV, backImage;
			TextView mTV, wenjianSum;
		}

	}

	/**
	 * 大小转换
	 */
	// 文件大小格式的转换
	private byte[] b = null;
	String sss = null;

	public String Datachange(int x) {
		if (x >= 1024 * 1024 * 1024) {// 转换成G
			b = (x * 0.9766 * 0.9766 * 0.9766 / 1000000000 + "").getBytes();
			sss = new String(b, 0, 6) + "G";
			return sss;
		} else if (x >= 1024 * 1024 && x < 1024 * 1024 * 1024) {// 转换成m
			b = (x * 0.9766 * 0.9766 / 1000000 + "").getBytes();
			sss = new String(b, 0, 6) + "m";
			return sss;
		} else if (x > 1024 && x <= 1024 * 1024) {// 转换成k
			b = (x * 0.9766 / 1000 + "").getBytes();
			sss = new String(b, 0, 6) + "k";
			return sss;
		} else {// 字节
			return x + "字节";
		}
	}

	/**
	 * 调用系统打开其他文件
	 */
	/** 调用系统的方法，来打开文件的方法 */
	private void openFile(File file) {
		if (file.isDirectory()) {
			initFileListInfo(file.getPath());
		} else {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(file), getMIMEType(file));
			startActivity(intent);
		}
	}

	/**
	 * 获得MIME类型的方法
	 * */
	private String getMIMEType(File file) {
		String type = "";
		String fileName = file.getName();
		fileEndsname = fileName.substring(fileName.lastIndexOf(".") + 1,
				fileName.length()).toLowerCase();// 取出文件后缀名并转成小写
		if (fileEndsname.equals("m4a") || fileEndsname.equals("mp3")
				|| fileEndsname.equals("mid") || fileEndsname.equals("xmf")
				|| fileEndsname.equals("ogg") || fileEndsname.equals("wav")) {
			type = "audio/*";// 系统将列出所有可能打开音频文件的程序选择器
		} else if (fileEndsname.equals("3gp") || fileEndsname.equals("mp4")) {
			type = "video/*";// 系统将列出所有可能打开视频文件的程序选择器
		} else if (fileEndsname.equals("jpg") || fileEndsname.equals("gif")
				|| fileEndsname.equals("png") || fileEndsname.equals("jpeg")
				|| fileEndsname.equals("bmp")) {
			type = "image/*";// 系统将列出所有可能打开图片文件的程序选择器
		} else {
			type = "*/*"; // 系统将列出所有可能打开该文件的程序选择器
		}
		return type;
	}

	/**
	 * 返回父节点
	 */
	// 返回
	private void initAddBackUp(String filePath, String phone_sdcard) {

		if (!filePath.equals(phone_sdcard)) {
			/* 列表项的第1项设置为返回上一级 */
			mFileName.add("BacktoUp");
			mFilePaths.add(new File(filePath).getParent());// 回到当前目录的父目录即回到上级
		}

	}
}
