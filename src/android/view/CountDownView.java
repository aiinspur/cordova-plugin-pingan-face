
package com.pingan.eauthsdk.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class CountDownView extends View {

	float o_x;
	float o_y;
	int width;
	int height;
	float userWidth = -1f;
	float useBaseLine = -1f;
	double maxwidth;
	float detaDegree = 0.0f;
	private Bitmap rotatBitmap;
	private Bitmap newBitmap;

	private Paint paint;
	private int countDownText;
	private ValueAnimator animation;

	//private MyApplication myApp = null;

	public CountDownView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initSize();
		//myApp = (MyApplication) context.getApplicationContext(); // 共享信息类创建
	}

	public void setRotatBitmap(Bitmap bitmap) {
		rotatBitmap = bitmap;
		initSize();
		postInvalidate();
	}

	public void setCountDownText(int mCountDownText) {
		this.countDownText = mCountDownText;
		postInvalidate();
	}

	public void setRotatDrawableResource(int id) {
		BitmapDrawable drawable = (BitmapDrawable) getContext().getResources().getDrawable(id);
		setRotatDrawable(drawable);
	}

	public void setRotatDrawable(BitmapDrawable drawable) {
		rotatBitmap = drawable.getBitmap();
		initSize();
		postInvalidate();
	}

	/**
	 * 
	 * @param added
	 */
	private void setDegree(float deta_degree) {
		detaDegree = deta_degree;
		if (detaDegree > 360 || detaDegree < -360) {
			detaDegree = detaDegree % 360;
		}
		postInvalidate();
	}


	private void initSize() {
		if (rotatBitmap == null) {
			return;
		}

		if (newBitmap == null) {
			Matrix _mMatrix = new Matrix();
			float widthRate = ((float) getLayoutParams().width) / ((float) rotatBitmap.getWidth());
			float heightRate = ((float) getLayoutParams().height) / ((float) rotatBitmap.getHeight());
			_mMatrix.preScale(widthRate, heightRate);
			try {
				newBitmap = Bitmap.createBitmap(rotatBitmap, 0, 0, rotatBitmap.getWidth(), rotatBitmap.getHeight(),
						_mMatrix, true);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				return;
			}
		}

		width = getLayoutParams().width;
		height = getLayoutParams().height;

		maxwidth = Math.sqrt(width * width + height * height);

		o_x = o_y = (float) (maxwidth / 2); 

		if (paint == null) {
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setTextSize((float) (maxwidth / 2.5));
			paint.setColor(android.graphics.Color.GRAY);
			paint.setTextAlign(Paint.Align.CENTER);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (newBitmap == null || newBitmap.isRecycled())
			return;

		Matrix matrix = new Matrix();
		matrix.setTranslate((float) width / 2, (float) height / 2);
		matrix.preRotate(detaDegree);
		matrix.preTranslate(-(float) width / 2, -(float) height / 2);
		matrix.postTranslate((float) (maxwidth - width) / 2, (float) (maxwidth - height) / 2);

		canvas.drawBitmap(newBitmap, matrix, null);

		if (userWidth == -1 || useBaseLine == -1) {
			FontMetrics fmFontMetrics = paint.getFontMetrics();
			float fheight = fmFontMetrics.bottom - fmFontMetrics.top;
			userWidth = getWidth() / 2;
			useBaseLine = getHeight() - (getHeight() - fheight) / 2 - fmFontMetrics.bottom;
		}
		
		canvas.drawText(countDownText + "", userWidth, useBaseLine, paint);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension((int) maxwidth, (int) maxwidth);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		Log.v("CountDownView", "onDetachedFromWindow");
		release();
	}

	private void release() {
		if (rotatBitmap != null) {
			rotatBitmap = null;
		}
		if (newBitmap != null) {
			newBitmap.recycle();
			newBitmap = null;
		}
		if (animation != null) {
			animation.removeAllListeners();
			animation.removeAllUpdateListeners();
			animation.cancel();
			animation = null;
		}
		if (paint != null) {
			paint = null;
		}
	}

	public void autoCountDown(long timer) {
		detaDegree = detaDegree + 18;
		if (detaDegree > 360 || detaDegree < -360) {
			detaDegree = detaDegree % 360;
		}
		setCountDownText((int) (timer / 1000));
	}

	int mCount = 0;

	public void startWithClockCountDown(int countDown, final CountDownListener countDownListener) {
		if (countDown < 1) {
			return;
		}
		mCount = countDown;

		if (animation != null) {
			animation.removeAllListeners();
			animation.removeAllUpdateListeners();
			animation.cancel();
			animation = null;
		}

		animation = ValueAnimator.ofFloat(0.0f, 360.0f);
		// 在下面的代码中，当start方法调用的时候，ValueAnimator 在0和1之间计算动画的值，持续时间是1000ms.
		animation.setDuration(1000);

		if (countDown > 1) {
			animation.setRepeatCount(countDown - 1);
		}
		animation.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float degree = (Float) animation.getAnimatedValue();
				setDegree(degree);
			}
		});
		animation.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
				setCountDownText(mCount);
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
				mCount = mCount - 1;
				//myApp.setCountDown(mCount);
				System.out.println("1.mCount:" + String.valueOf(mCount));
				setCountDownText(mCount);
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				mCount = mCount - 1;
				setCountDownText(mCount);
				if (countDownListener != null) {
					countDownListener.onComplete();
				}
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}
		});

		animation.setInterpolator(new LinearInterpolator());
		animation.start();
	}

	public void stopWithClockCountDown() {
		if (animation != null) {
			animation.removeAllListeners();
			animation.removeAllUpdateListeners();
			animation.cancel();
			animation = null;
		}
	}

	public interface CountDownListener {
		public void onComplete();
	}

}
