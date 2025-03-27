package com.example.certificateviewer;

import android.view.MotionEvent;
import android.view.View;

public class DragTouchListener implements View.OnTouchListener {
    private float dX, dY;
    private int lastAction;

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dX = view.getX() - event.getRawX();
                dY = view.getY() - event.getRawY();
                lastAction = MotionEvent.ACTION_DOWN;
                break;

            case MotionEvent.ACTION_MOVE:
                view.setX(event.getRawX() + dX);
                view.setY(event.getRawY() + dY);
                lastAction = MotionEvent.ACTION_MOVE;
                break;

            case MotionEvent.ACTION_UP:
                if (lastAction == MotionEvent.ACTION_DOWN) {
                    view.performClick();
                }
                break;

            default:
                return false;
        }
        return true;
    }
}
