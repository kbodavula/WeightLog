package com.bodavula.weightlog.utilities;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kbodavula on 8/2/16.
 */
public class AppUtils {
    // Helper method to convert milli seconds into formatted date.
    public static String dateFormatter(long millis, String format) {
        if (millis > 0) {
            if (TextUtils.isEmpty(format)) {
                format = Constants.DEFAULT_DATE_FORMAT;
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
            return dateFormat.format(new Date(millis));
        }
        return "";
    }

}