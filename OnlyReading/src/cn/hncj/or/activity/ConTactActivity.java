package cn.hncj.or.activity;

import java.nio.channels.FileChannel.MapMode;
import java.util.HashMap;
import java.util.Map;

import cn.hncj.or.config.Const;
import cn.hncj.or.http.HttptestUtils;

import com.hncj.activity.R;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ConTactActivity extends BaseActivity {
	private TextView submit;
	private EditText content, address;
	private ImageButton button;
	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);
		submit = (TextView) findViewById(R.id.submit);
		content = (EditText) findViewById(R.id.content);
		address = (EditText) findViewById(R.id.address);
		button = (ImageButton) findViewById(R.id.back_home);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ConTactActivity.this,
						AboutMsgActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.tran_pre_in,
						R.anim.tran_pre_out);
				finish();
			}
		});
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String con = content.getText().toString().trim();
				String add = address.getText().toString().trim();
				if (!TextUtils.isEmpty(con) && !TextUtils.isEmpty(add)) {
					new MySubmit().execute(con,add);
					showdialog();
				}
			}
		});
		address.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (!TextUtils.isEmpty(content.getText().toString().trim())
						&& !TextUtils.isEmpty(address.getText().toString()
								.trim())) {
					submit.setTextColor(Color.WHITE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (TextUtils.isEmpty(content.getText().toString().trim())) {
					submit.setTextColor(Color.GRAY);
				}
			}
		});
	}
	public void showdialog() {
		dialog = new ProgressDialog(this);
	    dialog.setCancelable(false);
	    dialog.show();
	    dialog.setContentView(R.layout.mydialog);
		dialog.setCanceledOnTouchOutside(false);
	}
	class MySubmit  extends AsyncTask<String, Void, String>{
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			Map<String, String> map=new HashMap<String, String>();
			map.put("cont", params[0]);
			map.put("add", params[1]);
			String result=HttptestUtils.submitPostData(map, "UTF-8", Const.SUBMITMSGPATH);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			
			if(result.equals("S")){
				dialog.dismiss();
				content.setText("");
				address.setText("");
				submit.setTextColor(Color.GRAY);
				Toast.makeText(ConTactActivity.this,"反馈成功，感谢你的意见", Toast.LENGTH_SHORT).show();
			}else{
				dialog.dismiss();
				Toast.makeText(ConTactActivity.this,"网络异常", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
