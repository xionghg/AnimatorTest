package com.yff.example.animatortest.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by honggang.xiong on 2018/7/26.
 *
 * @author honggang.xiong
 */
public class HeartView extends View {

    private static final float CENTER_TOP_Y_RATE = 0.3f;    // 中间顶部比例
    private static final float MOST_WIDTH_RATE = 0.49f;     // 心形一半most宽度
    private static final float LINE_WIDTH_RATE = 0.35f;     // 左右边线宽度比例
    private static final float K_1 = 1.14f;                 // 左右边线斜率
    private static final float K_2 = 0.80f;                 // 顶部圆球曲率

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path path = new Path();

    private float heartCenterX = 0;     // 中心x坐标
    private float heartCenterTopY;      // 中心最高点y坐标
    private float heartCenterBottomY;   // 中心最低点y坐标
    private float leftmostX;
    private float rightmostX;
    private float lineLeftX;
    private float lineRightX;
    private float lineTopY;
    private float quadY1;
    private float quadY2;
    private int heartColor = Color.BLACK;

    public HeartView(Context context) {
        super(context);
    }

    public HeartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getHeartColor() {
        return heartColor;
    }

    public void setHeartColor(int heartColor) {
        this.heartColor = heartColor;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = w - getPaddingRight();
        int bottom = h - getPaddingBottom();

        if (left < right && top < bottom) {
            final float width = right - left;
            final float height = bottom - top;

            heartCenterX = left + width * 0.5f;
            heartCenterTopY = top + height * CENTER_TOP_Y_RATE;
            heartCenterBottomY = top + height * 0.99f;
            leftmostX = heartCenterX - width * MOST_WIDTH_RATE;
            rightmostX = heartCenterX + width * MOST_WIDTH_RATE;
            lineLeftX = heartCenterX - width * LINE_WIDTH_RATE;
            lineRightX = heartCenterX + width * LINE_WIDTH_RATE;
            lineTopY = heartCenterBottomY - K_1 * LINE_WIDTH_RATE * height;
            quadY1 = heartCenterBottomY - K_1 * MOST_WIDTH_RATE * height;
            quadY2 = heartCenterTopY - K_2 * MOST_WIDTH_RATE * height;
        } else {
            heartCenterX = 0;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawHeart(canvas);
    }

    private void drawHeart(Canvas canvas) {
        if (heartCenterX <= 0) {
            return;
        }
        paint.setColor(heartColor);
        path.reset();

        path.moveTo(heartCenterX, heartCenterBottomY);
        path.lineTo(lineLeftX, lineTopY);
        path.quadTo(leftmostX, quadY1, leftmostX, heartCenterTopY);

        path.cubicTo(leftmostX, quadY2, heartCenterX, quadY2, heartCenterX, heartCenterTopY);
        path.cubicTo(heartCenterX, quadY2, rightmostX, quadY2, rightmostX, heartCenterTopY);

        path.quadTo(rightmostX, quadY1, lineRightX, lineTopY);
        path.lineTo(heartCenterX, heartCenterBottomY);

        canvas.drawPath(path, paint);
    }

}
