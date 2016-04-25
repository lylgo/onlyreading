package cn.hncj.or.activity;

import java.io.File;
import java.io.InputStream;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import cn.hncj.or.config.Const;
import cn.hncj.or.http.HttptestUtils;

import com.hncj.activity.R;

public class SplashViewActivity extends BaseActivity {
	private ImageView splashView;
	private Handler handler;
	private Runnable runnable;
	private Animation animation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_view);
		creadFile();// 创建路径
		splashView = (ImageView) findViewById(R.id.splash);
		splashView.setScaleType(ScaleType.FIT_XY);
		splashView.setImageResource(R.drawable.img_loading);
		animation = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.alpha);
		splashView.setAnimation(animation);
		handler = new Handler();
		runnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SplashViewActivity.this,
						MainActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
				finish();
			}
		};
		handler.postDelayed(runnable, 8000);
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(2000);
					new GetImga().execute();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	class GetImga extends AsyncTask<Void, Void, InputStream>{
		@Override
		protected InputStream doInBackground(Void... params) {
			// TODO Auto-generated method stub
			InputStream in=HttptestUtils.submitPostData(Const.GETIMAGEPATH);
			return in;
		}
		@Override
		protected void onPostExecute(InputStream result) {
			if(result!=null){
				splashView.setImageBitmap(BitmapFactory.decodeStream(result));
			}
		}
	}
	/**
	 * 创建image文件
	 */
	public void creadFile() {
		File flie = new File(Const.LocalFile + "/splash");
		if (flie.isFile()) {
			return;
		} else {
			flie.mkdirs();
		}
	}
}
