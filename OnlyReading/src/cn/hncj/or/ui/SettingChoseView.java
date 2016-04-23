package cn.hncj.or.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hncj.activity.R;

/**
 * �Զ���ѡ�񲼾֣���Ͽؼ���
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
	 * ��ʼ�������ļ�
	 * 
	 * @param context
	 */
	private void iniView(Context context) {
		// TODO Auto-generated method stub
		// ��һ�������ļ����ؽ���
		View.inflate(context, R.layout.item_setting_show_toast, this);// thisָ���Ǹ��ؼ�
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
		// ����ֶ���Ϣ
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
	 * ��̬����ѡ��״̬�ַ�
	 */
	public void setDsc(String title) {
		tv_desc.setText(title);
	}
}
