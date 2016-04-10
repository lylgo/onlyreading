package cn.hncj.or.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.UUID;

import android.graphics.Bitmap;
import android.util.Base64;

public class HttpUtils {
	private static String path = "http://192.168.0.109:8085/reguser";
	private static OutputStream outputStream;
	private static int response;
	public static String submitPostData(Map<String,String> map, String encode) {
		byte[] data = getRequestData( map, encode).toString().getBytes();// ���������
		try {
			HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(
					path.toString()).openConnection();
			httpURLConnection.setConnectTimeout(3000);
			httpURLConnection.setDoInput(true); // �����������Ա�ӷ�������ȡ����
			httpURLConnection.setDoOutput(true); // ����������Ա���������ύ����
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setUseCaches(false); // ʹ��Post��ʽ����ʹ�û���
			// ������������������ı�����
			httpURLConnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			// ����������ĳ���
			httpURLConnection.setRequestProperty("Content-Length",
					String.valueOf(data.length));
			// �����������������д������
			try {
				outputStream = httpURLConnection.getOutputStream();
			} catch (IOException e) {
				if (outputStream == null) {
					return "F";
				}
			}
			outputStream.write(data);
			int response = httpURLConnection.getResponseCode(); // ��÷���������Ӧ��
			if (response == HttpURLConnection.HTTP_OK) {
				InputStream inptStream = httpURLConnection.getInputStream();
				return dealResponseResult(inptStream); // �������������Ӧ���
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return String.valueOf(response);
	}
	/**
	 * ��������ֵ
	 * 
	 * @param inputStream
	 * @return
	 */
	public static String dealResponseResult(InputStream inputStream) {
		String resultData = null; // �洢������
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
		try {
			while ((len = inputStream.read(data)) != -1) {
				byteArrayOutputStream.write(data, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		resultData = new String(byteArrayOutputStream.toByteArray());
		return resultData;
	}

	public static StringBuffer getRequestData(Map<String,String> map, String encode) {
		StringBuffer stringBuffer = new StringBuffer(); // �洢��װ�õ���������Ϣ
		try {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				stringBuffer.append(entry.getKey()).append("=")
						.append(URLEncoder.encode(entry.getValue(), encode))
						.append("&");
			}
			stringBuffer.deleteCharAt(stringBuffer.length() - 1); // ɾ������һ��"&"
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}

}
