package cn.hncj.or.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hncj.activity.R;

/**
 * 自定义选择布局（组合控件）
 * 
 * @author Administrator
 * 
 */
public class SettingItemView extends RelativeLayout {
	private CheckBox cb_status;
	private TextView tv_title;
	private TextView tv_desc;
	private String desc_on;
	private String desc_off;

	/**
	 * 初始化布局文件
	 * 
	 * @param context
	 */
	private void iniView(Context context) {
		// TODO Auto-generated method stub
		// 把一个布局文件加载进来
		View.inflate(context, R.layout.item_setting_view, this);// this指的是父控件
		cb_status = (CheckBox) findViewById(R.id.cb_status);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
	}

	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		iniView(context);
	}

	/**
	 * 带有两个参数的构造方法，布局文件中使用
	 * 
	 * @param context
	 * @param attrs
	 *            《atts是数组的的形式，储存着自定义布局中的描述信息》
	 */
	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		iniView(context);
		// 获得字段信息
		String title = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.hncj.activity",
				"title");
		desc_on = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.hncj.activity",
				"desc_on");
		desc_off = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.hncj.activity",
				"desc_off");
		tv_title.setText(title);
		tv_desc.setText(desc_off);
	}

	public SettingItemView(Context context) {
		super(context);
		iniView(context);
	}

	/**
	 * 校验组合控件是否选中
	 */
	public boolean isChecked() {
		cb_status
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							tv_desc.setText(desc_on);
						} else {
							tv_desc.setText(desc_off);
						}
					}
				});
		return cb_status.isChecked();
	}

	/**
	 * 设置组合控件的状态
	 */
	public void setChecked(boolean check) {
		cb_status.setChecked(check);// true是选中状态
		if (check) {
			tv_desc.setText(desc_on);
		} else {
			tv_desc.setText(desc_off);
		}
	}

	/**
	 * 动态设置选中状态字符
	 */
	public void setDsc(String title) {
		tv_desc.setText(title);
	}
}
