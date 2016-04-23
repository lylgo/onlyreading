package cn.hncj.or.read;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
@SuppressWarnings("unused")
public class SpeechRead {
	private SpeechSynthesizer mTts;
	private String mEngineType = SpeechConstant.TYPE_CLOUD;
	// 默认发音人
	private String voicer;
	// 缓冲进度
	private int mPercentForBuffering = 0;
	// 播放进度
	private int mPercentForPlaying = 0;
    private Context context;
    private SharedPreferences sp;
    private  String[] items = { "xiaoqi", "xiaoyu", "xiaomei", "xiaorong", "xiaoqian"," xiaokun","xiaoqiang","nannan" };
	public SpeechRead(Context context) {
		sp = context.getSharedPreferences("bookconfig",context.MODE_PRIVATE);
		int id=sp.getInt("sing", 0);
		voicer=items[id];
		mTts = SpeechSynthesizer.createSynthesizer(context, mTtsInitListener);
		int code = mTts.startSpeaking("", mTtsListener);// 内容
		this.context=context;
		setParam();
	}

	public void getSpeech(String txt) {
		mTts.startSpeaking(txt, mTtsListener);
	}

	private InitListener mTtsInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			// Log.d(TAG, "InitListener init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				// showTip("初始化失败,错误码：" + code);
			} else {
				// 初始化成功，之后可以调用startSpeaking方法
				// 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
				// 正确的做法是将onCreate中的startSpeaking调用移至这里
			}
		}
	};

	/**
	 * 合成回调监听。
	 */
	private SynthesizerListener mTtsListener = new SynthesizerListener() {

		@Override
		public void onSpeakBegin() {
			// showTip("开始播放");
		}

		@Override
		public void onSpeakPaused() {
			// showTip("暂停播放");
		}

		@Override
		public void onSpeakResumed() {
			// showTip("继续播放");
		}

		@Override
		public void onBufferProgress(int percent, int beginPos, int endPos,
				String info) {
			// 合成进度
			mPercentForBuffering = percent;
			// showTip(String.format(getString(R.string.tts_toast_format),
			// mPercentForBuffering, mPercentForPlaying));
		}

		@Override
		public void onSpeakProgress(int percent, int beginPos, int endPos) {
			// 播放进度
			mPercentForPlaying = percent;
			// showTip(String.format(getString(R.string.tts_toast_format),
			// mPercentForBuffering, mPercentForPlaying));
		}

		@Override
		public void onCompleted(SpeechError error) {
			if (error == null) {
				Intent intent=new Intent("READ.STOP");
				context.sendBroadcast(intent);				
			} else if (error != null) {
				// showTip(error.getPlainDescription(true));
			}
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			// 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
			// 若使用本地能力，会话id为null
			// if (SpeechEvent.EVENT_SESSION_ID == eventType) {
			// String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
			// Log.d(TAG, "session id =" + sid);
			// }
		}
	};
	private void setParam() {
		// 清空参数
		mTts.setParameter(SpeechConstant.PARAMS, null);
		// 根据合成引擎设置相应参数
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		// 设置在线合成发音人
		mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
		// 设置合成语速
		mTts.setParameter(SpeechConstant.SPEED, String.valueOf(50));
		// 设置合成音调
		mTts.setParameter(SpeechConstant.PITCH, String.valueOf(50));
		// 设置合成音量
		mTts.setParameter(SpeechConstant.VOLUME, String.valueOf(50));
		// 设置播放器音频流类型
		mTts.setParameter(SpeechConstant.STREAM_TYPE, String.valueOf(3));
		// 设置播放合成音频打断音乐播放，默认为true
		mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
		//mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
		//mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH,
			//	Environment.getExternalStorageDirectory() + "/msc/tts.wav");
	}
   public void stopSpeechread(){
		mTts.stopSpeaking();
		// 退出时释放连接
		mTts.destroy();
   }
}
