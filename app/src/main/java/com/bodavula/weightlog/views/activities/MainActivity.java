package com.bodavula.weightlog.views.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.bodavula.weightlog.R;
import com.bodavula.weightlog.db.WeightLogDBHelper;
import com.bodavula.weightlog.model.WeightEntry;
import com.bodavula.weightlog.views.fragments.WeightEntriesList;
import com.bodavula.weightlog.views.fragments.WeightInputFragment;

/**
 * Created by kbodavula on 8/3/16.
 *
 * Main activity which holds the different fragment based on different states.
 * This is just container to update different fragments and communicate with them.
 */
public class MainActivity extends AppCompatActivity
                          implements WeightEntriesList.WeightEntryListItemClickListener, WeightInputFragment.ViewHistoryClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Main activity layout file.
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            // Setting the database.
            WeightLogDBHelper.getInstance(this);

            // On app launch loading the weight entry fragment to add weight entry.
            WeightEntryItemClicked(null);
        }
    }

    // Loading the weight entry fragment with weight entry as argument.
    // For new weight entry argument would null.
    @Override
    public void WeightEntryItemClicked(WeightEntry weightEntry) {
        updateFragment(WeightInputFragment.getInstance(weightEntry));
    }

    // Loading the weight entries list fragment.
    @Override
    public void viewHistoryClick() {
        updateFragment(new WeightEntriesList());
    }


    // Handling the fragment changes.
    private void updateFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentManager.popBackStackImmediate();
        if (!fragment.isAdded()) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_left, R.anim.slide_right);
            fragmentTransaction.replace(R.id.content, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    // Handling back button.
    @Override
    public void onBackPressed() {
        finish();
    }
}