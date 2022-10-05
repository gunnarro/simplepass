package com.gunnarro.android.simplepass.ui.swipe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Generic class for handle swipe actions
 */
public abstract class SwipeCallback extends ItemTouchHelper.Callback {

    private static final float SWIPE_THRESHOLD = 0.8f;
    private final Paint clearPaint;
    private final ColorDrawable background;
    private final Drawable swipeDrawable;
    private final int backgroundColor;
    private final int intrinsicWidth;
    private final int intrinsicHeight;
    private final int swipeDirection;

    protected SwipeCallback(@NonNull Context context, @NonNull Integer swipeDirection, int backgroundColor, @DrawableRes int drawableIcon) {
        this.swipeDirection = swipeDirection;
        this.background = new ColorDrawable();
        this.backgroundColor = backgroundColor;
        this.clearPaint = new Paint();
        this.clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        this.swipeDrawable = ContextCompat.getDrawable(context, drawableIcon);
        this.intrinsicWidth = swipeDrawable.getIntrinsicWidth();
        this.intrinsicHeight = swipeDrawable.getIntrinsicHeight();
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(ItemTouchHelper.ACTION_STATE_IDLE, swipeDirection);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getHeight();

        boolean isCancelled = dX == 0 && !isCurrentlyActive;

        if (isCancelled) {
            clearCanvas(canvas, itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
            super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            return;
        }

        background.setColor(backgroundColor);
        // dX – The amount of horizontal displacement caused by user's action
        if (dX < 0 ) {
            // for left swipe
            background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else {
            // for right swipe
            background.setBounds(itemView.getLeft() + (int) dX, itemView.getTop(), itemView.getLeft(), itemView.getBottom());
        }
        background.draw(canvas);

        int iconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
        int snaciconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
        int iconMargin = (itemHeight - intrinsicHeight) / 2;
        int iconLeft = itemView.getRight() - iconMargin - intrinsicWidth;
        int iconRight = itemView.getRight() - iconMargin;
        int iconBottom = iconTop + intrinsicHeight;

        swipeDrawable.setBounds(iconLeft, iconTop, iconRight, iconBottom);
        swipeDrawable.draw(canvas);
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void clearCanvas(Canvas canvas, Float left, Float top, Float right, Float bottom) {
        canvas.drawRect(left, top, right, bottom, clearPaint);
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return SWIPE_THRESHOLD;
    }
}