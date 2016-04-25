package cn.hncj.or.activity;

import java.util.HashMap;
import java.util.Map;

import cn.hncj.or.config.Const;
import cn.hncj.or.http.HttptestUtils;

import com.hncj.activity.R;
import com.iflytek.thridparty.e;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UpdateMsgActivity extends BaseActivity {
	private EditText editText;
	private Button button;
	private String flag,email,msg;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_updateusermsg);
		sp = getSharedPreferences("land", MODE_PRIVATE);
		email= sp.getString("email", "");
		editText=(EditText) findViewById(R.id.msg);
		button=(Button) findViewById(R.id.up_button);
		Intent intent=getIntent();
		flag=intent.getStringExtra("flag");
		if(flag.equals("N")){
			editText.setHint("请输入新昵称");
		}else{
			editText.setHint("请输入新密码");
		}
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 msg=editText.getText().toString().trim();
				if(flag.equals("N")){
				  if(!TextUtils.isEmpty(msg)){
					  new UpdataMsg().execute(email,msg,"n");
				  }else{
					  Toast.makeText(UpdateMsgActivity.this, "昵称不能为空",
								Toast.LENGTH_SHORT).show();
				  }
				}else{
					 if(!TextUtils.isEmpty(msg)){
						  new UpdataMsg().execute(email,msg,"p");
					  }else{
						  Toast.makeText(UpdateMsgActivity.this, "密码不能为空",
									Toast.LENGTH_SHORT).show();
					  }
				}
			}
		});
	}
	class UpdataMsg extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stubd
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			@SuppressWarnings("unchecked")
			Map<String, String> map = new HashMap<String, String>();
			map.put("email", params[0]);
			map.put("num", params[1]);
			map.put("type", params[2]);
			String result = HttptestUtils.submitPostData(map, "UTF-8",
					Const.UPDEUSERPath);
			return result;
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			if (result.equals("E")) {
				if(flag.equals("N")){
					Toast.makeText(UpdateMsgActivity.this, "服务器异常，昵称更新失败",
							Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(UpdateMsgActivity.this, "服务器异常，密码更新失败",
							Toast.LENGTH_SHORT).show();
				}
				
			} else {
				if(flag.equals("N")){
				Editor editor=sp.edit();
				editor.putString("name", msg);
				editor.commit();
				}
				Intent intent = new Intent(UpdateMsgActivity.this,
						MyCenterActiivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
				finish();
			}
		}
	}

}
