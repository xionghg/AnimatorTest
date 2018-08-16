package com.yff.example.animatortest.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;

import com.yff.example.animatortest.widget.HeartView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Random;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

/**
 * Created by honggang.xiong on 2018/8/8.
 *
 * @author honggang.xiong
 */
public class Kaleidoscope {

    private static final boolean DEBUG = true;
    private static final int MESSAGE_SHOW_NEXT = 1;
    private FrameLayout container;
    private ArrayList<ObjectAnimator> runningAnimators = new ArrayList<>();
    private LinkedList<Pair<HeartView, Path>> pairPool = new LinkedList<>();
    private int current = 0;
    private int newViewAmount = 0;
    private long startTime = 0;
    private Rect containerArea = new Rect();
    private boolean isPendingStop = false;

    private int total;            // 总数量
    private int duration;         // 总持续时间
    private int singleDuration;   // 单个持续时间
    private IColorRule colorRule;
    private ISizeRule sizeRule;

    private Handler mainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_SHOW_NEXT) {
                checkOrShowView();
            }
        }
    };

    private Kaleidoscope(Builder builder) {
        container = Objects.requireNonNull(builder.frameLayout, "a FrameLayout container is needed!");
        colorRule = Objects.requireNonNull(builder.colorRule);
        sizeRule = Objects.requireNonNull(builder.sizeRule);
        container.getGlobalVisibleRect(containerArea);
        total = checkRange(builder.total, 1, Integer.MAX_VALUE, "total");
        duration = checkRange(builder.duration, 0, Integer.MAX_VALUE, "duration");
        singleDuration = checkRange(builder.singleDuration, 0, Integer.MAX_VALUE, "duration");
    }

    public static Builder with(Activity activity) {
        return with(activity.findViewById(android.R.id.content));
    }

    public static Builder with(FrameLayout frameLayout) {
        return new Builder(frameLayout);
    }

    private static int checkRange(int val, int start, int end, String paramDescription) {
        if (val < start || val > end) {
            throw new IllegalArgumentException(paramDescription + " should between " + start + " and " + end + ", but get " + val);
        }
        return val;
    }

    private static void log(String string) {
        if (DEBUG) Log.d("AnimatorTest", string);
    }

    private static int dip2px(int dp) {
        return (int) (TypedValue.applyDimension(COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics()) + 0.5f);
    }

    private Pair<HeartView, Path> getViewPathPair(int current) {
        Pair<HeartView, Path> pair;
        if (pairPool.isEmpty()) {
            HeartView heartView = new HeartView(container.getContext());
            newViewAmount++;
            container.addView(heartView);
            pair = new Pair<>(heartView, new Path());
        } else {
            pair = pairPool.pop();
            pair.first.setTranslationX(0);
            pair.first.setTranslationY(0);
            pair.second.reset();
        }
        log("total=" + total + ", current=" + (current + 1) + ", newViewAmount=" + newViewAmount);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) pair.first.getLayoutParams();
        lp.gravity = Gravity.BOTTOM;
        int halfHeart = dip2px(sizeRule.getSizeInDp(current)) / 2;
        lp.width = halfHeart * 2;
        lp.height = halfHeart * 2;
        int x1 = new Random().nextInt(dip2px(200)) + containerArea.width() / 2 - halfHeart - dip2px(100);
        lp.leftMargin = x1;
        lp.bottomMargin = -halfHeart;
        pair.first.setLayoutParams(lp);
        pair.first.setHeartColor(colorRule.getColor(current));

        int y1 = -4 * halfHeart;
        int x2 = new Random().nextInt(containerArea.width() * 3) - containerArea.width();
        pair.second.moveTo(x1, containerArea.bottom - halfHeart);
        pair.second.quadTo(x1, y1, x2, y1);
        return pair;
    }

    private void showNextView(long delay) {
        final Pair<HeartView, Path> pair = getViewPathPair(current++);
        final HeartView heartView = pair.first;
        final Path path = pair.second;
        heartView.setVisibility(View.GONE);

        ObjectAnimator animator = ObjectAnimator.ofFloat(heartView, View.X, View.Y, path).setDuration(singleDuration);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setStartDelay(delay);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                heartView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                runningAnimators.remove(animator);
                pairPool.push(pair);
                if (isPendingStop && runningAnimators.isEmpty()) {
                    removeAllViews();
                }
            }
        });

        animator.start();
        runningAnimators.add(animator);
    }

    public void start() {
        current = newViewAmount = 0;
        startTime = System.currentTimeMillis();
        checkOrShowView();
    }

    private void checkOrShowView() {
        long passed = System.currentTimeMillis() - startTime;
        int progress = (int) (passed * total / duration);
        if (progress > total) progress = total;
        int need = progress - current;
        for (int i = 0; i < need; i++) {
            showNextView(i);
        }

        if (current < total) {
            mainHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_NEXT, duration / total);
        } else {
            stopSmoothly();
        }
    }

    public void stop() {
        mainHandler.removeCallbacksAndMessages(null);
        final int size = runningAnimators.size();
        log("stop " + size + " animators");
        // stop all animators before remove views
        for (int i = size - 1; i >= 0; i--) {
            runningAnimators.get(i).cancel();
        }
        removeAllViews();
    }

    private void removeAllViews() {
        isPendingStop = false;
        for (Pair<HeartView, Path> pair : pairPool) {
            container.removeView(pair.first);
        }
    }

    public void stopSmoothly() {
        if (isPendingStop) {
            return;
        }
        mainHandler.removeCallbacksAndMessages(null);
        isPendingStop = true;
    }

    public interface IColorRule {
        int getColor(int current);
    }

    public interface ISizeRule {
        int getSizeInDp(int current);
    }

    public static class RandomColorRule implements IColorRule {
        Random random = new Random();

        @Override
        public int getColor(int current) {
            int r = random.nextInt(255);
            int g = random.nextInt(255);
            int b = random.nextInt(255);
            return Color.argb(255, r, g, b);
        }
    }

    public static final class Builder {
        private FrameLayout frameLayout;
        private int total = 100;
        private int duration = 5000;
        private int singleDuration = 1200;
        private IColorRule colorRule = current -> Color.RED;
        private ISizeRule sizeRule = current -> 52;

        private Builder(FrameLayout val) {
            frameLayout = val;
        }

        public Builder total(int val) {
            total = val;
            return this;
        }

        public Builder duration(int val) {
            duration = val;
            return this;
        }

        public Builder singleDuration(int val) {
            singleDuration = val;
            return this;
        }

        public Builder colorRule(IColorRule val) {
            colorRule = val;
            return this;
        }

        public Builder sizeRule(ISizeRule val) {
            sizeRule = val;
            return this;
        }

        public Kaleidoscope build() {
            return new Kaleidoscope(this);
        }

        public void start() {
            build().start();
        }
    }
}
