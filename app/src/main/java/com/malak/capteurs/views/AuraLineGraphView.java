package com.malak.capteurs.views;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class AuraLineGraphView extends View {

    private final List<Float> signalHistory = new ArrayList<>();
    private final int historyLimit = 70;

    private final Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint curvePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public AuraLineGraphView(Context context) {
        super(context);

        gridPaint.setColor(Color.argb(80, 120, 130, 150));
        gridPaint.setStrokeWidth(2f);

        curvePaint.setStrokeWidth(6f);
        curvePaint.setStyle(Paint.Style.STROKE);
        curvePaint.setStrokeCap(Paint.Cap.ROUND);
        curvePaint.setStrokeJoin(Paint.Join.ROUND);

        fillPaint.setStyle(Paint.Style.FILL);

        labelPaint.setColor(Color.rgb(70, 78, 95));
        labelPaint.setTextSize(28f);
    }

    public void pushPoint(float value) {
        if (signalHistory.size() >= historyLimit) {
            signalHistory.remove(0);
        }

        signalHistory.add(value);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        canvas.drawColor(Color.TRANSPARENT);

        for (int i = 1; i <= 4; i++) {
            float y = i * height / 5f;
            canvas.drawLine(24, y, width - 24, y, gridPaint);
        }

        if (signalHistory.size() < 2) {
            canvas.drawText("En attente du signal capteur...", 40, height / 2f, labelPaint);
            return;
        }

        float min = Float.MAX_VALUE;
        float max = -Float.MAX_VALUE;

        for (float value : signalHistory) {
            min = Math.min(min, value);
            max = Math.max(max, value);
        }

        if (Math.abs(max - min) < 0.001f) {
            max = min + 1f;
        }

        Path curvePath = new Path();
        Path fillPath = new Path();

        float firstX = 32;
        float firstY = mapValue(signalHistory.get(0), min, max, height);

        curvePath.moveTo(firstX, firstY);
        fillPath.moveTo(firstX, height - 28);
        fillPath.lineTo(firstX, firstY);

        for (int i = 1; i < signalHistory.size(); i++) {
            float x = 32 + i * ((width - 64f) / (historyLimit - 1));
            float y = mapValue(signalHistory.get(i), min, max, height);

            curvePath.lineTo(x, y);
            fillPath.lineTo(x, y);
        }

        float lastX = 32 + (signalHistory.size() - 1) * ((width - 64f) / (historyLimit - 1));
        fillPath.lineTo(lastX, height - 28);
        fillPath.close();

        curvePaint.setShader(new LinearGradient(
                0, 0, width, 0,
                Color.rgb(255, 122, 138),
                Color.rgb(124, 131, 253),
                Shader.TileMode.CLAMP));

        fillPaint.setShader(new LinearGradient(
                0, 0, 0, height,
                Color.argb(90, 124, 131, 253),
                Color.argb(10, 255, 255, 255),
                Shader.TileMode.CLAMP));

        canvas.drawPath(fillPath, fillPaint);
        canvas.drawPath(curvePath, curvePaint);

        canvas.drawText("Min " + round(min) + "   Max " + round(max), 36, 36, labelPaint);
    }

    private float mapValue(float value, float min, float max, int height) {
        float ratio = (value - min) / (max - min);
        return height - 32 - ratio * (height - 72);
    }

    private String round(float value) {
        return String.format("%.2f", value);
    }
}