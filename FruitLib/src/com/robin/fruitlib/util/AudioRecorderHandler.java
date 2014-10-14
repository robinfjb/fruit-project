package com.robin.fruitlib.util;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.buihha.audiorecorder.Mp3Recorder;
import com.robin.fruitlib.R;
import com.robin.fruitlib.data.FruitPerference;

public class AudioRecorderHandler {
	private static final int MAX_TIME = 60; // 最长录制时间，单位秒，0为无时间限制
	private static final int MIX_TIME = 3; // 最短录制时间，单位秒，0为无时间限制，建议设为1
	private static final int MIN_TIME = 1; // 最短录制时间，单位秒，0为无时间限制，建议设为1
	private static final int RECORD_NO = 0; // 不在录音
	private static final int RECORD_ING = 1; // 正在录音
	private static final int RECODE_ED = 2; // 完成录音
	private MediaPlayer mediaPlayer;
	private static boolean playState = false; // 播放状态
	private static int RECODE_STATE = 0; // 录音的状态
	private static float recodeTime = 0.0f; // 录音的时间
	private static double voiceValue = 0.0; // 麦克风获取的音量值
	private Thread recordThread;
	// 获取类的实例
//	private ExtAudioRecorder extAudioRecorder; // 未压缩的录音（WAV）
	private Mp3Recorder mp3Recorder;
	private Dialog voiceImgdialog;
	private ImageView dialog_img;
	private Activity context;
	private static String wavName;
	private static AudioRecorderHandler instance;
	private String path;
	

	AudioRecorderHandler(Activity context) {
		this.context = context;
		mp3Recorder = new Mp3Recorder();
	}
	
	public static AudioRecorderHandler getInstance(Activity context) {
		if(instance == null) {
			instance = new AudioRecorderHandler(context);
		}
		return instance;
	}
	
