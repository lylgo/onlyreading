package cn.hncj.or.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import cn.hncj.or.config.Const;
import cn.hncj.or.function.CheckNetwork;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class ImageHttpUtils {
	public static void uploadMethod(File file, Bitmap map,
			final Context context, String email) {
		if (CheckNetwork.isNetworkAvailable(context)) {
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(new File(file.getPath(), email
						+ ".png"));
				map.compress(Bitmap.CompressFormat.PNG, 100, out);
				out.flush();
				out.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			RequestParams params = new RequestParams();
			params.addBodyParameter("email", email);
			params.addBodyParameter("file", new File(Environment
					.getExternalStorageDirectory().getPath()
					+ "/onlyreading/"
					+ email + ".png"));
			HttpUtils http = new HttpUtils();
			http.send(HttpRequest.HttpMethod.POST, Const.IMAGEUrlPath, params,
					new RequestCallBack<String>() {
						@Override
						public void onStart() {
						}

						@Override
						public void onLoading(long total, long current,
								boolean isUploading) {
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
						}

						@Override
						public void onFailure(HttpException error, String msg) {
							Toast.makeText(context, "Í·ÏñÉÏ´«Ê§°Ü",
									Toast.LENGTH_SHORT).show();
						}
					});

		}else{
			return;
		}
	}

}
