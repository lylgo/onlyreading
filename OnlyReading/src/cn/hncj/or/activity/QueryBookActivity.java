package cn.hncj.or.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
import android.widget.Toast;
import cn.hncj.or.config.Const;
import cn.hncj.or.db.BookDb;

import com.hncj.activity.R;

public class QueryBookActivity extends BaseActivity {
	private ListView booklist;
	private TextView pathtext;
	private ProgressDialog dialog;
	private int filename = 0;// �ļ�����
	private String fileEndsname;// �ļ���׺��
	private List<String> mFileName = null;
	private List<String> mFilePaths = null;
	private String mSDCard = "/";
	private String mCurrentFilePath = mSDCard;
	private PopupWindow popupWindow;
	private View popview;
	private Button button;
	private Button okButton, canButton;
	private BookDb bookdb;// �������ݿ�

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.query_book_activity);
		booklist = (ListView) findViewById(R.id.booklist);
		pathtext = (TextView) findViewById(R.id.flsh);
		showDialog();
		Thread thread = new Thread(runnable);
		thread.start();// �����̲߳���sd��
		listListenter(); // listView�ļ���
		button = (Button) findViewById(R.id.btn_leftTop);
		pathtext.setText("·����" + mCurrentFilePath);
		bookdb = new BookDb(this, Const.DB_TNAME);// �������ݿ�
		// ���ص���
		popview = this.getLayoutInflater().inflate(R.layout.addbook_popwindow,
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
	 * ��ӵĵ���
	 */
	public void showpop( final String parent,final String path) {
		popupWindow.showAtLocation(findViewById(R.id.querybook),
				Gravity.BOTTOM, 0, 0);
		popupWindow.setFocusable(true);// ��ý���
		popupWindow.setOutsideTouchable(true);// ����ⲿ��ʧ
		okButton = (Button) popview.findViewById(R.id.add_book);// ȷ�ϵ��밴ť
		canButton = (Button) popview.findViewById(R.id.cencel_book);
		okButton.setBackgroundResource(R.drawable.ok_button);
		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				insert(parent, path);//��������
				popupWindow.dismiss();//��ʾ����ʧ
				
			}
		});
		canButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
			}
		});
	}

	/**
	 * ����listview
	 */
	public void listListenter() {
		booklist.setOnItemClickListener(new ListView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				final File mFile = new File(mFilePaths.get(arg2));
				fileEndsname = mFile
						.getName()
						.substring(mFile.getName().lastIndexOf(".") + 1,
								mFile.getName().length()).toLowerCase();// ȡ���ļ���׺����ת��Сд
				if (mFile.canRead()) {// ������ļ��ǿɶ��ģ����ǽ�ȥ�鿴�ļ�
					if (mFile.isDirectory()) {// ������ļ��У���ֱ�ӽ�����ļ��У��鿴�ļ�Ŀ¼
						initFileListInfo(mFilePaths.get(arg2));
						booklist.setAdapter(new FileAdapter(
								QueryBookActivity.this, mFileName, mFilePaths));
						pathtext.setText("·����" + mCurrentFilePath);
					} else if (fileEndsname.equals("txt")) {
						Log.i("TTT",mFilePaths.get(arg2) + "------"
										+ mFile.getParent());
						showpop(mFile.getParent(), mFilePaths.get(arg2));
					} else {// ��������ļ���
						openFile(mFile);
					}
				}
			}
		});
	}

	/**
	 * ����������ݲ������ݿ�
	 */
	public void insert(String parent, String path) {
		SQLiteDatabase db = bookdb.getWritableDatabase();
		String str[]={"path"};
		Cursor cursor=db.query(Const.DB_TNAME, str, "path=?",new String[]{path}, null, null, null);
		if(cursor.getCount()!=0){
			Toast.makeText(this,"����Ŀ�Ѵ���", Toast.LENGTH_SHORT).show();
		}else{
			String sql= "insert into " + Const.DB_TNAME + " (parent,path, type,now,ready) values('" + parent + "','" + path
					+ "',0,0,null" + ");";
			Toast.makeText(this, "�����Ŀ�ɹ���",Toast.LENGTH_SHORT).show();
			db.execSQL(sql);	
		}
		db.close();
	}

	/**
	 * �����ļ�
	 */
	private void initFileListInfo(String filePath) {
		mCurrentFilePath = filePath;
		mFileName = new ArrayList<String>();
		mFilePaths = new ArrayList<String>();
		File mFile = new File(filePath);
		File[] mFiles = mFile.listFiles();// ���������ļ���·���µ������ļ�/�ļ���
		if (!mCurrentFilePath.equals(mSDCard)) {
			initAddBackUp(filePath, mSDCard);
		}

		/* �������ļ���Ϣ��ӵ������� */
		for (File mCurrentFile : mFiles) {
			if (mCurrentFile.canRead()) {// �ж��Ƿ�ɶ� �ж��Ƿ�ɶ� �ж��Ƿ�ɶ�
				mFileName.add(mCurrentFile.getName());
				mFilePaths.add(mCurrentFile.getPath());
			}
		}
		/* �������� */

	}

	/**
	 * ����Dialog
	 */
	public void showDialog() {
		dialog = new ProgressDialog(this);
		dialog.show();
		dialog.setCanceledOnTouchOutside(false);// ����ⲿ��ȡ��
		dialog.setContentView(R.layout.myprogressdialog);
	}

	/**
	 * �����߳�
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
				pathtext.setText("·����" + mCurrentFilePath);
				break;
			default:
				break;
			}

		}
	};

	/**
	 * �Զ���listView������
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
			String wenjianLength = Datachange((int) mFile.length());// �ļ���С

			if (mFileNameList.get(position).toString().equals("BacktoUp")) {
				viewHolder.wenjianSum.setVisibility(View.GONE);
				viewHolder.mIV.setVisibility(View.GONE);
				viewHolder.backImage.setImageBitmap(backImage);
				viewHolder.mTV.setTextSize(20);
				viewHolder.mTV.setText("��һ��");

			} else {

				viewHolder.mIV.setVisibility(View.VISIBLE);
				viewHolder.wenjianSum.setVisibility(View.VISIBLE);
				viewHolder.backImage.setImageBitmap(sanjiaoImage);
				viewHolder.mTV.setText(fileName);

				if (mFile.isDirectory()) {

					try {// ��ֹ�쳣
						File[] fi = mFile.listFiles();
						// �� ���ļ����� �м��� �ɶ��ļ�
						for (File mCurrentFile : fi) {
							if (mCurrentFile.canRead()) {// �ж��Ƿ�ɶ� �ж��Ƿ�ɶ� �ж��Ƿ�ɶ�
								filename++;
							}
						}

					} catch (Exception e) {
					}

					viewHolder.mIV.setImageBitmap(mFolder);
					viewHolder.wenjianSum.setText(filename + "���ļ�");
					filename = 0;
				} else {

					fileEndsname = fileName.substring(
							fileName.lastIndexOf(".") + 1, fileName.length())
							.toLowerCase();// ȡ���ļ���׺����ת��Сд
					if (fileEndsname.equals("m4a")
							|| fileEndsname.equals("mp3")
							|| fileEndsname.equals("mid")
							|| fileEndsname.equals("xmf")
							|| fileEndsname.equals("ogg")
							|| fileEndsname.equals("wav")) {
						viewHolder.mIV.setImageBitmap(mVideo);
						viewHolder.wenjianSum.setText(fileEndsname + "�ļ�"
								+ "  " + wenjianLength);
					} else if (fileEndsname.equals("3gp")
							|| fileEndsname.equals("mp4")) {
						viewHolder.mIV.setImageBitmap(mAudio);
						viewHolder.wenjianSum.setText(fileEndsname + "�ļ�"
								+ "  " + wenjianLength);
					} else if (fileEndsname.equals("jpg")
							|| fileEndsname.equals("gif")
							|| fileEndsname.equals("png")
							|| fileEndsname.equals("jpeg")
							|| fileEndsname.equals("bmp")) {
						viewHolder.mIV.setImageBitmap(mImage);
						viewHolder.wenjianSum.setText(fileEndsname + "�ļ�"
								+ "  " + wenjianLength);
					} else if (fileEndsname.equals("apk")) {
						viewHolder.mIV.setImageBitmap(mApk);
						viewHolder.wenjianSum.setText(fileEndsname + "�ļ�"
								+ "  " + wenjianLength);
					} else if (fileEndsname.equals("txt")) {
						viewHolder.mIV.setImageBitmap(mTxt);
						viewHolder.wenjianSum.setText(fileEndsname + "�ļ�"
								+ "  " + wenjianLength);
					} else if (fileEndsname.equals("zip")
							|| fileEndsname.equals("rar")) {
						viewHolder.mIV.setImageBitmap(mRar);
						viewHolder.wenjianSum.setText(fileEndsname + "�ļ�"
								+ "  " + wenjianLength);
					} else if (fileEndsname.equals("html")
							|| fileEndsname.equals("htm")
							|| fileEndsname.equals("mht")) {
						viewHolder.mIV.setImageBitmap(mWeb);
						viewHolder.wenjianSum.setText(fileEndsname + "�ļ�"
								+ "  " + wenjianLength);
					} else {
						viewHolder.mIV.setImageBitmap(mOthers);
						viewHolder.wenjianSum.setText("δ֪�ļ�" + "  "
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
	 * ��Сת��
	 */
	// �ļ���С��ʽ��ת��
	private byte[] b = null;
	String sss = null;

	public String Datachange(int x) {
		if (x >= 1024 * 1024 * 1024) {// ת����G
			b = (x * 0.9766 * 0.9766 * 0.9766 / 1000000000 + "").getBytes();
			sss = new String(b, 0, 6) + "G";
			return sss;
		} else if (x >= 1024 * 1024 && x < 1024 * 1024 * 1024) {// ת����m
			b = (x * 0.9766 * 0.9766 / 1000000 + "").getBytes();
			sss = new String(b, 0, 6) + "m";
			return sss;
		} else if (x > 1024 && x <= 1024 * 1024) {// ת����k
			b = (x * 0.9766 / 1000 + "").getBytes();
			sss = new String(b, 0, 6) + "k";
			return sss;
		} else {// �ֽ�
			return x + "�ֽ�";
		}
	}

	/**
	 * ����ϵͳ�������ļ�
	 */
	/** ����ϵͳ�ķ����������ļ��ķ��� */
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
	 * ���MIME���͵ķ���
	 * */
	private String getMIMEType(File file) {
		String type = "";
		String fileName = file.getName();
		fileEndsname = fileName.substring(fileName.lastIndexOf(".") + 1,
				fileName.length()).toLowerCase();// ȡ���ļ���׺����ת��Сд
		if (fileEndsname.equals("m4a") || fileEndsname.equals("mp3")
				|| fileEndsname.equals("mid") || fileEndsname.equals("xmf")
				|| fileEndsname.equals("ogg") || fileEndsname.equals("wav")) {
			type = "audio/*";// ϵͳ���г����п��ܴ���Ƶ�ļ��ĳ���ѡ����
		} else if (fileEndsname.equals("3gp") || fileEndsname.equals("mp4")) {
			type = "video/*";// ϵͳ���г����п��ܴ���Ƶ�ļ��ĳ���ѡ����
		} else if (fileEndsname.equals("jpg") || fileEndsname.equals("gif")
				|| fileEndsname.equals("png") || fileEndsname.equals("jpeg")
				|| fileEndsname.equals("bmp")) {
			type = "image/*";// ϵͳ���г����п��ܴ�ͼƬ�ļ��ĳ���ѡ����
		} else {
			type = "*/*"; // ϵͳ���г����п��ܴ򿪸��ļ��ĳ���ѡ����
		}
		return type;
	}

	/**
	 * ���ظ��ڵ�
	 */
	// ����
	private void initAddBackUp(String filePath, String phone_sdcard) {

		if (!filePath.equals(phone_sdcard)) {
			/* �б���ĵ�1������Ϊ������һ�� */
			mFileName.add("BacktoUp");
			mFilePaths.add(new File(filePath).getParent());// �ص���ǰĿ¼�ĸ�Ŀ¼���ص��ϼ�
		}

	}
}
