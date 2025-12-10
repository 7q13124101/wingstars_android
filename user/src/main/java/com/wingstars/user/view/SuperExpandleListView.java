package com.wingstars.user.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;


//因ExpandableListView嵌套在ScrollView中显示一条数据，这是因为 ExpandableListView 是一个高度可变的视图（它可以根据展开和折叠的状态动态改变高度），
//而 NestedScrollView 需要一个固定高度的子视图来正确工作。如果 ExpandableListView 的高度没有正确测量，NestedScrollView 可能无法正确显示其内容，
//从而导致只显示一条数据或列表没有完全展开。

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


