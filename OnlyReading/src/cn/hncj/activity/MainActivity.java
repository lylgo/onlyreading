package cn.hncj.activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import cn.hncj.fragment.FragmentBookcase;
import cn.hncj.fragment.FragmentBookstory;
import cn.hncj.fragment.FragmentHistory;
import cn.hncj.ui.SlideMenu;
import com.hncj.activity.R;
public class MainActivity extends FragmentActivity implements OnClickListener{
	private SlideMenu slideMenu;
	private RadioGroup group;
	private FragmentBookcase bookcase;
	private FragmentBookstory bookstory;
	private FragmentHistory history;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		slideMenu = (SlideMenu) findViewById(R.id.slide_menu);
		ImageView menuImg = (ImageView) findViewById(R.id.title_bar_menu_btn);
		menuImg.setOnClickListener(this);
		initView();
	}
	/**
	 * 加载侧边菜单
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_bar_menu_btn:
			if (slideMenu.isMainScreenShowing()){
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
		 bookcase= new FragmentBookcase();
		getSupportFragmentManager().beginTransaction().replace(R.id.main_content, bookcase).commit();
		group = (RadioGroup) findViewById(R.id.tab_menu);
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.book_jia:
					bookcase = new FragmentBookcase();
					getSupportFragmentManager().beginTransaction().replace(R.id.main_content, bookcase)
							.commit();
					break;
				case R.id.book_cheng:
					if (bookstory==null) {
						bookstory =new FragmentBookstory();
					}
					getSupportFragmentManager().beginTransaction().replace(R.id.main_content, bookstory).commit();
					break;
				case R.id.book_history:
					history = new FragmentHistory();
					getSupportFragmentManager().beginTransaction().replace(R.id.main_content, history)
							.commit();
					break;
				default:
					break;
				}

			}
		});
	}
}
