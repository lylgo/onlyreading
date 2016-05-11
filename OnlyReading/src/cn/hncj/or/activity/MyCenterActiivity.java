package cn.hncj.or.activity;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import cn.hncj.or.config.Const;
import cn.hncj.or.http.HttptestUtils;

import com.hncj.activity.R;
import com.iflytek.thridparty.ba;
import com.iflytek.thridparty.e;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("unused")
public class MyCenterActiivity extends BaseActivity implements OnClickListener {
	private TextView emailtext, nametext, passtext;
	private Button button;
	private ImageButton backname;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mycenter);
		sp = getSharedPreferences("land", MODE_PRIVATE);
		String email = sp.getString("email", "");
		new GetuserMsg().execute(email);
		nametext = (TextView) findViewById(R.id.name);
		passtext = (TextView) findViewById(R.id.pass);
		button = (Button) findViewById(R.id.re_button);
		backname = (ImageButton) findViewById(R.id.back_home);
		emailtext = (TextView) findViewById(R.id.email);
		if (!email.equals("")) {
			emailtext.setText(email);
		}
		button.setOnClickListener(this);
		backname.setOnClickListener(this);
		nametext.setOnClickListener(this);
		passtext.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.name:
			Intent intentn = new Intent(MyCenterActiivity.this,
					UpdateMsgActivity.class);
			intentn.putExtra("flag", "N");
			startActivity(intentn);
			overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
			break;
		case R.id.pass:
			Intent intentp = new Intent(MyCenterActiivity.this,
					UpdateMsgActivity.class);
			intentp.putExtra("flag", "P");
			startActivity(intentp);
			overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
			break;
		case R.id.back_home:
			Intent intent = new Intent(MyCenterActiivity.this,
					MainActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
			finish();
			break;
		case R.id.re_button:
			Editor editor = sp.edit();
			editor.putString("email", "");
			editor.putString("name", "尚未登录");
			editor.commit();
			Intent intentback = new Intent(MyCenterActiivity.this,
					MainActivity.class);
			startActivity(intentback);
			overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
			finish();
			break;
		default:
			break;
		}
	}

	class GetuserMsg extends AsyncTask<String, Void, String> {
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
			String result = HttptestUtils.submitPostData(map, "UTF-8",
					Const.GETMSGPath);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			if (result.equals("E")) {
				Toast.makeText(MyCenterActiivity.this, "服务器异常，获取信息失败",
						Toast.LENGTH_SHORT).show();
			} else {
				if(result.equals("")){
					Toast.makeText(MyCenterActiivity.this, "服务器异常，获取信息失败",
							Toast.LENGTH_SHORT).show();
				}else{
					String resu[] = result.split("#");
					nametext.setText(resu[0]);
					passtext.setText(resu[1]);
				}
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(MyCenterActiivity.this,
					MainActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
