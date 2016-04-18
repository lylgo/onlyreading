package cn.hncj.or.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import cn.hncj.or.config.Const;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class CompressBitmapUtils {
	@SuppressWarnings("unused")
	public static Bitmap compBitmap(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 4;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	public static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	@SuppressWarnings("unused")
	public static void getLocalBitmap(final String url,String email,final ImageView userview,final ImageView titleview) {
		File file=new File(url);
		if(file.exists()){
			FileInputStream in = null;
			try {
				in = new FileInputStream(url);
				Bitmap bitmap = BitmapFactory.decodeStream(in);
				userview.setImageBitmap(bitmap);
				Bitmap bit = CompressBitmapUtils.compBitmap(bitmap);
				titleview.setImageBitmap(bit);// 压缩处理
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{ 
			File fi=new File(Environment.getExternalStorageDirectory().getPath()+"/onlyreading");
			fi.mkdirs();
			HttpUtils http = new HttpUtils();
			HttpHandler handler = http.download(Const.DOWNIMAGEPath+email,
					Environment.getExternalStorageDirectory().getPath()+"/onlyreading/"+".png",
			    true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
			    true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
			    new RequestCallBack<File>() {
			        @Override
			        public void onStart() {
			        }
			        @Override
			        public void onLoading(long total, long current, boolean isUploading) {
			        }
			        @Override
			        public void onSuccess(ResponseInfo<File> responseInfo) {
			        	FileInputStream in = null;
						try {
							in = new FileInputStream(url);
							Bitmap bitmap = BitmapFactory.decodeStream(in);
							userview.setImageBitmap(bitmap);
							Bitmap bit = CompressBitmapUtils.compBitmap(bitmap);
							titleview.setImageBitmap(bit);// 压缩处理
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			        	
			        }
			        @Override
			        public void onFailure(HttpException error, String msg) {
			        }
			});
		}
	}
}
