package com.bodavula.weightlog.views.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.bodavula.weightlog.R;
import com.bodavula.weightlog.dao.WeightEntriesDAO;
import com.bodavula.weightlog.model.WeightEntry;
import com.bodavula.weightlog.utilities.AppUtils;
import com.bodavula.weightlog.utilities.Constants;

import java.text.DecimalFormat;
import java.util.Calendar;

/**
 * Created by kbodavula on 8/3/16.
 * Fragment to handle add/edit/delete actions of weight entry based on bundle argument (WeightEntry object).
 */
public class WeightInputFragment extends Fragment {
    private static final int LOW = 1;
    private static final int HIGH = 500;
    private static final String WEIGHT_ENTRY_KEY = "WeightEntry";
    private static final String LAST_WEIGHT = "LastSavedWeight";
    private static final String BUNDLE_DATE_TIME_KEY = "CurrentDateTime";
    private static final String BUNDLE_WEIGHT_KEY = "CurrentWeight";

    private long mDateTime;
    private float mWeight;
    private TextView mDateTextView;
    private EditText mWeightInput;
    private WeightEntry mWeightEntry;
    private ViewHistoryClickListener mViewHistoryClickListener;


    // Interface to communicate with activity.
    public interface ViewHistoryClickListener {
        void viewHistoryClick();
    }

    public WeightInputFragment() { }

    // Factory method to create fragment with passed WeightEntry param as argument.
    public static WeightInputFragment getInstance(WeightEntry entry) {
        WeightInputFragment fragment = new WeightInputFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(WEIGHT_ENTRY_KEY, entry);
        fragment.setArguments(bundle);
        return fragment;
    }


