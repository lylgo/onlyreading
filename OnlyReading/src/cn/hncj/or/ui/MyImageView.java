package cn.hncj.or.ui;

import java.util.Timer;
import java.util.TimerTask;

import com.hncj.activity.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * @author wulianghuan
 * 该类为自定义ImageView，用于显示背景图片，并显示背景图片到移动效果
 *
 */
public class MyImageView extends ImageView{
	private Bitmap back;		//背景图片资源
	private Bitmap mBitmap;		//生成位图	
	private double startX = 0;	//移动起始X坐标
	//构函数中必须有context,attributeSet这两参数，否则父类无法调
	public MyImageView(Context context,AttributeSet attributeSet)
	{
		super(context, attributeSet);
	    DisplayMetrics dm = getResources().getDisplayMetrics();
	    //屏幕宽度
	    int screenWidth = dm.widthPixels;  
	    //屏幕高度
	    int screenHeight = dm.heightPixels;      
		back = BitmapFactory.decodeResource(context.getResources(), R.drawable.rootblock_default_bg);
		mBitmap = Bitmap.createScaledBitmap(back, screenWidth*2, screenHeight, true);    
		final Handler handler = new Handler()
		{
			public void handleMessage(Message msg)
			{
				if (msg.what == 1)
				{
					if (startX <= -80)
					{
						startX = 0;
					}
					else
					{
						startX -= 0.09;
					}
				}
				invalidate();//UI界面刷新
			}
		};
		new Timer().schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				handler.sendEmptyMessage(1);
			}
		}, 0 , 20);
	}
	
	@Override
	public void onDraw(Canvas canvas)
	{
		canvas.drawBitmap(mBitmap, (float)startX , 0 , null);
	}
}
