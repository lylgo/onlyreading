package cn.hncj.or.activity;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import cn.hncj.or.config.Const;
import cn.hncj.or.function.CheckNetwork;
import cn.hncj.or.http.HttptestUtils;

import com.hncj.activity.R;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends BaseActivity {
	private Button button;
	private EditText editname,editpass;
	private TextView regintext,homebutton;
	private ProgressDialog dialog;
	private Map<String, String> map;
	private SharedPreferences sp;
	protected String name;
	protected String pass;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		button=(Button) findViewById(R.id.login_button);
		editname=(EditText) findViewById(R.id.name);
		editpass=(EditText) findViewById(R.id.pass);
		homebutton=(TextView) findViewById(R.id.back_home);
		regintext=(TextView) findViewById(R.id.regin);
		regintext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
				finish();
			}
		});
		homebutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
				finish();
			}
		});
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(check()){
					map=new HashMap<String, String>();
					map.put("email", name);
					map.put("pass", pass);
					if(CheckNetwork.isNetworkAvailable(LoginActivity.this)){
						new loginServer().execute();
					}
				}else{
					Toast.makeText(LoginActivity.this, "’À∫≈ªÚ√‹¬Î≤ªƒ‹Œ™ø’",Toast.LENGTH_SHORT ).show();
				}
			}
		});
		
	}
	
	public boolean check(){
		 name=editname.getText().toString().trim();
		 pass=editpass.getText().toString().trim();
		if(!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(pass)){
			return true;
		}else{
			return false;
		}
	}
	
	public void showdig() {
		
		dialog = new ProgressDialog(this);
	    dialog.setCancelable(false);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	class loginServer extends AsyncTask<Object,Void, String>{
		    @Override
		    protected void onPreExecute() {
		    	// TODO Auto-generated method stub
		    	showdig();
		    }
		@Override
		protected String doInBackground(Object... params) {
			// TODO Auto-generated method stub
			String msg=HttptestUtils.submitPostData(map, "utf-8", Const.LOGINEPath);
			return msg;
		}
        @SuppressLint("SimpleDateFormat")
		@Override
        protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        	String str[]=result.split("#");
        	Log.i("FFFF", str[0]+"kkkkkk");
        	if(str[0].equals("SU")){
        		dialog.dismiss();
        		sp=getSharedPreferences("land",MODE_PRIVATE );
				Editor editor=sp.edit();
				editor.putString("email", name);
				editor.putString("name",str[1] );
				SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				String date = sDateFormat.format(new java.util.Date());
				editor.putString("date", date);
				editor.commit();
    			Intent intent=new Intent(LoginActivity.this,MainActivity.class);
    			startActivity(intent);
    			finish();
    		}else if(str[0].equals("ERPASS")){
    			dialog.dismiss();
    			editname.setText("");
    			editpass.setText("");
    			Toast.makeText(LoginActivity.this,"’À∫≈ªÚ√‹¬Î¥ÌŒÛ",Toast.LENGTH_SHORT ).show();
    		}else if(str[0].equals("REG")){
    			dialog.dismiss();
    			Toast.makeText(LoginActivity.this,"’À∫≈≤ª¥Ê‘⁄",Toast.LENGTH_SHORT ).show();
    		}
        }
	}
}

