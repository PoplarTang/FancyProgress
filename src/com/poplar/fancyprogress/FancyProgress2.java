package com.poplar.fancyprogress;

import static com.poplar.fancyprogress.utils.Utils.evalute;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

public class FancyProgress2 extends View {

	private static final float mWidth = 100f;
	private Paint mPaint;
	
	public FancyProgress2(Context context) {
		this(context, null);
	}

	public FancyProgress2(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FancyProgress2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	}

	private PointF[] mBallCenters = new PointF[]{
		new PointF(0, 0),	
		new PointF(mWidth, 0)	
	};
	private int[] COLORS = new int[]{
		Color.parseColor("#00AAD0"),	
		Color.parseColor("#FD5652")
	};
	private float mRadius = 16f;
	private float mMaxRadius = 20f;
	private float mMinRadius = 12f;
	private float mPercent = 0f;
	
	public FancyProgress2 show(){
		setVisibility(View.VISIBLE);
		mAnim = ValueAnimator.ofFloat(1.0f);
		mAnim.setInterpolator(new LinearInterpolator());
		mAnim.setDuration(2000);
		mAnim.setRepeatCount(ValueAnimator.INFINITE);
		mAnim.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				mPercent = animation.getAnimatedFraction();
				isFront = mPercent < 0.5f;
				invalidate();
			}
		});
		mAnim.start();
		invalidate();
		
		return this;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		int measuredWidth = MeasureSpec.makeMeasureSpec((int)(mRadius * 1.5f * 2f + mWidth), MeasureSpec.EXACTLY);
		int measuredHeight = MeasureSpec.makeMeasureSpec((int)(mRadius * 1.5f * 2f), MeasureSpec.EXACTLY);
		
		setMeasuredDimension(measuredWidth, measuredHeight);
	}

	
	boolean isFront = true;
	private ValueAnimator mAnim;
	@Override
	protected void onDraw(Canvas canvas) {
		
		canvas.save();
		
		canvas.translate(mRadius * 1.5f, mRadius* 1.5f);
		
		if(isFront){
			for (int i = mBallCenters.length - 1; i >= 0 ; i--) {
				drawBall(canvas, i);
			}
		}else {
			for (int i = 0; i < mBallCenters.length; i++) {
				drawBall(canvas, i);
			}
		}
		
		canvas.restore();
		
	}

	private void drawBall(Canvas canvas, int i) {
		mPaint.setColor(COLORS[i]);
		PointF mCurrentCenter = getCurrentCenter(i , mPercent);
		float mCurrentRadius = getCurrentRadius(i, mPercent, i == 0 ? isFront : !isFront);
		canvas.drawCircle(mCurrentCenter.x, mCurrentCenter.y, mCurrentRadius, mPaint);
	}

	private float getCurrentRadius(int i, float percent, boolean front) {
		float fraction = percent % 0.5f / 0.5f;
		
		// 0f -> 1.0f -> 0f
		float tempFraction = 1 - Math.abs(fraction - 0.5f) * 2;
		
		if(front){
			// 放大
			return evalute(tempFraction, mRadius, mMaxRadius);
		}else {
			// 缩小
			return evalute(tempFraction, mRadius, mMinRadius);
		}
	}

	private PointF getCurrentCenter(int i, float percent) {
		
		PointF startP = null;
		PointF endP = null;
		float fraction = percent % 0.5f / 0.5f;
		
		int startOffset = (int) (percent / 0.5f);
		if(startOffset == 0 && i == 0){
			startP = mBallCenters[0];
			endP = mBallCenters[1];
		}else if (startOffset == 1 && i == 1) {
			startP = mBallCenters[0];
			endP = mBallCenters[1];
		}else {
			startP = mBallCenters[1];
			endP = mBallCenters[0];
		}

		return new PointF(evalute(fraction, startP.x, endP.x), evalute(fraction, startP.y, endP.y));
	}
	
	public void dismiss(){
		setVisibility(View.INVISIBLE);
		if(mAnim != null){
			mAnim.end();
		}
	}
}






