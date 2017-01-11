package com.pingan.eauthsdk.view;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.pingan.eauthsdk.util.GetResourceUtil;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 自定义控件：播放动画
 * 
 * @author tarena
 * 
 */
public class AnimationView extends View {
	Bitmap[] bitmaps = null;
	/**
	 * 当前播的是第几张图
	 */
	int currentIndex = 0;
	int viewWidth, viewHeight;
	int sleepTime = 1;
	boolean isRunning = true;
	Thread thread;
	Context context;
	Paint paint = new Paint();
	public AnimationView(Context context,
			AttributeSet attrs) {
		super(context, attrs);
		//读取xml中的属性
		sleepTime=1000;

		// 加载数组,数组是一个resources
		TypedArray taImage=context.
				getResources().
				obtainTypedArray(GetResourceUtil.getResourceByType(context,"array", "animationImages"));
		int length=taImage.length();
		bitmaps=new Bitmap[length];

		for(int i=0;i<length;i++)
		{
			int imageResId=taImage.getResourceId(i, 0);
			bitmaps[i]=getimage(imageResId);
		}

		thread = new Thread(new MyRunnable());
		thread.start();
		

	}
	//Measure测量：测量控件的宽度，高度
	@Override
	protected void onMeasure(int widthMeasureSpec, 
			int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		//测量模式
		int mode=MeasureSpec.getMode(heightMeasureSpec);
		//判断模式
		//AT_MOST是一种测量模式：控件的大小可以和父容器一样大
		if (mode==MeasureSpec.AT_MOST)
		{
			int imageWidth= bitmaps[0].getWidth();
			int imageHeight= bitmaps[0].getHeight();
			//设置控件的大小是图片的大小
			setMeasuredDimension(imageWidth, imageHeight);
		}
	}
	@Override
	protected void onDraw(Canvas canvas) {
		
		Bitmap bitmap = bitmaps[currentIndex];
		if (bitmap!=null)
		{
			// 居中
			int x = (viewWidth - bitmap.getWidth()) / 2;
			int y = (viewHeight - bitmap.getHeight()) / 2;
			canvas.drawBitmap(bitmap, x, y, paint);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		viewHeight = h;
		viewWidth = w;
	}
	
	private Bitmap getimage(int imageResId) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageResId, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 70f;//这里设置高度为70f

        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
          be = (int) (newOpts.outHeight / hh);
      }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeResource(getResources(), imageResId, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }
	
	private Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024>100) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩       
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中

        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

	class MyRunnable implements Runnable {

		@Override
		public void run() {
			while (isRunning) {
				try {
					currentIndex++;
					if (currentIndex >= bitmaps.length) {
						currentIndex = 0;
					}
					// 调用onDraw
					// invalidate()用在主线程
					// 工作线程
					postInvalidate();
					Thread.sleep(sleepTime);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
