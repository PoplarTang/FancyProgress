package com.poplar.fancyprogress;
import static com.poplar.fancyprogress.utils.Utils.evalute;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

public class FancyProgress4 extends View {

	private int[] COLORS = new int[] { 
			Color.parseColor("#FC5652"),
			Color.parseColor("#1E4B58"), 
			Color.parseColor("#FEB364"),
			Color.parseColor("#00B29E"), 
	};
	private float mRadius = 16f;
	private float mDistance2Next = 50.0f;
	
	private int mOneShotDuration = 1000;
	
	private float mStayPercent = 0.1f;
	
	private PointF[] mBallCenters = new PointF[]{
			new PointF(0, 0),
			new PointF(0 + mDistance2Next, 0),
			new PointF(0 + mDistance2Next, 0 + mDistance2Next),
			new PointF(0, 0 + mDistance2Next),
	};
	private float mPercent = 0;
	

	private Paint mPaint;
	private ValueAnimator mAnim;

	public FancyProgress4(Context context) {
		this(context, null);
	}

	public FancyProgress4(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FancyProgress4(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		
	}
	public void show(){
		mAnim = ValueAnimator.ofFloat(1.0f);
		mAnim.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				mPercent = animation.getAnimatedFraction();
				invalidate();
			}
		});
		mAnim.setInterpolator(new LinearInterpolator());
		mAnim.setRepeatCount(ValueAnimator.INFINITE);
		mAnim.setDuration(mOneShotDuration * 4);
		mAnim.start();
	}

	private float mStartX;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		switch (MotionEventCompat.getActionMasked(event)) {
			case MotionEvent.ACTION_DOWN:
				mStartX = event.getRawX();
				break; 
			case MotionEvent.ACTION_MOVE:
				
				float distance = event.getRawX() - mStartX;
				long playTime = (long) ((distance * 20) % (mOneShotDuration * 4));
				mAnim.setCurrentPlayTime(playTime);
				
				break;
			case MotionEvent.ACTION_UP:
				break;

			default:
				break;
		}
		
		return true;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		int measuredWidth = MeasureSpec.makeMeasureSpec((int)(mRadius * 2 + mDistance2Next), MeasureSpec.EXACTLY);
		int measuredHeight = MeasureSpec.makeMeasureSpec((int)(mRadius * 2 + mDistance2Next), MeasureSpec.EXACTLY);
		
		setMeasuredDimension(measuredWidth, measuredHeight);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.save();
		
		canvas.translate(mRadius, mRadius);
		
		for (int i = 0; i < mBallCenters.length; i++) {
			mPaint.setColor(COLORS[i]);
			
			PointF mCurrentCenter = getCurrentCenter(i, mPercent);
			canvas.drawCircle(mCurrentCenter.x, mCurrentCenter.y, mRadius, mPaint);
		}
		
		canvas.restore();
	}

	/**
	 * 获取当前坐标中心
	 * @param pointF
	 * @param i
	 * @param percent
	 * @return
	 */
	private PointF getCurrentCenter(int i, float percent) {
		
		PointF startP = null;
		PointF endP = null;
		float fraction = 0;
		
		int startOffset = (int) (percent / 0.25f);
		int endOffset = startOffset + 1;
		
		startP = getPointFromOffset(startOffset, i);
		endP = getPointFromOffset(endOffset, i);
		
		fraction = percent % 0.25f / 0.25f;
		Log.d("TAG", "center percent: " + (float)(Math.round(percent*100))/100);
		
		if(fraction < mStayPercent){
			return startP;
		}else if (fraction > 1 - mStayPercent) {
			return endP;
		}else {
			fraction = (fraction - mStayPercent) / (1.0f - mStayPercent * 2f);
			return new PointF(evalute(fraction, startP.x, endP.x), evalute(fraction, startP.y, endP.y));
		}
	}
	
	private PointF getPointFromOffset(int startOffset, int i) {
		return mBallCenters[Math.abs(i + startOffset) % mBallCenters.length];
	}

	public void dismiss(){
		setVisibility(View.INVISIBLE);
		if(mAnim != null){
			mAnim.end();
		}
	}

}
