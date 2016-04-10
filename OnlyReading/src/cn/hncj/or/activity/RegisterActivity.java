package cn.hncj.or.activity;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hncj.or.http.HttpUtils;

import com.hncj.activity.R;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends BaseActivity {
	private ImageButton backbutton;
	private EditText ninametext, passtext, repasstext, emailtext;
	private Button rebutton;
	private String name;
	private String pass;
	private String repass;
	private String email;
	private ProgressDialog dialog;
	private Map<String, String> map;
    private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		backbutton = (ImageButton) findViewById(R.id.back_home);
		ninametext = (EditText) findViewById(R.id.niname);
		passtext = (EditText) findViewById(R.id.pass);
		repasstext = (EditText) findViewById(R.id.repass);
		emailtext = (EditText) findViewById(R.id.email);
		rebutton = (Button) findViewById(R.id.re_button);
		backbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(RegisterActivity.this,
						MainActivity.class);
				startActivity(intent);
			}
		});
		rebutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean flag = check();
				if (flag) {
					map = new HashMap<String, String>();
					map.put("name", name);
					map.put("pass", pass);
					map.put("email", email);
					new regituser().execute(map);
				}
			}
		});
	}

	public boolean check() {
		name = ninametext.getText().toString().trim();
		pass = passtext.getText().toString().trim();
		repass = repasstext.getText().toString().trim();
		email = emailtext.getText().toString().trim();
		if (name.isEmpty()) {
			Toast.makeText(this, "�ǳƲ���Ϊ��", Toast.LENGTH_SHORT).show();
			return false;
		} else if (pass.isEmpty() || repass.isEmpty()) {
			Toast.makeText(this, "���벻��Ϊ��", Toast.LENGTH_SHORT).show();
		} else if (!TextUtils.equals(pass, repass)) {
			Toast.makeText(this, "���벻һ�£����������", Toast.LENGTH_SHORT).show();
			passtext.setText("");
			repasstext.setText("");
			return false;
		} else if (email.isEmpty()) {
			Toast.makeText(this, "���䲻��Ϊ��", Toast.LENGTH_SHORT).show();
			return false;
		} else if(!checkEmail(email)){
				Toast.makeText(this, "�����ʽ����ȷ������������", Toast.LENGTH_SHORT)
						.show();
				return false;
		}else{
			return true;
		}
		return false;
	}

	public void showdig() {
		dialog = new ProgressDialog(this);
		// dialog.setCancelable(false);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	// **
	class regituser extends AsyncTask<Object, Void, String> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stubd
			showdig();
		}
		@Override
		protected String doInBackground(Object... params) {
			// TODO Auto-generated method stub
			@SuppressWarnings("unchecked")
			Map<String, String> map = (Map<String, String>) params[0];
			String result = HttpUtils.submitPostData(map, "UTF-8");
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			dialog.dismiss();
			if (result.equals("SU")) {
				sp=getSharedPreferences("land",MODE_PRIVATE );
				Editor editor=sp.edit();
				editor.putString("name", name);
				SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				String date = sDateFormat.format(new java.util.Date());
				editor.putString("date", date);
				editor.commit();
				Intent intent = new Intent(RegisterActivity.this,
						MainActivity.class);
				startActivity(intent);
			}else if(result.equals("XM")){
				Toast.makeText(RegisterActivity.this, "��������ע��",
						Toast.LENGTH_SHORT).show();
			} else if (result.equals("ER")) {
				Toast.makeText(RegisterActivity.this, "ע��ʧ��",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(RegisterActivity.this, "�������쳣�����Ժ�����",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	// ������֤
	public static boolean checkEmail(String email) {
		boolean flag = false;
		try {
			String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
}
