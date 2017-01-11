package com.pingan.eauthsdk.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * 摄像头界面
 * 矩形框
 * @author Administrator
 *
 */
public class FaceMaskView extends View {
	
	Paint localPaint = null;
	RectF mFaceRect = null;
	RectF mDrawRect = null;
	private int high_colour = 0xffff0000;
	private boolean isFrontalCamera = true;

	public FaceMaskView(Context context, AttributeSet atti) {
		super(context, atti);
		mDrawRect = new RectF();
		localPaint = new Paint();
		localPaint.setColor(high_colour);
		localPaint.setStrokeWidth(5);
		localPaint.setStyle(Paint.Style.STROKE);
	}

	public void setRect(RectF rectF) {
		mFaceRect = rectF;
		postInvalidate();
	}

	public void setFrontal(boolean isFrontal) {
		this.isFrontalCamera = isFrontal;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if (mFaceRect == null)
			return;
		if (isFrontalCamera) {
			mDrawRect.set(getWidth() * (1 - mFaceRect.right), getHeight()
					* mFaceRect.top, getWidth() * (1 - mFaceRect.left),
					getHeight() * mFaceRect.bottom);
		} else {
			mDrawRect.set(getWidth() * mFaceRect.left, getHeight()
					* mFaceRect.top, getWidth() * mFaceRect.right, getHeight()
					* mFaceRect.bottom);
		}
		canvas.drawRect(mDrawRect, localPaint);
	}

}
