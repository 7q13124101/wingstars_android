package com.wingstars.base.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class ItemHotDecoration extends RecyclerView.ItemDecoration {

    private int space;  // 间隔大小

    public ItemHotDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left = space;  // 左侧间隔
//        outRect.right = space; // 右侧间隔
    }
}
