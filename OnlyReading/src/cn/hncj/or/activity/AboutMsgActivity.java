package cn.hncj.or.activity;

import java.io.File;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.hncj.or.config.Const;
import cn.hncj.or.http.HttptestUtils;

import com.hncj.activity.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class AboutMsgActivity extends BaseActivity{
	private TextView vision;
	private LinearLayout tetcontact,buttonflag;
	private SharedPreferences sp;
	private String visionname;
	private Button button;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aboutmsg);
		sp = getSharedPreferences("vision", MODE_PRIVATE);
		visionname=sp.getString("vision", "");
		tetcontact = (LinearLayout) findViewById(R.id.contentme);
		vision=(TextView) findViewById(R.id.vision);
		vision.setText(getVersionName());
		button=(Button) findViewById(R.id.flag_button);
		buttonflag = (LinearLayout) findViewById(R.id.flag);
		if (sp.getBoolean("flag", false)) {
			button.setVisibility(View.VISIBLE);
		}
		buttonflag.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(sp.getBoolean("flag", false)){
					showUpdateDialog();
				}else{
					Toast.makeText(AboutMsgActivity.this,"已是最新版本", Toast.LENGTH_SHORT).show();
				}
			}
		});
		tetcontact.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 Intent intent=new Intent(AboutMsgActivity.this,ConTactActivity.class);
				 startActivity(intent);
				 overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
			}
		});
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

	private String getVersionName() {
		// 管理手机APK
		PackageManager pm = getPackageManager();
		try {
			// 得到管理APK的清单文件
			PackageInfo info = pm.getPackageInfo(getPackageName(), 1);
			return info.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	/**
	 * 升起对话框
	 */
	protected void showUpdateDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("提示升级");
		builder.setMessage(sp.getString("desc","最新版本"));// 版本信息
		builder.setOnCancelListener(new OnCancelListener() {// 点击返回按钮，到主页面
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		builder.setPositiveButton("立即升级", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				HttpUtils utils=new HttpUtils();
				utils.download(Const.DOWNAPKPATH, Const.LocalFile + "/splash/"+"onlyreading"+visionname+".apk", new RequestCallBack<File>(){
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_SHORT)
						.show();
					}
					@Override
					public void onSuccess(ResponseInfo<File> arg0) {
						// TODO Auto-generated method stub
						installAPK(arg0.result);
					}
					private void installAPK(File t) {
						Intent intent = new Intent();
						intent.setAction("android.intent.action.VIEW");
						intent.addCategory("android.intent.category.DEFAULT");
						intent.setDataAndType(Uri.fromFile(t),
								"application/vnd.android.package-archive");

						startActivity(intent);

					}
				});
				
			}
		});
		
		builder.setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		builder.show();
	}

}
