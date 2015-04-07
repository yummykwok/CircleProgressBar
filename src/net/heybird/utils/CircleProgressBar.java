package net.heybird.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

public class CircleProgressBar extends ProgressBar {

    private float mScale = 1;
    private final int BASE_RADIUS = 180;
    private final int BASE_PROGRESS_HALF_WIDTH = 10;
    private final int BASE_TEXT_SIZE = 90;
    private final int MIN_ANGLE = 5;

    private String txtProgress;
    private Paint mPaintText, mPaintProgress, mPaintBackColor;
    private Rect txtRect;
    private final int COLOR = Color.parseColor("#51B902");
    private int mRadius;
    private int centerX, centerY;

    private RectF mRectF;
    private Handler mHandler = new Handler();
    private float startAngle = 0;
    private float mAngle = MIN_ANGLE;
    private int mOldProgress = 0;
    private int mProgress = 0;
    private boolean stopAnimation = false;
    private boolean animStated = false;
    private Runnable mRefreshThread = new Runnable(){

        @Override
        public void run() {
            startAngle += 1.5;

            if (startAngle>=360) {
            	startAngle = 0;
            }

            if(mOldProgress<=mProgress || mOldProgress<MIN_ANGLE) {
            	mOldProgress++;
                float percent = ((float) mOldProgress) / ((float)getMax());
                int i = (int) (percent * 100);
                i = i>100 ? 100:i;
                mAngle = 360.0f * percent;
                if (i ==100) {
                	txtProgress = "Done";                	
                } else {
                	txtProgress = String.valueOf(i) + "%";
                }
            }

            if (!stopAnimation) {
                mHandler.postDelayed(mRefreshThread, 10);
            }

            if (getVisibility()==View.VISIBLE && mOldProgress<=getMax()) {
                invalidate();
            }
        }

    };


    private void startAnimation(){
    	if (!animStated) {
    		stopAnimation = false;
        	mHandler.post(mRefreshThread);
        	animStated = true;
    	}
    }

    private void stopAnimation(){
    	mHandler.removeCallbacks(mRefreshThread);
    	stopAnimation = true;
    	animStated = false;
    }

    private void init(){
        txtRect = new Rect();
        mRectF = new RectF();
        mPaintText=new Paint();
        mPaintText.setAntiAlias(true);
        mPaintText.setColor(COLOR);
        mPaintText.setTextAlign(Align.CENTER);
        mPaintProgress = new Paint();
        mPaintProgress.setAntiAlias(true);
        mPaintProgress.setStyle(Style.STROKE);
        mPaintProgress.setColor(COLOR);
        mPaintBackColor = new Paint();
        mPaintBackColor.setStyle(Style.STROKE);
        mPaintBackColor.setAntiAlias(true);
        mPaintBackColor.setColor(COLOR);
    }
    
    public CircleProgressBar(Context context) {
        super(context);
        init();
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override  
    public synchronized void setProgress(int progress) {
        if (progress>getMax()) {
        	progress = getMax();
        }
        mProgress = progress;
        super.setProgress(progress);
    }

    @Override  
    protected synchronized void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        //canvas.drawCircle(centerX, centerY, mRadius, mPaintBackColor);
        this.mPaintText.getTextBounds(this.txtProgress, 0, this.txtProgress.length(), txtRect);
        canvas.drawText(this.txtProgress, centerX, centerY + txtRect.height()/2, this.mPaintText);
        canvas.drawArc(mRectF, startAngle, mAngle, false, mPaintProgress);
        canvas.drawArc(mRectF, 0, 360, false, mPaintBackColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w/2;
        centerY = h/2;
        mRadius = centerX<=centerY ? centerX : centerY;
        mScale = ((float)mRadius) / ((float)BASE_RADIUS);
        mPaintText.setTextSize(BASE_TEXT_SIZE*mScale);
        mPaintProgress.setStrokeWidth(DensityUtil.dip2px(this.getContext(), BASE_PROGRESS_HALF_WIDTH*2*mScale));
        mPaintBackColor.setStrokeWidth(DensityUtil.dip2px(this.getContext(), BASE_PROGRESS_HALF_WIDTH*mScale/5));
        mRectF.top = centerY - mRadius + BASE_PROGRESS_HALF_WIDTH*mScale;
        mRectF.bottom = centerY + mRadius - BASE_PROGRESS_HALF_WIDTH*mScale;
        mRectF.left = centerX - mRadius + BASE_PROGRESS_HALF_WIDTH*mScale;
        mRectF.right = centerX + mRadius - BASE_PROGRESS_HALF_WIDTH*mScale;
        startAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
    	super.onDetachedFromWindow();
    	stopAnimation();
    }
}
