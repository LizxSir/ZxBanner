package com.example.banner;

import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.widget.ProgressBar;

public class ProgressIndicator extends ProgressBar implements Indicator {
    private int height;

    private int count;

    private int mUnSelectColor;
    private int mSelectColor;

    public ProgressIndicator(Context context) {
        super(context,null,android.R.style.Widget_DeviceDefault_Light_ProgressBar_Horizontal);

    }


    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        setBackgroundColor(mUnSelectColor);
        setProgressDrawable(new ClipDrawable(new ColorDrawable(mSelectColor), Gravity.LEFT, ClipDrawable.HORIZONTAL));
    }

    @Override
    public void setRadio(int radio) {
        this.height = 2 * radio;
        requestLayout();
    }

    @Override
    public void setCount(int count) {
        this.count = count;
        setMax(count);
    }

    @Override
    public void setCurrent(int index) {
        if(this.count <= 0)
            return;

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N){
            setProgress(index+1,true);
        }else{
            setProgress(index+1);
        }
    }

    @Override
    public void setUnSelectColor(int color) {

        mUnSelectColor = color;

    }

    @Override
    public void setSelectColor(int color) {

        mSelectColor = color;
    }

    @Override
    public void setMargin(int margin) { }
}
