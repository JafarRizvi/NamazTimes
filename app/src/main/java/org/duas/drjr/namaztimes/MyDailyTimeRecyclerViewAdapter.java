package org.duas.drjr.namaztimes;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.duas.drjr.namaztimes.DailyTimeFragment.OnListFragmentInteractionListener;
import org.duas.drjr.namaztimes.logic.DailyTimeContent.DailyTime;

import java.util.List;

public class MyDailyTimeRecyclerViewAdapter extends RecyclerView.Adapter<MyDailyTimeRecyclerViewAdapter.ViewHolder> {

    private final List<DailyTime> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyDailyTimeRecyclerViewAdapter(List<DailyTime> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dailytime_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mTitleView.setText(mValues.get(position).title);
        //holder.mContentView.setText(mValues.get(position).description);
        //holder.mTimeView.setText(mValues.get(position).time);
        holder.mTimeView.setText(mValues.get(position).time);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        //public final TextView mContentView;
        public final TextView mTimeView;
        //public final TextView mTime2View;
        public DailyTime mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.title);
            //mContentView = (TextView) view.findViewById(R.id.description);
            //mTimeView = (TextView) view.findViewById(R.id.time);
            mTimeView = (TextView) view.findViewById(R.id.time);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
