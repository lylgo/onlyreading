package cn.hncj.or.utils;

import android.app.Application;
import android.content.Context;
/**
 * ȫ�ֻ��context
 * @author Administrator
 *
 */
public class MyApplication extends Application {
   public static Context context;
   @Override
	public void onCreate() {
		// TODO Auto-generated method stub
	   context=getApplicationContext();
	}
   public  static Context getcontext(){
	   return context;
   }
}
