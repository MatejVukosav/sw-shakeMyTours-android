package com.codetroopers.shakemytours.ui.activity;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codetroopers.shakemytours.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {
    private OnItemClickListener mListener;
    private int mSelectedItem;

    public void setActive(int position) {
        mSelectedItem = position;
        notifyDataSetChanged();
    }

    public DrawerAdapter(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater vi = LayoutInflater.from(parent.getContext());
        View v = vi.inflate(R.layout.drawer_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        if (position == 0) {
            viewHolder.name.setText("Home");
        } else {
            viewHolder.name.setText("menu " + position);
        }
        viewHolder.name.setOnClickListener(v -> mListener.onClick(v, position));
        viewHolder.itemView.setActivated(position == mSelectedItem);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public interface OnItemClickListener {
        void onClick(View view, int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(android.R.id.text1)
        TextView name;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
