package demo.com.tableview.pag_2.test;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import demo.com.tableview.BuildConfig;

/**
 * Created by ${LuoChen} on 2017/6/19 11:44.
 * email:luochen0519@foxmail.com
 */

public class ScrollGroup extends RelativeLayout {

    private VelocityTracker mTracker;
    private float mStartX;
    private float mStartY;
    private float maxVelocity;//finging时的最大速率
    private float minVelocity;//finging时的最小速率
    private int unitis = 800;//一秒内移动多少个像素
    private Fling mFling;
    private View mView;

    public ScrollGroup(Context context) {
        this(context, null);
    }

    public ScrollGroup(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ScrollGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mTracker = VelocityTracker.obtain();

        mFling = new Fling(context);

        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        minVelocity = configuration.getScaledMinimumFlingVelocity();
        maxVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mView = getChildAt(0);

        if (mTracker == null) {
            mTracker = VelocityTracker.obtain();
        }
        mTracker.addMovement(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:


                mStartX = event.getX();
                mStartY = event.getY();

                break;
            case MotionEvent.ACTION_MOVE:

                int currentX = (int) event.getRawX();
                int currentY = (int) event.getRawY();

                int dix = (int) (currentX - mStartX);
                int diy = (int) (currentY - mStartY);

//                scrollBy(-dix, -diy);

                mView.layout(currentX, currentY, currentX + mView.getWidth(), currentY + mView.getHeight());


                postInvalidate();

                mStartX = currentX;
                mStartY = currentY;
                break;
            case MotionEvent.ACTION_UP:


                mTracker.computeCurrentVelocity(unitis, maxVelocity);
                float xVelocity = mTracker.getXVelocity();
                float yVelocity = mTracker.getYVelocity();

                if (BuildConfig.DEBUG)
                    Log.e("ScrollGroup", "xVelocity:" + xVelocity + " yVelocity=" + yVelocity);

                if (Math.abs(xVelocity) > minVelocity || Math.abs(yVelocity) > minVelocity) {
                    mFling.start((int) mStartX, (int) mStartY, (int) xVelocity, (int) yVelocity);
                } else {
                    if (mTracker != null) {
                        mTracker.recycle();
                        mTracker = null;
                    }
                }

                break;
        }

        return true;
    }


    private class Fling implements Runnable {
        private final Scroller mScroller;

        public Fling(Context context) {
            mScroller = new Scroller(context);
        }

        private void start(int mStartX, int mStartY, int xVelocity, int yVelocity) {
            mScroller.fling(mStartX, mStartY, xVelocity, yVelocity, 0, getWidth(), 0, getHeight());

            post(this);
        }

        @Override
        public void run() {

            if (!mScroller.isFinished()) {
                int currX = mScroller.getCurrX();
                int currY = mScroller.getCurrY();

                mView.layout(currX, currY, currX + mView.getWidth(), currY + mView.getHeight());
                postInvalidate();

//                scrollTo(currX, currY);

                boolean more = mScroller.computeScrollOffset();
                if (BuildConfig.DEBUG)
                    Log.e("Fling", "currX:" + currX + " currY=" + currY + " more=" + more);

                if (more) {
                    post(this);
                }
            } else {
                mView.layout(getWidth() / 2, getHeight() - mView.getHeight(), getWidth() / 2 + mView.getWidth(), getHeight());
                postInvalidate();
            }

        }
    }
}
