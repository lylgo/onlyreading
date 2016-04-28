package cn.hncj.or.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zrc.widget.SimpleFooter;
import zrc.widget.SimpleHeader;
import zrc.widget.ZrcListView;
import zrc.widget.ZrcListView.OnStartListener;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.hncj.or.config.Const;
import cn.hncj.or.db.Book;
import cn.hncj.or.http.DownBookHttpUtils;
import cn.hncj.or.http.HttptestUtils;
import cn.hncj.or.utils.JsonUtils;

import com.hncj.activity.R;

@SuppressLint("ViewHolder")
public class BookstoreActivity extends BaseActivity {
	private ViewPager viewPager;// ҳ������
	private ImageView imageView;// ����ͼƬ
	private TextView textView1, textView2;
	private List<View> views;// Tabҳ���б�
	private int offset = 0;// ����ͼƬƫ����
	private int currIndex = 0;// ��ǰҳ�����
	private int bmpW;// ����ͼƬ���
	private View view1, view2;// ����ҳ��
	private ZrcListView listone, listtwo;
	private Handler handler;
	private MyAdapter adapter;
	private MyAdaptertwo adaptertwo;
	private List<Book> books = new ArrayList<Book>();
	private List<Book> bookone = new ArrayList<Book>();
	private int cont = 2;// ����ҳ��
	private String Tonumberpage;// ��ҳ��
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bookstore);
		new myastybooklist().execute(new String[] { "1", "2", "page" });
		sp = getSharedPreferences("land", MODE_PRIVATE);
		InitImageView();
		InitTextView();
		InitViewPager();
		Intent intent = getIntent();
		int page = intent.getIntExtra("page", 0);
		viewPager.setCurrentItem(page);
		listone = (ZrcListView) view1.findViewById(R.id.zListone);
		listtwo = (ZrcListView) view2.findViewById(R.id.zListtwo);
		handler = new Handler();
		// ��������ˢ�µ���ʽ����ѡ�������û��Header���޷�����ˢ�£�
		SimpleHeader header = new SimpleHeader(this);
		header.setTextColor(0xff0066aa);
		header.setCircleColor(0xff33bbee);
		listone.setHeadable(header);
		listtwo.setHeadable(header);
		// ���ü��ظ������ʽ����ѡ��
		SimpleFooter footer = new SimpleFooter(this);
		footer.setCircleColor(0xff33bbee);
		listone.setFootable(footer);
		listtwo.setFootable(footer);
		// �����б�����ֶ�������ѡ��
		listone.setItemAnimForTopIn(R.anim.topitem_in);
		listone.setItemAnimForBottomIn(R.anim.bottomitem_in);
		listtwo.setItemAnimForTopIn(R.anim.topitem_in);
		listtwo.setItemAnimForBottomIn(R.anim.bottomitem_in);
		// ����ˢ���¼��ص�����ѡ��
