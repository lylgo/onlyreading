package cn.hncj.or.config;

import android.os.Environment;

public class Const {
	public static final String  DATABASE_NAME="onlyreading.db";//���ݿ�
	public static final String DB_TNAME="onlybook";//�����
	public static final String DB_TMARK="bookmark";//��ǩ���
	public static final int PHOTO_REQUEST_CAMERA = 1;// ����
	public static final int PHOTO_REQUEST_GALLERY = 2;// �������ѡ��
	public static final int PHOTO_REQUEST_CUT = 3;// ���
    public static final String IMAGEUrlPath="http://192.168.0.102:8085/upImage";
	public static final String REGiSTERPath = "http://192.168.0.102:8085/reguser";
	public static final String UPDEUSERPath = "http://192.168.0.102:8085/upUserMsg";
	public static final String DOWNIMAGEPath = "http://192.168.0.102:8085/downImage?email=";
	public static final String LOGINEPath = "http://192.168.0.102:8085/login";
	public static final String GETMSGPath = "http://192.168.0.102:8085/getUserMsg";
	public static final String GETBOOKLISTPATH = "http://192.168.0.102:8085/book/getListBook";
	public static final String PAGENUMBERPATH = "http://192.168.0.102:8085/book/getpagenumber";
	public static final String DOWNBOOKPATH = "http://192.168.0.102:8085/book/downbook";
	public static final String COLLEBOOKPATH = "http://192.168.0.102:8085/book/collecbook";
	public static final String GETIMAGEPATH = "http://192.168.0.102:8085/book/getimge";
	public static final String COLLEBOOKLISTPATH = "http://192.168.0.102:8085/book/collecbooklist";
	public static final String DELCOLLEBOOKLISTPATH = "http://192.168.0.102:8085/book/delcobooklist";
	public static final String LocalFile=Environment.getExternalStorageDirectory().getPath()+"/onlyreading/";
}