package com.example.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

public class ZxBanner extends ConstraintLayout implements LifecycleObserver {

    private static final int DEFAULT_INTERVAL = 3000;
    private static final int DEFAULT_SCROLL_TIME = 1000;

    private static final int SCROLL_MODE_VERTICAL = 1;
    private static final int SCROLL_MODE_HORIZONTAL = 0;
    private static final int INDICATOR_STYLE_CIRCLE = 1;
    private static final int INDICATOR_STYLE_PROGRESS = 2;

    private static final int DEFAULT_MARGIN_DP = 12;
    private static final int DEFAULT_INDICATOR_RADIO = 3;
    private static final int DEFAULT_TITLE_SIZE_DP = 12;

    LifecycleOwner mLifecycleOwner;
    private ViewPager2 mPager;
    private TextView mTitle;

    private Indicator mIndicator;

    private ArrayList<? extends IBannerData> mDatas;

    private int mIds = 0x1000;

    private int mIndicatorEndMargin;
    private int mInterval;  // 切换间隔时间
    private int mScrollTime; // 动画执行时间
    private int mScrollMode; //  切换模式，垂直或者水平方向
    private int mMarginTitleStart;
    private int mTitleTextSize;
    private int mTitleTextColor;
    private int mIndicatorRadio;
    private int mIndicatorSelectColor;
    private int mIndicatorUnSelectColor;
    private int mIndicatorMargin;
    private int mTitleMarginTop;
    private int mTitleMarginBottom;
    private int mIndicatorStyle;

    private boolean mTitleMarquee;
    private boolean mIsShowTitleBgView; // 是否显示 title 的那个半透明背景

    private boolean mIsAutoLoop = true; // 是否自动循环

    public ZxBanner(Context context) {
        super(context);
    }

