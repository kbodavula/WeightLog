package com.bodavula.weightlog.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bodavula.weightlog.R;
import com.bodavula.weightlog.model.WeightEntry;
import com.bodavula.weightlog.utilities.AppUtils;
import com.bodavula.weightlog.utilities.Constants;
import com.bodavula.weightlog.views.fragments.WeightEntriesList;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by kbodavula on 8/3/16.
 * Adapter to feed the Entries List recycler.
 */
public class EntriesAdapter extends RecyclerView.Adapter<EntriesAdapter.WeightEntryHolder> {
    private List<WeightEntry> mEntries;
    private Context mContext;
    private WeightEntriesList.WeightEntryListItemClickListener mListener;

    // Constructor with list of weight entries and listener to navigate to edit screen on each item click.
    public EntriesAdapter(List<WeightEntry> entries,  WeightEntriesList.WeightEntryListItemClickListener listener) {
        mEntries = entries;
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mEntries.size();
    }

    // Inflate view from layout.
    @Override
    public WeightEntryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.weight_row, parent, false);
        return new WeightEntryHolder(view);
    }

    // Bind the data view holder for current position.
    @Override
    public void onBindViewHolder(WeightEntryHolder holder, final int position) {
        // get current weight entry object frm list.
        WeightEntry entry = mEntries.get(position);

        // Load UI controls with data.
        holder.mDate.setText(AppUtils.dateFormatter(entry.getDateTime(), Constants.DEFAULT_DATE_FORMAT));
        holder.mWeight.setText(String.format("%1s %2s", new DecimalFormat("#.00").format(entry.getWeight()), mContext.getString(R.string.lbs_text)));

        // Recycler view click takes to edit fragment with current weight entry as argument to update.
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.WeightEntryItemClicked(mEntries.get(position));
            }
        });
    }

    public void setData(List<WeightEntry> entries) {
        mEntries = entries;
    }

    // Weight entry row view holder.
    static class WeightEntryHolder extends RecyclerView.ViewHolder {
        public TextView mDate;
        public TextView mWeight;
        public View mView;

        public WeightEntryHolder(View itemView) {
            super(itemView);
            mView = itemView.findViewById(R.id.weight_card_view);
            mDate = (TextView) itemView.findViewById(R.id.date);
            mWeight = (TextView) itemView.findViewById(R.id.weight);
        }
    }
}
