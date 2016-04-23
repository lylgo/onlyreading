package cn.hncj.or.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import cn.hncj.or.ui.SettingChoseView;
import cn.hncj.or.ui.SettingItemView;

import com.hncj.activity.R;

public class SettingActivity extends BaseActivity implements OnClickListener {
	private SettingItemView setligemodel;
	private SettingChoseView setsingmodel,settxtmodel;
	private SharedPreferences sp;
	private Editor editor;
	private ImageButton button;
	private Dialog dialog;
	private ListView singlist;
	private List<String> data,txtData;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acticvity_setting);
		sp = getSharedPreferences("bookconfig", MODE_PRIVATE);
		getData();
		getTxtType();
		editor = sp.edit();
		int id = sp.getInt("sing", 0);
		int txtid=sp.getInt("txttype", 0);
		setligemodel = (SettingItemView) findViewById(R.id.set_light_model);
		setsingmodel = (SettingChoseView) findViewById(R.id.set_sing_model);
		settxtmodel=(SettingChoseView) findViewById(R.id.set_txt_model);
		setsingmodel.setColor(data.get(id));
		settxtmodel.setColor(txtData.get(txtid));
		button = (ImageButton) findViewById(R.id.back_home);
		boolean flas = sp.getBoolean("night", false);
		setligemodel.setChecked(flas);
		setligemodel.setOnClickListener(this);
		button.setOnClickListener(this);
		setsingmodel.setOnClickListener(this);
		settxtmodel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.set_light_model:
			if (setligemodel.isChecked()) {
				setligemodel.setChecked(false);
				editor.putBoolean("night", false);
				editor.commit();
			} else {
				setligemodel.setChecked(true);
				editor.putBoolean("night", true);
				editor.commit();
			}
			break;
		case R.id.back_home:
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
			finish();
			break;
		case R.id.set_sing_model:
			showSetSing();
			break;
		case R.id.set_txt_model:
			showSetTxt();
			break;
		default:
			break;
		}
	}

	/**
	 * ����������
	 */
	private void showSetSing() {
		dialog = new Dialog(this, R.style.dialog);
		dialog.setCanceledOnTouchOutside(false);
		View view = View.inflate(this, R.layout.dialog_set_sing, null);
		singlist = (ListView) view.findViewById(R.id.singlist);
		singlist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
              editor.putInt("sing", position);
              editor.commit();
              setsingmodel.setColor(data.get(position));
              dialog.dismiss();
			}
		});
		singlist.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,getData()));
		dialog.setContentView(view);
		dialog.show();
	}
	/**
	 * ��������
	 */
	private void showSetTxt() {
		dialog = new Dialog(this, R.style.dialog);
		dialog.setCanceledOnTouchOutside(false);
		View view = View.inflate(this, R.layout.dialog_set_sing, null);
		TextView title=(TextView) view.findViewById(R.id.diog_title);
		title.setText("��ѡ���Ķ�����");
		singlist = (ListView) view.findViewById(R.id.singlist);
		singlist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
              editor.putInt("txttype", position);
              editor.commit();
              settxtmodel.setColor(txtData.get(position));
              dialog.dismiss();
			}
		});
		singlist.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,getTxtType()));
		dialog.setContentView(view);
		dialog.show();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private List<String> getData() {
		data = new ArrayList<String>();
		data.add("С��-Ů-��ͨ��");
		data.add("С��-��-��ͨ��");
		data.add("С÷-Ů-����");
		data.add("С��-Ů-�Ĵ���");
		data.add("Сܿ-Ů-������");
		data.add("С��-����-���ϻ�");
		data.add("Сǿ����-���ϻ�");
		data.add("�骡�Ůͯ-��ͨ��");
		return data;
	}
	private List<String> getTxtType(){
		txtData=new ArrayList<String>();
		txtData.add("ϵͳĬ��");
		txtData.add("��������");
		txtData.add("����");
		txtData.add("��Բ����");
		return txtData;
	}
}
