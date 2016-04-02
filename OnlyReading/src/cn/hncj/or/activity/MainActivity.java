package cn.hncj.or.activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import cn.hncj.or.ui.CircleImageView;
import cn.hncj.or.ui.SlideMenu;
import com.hncj.activity.R;
import com.hncj.or.frament.FragmentBookhistory;
import com.hncj.or.frament.FragmentBookstack;
import com.hncj.or.frament.FragmentBookstore;

public class MainActivity extends FragmentActivity implements OnClickListener {
	private SlideMenu slideMenu;
	private RadioGroup group;
	private GridView menugridView;
	private CircleImageView cricleview;
	private FragmentBookstack bookcase;
	private FragmentBookstore bookstory;
	private FragmentBookhistory history;
	private Myadpter adapter;
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
	}
/**
 * 菜单适配
 */
	class Myadpter extends BaseAdapter{
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
			View view=View.inflate(MainActivity.this, R.layout.list_item_menu, null);
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
	 * 加载Fragment布局
	 */
	public void initView() {
		bookcase = new FragmentBookstack();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.main_content, bookcase).commit();
		group = (RadioGroup) findViewById(R.id.tab_menu);
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.book_jia:
					bookcase = new FragmentBookstack();
					getSupportFragmentManager().beginTransaction()
							.replace(R.id.main_content, bookcase).commit();
					break;
				case R.id.book_cheng:
					if (bookstory == null) {
						bookstory = new FragmentBookstore();
					}
					getSupportFragmentManager().beginTransaction()
							.replace(R.id.main_content, bookstory).commit();
					break;
				case R.id.book_history:
					history = new FragmentBookhistory();
					getSupportFragmentManager().beginTransaction()
							.replace(R.id.main_content, history).commit();
					break;
				default:
					break;
				}

			}
		});
	}

	
}
