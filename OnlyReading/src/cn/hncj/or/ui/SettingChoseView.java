package cn.hncj.or.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hncj.activity.R;

/**
 * 自定义选择布局（组合控件）
 * 
 * @author Administrator
 * 
 */
public class SettingChoseView extends RelativeLayout {
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
		View.inflate(context, R.layout.item_setting_show_toast, this);// this指的是父控件
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
	}

	public SettingChoseView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		iniView(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public SettingChoseView(Context context, AttributeSet attrs) {
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
		tv_title.setTextSize(18);
		tv_title.setText(title);
		tv_desc.setText(desc_off);
	}

	public SettingChoseView(Context context) {
		super(context);
		iniView(context);
	}

	public void setColor(String name) {
			tv_desc.setText(name);
	}
	/**
	 * 动态设置选中状态字符
	 */
	public void setDsc(String title) {
		tv_desc.setText(title);
	}
}