	public void play(String uri) {
		if (!playState) {
			mediaPlayer = new MediaPlayer();
			try {
				mediaPlayer.setDataSource(uri);
				mediaPlayer.prepare();
				mediaPlayer.start();
				playState = true;
				mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						if (playState) {
							playState = false;
						}
					}
				});
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
				playState = false;
			} else {
				playState = false;
			}
		}
	}
	
	public void playAsync(String uri) {
		if (!playState) {
			mediaPlayer = new MediaPlayer();
			try {
				mediaPlayer.setDataSource(uri);
//				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//				mediaPlayer.prepareAsync();
				mediaPlayer.prepare();
				mediaPlayer.start();
				playState = true;
				mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						if (playState) {
							playState = false;
						}
					}
				});
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
				playState = false;
			} else {
				playState = false;
			}
		}
	}
	
	public void playFromRaw(int id) {
		mediaPlayer = MediaPlayer.create(context, id);
		mediaPlayer.start();
	}

	public void play() {
		play(getAmrPath());
	}
	
	public int getAudioLength() {
		MediaPlayer md = new MediaPlayer();
        try {
            md.setDataSource(getAmrPath());
            md.prepare();
            return md.getDuration() / 1000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
	}

	public void recordStart(IAudioListener listener) {
		if (RECODE_STATE != RECORD_ING) {
			scanOldFile();
			String phone = FruitPerference.getUserMobile(context);
			wavName = phone + "-" + TimeUtil.getNowDate() + TimeUtil.getNowTime() + ".mp3";
			RECODE_STATE = RECORD_ING;
			listener.start();
			recordThread = new Thread(ImgThread);
			recordThread.start();
//			extAudioRecorder = ExtAudioRecorder.getInstanse(false); //未压缩的录音（WAV）
			
			if (isExternalStorageWritable()) {
				recordChat(mp3Recorder,  Environment.getExternalStorageDirectory() + "/com.fruit/", wavName);
			} else {
				recordChat(mp3Recorder,  context.getCacheDir() + "/com.fruit/", wavName);
			}
		}
	}

	public void recordEnd(IAudioListener listener) {
		if (RECODE_STATE == RECORD_ING) {
			RECODE_STATE=RECODE_ED;
			if (voiceImgdialog.isShowing()) {
				voiceImgdialog.dismiss();
			}
			voiceValue = 0.0;
			if(recodeTime < MIN_TIME) {
				scanOldFile();
				showWarnToastOneSecond(context);
				RECODE_STATE=RECORD_NO;
				stopRecord(mp3Recorder);
			} else if (recodeTime < MIX_TIME) {
				scanOldFile();
				showWarnToast(context);
				RECODE_STATE=RECORD_NO;
				stopRecord(mp3Recorder);
			} else {
				stopRecord(mp3Recorder);
				if (isExternalStorageWritable()) {
					Uri uri = Uri.parse(Environment.getExternalStorageDirectory() + "/com.fruit/" + wavName);
					listener.success(uri,wavName);
				} else {
					Uri uri = Uri.parse(context.getCacheDir() + "/com.fruit/" + wavName);
					listener.success(uri,wavName);
				}
			}
			
		}
	}

	// 删除老文件
	public void scanOldFile() {
		if(TextUtils.isEmpty(wavName)) {
			return;
		}
		if (isExternalStorageWritable()) {
			File file = new File(Environment.getExternalStorageDirectory(),"/com.fruit/" + wavName);
			if (file.exists()) {
				file.delete();
			}
		} else {
			File file = new File(context.getCacheDir(),"/com.fruit/" + wavName);
			if (file.exists()) {
				file.delete();
			}
		}
	}

	// 获取文件手机路径
	private String getAmrPath() {
		if (isExternalStorageWritable()) {
			File file = new File(Environment.getExternalStorageDirectory(),"/com.fruit/" + wavName);
			return file.getAbsolutePath();
		} else {
			File file = new File(context.getCacheDir(),"/com.fruit/" + wavName);
			return file.getAbsolutePath();
		}
		
	}

	// 录音线程
	private Runnable ImgThread = new Runnable() {

		@Override
		public void run() {
			recodeTime = 0.0f;
			while (RECODE_STATE == RECORD_ING) {
				if (recodeTime >= MAX_TIME && MAX_TIME != 0) {
					imgHandle.sendEmptyMessage(0);
				} else {
					try {
						Thread.sleep(200);
						recodeTime += 0.2;
						if (RECODE_STATE == RECORD_ING) {
							voiceValue += 200;
							imgHandle.sendEmptyMessage(1);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		Handler imgHandle = new Handler(new Handler.Callback() {
			
			@Override
			public boolean handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					// 录音超过15秒自动停止
					if (RECODE_STATE == RECORD_ING) {
						RECODE_STATE = RECODE_ED;
						if (voiceImgdialog.isShowing()) {
							voiceImgdialog.dismiss();
						}
						voiceValue = 0.0;
						stopRecord(mp3Recorder);

						if (recodeTime < 1.0) {
							showWarnToast(context);
							RECODE_STATE = RECORD_NO;
						} else {
							//录音完成
						}
					}
					break;
				case 1:
					setDialogImage();
					break;
				default:
					break;
				}
				return false;
			}
		});
	};

	// 录音时显示Dialog
	public void showVoiceDialog(Context context) {
		voiceImgdialog = new Dialog(context, R.style.DialogStyle);
		voiceImgdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		voiceImgdialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		voiceImgdialog.setContentView(R.layout.view_voice_dialog);
		dialog_img = (ImageView) voiceImgdialog.findViewById(R.id.dialog_img);
		voiceImgdialog.show();
	}

	// 录音Dialog图片随声音大小切换
	private void setDialogImage() {
		if (voiceValue < 400.0) {
			dialog_img.setImageResource(R.drawable.record_animate_01);
		} else if (voiceValue > 400.0 && voiceValue < 1600) {
			dialog_img.setImageResource(R.drawable.record_animate_03);
		} else if (voiceValue > 1600.0 && voiceValue < 3200) {
			dialog_img.setImageResource(R.drawable.record_animate_05);
		} else if (voiceValue > 3200.0 && voiceValue < 7000) {
			dialog_img.setImageResource(R.drawable.record_animate_07);
		} else if (voiceValue > 7000.0 && voiceValue < 14000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_09);
		} else if (voiceValue > 14000.0 && voiceValue < 20000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_11);
		} else if (voiceValue > 20000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_13);
		}
	}

	// 录音时间太短时Toast显示
		private void showWarnToastOneSecond(Context context){
			 Toast toast = new Toast(context);
			 LinearLayout linearLayout = new LinearLayout(context);
			 linearLayout.setOrientation(LinearLayout.VERTICAL); 
			 linearLayout.setPadding(20, 20, 20, 20);
			
			// 定义一个ImageView
			 ImageView imageView = new ImageView(context);
			 imageView.setImageResource(R.drawable.voice_to_short); // 图标
			 
			 TextView mTv = new TextView(context);
			 mTv.setText("按住语音才能生成语音订单！");
			 mTv.setTextSize(14);
			 mTv.setTextColor(Color.WHITE);//字体颜色
			 //mTv.setPadding(0, 10, 0, 0);
			 
			// 将ImageView和ToastView合并到Layout中
			 linearLayout.addView(imageView);
			 linearLayout.addView(mTv);
			 linearLayout.setGravity(Gravity.CENTER);//内容居中
			 linearLayout.setBackgroundResource(R.drawable.record_bg);//设置自定义toast的背景
			 
			 toast.setView(linearLayout); 
			 toast.setGravity(Gravity.CENTER, 0,0);//起点位置为中间     100为向下移100dp
			 toast.show();				
		}
		
	// 录音时间太短时Toast显示
	private void showWarnToast(Context context){
		 Toast toast = new Toast(context);
		 LinearLayout linearLayout = new LinearLayout(context);
		 linearLayout.setOrientation(LinearLayout.VERTICAL); 
		 linearLayout.setPadding(20, 20, 20, 20);
		
		// 定义一个ImageView
		 ImageView imageView = new ImageView(context);
		 imageView.setImageResource(R.drawable.voice_to_short); // 图标
		 
		 TextView mTv = new TextView(context);
		 mTv.setText("您的录音时间过短！");
		 mTv.setTextSize(14);
		 mTv.setTextColor(Color.WHITE);//字体颜色
		 //mTv.setPadding(0, 10, 0, 0);
		 
		// 将ImageView和ToastView合并到Layout中
		 linearLayout.addView(imageView);
		 linearLayout.addView(mTv);
		 linearLayout.setGravity(Gravity.CENTER);//内容居中
		 linearLayout.setBackgroundResource(R.drawable.record_bg);//设置自定义toast的背景
		 
		 toast.setView(linearLayout); 
		 toast.setGravity(Gravity.CENTER, 0,0);//起点位置为中间     100为向下移100dp
		 toast.show();				
	}

	/**
	 * 录制wav格式文件
	 * 
	 * @param path
	 *            : 文件路径
	 */
	public void recordChat(Mp3Recorder recorder, String savePath, String fileName) {
/*//		File dir = new File(Environment.getExternalStorageDirectory(), savePath);
		File dir = new File(savePath);
		// 如果该目录没有存在，则新建目录
		if (dir.list() == null || !dir.exists()) {
			dir.mkdirs();
		}
		// 获取录音文件
		File file = new File(savePath+fileName);
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// 设置输出文件
		extAudioRecorder.setOutputFile(savePath+fileName);
		extAudioRecorder.prepare();
		// 开始录音
		extAudioRecorder.start();*/
		try {
			recorder.startRecording(savePath, fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 停止录音
	 * 
	 * @param mediaRecorder
	 *            待停止的录音机
	 * @return 返回
	 */
	public void stopRecord(final Mp3Recorder recorder) {
		try {
			recorder.stopRecording();	
		} catch(IOException e) {
			Log.d("MainActivity", "Stop error");
		}
	}
	
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}
}
