package com.wingstars.user.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;


public class SuperExpandleListView extends ExpandableListView {
    public SuperExpandleListView(Context context) {
        super(context);
    }

    public SuperExpandleListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SuperExpandleListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int makeMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, makeMeasureSpec);

    }
}


