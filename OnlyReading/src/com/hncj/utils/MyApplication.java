package com.hncj.utils;

import android.app.Application;
import android.content.Context;
/**
 * 全局获得context
 * @author Administrator
 *
 */
public class MyApplication extends Application {
   private static Context context;
   @Override
	public void onCreate() {
		// TODO Auto-generated method stub
	   context=getApplicationContext();
	}
   public  static Context getcontext(){
	   return context;
   }
}
