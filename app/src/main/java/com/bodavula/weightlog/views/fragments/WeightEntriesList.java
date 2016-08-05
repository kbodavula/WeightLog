package com.bodavula.weightlog.views.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bodavula.weightlog.R;
import com.bodavula.weightlog.adapters.EntriesAdapter;
import com.bodavula.weightlog.dao.WeightEntriesDAO;
import com.bodavula.weightlog.model.WeightEntry;

import java.util.List;

/**
 * Created by kbodavula on 8/3/16.
 * Fragment to show list of weight entries in recycler view.
 */
public class WeightEntriesList extends Fragment {

    private WeightEntryListItemClickListener mWeightEntryListItemClickListener;
    private List<WeightEntry> mEntries;

    // Interface to communicate with activity to to load weight entry item click
    // to load the weight entry edit/delete fragment.
    public interface WeightEntryListItemClickListener {
        void WeightEntryItemClicked(WeightEntry weightEntry);
    }

    public WeightEntriesList() { }

    // On attach getting activity reference to communicate with activity on item click.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mWeightEntryListItemClickListener = (WeightEntryListItemClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " need to implement WeightEntryListItemClickListener");
        }
    }

    // Fragment view populating populating with data.
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflating fragment's view.
        View view =  inflater.inflate(R.layout.fragment_weight_entries_list, container, false);

        // Executing async task to fetch weight entries list.
        new WeightEntriesTask().execute();

        // To add new weight entry from floating action bar button.
        view.findViewById(R.id.add_new).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWeightEntryListItemClickListener.WeightEntryItemClicked(null);
            }
        });

        return view;
    }

    // Load the weight entries list data.
    private void loadWeightEntries() {
        View view = getView();
        final RecyclerView entriesRecycler = (RecyclerView) view.findViewById(R.id.entries_list);

        // When we have weight entries creating showing the list with recycler view.
        if (mEntries != null && mEntries.size() > 0) {
            view.findViewById(R.id.no_history).setVisibility(View.GONE);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            EntriesAdapter adapter = new EntriesAdapter(mEntries, mWeightEntryListItemClickListener);
            entriesRecycler.setLayoutManager(mLayoutManager);
            entriesRecycler.setItemAnimator(new DefaultItemAnimator());
            entriesRecycler.setAdapter(adapter);
        } else {  // If there are no weight entries available then showing message.
            entriesRecycler.setVisibility(View.GONE);
            view.findViewById(R.id.no_history).setVisibility(View.VISIBLE);
        }
    }

    class WeightEntriesTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() { }

        @Override
        protected Boolean doInBackground(Void... params) {
            // Getting the weight entries from database.
            mEntries = new WeightEntriesDAO(getContext()).getWeightEntries();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // Populate UI with weight entries data.
            loadWeightEntries();
        }
    }
}