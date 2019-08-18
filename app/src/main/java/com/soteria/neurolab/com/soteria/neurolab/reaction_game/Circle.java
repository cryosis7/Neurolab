package com.soteria.neurolab.com.soteria.neurolab.reaction_game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.soteria.neurolab.R;

class Circle extends View {

    private Paint paint;

    public Circle(Context context) {
        super(context);

        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.GREEN);
        canvas.drawCircle(200, 200, 100, paint);
    }
}
