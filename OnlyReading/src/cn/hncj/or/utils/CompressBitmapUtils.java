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
		if (baos.toByteArray().length / 1024 > 1024) {// �ж����ͼƬ����1M,����ѹ������������ͼƬ��BitmapFactory.decodeStream��ʱ���
			baos.reset();// ����baos�����baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// ����ѹ��50%����ѹ��������ݴ�ŵ�baos��
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// ��ʼ����ͼƬ����ʱ��options.inJustDecodeBounds ���true��
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// ���������ֻ��Ƚ϶���800*480�ֱ��ʣ����ԸߺͿ���������Ϊ
		float hh = 800f;// �������ø߶�Ϊ800f
		float ww = 480f;// �������ÿ��Ϊ480f
		// ���űȡ������ǹ̶��������ţ�ֻ�ø߻��߿�����һ�����ݽ��м��㼴��
		int be = 4;// be=1��ʾ������
		if (w > h && w > ww) {// �����ȴ�Ļ����ݿ�ȹ̶���С����
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// ����߶ȸߵĻ����ݿ�ȹ̶���С����
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// �������ű���
		// ���¶���ͼƬ��ע���ʱ�Ѿ���options.inJustDecodeBounds ���false��
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);// ѹ���ñ�����С���ٽ�������ѹ��
	}

	public static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// ����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // ѭ���ж����ѹ����ͼƬ�Ƿ����100kb,���ڼ���ѹ��
			baos.reset();// ����baos�����baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// ����ѹ��options%����ѹ��������ݴ�ŵ�baos��
			options -= 10;// ÿ�ζ�����10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// ��ѹ���������baos��ŵ�ByteArrayInputStream��
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// ��ByteArrayInputStream��������ͼƬ
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
				titleview.setImageBitmap(bit);// ѹ������
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			HttpUtils http = new HttpUtils();
			HttpHandler handler = http.download(Const.DOWNIMAGEPath+email,
					Environment.getExternalStorageDirectory().getPath()+"/onlyreading/"+".png",
			    true, // ���Ŀ���ļ����ڣ�����δ��ɵĲ��ּ������ء���������֧��RANGEʱ���������ء�
			    true, // ��������󷵻���Ϣ�л�ȡ���ļ�����������ɺ��Զ���������
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
							titleview.setImageBitmap(bit);// ѹ������
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
