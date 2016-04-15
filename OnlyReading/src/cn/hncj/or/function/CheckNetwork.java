package cn.hncj.or.function;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
public class CheckNetwork {
	public static boolean isNetworkAvailable(Context context) {  
        ConnectivityManager connectivity = (ConnectivityManager) context  
                .getSystemService(Context.CONNECTIVITY_SERVICE);  
        if (connectivity != null) {  
            NetworkInfo info = connectivity.getActiveNetworkInfo();  
            if (info != null && info.isConnected())   
            {  
                if (info.getState() == NetworkInfo.State.CONNECTED)   
                {  
                    return true;  
                }  
            }  
        }  
        Toast.makeText(context, "网络不可用，请检查网络设置", Toast.LENGTH_SHORT).show();
        return false;  
    }  

}
