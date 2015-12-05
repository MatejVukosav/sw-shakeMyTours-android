package com.codetroopers.shakemytours.ui.activity.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codetroopers.shakemytours.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ViewState extends LinearLayout {

    @Bind(R.id.textview)
    TextView mTextView;

    public ViewState(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ViewState(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ViewState(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        LayoutInflater.from(context).inflate(R.layout.view_state, this);

        ButterKnife.bind(this);

        TypedArray a = null;
        try {
            a = context.obtainStyledAttributes(attrs, R.styleable.StateView, defStyle, 0);
            if (a == null) {
                return;
            }

            setText(a.getString(R.styleable.StateView_android_text));
        } finally {
            if (a != null) {
                a.recycle();
            }
        }
    }

    public void setText(int resid) {
        mTextView.setText(resid);
    }

    public void setText(String text) {
        mTextView.setText(text);
    }


    @Override
    public void setEnabled(boolean enabled) {
        mTextView.setEnabled(enabled);
        super.setEnabled(enabled);
    }
}
