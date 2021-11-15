package com.yellastrodev.oneguard.swipemenu;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.view.MotionEventCompat;
import com.yellastrodev.oneguard.MainActivity;

public class SwipeTouchListn implements View.OnTouchListener 
	{

	/**
	 * Detects left and right swipes across a view.
	 */
	

	private MainActivity mAct;

	private static final int INVALID_POINTER_ID = 0;

	private float mLastTouchX,mPosX,mPosY;

	private float mLastTouchY;

	private DrawMenu mvMenu;

	public SwipeTouchListn(MainActivity context,DrawMenu fZm) {
		mAct = context;
		mvMenu = fZm;
		//gestureDetector = new GestureDetector(context, new GestureListener());
	}

	// The ‘active pointer’ is the one currently moving our object.
	private int mActivePointerId = INVALID_POINTER_ID;

	@Override
	public boolean onTouch(View p1, MotionEvent ev) {
		
		// Let the ScaleGestureDetector inspect all events.
		//mScaleDetector.onTouchEvent(ev);

		final int action = MotionEventCompat.getActionMasked(ev);

		switch (action) {
			case MotionEvent.ACTION_DOWN: {
				mPosX = 0;
					final int pointerIndex = MotionEventCompat.getActionIndex(ev);
					final float x = MotionEventCompat.getX(ev, pointerIndex);
					final float y = MotionEventCompat.getY(ev, pointerIndex);

					// Remember where we started (for dragging)
					mLastTouchX = x;
					mLastTouchY = y;
					// Save the ID of this pointer (for dragging)
					mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
					break;
				}

			case MotionEvent.ACTION_MOVE: {
					// Find the index of the active pointer and fetch its position
					final int pointerIndex =
						MotionEventCompat.findPointerIndex(ev, mActivePointerId);

					final float x = MotionEventCompat.getX(ev, pointerIndex);
					final float y = MotionEventCompat.getY(ev, pointerIndex);

					// Calculate the distance moved
					final float dx = x - mLastTouchX;
					final float dy = y - mLastTouchY;

					mPosX += dx;
					mPosY += dy;

					//invalidate();

					// Remember this touch position for the next move event
					mLastTouchX = x;
					mLastTouchY = y;
					
					mAct.logText("x: "+mPosX);
					mvMenu.onMove(mPosX);

					break;
				}

			case MotionEvent.ACTION_UP: {
					mvMenu.onUp();
					mActivePointerId = INVALID_POINTER_ID;
					break;
				}

			case MotionEvent.ACTION_CANCEL: {
					mActivePointerId = INVALID_POINTER_ID;
					break;
				}

			case MotionEvent.ACTION_POINTER_UP: {

					final int pointerIndex = MotionEventCompat.getActionIndex(ev);
					final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);

					if (pointerId == mActivePointerId) {
						// This was our active pointer going up. Choose a new
						// active pointer and adjust accordingly.
						final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
						mLastTouchX = MotionEventCompat.getX(ev, newPointerIndex);
						mLastTouchY = MotionEventCompat.getY(ev, newPointerIndex);
						mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
					}
					break;
				}
		}
		return true;
	}
}
