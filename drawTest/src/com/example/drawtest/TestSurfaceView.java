package com.example.drawtest;


import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class TestSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

	private Thread draw_thread = null;
	private Bitmap backgroundBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.brick);
	private Context context = null;
	public TestSurfaceView(Context context) {
		super(context);
		this.context = context;
		getHolder().addCallback(this);
		initialize();
	}

	@Override
	public void run() {
		while (draw_thread!=null) {
			doDraw(getHolder());
		}
	}

	private void doDraw(SurfaceHolder holder) {
		Canvas c = holder.lockCanvas();
		if(c == null){
			return;
		}

		if(backgroundBitmap != null){
			c.drawBitmap(backgroundBitmap, 0,0,null);
		}

		holder.unlockCanvasAndPost(c);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		initialize();
		draw_thread = new Thread(this);
		draw_thread.start();
	}

	private void initialize() {
		android.graphics.Point p = getRealSize();
		Bitmap bitmap = backgroundBitmap;

		backgroundBitmap = Bitmap.createBitmap(p.x,p.y,Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(backgroundBitmap);

		Matrix matrix = new Matrix();
		float scaleX = 2;
		float scaleY = 2;
		matrix.postScale(scaleX, scaleY);

		Bitmap tempBitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
		c.drawBitmap(tempBitmap,0,0, null);// <<---This line

		int tempWidth = tempBitmap.getWidth();

		for (int i=0;i<p.x/tempWidth;i++){
			for(int j=0;j<p.y/tempWidth;j++){
				c.drawBitmap(tempBitmap, i*tempWidth,j*tempWidth, null);
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		draw_thread = null;
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public Point getRealSize() {//kokodesakiniWIndowmanagerwodounikasinaitodame

		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point real = new Point(0, 0);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			// Android 4.2以上
			display.getRealSize(real);
			return real;

		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			// Android 3.2以上
			try {
				Method getRawWidth = Display.class.getMethod("getRawWidth");
				Method getRawHeight = Display.class.getMethod("getRawHeight");
				int width = (Integer) getRawWidth.invoke(display);
				int height = (Integer) getRawHeight.invoke(display);
				real.set(width, height);
				return real;

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
			int temp = display.getWidth();
			real.set(temp, display.getHeight());
		}

		return real;
	}

}
