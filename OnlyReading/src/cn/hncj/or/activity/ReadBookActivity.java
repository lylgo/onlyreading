package cn.hncj.or.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.hncj.or.config.Const;
import cn.hncj.or.db.BooKMark;
import cn.hncj.or.db.BookDb;
import cn.hncj.or.function.PageWidget;
import cn.hncj.or.read.BookMarkAdapter;
import cn.hncj.or.read.BookPageFactory;
import cn.hncj.or.read.SpeechRead;

import com.hncj.activity.R;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

@SuppressLint({ "WrongCall", "ClickableViewAccessibility" })
// implements OnClickListener
public class ReadBookActivity extends BaseActivity {
	private static int begin = 0;// 记录的书籍开始位置
	public static Canvas mCurPageCanvas, mNextPageCanvas;
	private static String word = "";// 记录当前页面的文字
	private String bookPath;// 记录读入书的路径
	private SharedPreferences.Editor editor;
	private Boolean isNight = false, menupop = true, rollread = false,
			textShow = false,readnow=false; // 亮度模式,白天和晚上
	protected int jumpPage;// 记录跳转进度条
	private int light; // 亮度值
	private WindowManager.LayoutParams windlp;
	private TextView poptext, bookname;
	private BookDb bookDb;
	private Bitmap mCurPageBitmap, mNextPageBitmap;
	private PageWidget mPageWidget;
	private PopupWindow settingPop, topPop, setLightPop, settxtSizePop;
	private BookPageFactory pagefactory;
	private View settingPopView, topPopView, setLightView, settxtSizeView;
	private int screenHeight, screenWidth;
	private ListView bookmarklist;
	private SeekBar proressbar, seeklightbar, sizeTextBar;
	private RadioGroup popgroup, bgcolorgroup;
	@SuppressWarnings("unused")
	private ImageButton backButton, singButton, noteButton, saveeye;
	private Boolean show = false, lightShow = false;//
	private int size = 27;
	private SharedPreferences sp;
	private float startX;
	private int defaultSize = 0;
	private Dialog dialog;
	private ImageView light_setting;
	private BookMarkBroadReceiver adapterReceiver;
	private RollRead rollreadRecriver;
	private BookMarkAdapter adapter;
	private Handler handler;
	private Timer timer;
	private TimerTask task;
	private SpeechRead speech;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		speech=new SpeechRead(this);
		adapterReceiver = new BookMarkBroadReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("BookMarkAdapter.Change");
		registerReceiver(adapterReceiver, filter);
		rollreadRecriver=new RollRead();
		IntentFilter Rfilter = new IntentFilter();
		Rfilter.addAction("READ.STOP");
		registerReceiver(rollreadRecriver, Rfilter);
		WindowManager manage = getWindowManager();
		Display display = manage.getDefaultDisplay();
		screenWidth = display.getWidth();
		screenHeight = display.getHeight();
		defaultSize = (screenWidth * 20) / 320;
		mCurPageBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
				Bitmap.Config.ARGB_8888);
		mNextPageBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
				Bitmap.Config.ARGB_8888);
		mCurPageCanvas = new Canvas(mCurPageBitmap);// 设置画布
		mNextPageCanvas = new Canvas(mNextPageBitmap);
		mPageWidget = new PageWidget(this, screenWidth, screenHeight);// 生成页面view
		setContentView(R.layout.activity_readbook);
		RelativeLayout readlayout = (RelativeLayout) findViewById(R.id.readlayout);
		readlayout.addView(mPageWidget);// 加载viewd到当前的布局
		Intent intent = getIntent();
		bookPath = intent.getStringExtra("path");
		mPageWidget.setBitmaps(mCurPageBitmap, mCurPageBitmap);
		mPageWidget.setOnTouchListener(new OnTouchListener() {
			@SuppressWarnings("unused")
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				boolean ret = false;
				if (v == mPageWidget) {// 当前触摸的是该view对象
					if (!show) { // popwind没有显示
						if (e.getAction() == MotionEvent.ACTION_DOWN) {
							startX = e.getX();
							if (startX < (screenWidth / 3)
									|| startX > (screenWidth * 2 / 3)) {
								startX = 0;
								mPageWidget.abortAnimation();
								mPageWidget.calcCornerXY(e.getX(), e.getY());
								pagefactory.onDraw(mCurPageCanvas);
								if (mPageWidget.DragToRight()) {// 上一页
									try {
										pagefactory.prePage();
										begin = pagefactory.getM_mbBufBegin();// 获取当前阅读位置
										word = pagefactory.getFirstLineText();// 获取当前阅读位置的首行文字
									} catch (IOException e1) {
									}
									if (pagefactory.isfirstPage()) {
										Toast.makeText(ReadBookActivity.this,
												"当前是第一页", Toast.LENGTH_SHORT)
												.show();
										return false;
									}
									pagefactory.onDraw(mNextPageCanvas);
								} else {// 下一页
									try {
										pagefactory.nextPage();
										begin = pagefactory.getM_mbBufBegin();// 获取当前阅读位置
										word = pagefactory.getFirstLineText();// 获取当前阅读位置的首行文字
									} catch (IOException e1) {
									}
									if (pagefactory.islastPage()) {
										Toast.makeText(ReadBookActivity.this,
												"已经是最后一页了", Toast.LENGTH_SHORT)
												.show();
										return false;
									}
									pagefactory.onDraw(mNextPageCanvas);
								}
								mPageWidget.setBitmaps(mCurPageBitmap,
										mNextPageBitmap);
							} else {
								showPop();
								startX = 0;
								return false;
							}
						}
						editor.putInt(bookPath + "begin", begin).commit();
						ret = mPageWidget.doTouchEvent(e);
						return ret;
					} else {
						stopPop();
					}
				}
				return false;
			}
		});

		setInitPop();

		// 提取记录在sharedpreferences的各种状态
		sp = getSharedPreferences("bookconfig", MODE_PRIVATE);
		editor = sp.edit();
		getSize();// 获取配置文件中的size大小
		getLight();// 获取配置文件中的light值
		boolean night = sp.getBoolean("night", false);
		// 设置当前页面的的亮度值 0.0-1.0之间的一个float类型数值
		windlp = getWindow().getAttributes();
		windlp.screenBrightness = light / 10.0f < 0.01f ? 0.01f : light / 10.0f;
		getWindow().setAttributes(windlp);
		pagefactory = new BookPageFactory(screenWidth, screenHeight);// 书工厂,加载该书到内存
		if (night) {
			pagefactory.setBgBitmap(BitmapFactory.decodeResource(
					this.getResources(), R.drawable.night_book_bg));
			pagefactory.setM_textColor(Color.rgb(255, 255, 240));
		} else {
			pagefactory.setBgBitmap(BitmapFactory.decodeResource(
					this.getResources(), R.drawable.book_image));
			pagefactory.setM_textColor(Color.rgb(54, 54, 54));// 设置字体
		}
		begin = sp.getInt(bookPath + "begin", 0);
		try {
			pagefactory.openbook(bookPath, begin);
			pagefactory.setM_fontSize(30);// 设置字体
			pagefactory.onDraw(mCurPageCanvas);// 在工厂中对画布进行编辑
			word = pagefactory.getFirstLineText();
			editor.putString("history", bookPath);
			editor.commit();
		} catch (Exception e1) {
			Toast.makeText(this, "打开电子书失败", Toast.LENGTH_SHORT).show();
		}
		bookDb = new BookDb(this);
	}

	/**
	 * 记录配置文件中亮度值和横竖屏
	 */
	private void setLight() {
		try {
			editor.putInt("light", light);
			if (isNight) {
				editor.putBoolean("night", true);
				isNight = false;
			} else {
				isNight = true;
				editor.putBoolean("night", false);
			}
			editor.commit();
		} catch (Exception e) {
			return;
		}
	}

	/**
	 * 初始化所有POPUPWINDOW
	 */
	@SuppressLint({ "InflateParams", "InlinedApi" })
	private void setInitPop() {
		settingPopView = this.getLayoutInflater().inflate(
				R.layout.pop_showseting, null);
		settingPop = new PopupWindow(settingPopView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		topPopView = this.getLayoutInflater().inflate(R.layout.pop_showtop,
				null);
		topPop = new PopupWindow(topPopView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		setLightView = this.getLayoutInflater().inflate(
				R.layout.pop_light_seting, null);
		setLightPop = new PopupWindow(setLightView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		settxtSizeView = this.getLayoutInflater().inflate(
				R.layout.pop_txtsize_seting, null);
		settxtSizePop = new PopupWindow(settxtSizeView,
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	}

	@SuppressLint({ "InlinedApi", "InflateParams" })
	private void showPop() {
		show = true;
		settingPop.showAtLocation(findViewById(R.id.readlayout),
				Gravity.BOTTOM, 0, 0);
		proressbar = (SeekBar) settingPopView
				.findViewById(R.id.pop_progressbar);
		poptext = (TextView) settingPopView.findViewById(R.id.pop_textpress);
		popgroup = (RadioGroup) settingPopView.findViewById(R.id.pop_menu);
		float pregress = begin * 100 / pagefactory.getM_mbBufLen();
		poptext.setText(pregress + "%");
		proressbar.setProgress(begin * 100 / pagefactory.getM_mbBufLen());
		proressbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				poptext.setText(progress + "%");
				begin = (pagefactory.getM_mbBufLen() * progress) / 100;
				editor.putInt(bookPath + "begin", begin).commit();
				pagefactory.setM_mbBufBegin(begin);
				pagefactory.setM_mbBufEnd(begin);
				try {
					if (progress == 100) {
						pagefactory.prePage();
						pagefactory.getM_mbBufBegin();
						begin = pagefactory.getM_mbBufEnd();
						pagefactory.setM_mbBufBegin(begin);
						pagefactory.setM_mbBufBegin(begin);
					}
				} catch (IOException e) {
				}
				proressbar.setFocusable(false);
				proressbar.setClickable(false);
				postInvalidateUI();
			}
		});
		popgroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.note:
					showBookmark();
					RadioButton button = (RadioButton) group
							.findViewById(R.id.note);
					button.setChecked(false);
					break;
				case R.id.right:
					stopPop();
					showLightPop();
					RadioButton lightbutton = (RadioButton) group
							.findViewById(R.id.right);
					lightbutton.setChecked(false);
					break;
				case R.id.rollreading:
					if (rollread) {
						timer.cancel();
						Toast.makeText(ReadBookActivity.this, "已退出滚屏阅读",
								Toast.LENGTH_SHORT).show();

					} else {
						Toast.makeText(ReadBookActivity.this, "滚屏阅读开启",
								Toast.LENGTH_SHORT).show();
						rollRead();

					}
					stopPop();
					RadioButton rollbutton = (RadioButton) group
							.findViewById(R.id.rollreading);
					rollbutton.setChecked(false);
					break;
				case R.id.readset:
					stopPop();
					showTxtSizePop();
					RadioButton readbutton = (RadioButton) group
							.findViewById(R.id.readset);
					readbutton.setChecked(false);
					break;
				default:
					break;
				}

			}
		});
		topPop.showAtLocation(findViewById(R.id.readlayout), Gravity.TOP, 0, 0);
		backButton = (ImageButton) topPopView.findViewById(R.id.pop_back);
		singButton = (ImageButton) topPopView.findViewById(R.id.pop_sing);
		noteButton = (ImageButton) topPopView.findViewById(R.id.pop_note);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ReadBookActivity.this,
						MainActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.tran_pre_in,
						R.anim.tran_pre_out);
				finish();
			}
		});
		noteButton.setOnClickListener(new OnClickListener() {// 添加书签
					@SuppressLint("SimpleDateFormat")
					@Override
					public void onClick(View v) {
						try {
							SQLiteDatabase data = bookDb.getWritableDatabase();
							SimpleDateFormat sf = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm");
							String time = sf.format(new Date());
							Cursor cursor = data.query(Const.DB_TMARK, null,
									"begin=?",
									new String[] { String.valueOf(begin) },
									null, null, null);
							if (cursor.getCount() == 0) {
								data.execSQL(
										"insert into bookmark (path ,begin,word,time) values (?,?,?,?)",
										new String[] { bookPath, begin + "",
												word, time });
							}
							data.close();
							Toast.makeText(ReadBookActivity.this, "添加书签成功",
									Toast.LENGTH_SHORT).show();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							Toast.makeText(ReadBookActivity.this, "添加书签失败",
									Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}

					}
				});
		singButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(ReadBookActivity.this, "开启自动阅读",Toast.LENGTH_SHORT).show();
				Vector<String> spread=pagefactory.getbooktext();
				speech.getSpeech(spread.toString().replace(",",""));
				stopPop();
				readnow=true;
			}
		});
	}

	/**
	 * 阅读设置
	 */
	@SuppressLint("NewApi")
	private void showTxtSizePop() {
		show=false;
		textShow = true;
		settxtSizePop.showAtLocation(findViewById(R.id.readlayout),
				Gravity.BOTTOM, 0, 0);
		sizeTextBar = (SeekBar) settxtSizeView
				.findViewById(R.id.pop_sizeressbar);
		bgcolorgroup = (RadioGroup) settxtSizeView.findViewById(R.id.set_color);
		sizeTextBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				pagefactory.setM_fontSize(progress + 15);
				pagefactory.setM_mbBufBegin(begin);
				pagefactory.setM_mbBufEnd(begin);
				editor.putInt("size", progress + 15);
				editor.commit();
				postInvalidateUI();
			}
		});
		bgcolorgroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				RadioButton imageone = (RadioButton) group
						.findViewById(R.id.imageone);
				RadioButton imagetwo = (RadioButton) group
						.findViewById(R.id.imagetwo);
				RadioButton imagethree = (RadioButton) group
						.findViewById(R.id.imagethree);
				RadioButton imagefour = (RadioButton) group
						.findViewById(R.id.imagefour);
				switch (checkedId) {
				case R.id.imageone:
					pagefactory.setBgBitmap(null);
					imageone.setBackgroundResource(R.drawable.imagrone_press);
					imagetwo.setBackgroundResource(R.drawable.imagetwo);
					imagethree.setBackgroundResource(R.drawable.imagethree);
					imagefour.setBackgroundResource(R.drawable.imagefour);
					pagefactory.setBgcolor(0xffDED6BD);
					postInvalidateUI();
					break;
				case R.id.imagetwo:
					pagefactory.setBgBitmap(null);
					imagetwo.setBackgroundResource(R.drawable.imagetwo_press);
					imageone.setBackgroundResource(R.drawable.imagrone);
					imagethree.setBackgroundResource(R.drawable.imagethree);
					imagefour.setBackgroundResource(R.drawable.imagefour);
					pagefactory.setBgcolor(0xffCEE9F1);
					postInvalidateUI();
					break;
				case R.id.imagethree:
					pagefactory.setBgBitmap(null);
					imagethree.setBackgroundResource(R.drawable.imagethree_press);
					imageone.setBackgroundResource(R.drawable.imagrone);
					imagetwo.setBackgroundResource(R.drawable.imagetwo);
					imagefour.setBackgroundResource(R.drawable.imagefour);
					pagefactory.setBgcolor(0xffF6DADA);
					postInvalidateUI();
					break;
				case R.id.imagefour:
					pagefactory.setBgBitmap(null);
					imagefour.setBackgroundResource(R.drawable.imagrone_press);
					imagethree.setBackgroundResource(R.drawable.imagethree);
					imageone.setBackgroundResource(R.drawable.imagrone);
					imagetwo.setBackgroundResource(R.drawable.imagetwo);
					pagefactory.setBgcolor(0xffE7E7E7);
					postInvalidateUI();
					break;
				default:
					break;
				}
			}
		});

	}

	/**
	 * 亮度设置
	 */
	public void showLightPop() {
		show=false;
		lightShow = true;
		setLightPop.showAtLocation(findViewById(R.id.readlayout),
				Gravity.BOTTOM, 0, 0);
		seeklightbar = (SeekBar) setLightView
				.findViewById(R.id.pop_lightressbar);
		saveeye = (ImageButton) setLightView.findViewById(R.id.saveeye);
		light_setting = (ImageView) setLightView
				.findViewById(R.id.light_setting);
		seeklightbar.setProgress(light);
		boolean night = sp.getBoolean("night", false);
		if (night) {
			saveeye.setBackgroundResource(R.drawable.pop_light_check_press);
			light_setting.setBackgroundResource(R.drawable.night_01);
		} else {
			saveeye.setBackgroundResource(R.drawable.pop_light_check);
			light_setting.setBackgroundResource(R.drawable.day_01);
		}
		saveeye.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isNight) {
					setLight();
					pagefactory.setM_textColor(Color.rgb(255, 255, 240));
					pagefactory.setBgBitmap(BitmapFactory.decodeResource(
							ReadBookActivity.this.getResources(),
							R.drawable.night_book_bg));
					saveeye.setBackgroundResource(R.drawable.pop_light_check_press);
					light_setting.setBackgroundResource(R.drawable.night_01);
					postInvalidateUI();
				} else {
					setLight();
					pagefactory.setM_textColor(Color.rgb(54, 54, 54));
					pagefactory.setBgBitmap(BitmapFactory.decodeResource(
							ReadBookActivity.this.getResources(),
							R.drawable.book_image));
					saveeye.setBackgroundResource(R.drawable.pop_light_check);
					light_setting.setBackgroundResource(R.drawable.day_01);
					postInvalidateUI();
				}
			}
		});
		seeklightbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				light = progress;
				setLight();
				windlp.screenBrightness = light / 10.0f < 0.01f ? 0.01f
						: light / 10.0f;
				getWindow().setAttributes(windlp);
			}
		});
	}

	/**
	 * 开启滚屏
	 */
	private void rollRead() {
		timer = new Timer();
		task = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
			}
		};
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if (msg.what == 1) {
					mPageWidget.abortAnimation();
					mPageWidget.calcCornerXY(screenWidth, screenHeight);
					pagefactory.onDraw(mCurPageCanvas);
					try {
						pagefactory.nextPage();
						begin = pagefactory.getM_mbBufBegin();// 获取当前阅读位置
						word = pagefactory.getFirstLineText();// 获取当前阅读位置的首行文字
						pagefactory.onDraw(mNextPageCanvas);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
					editor.putInt(bookPath + "begin", begin).commit();
					mPageWidget.rollread();
				}
			}
		};
		rollread = true;
		timer.schedule(task, 15000, 15000);
	}

	private void stopPop() {
		show = false;
		settingPop.dismiss();
		topPop.dismiss();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	/**
	 * 刷新界面
	 */
	public void postInvalidateUI() {
		mPageWidget.abortAnimation();
		pagefactory.onDraw(mCurPageCanvas);
		try {
			pagefactory.currentPage();
			begin = pagefactory.getM_mbBufBegin();// 获取当前阅读位置
			word = pagefactory.getFirstLineText();// 获取当前阅读位置的首行文字
		} catch (IOException e1) {

		}
		pagefactory.onDraw(mNextPageCanvas);
		mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
		mPageWidget.postInvalidate();
	}

	/**
	 * 展示书签的dialog
	 */
	private void showBookmark() {
		dialog = new Dialog(this, R.style.dialog);
		dialog.setCanceledOnTouchOutside(false);
		View view = View.inflate(ReadBookActivity.this,
				R.layout.dialog_showbookmark, null);
		String[] name = bookPath.split("/");
		bookname = (TextView) view.findViewById(R.id.bookname);
		bookname.setText(name[name.length - 1].substring(0,
				name[name.length - 1].length() - 4) + "书签");
		bookmarklist = (ListView) view.findViewById(R.id.bookmark);
		bookmarklist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				List<BooKMark> list = adapter.getMarklist();
				pagefactory.setM_mbBufBegin(list.get(position).getBegin());
				pagefactory.setM_mbBufEnd(list.get(position).getBegin());
				postInvalidateUI();
				dialog.dismiss();
				stopPop();
			}
		});
		adapter = new BookMarkAdapter(ReadBookActivity.this, bookPath);
		bookmarklist.setAdapter(adapter);
		dialog.setContentView(view);
		dialog.show();
	}

	class BookMarkBroadReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			adapter.notifyDataSetChanged();// 监听数据改变
		}

	}
	
	class RollRead extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			mPageWidget.abortAnimation();
			mPageWidget.calcCornerXY(screenWidth, screenHeight);
			pagefactory.onDraw(mCurPageCanvas);
			try {
				pagefactory.nextPage();
				begin = pagefactory.getM_mbBufBegin();// 获取当前阅读位置
				word = pagefactory.getFirstLineText();// 获取当前阅读位置的首行文字
				pagefactory.onDraw(mNextPageCanvas);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
			editor.putInt(bookPath + "begin", begin).commit();
			mPageWidget.rollread();
			word = pagefactory.getFirstLineText();
			Vector<String> spread=pagefactory.getbooktext();
			speech.getSpeech(spread.toString().replace(",",""));
		}
		
	}
	/**
	 * 读取配置文件中亮度值
	 */
	private void getLight() {
		light = sp.getInt("light", 5);
		isNight = sp.getBoolean("night", false);
	}

	/**
	 * 读取配置文件中字体大小
	 */
	private void getSize() {
		size = sp.getInt("size", defaultSize);
	}

	/**
	 * 监听回退键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (show) {
				stopPop();
			} else if (lightShow) {
				setLightPop.dismiss();
				lightShow=false;
			} else if (rollread) {
				timer.cancel();
				rollread = false;
				Toast.makeText(ReadBookActivity.this, "已退出滚屏阅读",
						Toast.LENGTH_SHORT).show();
			} else if (textShow) {
				settxtSizePop.dismiss();
				textShow = false;
			}else if(readnow){
				speech.stopSpeechread();
				readnow=false;
				Toast.makeText(ReadBookActivity.this, "已退出阅读模式", Toast.LENGTH_SHORT).show();
			} else {
				this.finish();
			}
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (menupop) {
				menupop = false;
				showPop();
			} else {
				menupop = true;
				stopPop();
			}

		}
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		pagefactory = null;
		mPageWidget = null;
		finish();
	}
}
