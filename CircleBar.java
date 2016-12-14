package org.mding.gym.utils.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import org.mding.gym.R;

import java.text.DecimalFormat;


/**
 * Title: CircleBar
 * CreateTime:2016/11/1  10:22
 *
 * @author shutingChen
 * @version 1.0
 */
public class CircleBar extends View {

    private int mHintColor;
    private int mBarColor;
    private int mTextColor;
    private float mBarMax;
    private float mBarProgress;
    private Paint mPaint;
    private float mBarTextSize;
    private float mBarWidth;
    private String mText;
    private Rect mBound;
    private float barPadding;
    private int mBackgroundColor;
    private float percent;
    private float currentPercent = 0;
    private long duration;
    public static final int FOR_REFRESH = 9000;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FOR_REFRESH:
                    if (currentPercent < percent) {
                        currentPercent = (float) (currentPercent + percent * 0.01);
                        invalidate();
                    }
                    break;
            }
        }
    };
    private DecimalFormat decimalFormat;


    public CircleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleBar, defStyleAttr, 0);
        int n = typedArray.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.CircleBar_barColor:
                    mBarColor = typedArray.getColor(attr, Color.BLUE);
                    break;
                case R.styleable.CircleBar_barTextColor:
                    mTextColor = typedArray.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.CircleBar_barHintColor:
                    mHintColor = typedArray.getColor(attr, Color.TRANSPARENT);
                    break;
                case R.styleable.CircleBar_barBackGroundColor:
                    mBackgroundColor = typedArray.getColor(attr, Color.WHITE);
                    break;
                case R.styleable.CircleBar_barMax:
                    mBarMax = typedArray.getFloat(attr, 1);
                    break;
                case R.styleable.CircleBar_barProgress:
                    mBarProgress = typedArray.getFloat(attr, 0);
                    break;
                case R.styleable.CircleBar_barTextSize:
                    mBarTextSize = typedArray.getDimension(attr, 16);
                    break;
                case R.styleable.CircleBar_barWidth:
                    mBarWidth = typedArray.getDimension(attr, 5);
                    break;
                case R.styleable.CircleBar_barText:
                    mText = typedArray.getString(attr);
                    break;
                case R.styleable.CircleBar_barPadding:
                    barPadding = typedArray.getDimension(attr, 10);
                    break;
                case R.styleable.CircleBar_duration:
                    duration = typedArray.getInteger(attr, 2000);
                    break;
            }
        }
        typedArray.recycle();

        decimalFormat = new DecimalFormat("0.00");
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(mBarTextSize);
        mPaint.setColor(mTextColor);
        mBound = new Rect();
        percent = mBarProgress / mBarMax;
        if (mText == null || mText.trim().length() == 0) {
            mText = decimalFormat.format(percent * 100) + "%";
        }
        mPaint.setTextSize(mBarTextSize);
        mPaint.getTextBounds(mText, 0, mText.length(), mBound);
        currentPercent = 0;

    }


    public CircleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleBar(Context context) {
        this(context, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        percent = mBarProgress / mBarMax;

        mText = decimalFormat.format(percent * 100) + "%";
        mPaint.setTextSize(mBarTextSize);
        mPaint.getTextBounds(mText, 0, mText.length(), mBound);
        int center = getWidth() / 2;

        mPaint.setStyle(Paint.Style.STROKE);//设置空心
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(mHintColor);
        mPaint.setStrokeWidth(mBarWidth);
        canvas.drawArc(new RectF(mBarWidth, mBarWidth, getWidth() - mBarWidth, getWidth() - mBarWidth), 0, 360, false, mPaint);

        mPaint.setColor(mBarColor);
        mPaint.setStrokeWidth(mBarWidth);
        canvas.drawArc(new RectF(mBarWidth, mBarWidth, getWidth() - mBarWidth, getWidth() - mBarWidth), 270, 360 * currentPercent, false, mPaint);
        //绘制文字
        mPaint.setStyle(Paint.Style.FILL);//设置空心
        mPaint.setStrokeWidth(mBarTextSize);
        mPaint.setColor(mTextColor);
        canvas.drawText(mText, center - mBound.width() / 2, center + mBound.height() / 2, mPaint);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep((long) (duration*0.01));
                    handler.sendEmptyMessage(FOR_REFRESH);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void refresh() {
        currentPercent = 0;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = 0;
        int height = 0;
        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                width = widthSize;
                break;
            case MeasureSpec.AT_MOST:
                mPaint.setTextSize(mBarTextSize);
                mPaint.getTextBounds(mText, 0, mText.length(), mBound);
                float textWidth = mBound.width();
                float textHeight = mBound.height();
                float obl = (float) Math.sqrt(Math.pow(textWidth, 2) + Math.pow(textHeight, 2));
                int desired = (int) (getPaddingLeft() + Math.sqrt(Math.pow((obl + barPadding), 2) - Math.pow(textHeight, 2)) * 2 + mBarWidth * 2 + getPaddingRight());
                width = desired;
                break;
        }

        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                height = heightSize;
                break;
            case MeasureSpec.AT_MOST:
                height = width;
                break;
        }
        setMeasuredDimension(width, height);
    }

    public int getmHintColor() {
        return mHintColor;
    }

    public CircleBar setmHintColor(int mHintColor) {
        this.mHintColor = mHintColor;
        refresh();
        return this;
    }

    public int getmBarColor() {
        return mBarColor;
    }

    public CircleBar setmBarColor(int mBarColor) {
        this.mBarColor = mBarColor;
        refresh();
        return this;
    }

    public int getmTextColor() {
        return mTextColor;
    }

    public CircleBar setmTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
        refresh();
        return this;
    }

    public float getmBarMax() {
        return mBarMax;
    }

    public CircleBar setmBarMax(float mBarMax) {
        this.mBarMax = mBarMax;
        refresh();
        return this;
    }

    public float getmBarProgress() {
        return mBarProgress;
    }

    public CircleBar setmBarProgress(float mBarProgress) {
        this.mBarProgress = mBarProgress;
        refresh();
        return this;
    }

    public Paint getmPaint() {
        return mPaint;
    }

    public CircleBar setmPaint(Paint mPaint) {
        this.mPaint = mPaint;
        refresh();
        return this;
    }

    public float getmBarTextSize() {
        return mBarTextSize;
    }

    public CircleBar setmBarTextSize(float mBarTextSize) {
        this.mBarTextSize = mBarTextSize;
        refresh();
        return this;
    }

    public float getmBarWidth() {
        return mBarWidth;
    }

    public CircleBar setmBarWidth(float mBarWidth) {
        this.mBarWidth = mBarWidth;
        refresh();
        return this;
    }

    public String getmText() {
        return mText;
    }

    public CircleBar setmText(String mText) {
        this.mText = mText;
        refresh();
        return this;
    }

    public Rect getmBound() {
        return mBound;
    }

    public CircleBar setmBound(Rect mBound) {
        this.mBound = mBound;
        refresh();
        return this;
    }

    public float getBarPadding() {
        return barPadding;
    }

    public CircleBar setBarPadding(float barPadding) {
        this.barPadding = barPadding;
        refresh();
        return this;
    }

    public int getmBackgroundColor() {
        return mBackgroundColor;
    }

    public CircleBar setmBackgroundColor(int mBackgroundColor) {
        this.mBackgroundColor = mBackgroundColor;
        refresh();
        return this;
    }

    public long getDuration() {
        return duration;
    }

    public CircleBar setDuration(long duration) {
        this.duration = duration;
        return this;
    }
}
