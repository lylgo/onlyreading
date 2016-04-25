package cn.hncj.or.activity;

import com.hncj.activity.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

public class AboutMsgActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aboutmsg);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(AboutMsgActivity.this,
					MainActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
