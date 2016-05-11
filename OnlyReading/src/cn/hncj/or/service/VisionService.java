package cn.hncj.or.service;

import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.hncj.or.config.Const;
import cn.hncj.or.function.CheckNetwork;
import cn.hncj.or.http.HttptestUtils;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class VisionService extends IntentService {
	private SharedPreferences sp;

	public VisionService() {
		super("VisionService");
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		sp = this.getSharedPreferences("vision", MODE_PRIVATE);
		InputStream in = HttptestUtils.submitPostData(Const.VISIONMSGPATH);
		String result = null;
		if (in != null) {
			result = HttptestUtils.dealResponseResult(in);
		} else {
			return;
		}
		try {
			JSONObject object = new JSONObject(result);
			String vision = object.getString("vision");
			String desc = object.getString("desce");
			Editor editor = sp.edit();
			if (!vision.equals(getVersionName())) {
				editor.putBoolean("flag", true);
				editor.putString("vision", vision);
				editor.putString("desc", desc);
				editor.commit();
			}else{
				editor.putBoolean("flag", false);
				editor.commit();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean  flag=CheckNetwork.isNetworkAvailable(this);
		if(!flag){
			sp = getSharedPreferences("land", MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("email", "");
			editor.putString("name", "ÉÐÎ´µÇÂ¼");
			editor.commit();
		}
	}
	
	private String getVersionName() {
		PackageManager pm = getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(getPackageName(), 1);
			return info.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
}
