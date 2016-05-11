package cn.hncj.or.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.hncj.or.config.Const;
import cn.hncj.or.db.Book;
import cn.hncj.or.http.DownBookHttpUtils;
import cn.hncj.or.http.HttptestUtils;
import cn.hncj.or.utils.JsonUtils;

import com.hncj.activity.R;

public class CollecActivity extends BaseActivity {
	private GridView gridView;
	private ImageButton button;
	private SharedPreferences sp;
	private Myadapter adapter;
	private List<Book> list;
	private String uid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collection);
		sp = getSharedPreferences("land", MODE_PRIVATE);
		uid = sp.getString("email", "");
		list = new ArrayList<Book>();
		new loginServer().execute(uid,"flag");
		gridView = (GridView) findViewById(R.id.collec_gridview);
		button = (ImageButton) findViewById(R.id.back_home);

		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CollecActivity.this,
						MainActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
				finish();
			}
		});
		adapter = new Myadapter();
		gridView.setAdapter(adapter);

	}

	private class Myadapter extends BaseAdapter {
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
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
			// TODO Auto-generated method stub
			View view;
			if (convertView == null) {
				view = View.inflate(CollecActivity.this,
						R.layout.item_collect_book, null);// 加载自定义布局
			} else {
				view = convertView;
			}
			final int id = position;
			TextView nametext = (TextView) view.findViewById(R.id.book_name);
			final ProgressBar bar = (ProgressBar) view.findViewById(R.id.down_book);
			Button delbutton = (Button) view.findViewById(R.id.delect);
			final Button downbutton = (Button) view.findViewById(R.id.down);
			nametext.setText(list.get(id).getBookName());
			delbutton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					new loginServer().execute(new String[] { uid,
							String.valueOf(list.get(id).getId()) });
				}
			});
			downbutton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					DownBookHttpUtils.downloadbook(bar, CollecActivity.this, String.valueOf(list.get(id).getId()), downbutton, list.get(id).getBookName());
				}
			});
			return view;
		}

	}

	class loginServer extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String msg;
			Map<String, String> map = new HashMap<String, String>();
			map.put("num", params[0]);
			if (params[1].equals("flag")) {
				msg = HttptestUtils.submitPostData(map, "utf-8",
						Const.COLLEBOOKLISTPATH);
			} else {
				map.put("num1", params[1]);
				msg = HttptestUtils.submitPostData(map, "utf-8",
						Const.DELCOLLEBOOKLISTPATH);
			}
			return msg;
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			list = JsonUtils.JsonTools(result);
			if (result.equals("S")) {
				new loginServer().execute(uid,"flag");
				Toast.makeText(CollecActivity.this, "删除成功", Toast.LENGTH_SHORT)
						.show();
				adapter.notifyDataSetChanged();
			} else if (result.equals("E")) {
				Toast.makeText(CollecActivity.this, "删除失败", Toast.LENGTH_SHORT)
						.show();
			} else {
				if (list.size() == 0) {
					Toast.makeText(CollecActivity.this, "暂无收藏",
							Toast.LENGTH_SHORT).show();
				} else {
					adapter.notifyDataSetChanged();
				}
			}
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
			Intent intent = new Intent(CollecActivity.this,
					MainActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
