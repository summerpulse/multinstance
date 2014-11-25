package com.example.multinstance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MultipleVideoPlayActivity extends Activity implements
		OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener,
		OnVideoSizeChangedListener, SurfaceHolder.Callback
{
	private static final String TAG = "MediaPlayer";
	private static final int[] SURFACE_RES_IDS =
	{ R.id.video_1_surfaceview
	, R.id.video_2_surfaceview
	, R.id.video_3_surfaceview
	};

	private MediaPlayer[] mMediaPlayers = new MediaPlayer[SURFACE_RES_IDS.length];
	private SurfaceView[] mSurfaceViews = new SurfaceView[SURFACE_RES_IDS.length];
	private SurfaceHolder[] mSurfaceHolders = new SurfaceHolder[SURFACE_RES_IDS.length];
	private boolean[] mSizeKnown = new boolean[SURFACE_RES_IDS.length];
	private boolean[] mVideoReady = new boolean[SURFACE_RES_IDS.length];

	@Override
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.multi_videos_layout);
		Log.d(TAG,
				"SURFACE_RES_IDS.length=" + SURFACE_RES_IDS.length
						+ ", Environment.getExternalStorageDirectory="
						+ Environment.getExternalStorageDirectory());
		createPath("waththefuck");
		ContextWrapper c = new ContextWrapper(this);
		Log.d(TAG,
				"Context.getApplicationInfo().dataDir="
						+ c.getApplicationInfo().dataDir);
		try
		{
			writeFile("abc","def");
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// create surface holders
		for (int i = 0; i < mSurfaceViews.length; i++)
		{
			mSurfaceViews[i] = (SurfaceView) findViewById(SURFACE_RES_IDS[i]);
			mSurfaceHolders[i] = mSurfaceViews[i].getHolder();
			mSurfaceHolders[i].addCallback(this);
			mSurfaceHolders[i].setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
	}

	public void onBufferingUpdate(MediaPlayer player, int percent)
	{
		Log.d(TAG, "MediaPlayer(" + indexOf(player)
				+ "): onBufferingUpdate percent: " + percent);
	}

	public void onCompletion(MediaPlayer player)
	{
		Log.d(TAG, "MediaPlayer(" + indexOf(player) + "): onCompletion called");
	}

	public void onVideoSizeChanged(MediaPlayer player, int width, int height)
	{
		Log.v(TAG, "MediaPlayer(" + indexOf(player)
				+ "): onVideoSizeChanged called");
		if (width == 0 || height == 0)
		{
			Log.e(TAG, "invalid video width(" + width + ") or height(" + height
					+ ")");
			return;
		}

		int index = indexOf(player);
		if (index == -1)
			return; // sanity check; should never happen
		mSizeKnown[index] = true;
		if (mVideoReady[index] && mSizeKnown[index])
		{
			startVideoPlayback(player);
		}
	}

	public void onPrepared(MediaPlayer player)
	{
		Log.d(TAG, "MediaPlayer(" + indexOf(player) + "): onPrepared called");

		int index = indexOf(player);
		if (index == -1)
			return; // sanity check; should never happen
		mVideoReady[index] = true;
		if (mVideoReady[index] && mSizeKnown[index])
		{
			startVideoPlayback(player);
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int i, int j, int k)
	{
		Log.d(TAG, "SurfaceHolder(" + indexOf(holder)
				+ "): surfaceChanged called");
	}

	public void surfaceDestroyed(SurfaceHolder holder)
	{
		Log.d(TAG, "SurfaceHolder(" + indexOf(holder)
				+ "): surfaceDestroyed called");
	}

	public void surfaceCreated(SurfaceHolder holder)
	{
		Log.d(TAG, "SurfaceHolder(" + indexOf(holder)
				+ "): surfaceCreated called");

		int index = indexOf(holder);
		if (index == -1)
			return; // sanity check; should never happen
		try
		{
			mMediaPlayers[index] = new MediaPlayer();
			//AssetFileDescriptor afd = getAssets().openFd("small.3gp");
			// mMediaPlayers[index].setDataSource(afd.getFileDescriptor(),
			// afd.getStartOffset(), afd.getLength());
			Log.d(TAG, "index=" + index);

			switch(index)
			{
			case 0:
				mMediaPlayers[index].setDataSource("http://10.18.29.135:81/ali/world.mp4");
				break;
			case 1:
				mMediaPlayers[index].setDataSource("http://10.18.29.135:81/ali/small.mp4");
				break;
			case 2:
				mMediaPlayers[index].setDataSource("http://10.18.29.135:81/ali/test.mp4");
			}
			
			mMediaPlayers[index].setDisplay(mSurfaceHolders[index]);
			mMediaPlayers[index].prepare();
			mMediaPlayers[index].setOnBufferingUpdateListener(this);
			mMediaPlayers[index].setOnCompletionListener(this);
			mMediaPlayers[index].setOnPreparedListener(this);
			mMediaPlayers[index].setOnVideoSizeChangedListener(this);
			mMediaPlayers[index].setAudioStreamType(AudioManager.STREAM_MUSIC);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		releaseMediaPlayers();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		releaseMediaPlayers();
	}

	private void releaseMediaPlayers()
	{
		for (int i = 0; i < mMediaPlayers.length; i++)
		{
			if (mMediaPlayers[i] != null)
			{
				mMediaPlayers[i].release();
				mMediaPlayers[i] = null;
			}
		}
	}

	private void startVideoPlayback(MediaPlayer player)
	{
		Log.v(TAG, "MediaPlayer(" + indexOf(player) + "): startVideoPlayback");
		player.start();
	}

	private int indexOf(MediaPlayer player)
	{
		for (int i = 0; i < mMediaPlayers.length; i++)
			if (mMediaPlayers[i] == player)
				return i;
		return -1;
	}

	private int indexOf(SurfaceHolder holder)
	{
		for (int i = 0; i < mSurfaceHolders.length; i++)
			if (mSurfaceHolders[i] == holder)
				return i;
		return -1;
	}

	/**
	 * 5、创建目录
	 */
	private void createPath(String path)
	{
		File file = new File(path);
		if (!file.exists())
		{
			file.mkdir();
		}
	}

	// /读写/data/data/<应用程序名>目录上的文件:
	public void writeFile(String fileName, String writestr) throws IOException
	{
		try
		{
			FileOutputStream fout = openFileOutput(fileName, MODE_PRIVATE);
			byte[] bytes = writestr.getBytes();
			fout.write(bytes);
			fout.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}