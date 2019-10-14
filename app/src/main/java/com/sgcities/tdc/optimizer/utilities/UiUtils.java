package com.sgcities.tdc.optimizer.utilities;

import android.widget.TextView;

public class UiUtils {
    /***
     * Checks whether the badge can decrease even more
     * @param badge The {@link TextView} representing the badge
     * @return true if the badge can be decreased (larger than 0), false otherwise
     */
    public static boolean canDecreaseBadge(TextView badge) {
        int counter = Integer.parseInt(badge.getText().toString());
        return counter > 0;
    }
}
