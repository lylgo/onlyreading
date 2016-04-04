package cn.hncj.or.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
/**
 * 全局管理Activity
 * @author Administrator
 *
 */
public class ActivityCollector {
	public static List<Activity> activities = new ArrayList<Activity>();

	public static void addActivity(Activity act) {
		activities.add(act);

	}
	public static void removeActivity(Activity act) {
		activities.remove(act);
	}
	public static void finishall() {
		for (Activity act : activities) {
			if (act.isFinishing()) {
				act.finish();
			}
		}
	}
}
