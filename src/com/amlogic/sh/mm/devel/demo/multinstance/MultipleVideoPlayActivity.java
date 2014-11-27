package com.amlogic.sh.mm.devel.demo.multinstance;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.app.Activity;


import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class MultipleVideoPlayActivity extends Activity implements SurfaceHolder.Callback
//OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener,
//OnVideoSizeChangedListener, 
{
	private static final String TAG = "MediaCodec";
	private static final int[] SURFACE_RES_IDS =
	{ R.id.video_1_surfaceview, R.id.video_2_surfaceview };

//	private MediaPlayer[] mMediaPlayers = new MediaPlayer[SURFACE_RES_IDS.length];
	private SurfaceView[] mSurfaceViews = new SurfaceView[SURFACE_RES_IDS.length];
	private SurfaceHolder[] mSurfaceHolders = new SurfaceHolder[SURFACE_RES_IDS.length];
	private boolean[] mSizeKnown = new boolean[SURFACE_RES_IDS.length];
	private boolean[] mVideoReady = new boolean[SURFACE_RES_IDS.length];
	private PlayerThread[] mPlayers= new PlayerThread[SURFACE_RES_IDS.length];
	private String[] mUries = new String[SURFACE_RES_IDS.length];

	@Override
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.multi_videos_layout);
		Log.d(TAG,
				"SURFACE_RES_IDS.length=" + SURFACE_RES_IDS.length
						+ ", Environment.getExternalStorageDirectory="
						+ Environment.getExternalStorageDirectory());

		// create surface holders
		for (int i = 0; i < mSurfaceViews.length; i++)
		{
			mSurfaceViews[i] = (SurfaceView) findViewById(SURFACE_RES_IDS[i]);
			mSurfaceHolders[i] = mSurfaceViews[i].getHolder();
			mSurfaceHolders[i].addCallback(this);
			mSurfaceHolders[i].setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		
		
	}

//	public void onBufferingUpdate(MediaPlayer player, int percent)
//	{
//		Log.d(TAG, "MediaPlayer(" + indexOf(player)
//				+ "): onBufferingUpdate percent: " + percent);
//	}
//
//	public void onCompletion(MediaPlayer player)
//	{
//		Log.d(TAG, "MediaPlayer(" + indexOf(player) + "): onCompletion called");
//	}

//	public void onVideoSizeChanged(MediaPlayer player, int width, int height)
//	{
//		Log.v(TAG, "MediaPlayer(" + indexOf(player)
//				+ "): onVideoSizeChanged called");
//		if (width == 0 || height == 0)
//		{
//			Log.e(TAG, "invalid video width(" + width + ") or height(" + height
//					+ ")");
//			return;
//		}
//
//		int index = indexOf(player);
//		if (index == -1)
//			return; // sanity check; should never happen
//		mSizeKnown[index] = true;
//		if (mVideoReady[index] && mSizeKnown[index])
//		{
//			startVideoPlayback(player);
//		}
//	}

//	public void onPrepared(MediaPlayer player)
//	{
//		Log.d(TAG, "MediaPlayer(" + indexOf(player) + "): onPrepared called");
//
//		int index = indexOf(player);
//		if (index == -1)
//			return; // sanity check; should never happen
//		mVideoReady[index] = true;
//		if (mVideoReady[index] && mSizeKnown[index])
//		{
//			startVideoPlayback(player);
//		}
//	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height)
	{
		Log.d(TAG, "SurfaceHolder(" + indexOf(holder)
				+ "): surfaceChanged called," + "width=" + width + ",height="
				+ height);
		
		int index = indexOf(holder);
		mPlayers[index].start();
		
		
//		double w = mMediaPlayers[index].getVideoWidth();
//		double h = mMediaPlayers[index].getVideoHeight();
//
//		if (width > height)
//		{
//			mSurfaceViews[index].setLayoutParams(new LinearLayout.LayoutParams(
//					(int) (height * (w / h)), height));
//			Log.d(TAG, "mediaplayer:" + index + " has changed to width:"
//					+ ((int) (height * (w / h))) + ", height" + height);
//		} 
//		else
//		{
//			mSurfaceViews[index].setLayoutParams(new LinearLayout.LayoutParams(
//					width, (int) (width * (h / w))));
//			Log.d(TAG, "mediaplayer:" + index + " has changed to width:"
//					+ width + ", height" + ((int) (width * (h / w))));
//		}
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		Log.d(TAG, "SurfaceHolder(" + indexOf(holder)
				+ "): surfaceDestroyed called");

		int index = indexOf(holder);
		if (mPlayers[index] != null)
		{
			mPlayers[index].interrupt();
		}
		
	}

	public void surfaceCreated(SurfaceHolder holder)
	{
		Log.d(TAG, "SurfaceHolder(" + indexOf(holder)
				+ "): surfaceCreated called");

		
		int index = indexOf(holder);

		if (mPlayers[index] == null)
		{
			String defaultUri;
			if(index==0)
				defaultUri = "/data/media/0/small.mp4";
			else
				defaultUri = "/data/media/0/test.mp4";
			String uri = System.getProperty("media.test.uri"+index, defaultUri);
			Log.d(TAG, "============="+System.getProperty("media.test.uri0"));
			Log.d(TAG, "index="+index+", uri="+uri);
			mPlayers[index] = new PlayerThread(holder.getSurface(), uri);
			
		}
		
		
//		int index = indexOf(holder);
//		if (index == -1)
//			return; // sanity check; should never happen
//		try
//		{
//			mMediaPlayers[index] = new MediaPlayer();
//			Log.d(TAG, "index=" + index);
//
//			switch (index)
//			{
//			case 0:
//				mMediaPlayers[index]
//						.setDataSource("http://10.18.29.135:81/ali/small.mp4");
//				break;
//			case 1:
//				mMediaPlayers[index]
//						.setDataSource("http://10.18.29.135:81/ali/world.mp4");
//				break;
//			}
//
//			mMediaPlayers[index].setDisplay(mSurfaceHolders[index]);
//			mMediaPlayers[index].prepare();
//			mMediaPlayers[index].setOnBufferingUpdateListener(this);
//			mMediaPlayers[index].setOnCompletionListener(this);
//			mMediaPlayers[index].setOnPreparedListener(this);
//			mMediaPlayers[index].setOnVideoSizeChangedListener(this);
//			mMediaPlayers[index].setAudioStreamType(AudioManager.STREAM_MUSIC);
//		} catch (Exception e)
//		{
//			e.printStackTrace();
//		}
	}

