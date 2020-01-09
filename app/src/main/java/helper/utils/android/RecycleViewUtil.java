package helper.utils.android;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class RecycleViewUtil implements RecyclerView.OnItemTouchListener
{
    private GestureDetector gestureDetector;
    private ClickListener clickListener;
    private RecyclerView recyclerView;

    public RecycleViewUtil(Context context, final RecyclerView recyclerView, final ClickListener clickListener)
    {
        this.clickListener = clickListener;
        this.recyclerView = recyclerView;
        gestureDetector = new GestureDetector (context, new GestureDetector.SimpleOnGestureListener ()
        {
            @Override
            public boolean onSingleTapUp (MotionEvent e)
            {
                View child = recyclerView.findChildViewUnder (e.getX (), e.getY ());
                if (child != null && clickListener != null)
                {
                    clickListener.onClick (child, recyclerView.getChildLayoutPosition (child));
                }
                return super.onSingleTapUp (e);
            }

            @Override
            public void onLongPress (MotionEvent e)
            {
                View child = recyclerView.findChildViewUnder (e.getX (), e.getY ());
                if (child != null && clickListener != null)
                {
                    clickListener.onLongClick (child, recyclerView.getChildLayoutPosition (child));
                }
                super.onLongPress (e);
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent (RecyclerView rv, MotionEvent e)
    {
        View child = recyclerView.findChildViewUnder (e.getX (), e.getY ());
        if (child != null && clickListener != null && gestureDetector.onTouchEvent (e))
        {
            clickListener.onClick (child, rv.getChildLayoutPosition (child));
        }
        return false;
    }

    @Override
    public void onTouchEvent (RecyclerView rv, MotionEvent e)
    {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent (boolean b)
    {
    }

    public interface ClickListener
    {
        void onClick(View v, int position);

        void onLongClick(View v, int position);
    }
}
