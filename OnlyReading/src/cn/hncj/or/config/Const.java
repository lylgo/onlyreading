package cn.hncj.or.config;

import android.os.Environment;

public class Const {
	public static final String  DATABASE_NAME="onlyreading.db";//数据库
	public static final String DB_TNAME="onlybook";//添加书
	public static final String DB_TMARK="bookmark";//书签书库
	public static final int PHOTO_REQUEST_CAMERA = 1;// 拍照
	public static final int PHOTO_REQUEST_GALLERY = 3;// 从相册中选择
	public static final int PHOTO_REQUEST_CUT = 3;// 结果
	public static final String VISIONMSGPATH="http://192.168.191.3:8080/web_book/base/getvision";
	public static final String DOWNAPKPATH="http://192.168.191.3:8080/web_book/base/downapk";
	public static final String SUBMITMSGPATH="http://192.168.191.3:8080/web_book/base/contact";
    public static final String IMAGEUrlPath="http://192.168.191.3:8080/web_book/upImage";
	public static final String REGiSTERPath = "http://192.168.191.3:8080/web_book/reguser";
	public static final String UPDEUSERPath = "http://192.168.191.3:8080/web_book/upUserMsg";
	public static final String DOWNIMAGEPath = "http://192.168.191.3:8080/web_book/downImage?email=";
	public static final String LOGINEPath = "http://192.168.191.3:8080/web_book/login";
	public static final String GETMSGPath = "http://192.168.191.3:8080/web_book/getUserMsg";
	public static final String GETBOOKLISTPATH = "http://192.168.191.3:8080/web_book/book/getListBook";
	public static final String PAGENUMBERPATH = "http://192.168.191.3:8080/web_book/book/getpagenumber";
	public static final String DOWNBOOKPATH = "http://192.168.191.3:8080/web_book/book/downbook";
	public static final String COLLEBOOKPATH = "http://192.168.191.3:8080/web_book/book/collecbook";
	public static final String GETIMAGEPATH = "http://192.168.191.3:8080/web_book/book/getimge";
	public static final String COLLEBOOKLISTPATH = "http://192.168.191.3:8080/web_book/book/collecbooklist";
	public static final String DELCOLLEBOOKLISTPATH = "http://192.168.191.3:8080/web_book/book/delcobooklist";
	public static final String LocalFile=Environment.getExternalStorageDirectory().getPath()+"/onlyreading/";
}