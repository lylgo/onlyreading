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
 * �Զ���ѡ�񲼾֣���Ͽؼ���
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
	 * ��ʼ�������ļ�
	 * 
	 * @param context
	 */
	private void iniView(Context context) {
		// TODO Auto-generated method stub
		// ��һ�������ļ����ؽ���
		View.inflate(context, R.layout.item_setting_view, this);// thisָ���Ǹ��ؼ�
		cb_status = (CheckBox) findViewById(R.id.cb_status);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
	}

	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		iniView(context);
	}

	/**
	 * �������������Ĺ��췽���������ļ���ʹ��
	 * 
	 * @param context
	 * @param attrs
	 *            ��atts������ĵ���ʽ���������Զ��岼���е�������Ϣ��
	 */
	public SettingItemView(Context context, AttributeSet attrs) {
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
		tv_title.setText(title);
		tv_desc.setText(desc_off);
	}

	public SettingItemView(Context context) {
		super(context);
		iniView(context);
	}

	/**
	 * У����Ͽؼ��Ƿ�ѡ��
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
	 * ������Ͽؼ���״̬
	 */
	public void setChecked(boolean check) {
		cb_status.setChecked(check);// true��ѡ��״̬
		if (check) {
			tv_desc.setText(desc_on);
		} else {
			tv_desc.setText(desc_off);
		}
	}

	/**
	 * ��̬����ѡ��״̬�ַ�
	 */
	public void setDsc(String title) {
		tv_desc.setText(title);
	}
}
