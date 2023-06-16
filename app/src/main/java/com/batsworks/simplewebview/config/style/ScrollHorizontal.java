package com.batsworks.simplewebview.config.style;

import android.os.Handler;
import android.widget.HorizontalScrollView;

public class ScrollHorizontal {

    private final Handler handler;
    private final HorizontalScrollView scrollView;
    private final int speed;
    private final int delay;

    public ScrollHorizontal(Handler handler, HorizontalScrollView scrollView, int speed, int delay) {
        this.handler = handler;
        this.scrollView = scrollView;
        this.speed = speed;
        this.delay = delay;
    }

    public Runnable runScroll = new Runnable() {
        @Override
        public void run() {
            int currentScrollX = scrollView.getScrollX();
            int maxScrollX = scrollView.getChildAt(0).getWidth() - scrollView.getWidth();
            int newScrollX = currentScrollX + speed > 0 ? speed : 10;

            if (newScrollX >= maxScrollX) {
                newScrollX = 0;
            }
            scrollView.scrollTo(newScrollX, 0);
            handler.postDelayed(this, delay > 0 ? delay : 50);
        }
    };

}
