package com.itheima.liaotianqi.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/*
 * @创建者     Administrator
 * @创建时间   2016/12/1 16:15
 * @描述	      ${TODO}
 *
 * @更新者     $Author$
 * @更新时间   $Date$
 * @更新描述   ${TODO}
 */
public class EmojiView extends ViewGroup {
    private Context mContext;
    private boolean flag = false; //用于弹出弹出交换
    private Scroller mScroller;
    public EmojiView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public EmojiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public EmojiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    public void init(){
        mScroller = new Scroller(getContext());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        View liaoTianJieMian = getChildAt(0);
        View emoji = getChildAt(1);
        getChildAt(0).layout(l,t,r,b);

        getChildAt(1).layout(l,liaoTianJieMian.getMeasuredHeight(),r,liaoTianJieMian.getMeasuredHeight()+emoji.getMeasuredHeight());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View liaoTianJieMian = getChildAt(0);
        liaoTianJieMian.measure(widthMeasureSpec,heightMeasureSpec);

        View emoji = getChildAt(1);
        emoji.measure(widthMeasureSpec,emoji.getLayoutParams().height);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    public void xianShi(){
            View liaoTianJieMian = getChildAt(0);
            View emoji = getChildAt(1);
            mScroller.startScroll(0,0,0,emoji.getMeasuredHeight(),0);
//            scrollTo(0,emoji.getMeasuredHeight());
            invalidate();
    }
    public void yinCang(){
        View liaoTianJieMian = getChildAt(0);
        View emoji = getChildAt(1);
        mScroller.startScroll(0,emoji.getMeasuredHeight(),0,-emoji.getMeasuredHeight(),0);
//                    scrollTo(0,0);
        invalidate();
    }
    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mScroller.computeScrollOffset()) {
            int currY = mScroller.getCurrY();
            scrollTo(0, currY); // 滚过去
            invalidate();
        }
    }
}