    // On attach getting activity reference to communicate with activity to load the weight entry list.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mViewHistoryClickListener = (ViewHistoryClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " need to implement ViewHistoryClickListener");
        }
    }

    // Weight entry fragment view.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Check for argument, if WeightEntry object passed then treating as edit otherwise new entry.
        if (getArguments() != null && getArguments().getParcelable(WEIGHT_ENTRY_KEY) != null) {
            mWeightEntry =  getArguments().getParcelable(WEIGHT_ENTRY_KEY);
        }

        // Inflate the view from layout.
        View view =  inflater.inflate(R.layout.fragment_weight_entry, container, false);

        // Getting UI control references.
        Button addWeightBtn = (Button) view.findViewById(R.id.add_weight);
        Button reduceWeightBtn = (Button) view.findViewById(R.id.reduce_weight);
        mDateTextView = (TextView) view.findViewById(R.id.date_time);
        mWeightInput = (EditText) view.findViewById(R.id.weight_input);
        Button saveBtn = (Button) view.findViewById(R.id.save_btn);
        Button deleteBtn = (Button) view.findViewById(R.id.delete_btn);
        Button cancelBtn = (Button) view.findViewById(R.id.cancel_btn);
        View history = view.findViewById(R.id.weight_log);

        // Setting increase/reduce button text and listener to make weight change.
        addWeightBtn.setText(String.format(getString(R.string.add_btn_text), new DecimalFormat("0.0").format(Constants.WEIGHT_CHANGE)));
        reduceWeightBtn.setText(String.format(getString(R.string.reduce_btn_text), new DecimalFormat("0.0").format(Constants.WEIGHT_CHANGE)));
        addWeightBtn.setOnClickListener(weightChangeListener);
        reduceWeightBtn.setOnClickListener(weightChangeListener);

        // If passed WeightEntry is not null then showing the date and weight from object.
        // And showing edit/delete buttons and updating Add/Edit Entry button text.
        if (mWeightEntry != null) {
            mDateTime = mWeightEntry.getDateTime();
            // Setting weight from WeightEntry object to text field.
            mWeight = mWeightEntry.getWeight();
            saveBtn.setText(getString(R.string.edit_entry));
            history.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.VISIBLE);
        } else {
            // If object is null then considering it as new weight entry and setting the current date time and
            // showing last entered weight which stored in preferences.
            mWeight = getLastSavedWeight();
            mDateTime = System.currentTimeMillis();
            saveBtn.setText(getString(R.string.add_entry));
            deleteBtn.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.GONE);
            history.setVisibility(View.VISIBLE);
        }

        // Restoring state from orientation changes.
        if (savedInstanceState != null) {
            mDateTime = savedInstanceState.getLong(BUNDLE_DATE_TIME_KEY);
            mWeight = Float.parseFloat(savedInstanceState.getString(BUNDLE_WEIGHT_KEY));
        }

        // Handling save button click to add/edit weight entry.
        view.findViewById(R.id.save_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeightEntriesDAO weightEntriesDAO = new WeightEntriesDAO(getContext());
                // If in edit mode then setting current date and weight from UI control values.
                if (mWeightEntry != null) {
                    mWeightEntry.setDateTime(mDateTime);
                    mWeightEntry.setWeight(Float.parseFloat(mWeightInput.getText().toString()));
                } else { // If in add mode then setting id as zero and date, weight data from UI control value.
                    mWeightEntry = new WeightEntry(0, mDateTime, Float.parseFloat(mWeightInput.getText().toString()));
                }

                // Saving and on successful save getting boolean result true and false if not saved.
                boolean result = weightEntriesDAO.save(mWeightEntry);

                // Building the save message based on the above boolean result.
                String msg = result ? getString(R.string.save_success) : getString(R.string.save_fail);

                // On successful save storing current weight in shared preferences so that we show in add weight entry screen.
                // And user can use add/reduce 0.5 lbs using buttons instead of using keyboard.
                if (result) {
                    setLastSavedWeight(mWeightEntry.getWeight());
                }

                // Showing message as snack bar in the bottom of the screen.
                Snackbar.make(getView(), msg, Snackbar.LENGTH_LONG).show();

                // If this is in edit mode then on edit taking user back to weight entries list.
                if (mWeightEntry.getId() > 0) {
                    mViewHistoryClickListener.viewHistoryClick();
                }
            }
        });

        // Handling the delete button.
        view.findViewById(R.id.delete_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DAO
                WeightEntriesDAO weightEntriesDAO = new WeightEntriesDAO(getContext());

                // delete from database and on successful delete get true result.
                boolean result = weightEntriesDAO.delete(mWeightEntry);

                // Based on result building delete message.
                String msg = result ? getString(R.string.delete_success) : getString(R.string.delete_fail);

                // Showing above message as snack bar.
                Snackbar.make(getView(), msg, Snackbar.LENGTH_LONG).show();

                // Taking user back to weight log list.
                mViewHistoryClickListener.viewHistoryClick();
            }
        });

        // Weight format always to show two digits after decimal point.
        DecimalFormat decialFormat = new DecimalFormat("#.00");
        mWeightInput.setText(decialFormat.format(mWeight));
        mWeightInput.setSelection(mWeightInput.getText().toString().length());

        // Setting the formatted date in text view control.
        mDateTextView.setText(AppUtils.dateFormatter(mDateTime, Constants.DATE_TIME_FORMAT));

        // Setting on click listener on data to show date picker dialog.
        mDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        // Cancel button takes back to list in edit mode.
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewHistoryClickListener.viewHistoryClick();
            }
        });

        // Filtering weight inout text using text watcher to allow only values between 1- 500.
        mWeightInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    String current = s.toString();
                    if (current.equals(".") || Float.parseFloat(current) < LOW || Float.parseFloat(current) > HIGH) {
                        s.delete(current.length() - 1, current.length());
                    }
                    int posDot = current.indexOf(".");
                    if (posDot <= 0) {
                        return;
                    }
                    if (current.length() - posDot - 1 > 2) {
                        s.delete(posDot + 3, posDot + 4);
                    }
                }
            }
        });

        // In add mode showing history link to navigate log history.
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewHistoryClickListener.viewHistoryClick();
            }
        });

        return view;
    }

    // Showing date picker in dialog.
    private void showDatePicker(){
        // Inflate dialog view with date and buttons.
        final View dialogView = View.inflate(getActivity(), R.layout.calendar_layout, null);

        // building the dialog show with slide animation.
        final AlertDialog dialog = new AlertDialog.Builder(getActivity(), R.style.SlideStyle).create();

        // Setting the current date to date picker.
        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mDateTime);
        datePicker.updateDate(calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DATE));

        // After user change the date set button update date value in fragment and show the update date text field and dismiss the dialog.
        dialogView.findViewById(R.id.set_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                mDateTime = calendar.getTimeInMillis();
                mDateTextView.setText(AppUtils.dateFormatter(mDateTime, Constants.DATE_TIME_FORMAT));
                dialog.dismiss();
            }
        });

        // Cancel just dismiss dialog without doing any changes.
        dialogView.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Setting custom inflated view to dialog.
        dialog.setView(dialogView);
        dialog.show();
    }

    // Saving fragment state on orientation change.
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(BUNDLE_DATE_TIME_KEY, mDateTime);
        outState.putString(BUNDLE_WEIGHT_KEY, mWeightInput.getText().toString());
    }

    // Handling add/reduce 0.5 lbs button click to update weight text field value with weight range 1 - 500.
    View.OnClickListener weightChangeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            float change = v.getId() == R.id.add_weight ? Constants.WEIGHT_CHANGE : -1 * Constants.WEIGHT_CHANGE;
            float current = TextUtils.isEmpty(mWeightInput.getText()) ? 0.00f : Float.parseFloat(mWeightInput.getText().toString());
            float value = current + change;
            DecimalFormat decialFormat = new DecimalFormat("#.00");

            if (value < LOW) {
                mWeightInput.setText(decialFormat.format(LOW));
            } else if (value > HIGH) {
                mWeightInput.setText(decialFormat.format(HIGH));
            } else {
                mWeightInput.setText(decialFormat.format(value));
            }
            mWeightInput.setSelection(mWeightInput.getText().toString().length());
        }
    };

    // Saving last entered weight in shared preferences so that we read the from preferences and show it in weight edit text field.
    private void setLastSavedWeight(float weight) {
        SharedPreferences preferences = getContext().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        preferences.edit().putFloat(LAST_WEIGHT, weight).commit();
    }

    // Read the last entered value from preferences, very first time we will not have this so setting 100lbs as default if this can any value.
    private float getLastSavedWeight() {
        SharedPreferences preferences = getContext().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        return preferences.getFloat(LAST_WEIGHT, 100.0f);
    }
}