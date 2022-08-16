package com.capstone.wifiposition.utils;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// 单击/长按事件
public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

//    定义成员变量 （接口回调步骤2）
    private OnItemClickListener mOnItemClickListener;
//    private RecyclerView mRecyclerView;
    private GestureDetector mGestureDetector;

//    定义监听接口类 （接口回调步骤1）
    public interface OnItemClickListener {
//        单击事件接口
        void onItemClick(View view, int position);
//        长按事件接口
        void onItemLongClick(View view,int position);

    }

    public RecyclerItemClickListener(Context context, final RecyclerView mRecyclerView, final OnItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
//           单击事件
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
//            长按事件
            @Override
            public void onLongPress(MotionEvent e) {
                if (onItemClickListener != null){
                    View childView = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (childView != null) {
                        int position = mRecyclerView.getChildAdapterPosition(childView);
                        onItemClickListener.onItemLongClick(childView, position);
                    }
                } else {
                    super.onLongPress(e);
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        View childView = rv.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mOnItemClickListener != null && mGestureDetector.onTouchEvent(e)) {
            mOnItemClickListener.onItemClick(childView, rv.getChildAdapterPosition(childView));
        }
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