//	@Override
//	protected void onPause()
//	{
//		super.onPause();
//		releaseMediaPlayers();
//	}
//
//	@Override
//	protected void onDestroy()
//	{
//		super.onDestroy();
//		releaseMediaPlayers();
//	}

//	private void releaseMediaPlayers()
//	{
//		for (int i = 0; i < mMediaPlayers.length; i++)
//		{
//			if (mMediaPlayers[i] != null)
//			{
//				mMediaPlayers[i].release();
//				mMediaPlayers[i] = null;
//			}
//		}
//	}

//	private void startVideoPlayback(MediaPlayer player)
//	{
//		Log.v(TAG, "MediaPlayer(" + indexOf(player) + "): startVideoPlayback");
//		player.start();
//	}

//	private int indexOf(MediaPlayer player)
//	{
//		for (int i = 0; i < mMediaPlayers.length; i++)
//			if (mMediaPlayers[i] == player)
//				return i;
//		return -1;
//	}

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

	private class PlayerThread extends Thread
	{
		private MediaExtractor extractor;
		private MediaCodec decoder;
		private Surface surface;
		private String uri;

		public PlayerThread(Surface surface, String uri)
		{
			this.surface = surface;
			this.uri = uri;
		}

		@Override
		public void run()
		{
			extractor = new MediaExtractor();
			extractor.setDataSource(uri);

			for (int i = 0; i < extractor.getTrackCount(); i++)
			{
				MediaFormat format = extractor.getTrackFormat(i);
				String mime = format.getString(MediaFormat.KEY_MIME);
				if (mime.startsWith("video/"))
				{
					extractor.selectTrack(i);
					decoder = MediaCodec.createDecoderByType(mime);
					decoder.configure(format, surface, null, 0);
					break;
				}
			}

			if (decoder == null)
			{
				Log.e("DecodeActivity", "Can't find video info!");
				return;
			}

			decoder.start();

			ByteBuffer[] inputBuffers = decoder.getInputBuffers();
			ByteBuffer[] outputBuffers = decoder.getOutputBuffers();
			BufferInfo info = new BufferInfo();
			boolean isEOS = false;
			long startMs = System.currentTimeMillis();

			while (!Thread.interrupted())
			{
				if (!isEOS)
				{
					int inIndex = decoder.dequeueInputBuffer(10000);
					if (inIndex >= 0)
					{
						ByteBuffer buffer = inputBuffers[inIndex];
						int sampleSize = extractor.readSampleData(buffer, 0);
						if (sampleSize < 0)
						{
							// We shouldn't stop the playback at this point,
							// just pass the EOS
							// flag to decoder, we will get it again from the
							// dequeueOutputBuffer
							Log.d("DecodeActivity",
									"InputBuffer BUFFER_FLAG_END_OF_STREAM");
							decoder.queueInputBuffer(inIndex, 0, 0, 0,
									MediaCodec.BUFFER_FLAG_END_OF_STREAM);
							isEOS = true;
						} else
						{
							decoder.queueInputBuffer(inIndex, 0, sampleSize,
									extractor.getSampleTime(), 0);
							extractor.advance();
						}
					}
				}

				int outIndex = decoder.dequeueOutputBuffer(info, 10000);
				switch (outIndex)
				{
				case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
					Log.d("DecodeActivity", "INFO_OUTPUT_BUFFERS_CHANGED");
					outputBuffers = decoder.getOutputBuffers();
					break;
				case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
					Log.d("DecodeActivity",
							"New format " + decoder.getOutputFormat());
					break;
				case MediaCodec.INFO_TRY_AGAIN_LATER:
					Log.d("DecodeActivity", "dequeueOutputBuffer timed out!");
					break;
				default:
					ByteBuffer buffer = outputBuffers[outIndex];
					Log.v("DecodeActivity",
							"We can't use this buffer but render it due to the API limit, "
									+ buffer);

					// We use a very simple clock to keep the video FPS, or the
					// video
					// playback will be too fast
					while (info.presentationTimeUs / 1000 > System
							.currentTimeMillis() - startMs)
					{
						try
						{
							sleep(10);
						} catch (InterruptedException e)
						{
							e.printStackTrace();
							break;
						}
					}
					decoder.releaseOutputBuffer(outIndex, true);
					break;
				}

				// All decoded frames have been rendered, we can stop playing
				// now
				if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0)
				{
					Log.d("DecodeActivity",
							"OutputBuffer BUFFER_FLAG_END_OF_STREAM");
					break;
				}
			}

			decoder.stop();
			decoder.release();
			extractor.release();
		}
	}
}