//		listone.setOnRefreshStartListener(new OnStartListener() {
//			@Override
//			public void onStart() {
//				new myastybooklist().execute(String.valueOf(1));
//				refresh();
//			}
//		});
//		// ���ظ����¼��ص�����ѡ��
//		listone.setOnLoadMoreStartListener(new OnStartListener() {
//			@Override
//			public void onStart() {
//				new myastybooklist().execute(String.valueOf(cont));
//				loadMore();
//			}
//		});
		adapter = new MyAdapter();
		listone.setAdapter(adapter);
	}

	private void refresh() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (cont <= Integer.valueOf(Tonumberpage)) {
					// cont++;
					// adapter.notifyDataSetChanged();
					// listone.setRefreshSuccess("���سɹ�"); // ֪ͨ���سɹ�
					// //listone.startLoadMore();
				} else {
					listone.stopLoadMore();
					listone.setRefreshFail("����ʧ��");
				}
			}
		}, 2 * 1000);
	}

	private void loadMore() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				cont++;
				if (cont <= Integer.valueOf(Tonumberpage)) {
					adapter.notifyDataSetChanged();
					listone.setLoadMoreSuccess();
				} else {
					listone.stopLoadMore();
				}
			}
		}, 2 * 1000);
	}

	private class MyAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return books == null ? 0 : books.size();
		}

		@Override
		public Object getItem(int position) {
			return books.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			final ViewHoder viewhoder;
			if (convertView == null) {
				view = getLayoutInflater().inflate(R.layout.showdownbook, null);
				viewhoder = new ViewHoder();
				viewhoder.nametext = (TextView) view
						.findViewById(R.id.bookname);
				viewhoder.typetext = (TextView) view
						.findViewById(R.id.typebook);
				viewhoder.destext = (TextView) view.findViewById(R.id.desbook);
				viewhoder.downbutton = (Button) view
						.findViewById(R.id.down_button);
				viewhoder.progress = (ProgressBar) view
						.findViewById(R.id.downbar);
				view.setTag(viewhoder);
			} else {
				view = convertView;
				viewhoder = (ViewHoder) view.getTag();
			}
			viewhoder.nametext.setText(books.get(position).getBookName());
			viewhoder.typetext.setText(books.get(position).getBookType());
			viewhoder.destext.setText("\t\t"
					+ books.get(position).getDescribe());
			final int progre = position;
			viewhoder.downbutton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					DownBookHttpUtils.downloadbook(viewhoder.progress,
							BookstoreActivity.this, String.valueOf(books.get(
									progre).getId()), viewhoder.downbutton,
							books.get(progre).getBookName());
				}
			});
			return view;
		}

		class ViewHoder {
			TextView nametext, typetext, destext;
			Button downbutton;
			ProgressBar progress;
		}

	}

	/**
	 * �ڶ���ҳ������������
	 * 
	 * @author Administrator
	 */
	private class MyAdaptertwo extends BaseAdapter {
		@Override
		public int getCount() {
			return bookone == null ? 0 : bookone.size();
		}

		@Override
		public Object getItem(int position) {
			return bookone.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			final ViewHoder viewhoder;
			if (convertView == null) {
				view = getLayoutInflater().inflate(R.layout.vipshowdownbook,
						null);
				viewhoder = new ViewHoder();
				viewhoder.nametext = (TextView) view
						.findViewById(R.id.bookname);
				viewhoder.typetext = (TextView) view
						.findViewById(R.id.typebook);
				viewhoder.destext = (TextView) view.findViewById(R.id.desbook);
				viewhoder.downbutton = (Button) view
						.findViewById(R.id.down_button);
				viewhoder.progress = (ProgressBar) view
						.findViewById(R.id.downbar);
				viewhoder.collecbutton = (Button) view
						.findViewById(R.id.collect_button);
				view.setTag(viewhoder);
			} else {
				view = convertView;
				viewhoder = (ViewHoder) view.getTag();
			}
			viewhoder.nametext.setText(bookone.get(position).getBookName());
			viewhoder.typetext.setText(bookone.get(position).getBookType());
			viewhoder.destext.setText("\t\t"
					+ bookone.get(position).getDescribe());
			final int progre = position;
			viewhoder.downbutton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (sp.getString("email", "").equals("")) {
						Toast.makeText(BookstoreActivity.this, "�Բ�������δ��½",
								Toast.LENGTH_SHORT).show();
					} else {
						DownBookHttpUtils.downloadbook(viewhoder.progress,
								BookstoreActivity.this, String.valueOf(bookone
										.get(progre).getId()),
								viewhoder.downbutton, bookone.get(progre)
										.getBookName());
					}
				}
			});
			viewhoder.collecbutton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (sp.getString("email", "").equals("")) {
						Toast.makeText(BookstoreActivity.this, "�Բ�������δ��½",
								Toast.LENGTH_SHORT).show();
					} else {
						String email = sp.getString("email", "");
						new myastybooklist().execute(new String[] { email,
								String.valueOf(bookone.get(progre).getId()),
								"coll" });
					}
				}
			});
			return view;
		}

		class ViewHoder {
			TextView nametext, typetext, destext;
			Button downbutton, collecbutton;
			ProgressBar progress;

		}

	}

	class myastybooklist extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			Map<String, String> map = new HashMap<String, String>();
			map.put("num", params[0]);
			map.put("type", params[1]);
			map.put("net", params[2]);
			String result = null;
			if (map.get("net").equals("page")) {
				result = HttptestUtils.submitPostData(map, "UTF-8",
						Const.GETBOOKLISTPATH);
			} else {
				result = HttptestUtils.submitPostData(map, "UTF-8",
						Const.COLLEBOOKPATH);
			}
			if (cont == 2) {
				Map<String, String> hashmap = new HashMap<String, String>();
				Tonumberpage = HttptestUtils.submitPostData(hashmap, "UTF-8",
						Const.PAGENUMBERPATH);
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equals("S")) {
				Toast.makeText(BookstoreActivity.this, "�ղسɹ�",
						Toast.LENGTH_SHORT).show();
			} else if (result.equals("E")) {
				Toast.makeText(BookstoreActivity.this, "�ղ�ʧ��",
						Toast.LENGTH_SHORT).show();
			} else {
				if (viewPager.getCurrentItem() == 0) {
					List<Book> rebook = JsonUtils.JsonTools(result);
					for (Book book : rebook)
						books.add(book);
					adapter = new MyAdapter();
					rebook.clear();
					listone.setAdapter(adapter);
				} else {
					bookone = JsonUtils.JsonTools(result);
					adaptertwo = new MyAdaptertwo();
					listtwo.setAdapter(adaptertwo);
				}
			}
		}
	}

	/**
	 * һ������ViewPagef
	 */
	@SuppressLint("InflateParams")
	private void InitViewPager() {
		viewPager = (ViewPager) findViewById(R.id.vPager);
		views = new ArrayList<View>();
		LayoutInflater inflater = getLayoutInflater();
		view1 = inflater.inflate(R.layout.viewpageone, null);
		view2 = inflater.inflate(R.layout.viewpagetwo, null);
		views.add(view1);
		views.add(view2);
		viewPager.setAdapter(new MyViewPagerAdapter(views));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	/**
	 * ��ʼ��ͷ��
	 */

	private void InitTextView() {
		textView1 = (TextView) findViewById(R.id.text1);
		textView2 = (TextView) findViewById(R.id.text2);
		textView1.setOnClickListener(new MyOnClickListener(0));
		textView2.setOnClickListener(new MyOnClickListener(1));
	}

	/**
	 * 2 * ��ʼ������ 3
	 */

	private void InitImageView() {
		imageView = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.bar)
				.getWidth();// ��ȡͼƬ���
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// ��ȡ�ֱ��ʿ��
		offset = (screenW / 2 - bmpW) / 2;// ����ƫ����
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		imageView.setImageMatrix(matrix);// ���ö�����ʼλ��
	}

	/**
	 * 
	 * ͷ�������� 3
	 */
	private class MyOnClickListener implements OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		public void onClick(View v) {
			viewPager.setCurrentItem(index);
		}
	}

	/**
	 * viewPager�������
	 * 
	 * @author Administrator
	 * 
	 */
	public class MyViewPagerAdapter extends PagerAdapter {
		private List<View> mListViews;

		public MyViewPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mListViews.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(mListViews.get(position), 0);
			return mListViews.get(position);
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {
		int one = offset * 2 + bmpW;// ҳ��1 -> ҳ��2 ƫ����

		public void onPageScrollStateChanged(int arg0) {
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		public void onPageSelected(int arg0) {
			Animation animation = new TranslateAnimation(one * currIndex, one
					* arg0, 0, 0);
			currIndex = arg0;
			animation.setFillAfter(true);// True:ͼƬͣ�ڶ�������λ��
			animation.setDuration(300);
			imageView.startAnimation(animation);
			if (viewPager.getCurrentItem() == 1) {
				new myastybooklist().execute(new String[] { "1", "1", "page" });
			}else{
				if(books.size()==0){
					new myastybooklist().execute(new String[] { "1", "2", "page" });
				}
			}
		}
	}
}