    public ZxBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        initValue(attrs);

    }

    public ZxBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initValue(attrs);
    }



    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();


    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void initValue(AttributeSet attributeSet){

        TypedArray array = getContext().obtainStyledAttributes(attributeSet, R.styleable.ZxBanner);

        /**
         *   private int mIndicatorEndMargin;
         *     private int mInterval;  // 切换间隔时间
         *     private int mScrollTime; // 动画执行时间
         *     private int mScrollMode; //  切换模式，垂直或者水平方向
         *     private int mMarginTitleStart;
         *     private int mTitleTextSize;
         *     private int mTitleTextColor;
         *     private int mIndicatorRadio;
         *     private int mIndicatorSelectColor;
         *     private int mIndicatorUnSelectColor;
         *     private int mTitleMarginTop;
         *     private int mTitleMarginBottom;
         *      private boolean mShowTitleBgView;
         *       private boolean isAutoLoop = true;
         */



        mTitleMarginTop = array.getDimensionPixelSize(R.styleable.ZxBanner_bannerTitleMarginTop,DEFAULT_MARGIN_DP);
        mTitleMarginBottom = array.getDimensionPixelSize(R.styleable.ZxBanner_bannerTitleMarginBottom,DEFAULT_MARGIN_DP);

        mIsShowTitleBgView = array.getBoolean(R.styleable.ZxBanner_bannerIsShowTitleBgView,true);
        mIsAutoLoop = array.getBoolean(R.styleable.ZxBanner_bannerIsAutoLoop,true);

        mIndicatorSelectColor = array.getColor(R.styleable.ZxBanner_bannerIndicatorSelectColor,Color.GRAY);
        mIndicatorUnSelectColor = array.getColor(R.styleable.ZxBanner_bannerIndicatorUnSelectColor,Color.WHITE);
        mIndicatorEndMargin = array.getDimensionPixelSize(R.styleable.ZxBanner_bannerIndicatorEndMargin,dip2px(DEFAULT_MARGIN_DP));
        mIndicatorMargin = array.getDimensionPixelSize(R.styleable.ZxBanner_bannerIndicatorMargin,dip2px(DEFAULT_INDICATOR_RADIO));

        mInterval = array.getInt(R.styleable.ZxBanner_bannerPageSwitchInterval,DEFAULT_INTERVAL);

        mScrollTime = array.getInt(R.styleable.ZxBanner_bannerPageScrollTime,DEFAULT_SCROLL_TIME);
        mScrollMode = array.getInt(R.styleable.ZxBanner_bannerPageScrollMode,SCROLL_MODE_HORIZONTAL);
        mMarginTitleStart = array.getDimensionPixelSize(R.styleable.ZxBanner_bannerMarginTitleStart,DEFAULT_MARGIN_DP);
        mTitleTextSize = array.getDimensionPixelSize(R.styleable.ZxBanner_bannerTitleTextSize,dip2px(DEFAULT_TITLE_SIZE_DP));
        mTitleTextColor = array.getColor(R.styleable.ZxBanner_bannerTitleTextColor,Color.WHITE);
        mIndicatorRadio = array.getDimensionPixelOffset(R.styleable.ZxBanner_bannerIndicatorRadio,dip2px(DEFAULT_INDICATOR_RADIO));

        mTitleMarquee = array.getBoolean(R.styleable.ZxBanner_bannerTitleMarquee,true);

        mIndicatorStyle = array.getInt(R.styleable.ZxBanner_bannerIndicatorStyle,INDICATOR_STYLE_CIRCLE);

        array.recycle();

    }



    private void initView(){

        // Step1 : 添加一个ViewPager2

        mPager = new ViewPager2(getContext());
        mPager.setId(mIds++);


        mPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {

                mTitle.setText(mDatas.get(position % mDatas.size()).getTitle());

                mIndicator.setCurrent(position % mDatas.size());
            }
        });

        addView(mPager);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this);

        constraintSet.connect(mPager.getId(),ConstraintSet.START,ConstraintSet.PARENT_ID,ConstraintSet.START);
        constraintSet.connect(mPager.getId(),ConstraintSet.TOP,ConstraintSet.PARENT_ID,ConstraintSet.TOP);
        constraintSet.connect(mPager.getId(),ConstraintSet.END,ConstraintSet.PARENT_ID,ConstraintSet.END);
        constraintSet.connect(mPager.getId(),ConstraintSet.BOTTOM,ConstraintSet.PARENT_ID,ConstraintSet.BOTTOM);

        constraintSet.constrainWidth(mPager.getId(),ConstraintSet.MATCH_CONSTRAINT);
        constraintSet.constrainHeight(mPager.getId(),ConstraintSet.MATCH_CONSTRAINT);


        // Step2： 添加一个Title 的 半透明背景

        ImageView mask = new ImageView(getContext());
        mask.setBackgroundColor(Color.parseColor("#40000000"));
        mask.setId(mIds++);
        addView(mask);


        constraintSet.connect(mask.getId(),ConstraintSet.START,ConstraintSet.PARENT_ID,ConstraintSet.START);
        constraintSet.connect(mask.getId(),ConstraintSet.END,ConstraintSet.PARENT_ID,ConstraintSet.END);
        constraintSet.connect(mask.getId(),ConstraintSet.BOTTOM,ConstraintSet.PARENT_ID,ConstraintSet.BOTTOM);
        constraintSet.constrainWidth(mask.getId(),ConstraintSet.MATCH_CONSTRAINT);

        TextView textView = new TextView(getContext());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,mTitleTextSize);
        textView.setSingleLine(true);

        int measureWidth = View.MeasureSpec.makeMeasureSpec(getResources().getDisplayMetrics().widthPixels, View.MeasureSpec.AT_MOST);
        int measureHeight = View.MeasureSpec.makeMeasureSpec(getResources().getDisplayMetrics().heightPixels, View.MeasureSpec.AT_MOST);

        textView.measure(measureWidth,measureHeight);

        int height = textView.getMeasuredHeight();
        constraintSet.constrainHeight(mask.getId(),height + mTitleMarginBottom + mTitleMarginTop);
        if(!mIsShowTitleBgView){
            mask.setVisibility(INVISIBLE);
        }
        // step3 添加indicator

        if(mIndicatorStyle == INDICATOR_STYLE_CIRCLE){
            mIndicator = new CircleIndicator(getContext());
            mIndicator.setMargin(mIndicatorMargin);
        }else{
            mIndicator = new ProgressIndicator(getContext());
        }


        mIndicator.setUnSelectColor(mIndicatorUnSelectColor);
        mIndicator.setSelectColor(mIndicatorSelectColor);
        mIndicator.setRadio(mIndicatorRadio);
        mIndicator.setId(mIds++);
        addView((View) mIndicator);


        if(mIndicatorStyle == INDICATOR_STYLE_CIRCLE){
            constraintSet.connect(mIndicator.getId(),ConstraintSet.END,ConstraintSet.PARENT_ID,ConstraintSet.END,mIndicatorEndMargin);
            constraintSet.connect(mIndicator.getId(),ConstraintSet.BOTTOM,mask.getId(),ConstraintSet.BOTTOM);
            constraintSet.connect(mIndicator.getId(),ConstraintSet.TOP,mask.getId(),ConstraintSet.TOP);

            constraintSet.constrainWidth(mIndicator.getId(),ConstraintSet.WRAP_CONTENT);
            constraintSet.constrainHeight(mIndicator.getId(),ConstraintSet.WRAP_CONTENT);
        }else{

            constraintSet.connect(mIndicator.getId(),ConstraintSet.END,ConstraintSet.PARENT_ID,ConstraintSet.END,mIndicatorEndMargin);
            constraintSet.connect(mIndicator.getId(),ConstraintSet.START,ConstraintSet.PARENT_ID,ConstraintSet.START,mIndicatorEndMargin);
            constraintSet.connect(mIndicator.getId(),ConstraintSet.BOTTOM,ConstraintSet.PARENT_ID,ConstraintSet.BOTTOM);
            constraintSet.connect(mIndicator.getId(),ConstraintSet.TOP,mask.getId(),ConstraintSet.BOTTOM);

            constraintSet.constrainWidth(mIndicator.getId(),ConstraintSet.WRAP_CONTENT);
            constraintSet.constrainHeight(mIndicator.getId(),ConstraintSet.WRAP_CONTENT);
        }

        // step4 添加 title
        mTitle = new TextView(getContext());
        mTitle.setId(mIds++);
        mTitle.setTextColor(mTitleTextColor);
        mTitle.setSingleLine(true);
        if(mTitleMarquee){
            mTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            mTitle.setSelected(true);
            mTitle.setMarqueeRepeatLimit(-1);
        }else{
            mTitle.setEllipsize(TextUtils.TruncateAt.END);
        }

        mTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,mTitleTextSize);
        mTitle.setTextColor(mTitleTextColor);

        addView(mTitle);

        constraintSet.connect(mTitle.getId(),ConstraintSet.START,ConstraintSet.PARENT_ID,ConstraintSet.START,mMarginTitleStart);

        if(mIndicatorStyle == INDICATOR_STYLE_CIRCLE){
            constraintSet.connect(mTitle.getId(),ConstraintSet.END,mIndicator.getId(),ConstraintSet.START,mIndicatorEndMargin);

        }else{
            constraintSet.connect(mTitle.getId(),ConstraintSet.END,ConstraintSet.PARENT_ID,ConstraintSet.END,mIndicatorEndMargin);
        }

        constraintSet.connect(mTitle.getId(),ConstraintSet.TOP,mask.getId(),ConstraintSet.TOP,mTitleMarginTop);

        constraintSet.constrainWidth(mTitle.getId(),ConstraintSet.MATCH_CONSTRAINT);
        constraintSet.constrainHeight(mTitle.getId(),ConstraintSet.WRAP_CONTENT);

        constraintSet.applyTo(this);


    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {


        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:{
                stopLoop();
                break;
            }

            case MotionEvent.ACTION_MOVE:{
                if(mScrollMode == SCROLL_MODE_HORIZONTAL){
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
            }

            case MotionEvent.ACTION_UP:{
                startLoop();
            }

        }


        return  super.dispatchTouchEvent(ev);


    }


    public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        this.mLifecycleOwner = lifecycleOwner;

        mLifecycleOwner.getLifecycle().addObserver(this);
    }

    public void setData(BannerAdapter adapter){

        mDatas = adapter.getDataList();

        if(mDatas == null || mDatas.size() == 0){
            return;
        }

        mPager.setAdapter(adapter);

        mPager.setUserInputEnabled(true);

        if(mScrollMode == SCROLL_MODE_HORIZONTAL){
            mPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        }else{
            mPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        }


        ScrollSpeedManger.reflectLayoutManager(this);

        int initPosition = Integer.MAX_VALUE /2;

        initPosition  =  initPosition - (initPosition % mDatas.size());

        mPager.setCurrentItem(initPosition,false);

        mIndicator.setCount(mDatas.size());
        mIndicator.setCurrent(initPosition % mDatas.size());

        startLoop();
    }



    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if(visibility == VISIBLE){
            startLoop();
        }else{
            stopLoop();
        }
    }


    private Runnable mLoopTask = new Runnable() {
        @Override
        public void run() {
            int cIndex = mPager.getCurrentItem();
            mPager.setCurrentItem(++cIndex, true);
            Log.d("Test3"," start switch " + ZxBanner.this.hashCode());
            getHandler().postDelayed(this, mInterval);
        }
    };

    private boolean isOnResume = false;
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume(){
        isOnResume = true;
        startLoop();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause(){
        isOnResume = false;
        stopLoop();
    }


    public void startLoop(){
        Log.d("Test3"," start Loop " + hashCode());

        stopLoop();
        if(mIsAutoLoop && (mDatas != null &&  mDatas.size() > 1) && (getVisibility() == VISIBLE) && isOnResume ){
            getHandler().postDelayed(mLoopTask, mInterval);
        }
    }



    public void stopLoop(){
        Log.d("Test3"," stop Loop " + hashCode());
        if(getHandler() != null){
            getHandler().removeCallbacks(mLoopTask);
        }


    }

    public int getScrollTime(){

        return mScrollTime;
    }

    public ViewPager2 getViewPager2(){

        return mPager;
    }


    public  int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
