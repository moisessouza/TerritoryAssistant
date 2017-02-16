package ged.mediaplayerremote.presentation.util;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.concurrent.TimeUnit;

/**
 * An utility class with various static methods.
 */
public class Toolbox {

    /**
     * Converts density-independent pixels into pixels.
     *
     * @param dp Input in density-independent pixels.
     * @return Result value in pixels.
     */
    public static int dpToPx(int dp) {
        Resources resources = Resources.getSystem();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return (int) (dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));

    }

    /**
     * Converts position timestamp from milliseconds to  hh:mm:ss String format.
     *
     * @param position Time in milliseconds.
     * @return Time in hh:mm:ss format.
     */
    @SuppressWarnings("DefaultLocale")
    public static String positionIntToString(int position) {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(position),
                TimeUnit.MILLISECONDS.toMinutes(position) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(position)),
                TimeUnit.MILLISECONDS.toSeconds(position) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(position)));


    }
}
