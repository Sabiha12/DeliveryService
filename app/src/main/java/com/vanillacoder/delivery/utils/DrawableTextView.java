package com.vanillacoder.delivery.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class DrawableTextView extends AppCompatTextView {
    private DrawableRightClickListener mDrawableRightClickListener;
    private DrawableLeftClickListener mDrawableLeftClickListener;
    final int DRAWABLE_LEFT = 0;//Left picture
    final int DRAWABLE_TOP = 1;//Picture above
    final int DRAWABLE_RIGHT = 2;//Right picture
    final int DRAWABLE_BOTTOM = 3;//The following picture
    public DrawableTextView(Context context) {
        this(context,null);
    }
    public DrawableTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }
    public DrawableTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    //Define an interface to listen on the right
    public interface DrawableRightClickListener{
        void onDrawableRightClickListener(View view);
    }
    //Define an interface to listen on the left
    public interface DrawableLeftClickListener{
        void onDrawableLeftClickListener(View view);
    }
    //Define a set method for right listening
    public void setDrawableRightClickListener(DrawableRightClickListener listener) {
        this.mDrawableRightClickListener = listener;
    }
    //Define a set method for left listening
    public void setDrawableLeftClickListener(DrawableLeftClickListener listener) {
        this.mDrawableLeftClickListener = listener;
    }
    //Define a right-hand back listening method
    public DrawableRightClickListener getDrawableRightClick() {
        return mDrawableRightClickListener;
    }
    //Define a method to return to listening on the left
    public DrawableLeftClickListener getDrawableLeftClick() {
        return mDrawableLeftClickListener;
    }

    //Rewriting onTouchEvent Method
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mDrawableRightClickListener != null) {
                    Drawable drawableRight = getCompoundDrawables()[DRAWABLE_RIGHT] ;
                    //The judgment is based on obtaining that the x value of the click area relative to the screen is greater than the rightmost boundary of the control minus the width of the right control.
                    if (drawableRight != null && event.getRawX() >= (getRight() - drawableRight.getBounds().width())) {
                        mDrawableRightClickListener.onDrawableRightClickListener(this);
                        return true ;//The return value must be true, otherwise it cannot respond.
                    }
                }
                if (mDrawableLeftClickListener != null) {
                    Drawable drawableLeft = getCompoundDrawables()[DRAWABLE_LEFT] ;
                    //The judgment is based on the fact that the x value of the click area relative to the screen is less than the leftmost boundary of the control plus the width of the left control.
                    if (drawableLeft != null && event.getRawX() <= (getLeft() + drawableLeft.getBounds().width())) {
                        mDrawableLeftClickListener.onDrawableLeftClickListener(this) ;
                        return true ;
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}