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
	// Ĭ�Ϸ�����
	private String voicer;
	// �������
	private int mPercentForBuffering = 0;
	// ���Ž���
	private int mPercentForPlaying = 0;
    private Context context;
    private SharedPreferences sp;
    private  String[] items = { "xiaoqi", "xiaoyu", "xiaomei", "xiaorong", "xiaoqian"," xiaokun","xiaoqiang","nannan" };
	public SpeechRead(Context context) {
		sp = context.getSharedPreferences("bookconfig",context.MODE_PRIVATE);
		int id=sp.getInt("sing", 0);
		voicer=items[id];
		mTts = SpeechSynthesizer.createSynthesizer(context, mTtsInitListener);
		int code = mTts.startSpeaking("", mTtsListener);// ����
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
				// showTip("��ʼ��ʧ��,�����룺" + code);
			} else {
				// ��ʼ���ɹ���֮����Ե���startSpeaking����
				// ע���еĿ�������onCreate�����д�����ϳɶ���֮�����Ͼ͵���startSpeaking���кϳɣ�
				// ��ȷ�������ǽ�onCreate�е�startSpeaking������������
			}
		}
	};

	/**
	 * �ϳɻص�������
	 */
	private SynthesizerListener mTtsListener = new SynthesizerListener() {

		@Override
		public void onSpeakBegin() {
			// showTip("��ʼ����");
		}

		@Override
		public void onSpeakPaused() {
			// showTip("��ͣ����");
		}

		@Override
		public void onSpeakResumed() {
			// showTip("��������");
		}

		@Override
		public void onBufferProgress(int percent, int beginPos, int endPos,
				String info) {
			// �ϳɽ���
			mPercentForBuffering = percent;
			// showTip(String.format(getString(R.string.tts_toast_format),
			// mPercentForBuffering, mPercentForPlaying));
		}

		@Override
		public void onSpeakProgress(int percent, int beginPos, int endPos) {
			// ���Ž���
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
			// ���´������ڻ�ȡ���ƶ˵ĻỰid����ҵ�����ʱ���Ựid�ṩ������֧����Ա�������ڲ�ѯ�Ự��־����λ����ԭ��
			// ��ʹ�ñ����������ỰidΪnull
			// if (SpeechEvent.EVENT_SESSION_ID == eventType) {
			// String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
			// Log.d(TAG, "session id =" + sid);
			// }
		}
	};
	private void setParam() {
		// ��ղ���
		mTts.setParameter(SpeechConstant.PARAMS, null);
		// ���ݺϳ�����������Ӧ����
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		// �������ߺϳɷ�����
		mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
		// ���úϳ�����
		mTts.setParameter(SpeechConstant.SPEED, String.valueOf(50));
		// ���úϳ�����
		mTts.setParameter(SpeechConstant.PITCH, String.valueOf(50));
		// ���úϳ�����
		mTts.setParameter(SpeechConstant.VOLUME, String.valueOf(50));
		// ���ò�������Ƶ������
		mTts.setParameter(SpeechConstant.STREAM_TYPE, String.valueOf(3));
		// ���ò��źϳ���Ƶ������ֲ��ţ�Ĭ��Ϊtrue
		mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
		// ������Ƶ����·����������Ƶ��ʽ֧��pcm��wav������·��Ϊsd����ע��WRITE_EXTERNAL_STORAGEȨ��
		// ע��AUDIO_FORMAT���������Ҫ���°汾������Ч
		//mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
		//mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH,
			//	Environment.getExternalStorageDirectory() + "/msc/tts.wav");
	}
   public void stopSpeechread(){
		mTts.stopSpeaking();
		// �˳�ʱ�ͷ�����
		mTts.destroy();
   }
}
