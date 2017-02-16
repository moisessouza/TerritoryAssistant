package ged.mediaplayerremote.presentation.view.widget;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;
import ged.mediaplayerremote.R;
import ged.mediaplayerremote.presentation.view.activity.MainActivity;

/**
 * Factory that creates different versions of a {@link Notification} widget_control.
 */
public class WidgetFactory {

    public static final String WIDGET_BUTTON_CLICKED_INTENT = "ged.mediaplayerremote.WIDGET_BUTTON_CLICKED_INTENT";

    public static final String WIDGET_BUTTON_NAME = "buttonName";

    private static Notification widgetPause;
    private static Notification widgetPlay;
    private static Notification widgetDisconnected;

    /**
     * Get a notification widget_control with 5 buttons (rewind, fast forward, pause, volume down, volume up)
     */
    public static Notification getWidgetPause(Context context) {
        if (widgetPause == null) {
            widgetPause = createControlWidget(context, WidgetMode.PAUSE);
        }
        return widgetPause;
    }

    /**
     * Get a notification widget_control with 5 buttons (rewind, fast forward, play, volume down, volume up)
     */
    public static Notification getWidgetPlay(Context context) {
        if (widgetPlay == null) {
            widgetPlay = createControlWidget(context, WidgetMode.PLAY);
        }
        return widgetPlay;
    }

    /**
     * Get a notification widget_control with a disconnected message and a reconnect button.
     */
    public static Notification getWidgetDisconnected(Context context) {
        if (widgetDisconnected == null) {
            widgetDisconnected = createDisconnectedWidget(context, WidgetMode.DISCONNECTED, 0);
        }
        return widgetDisconnected;
    }

    /**
     * Get a notification widget_control with a reconnecting message and an incrementing retry counter.
     */
    public static Notification getWidgetConnecting(Context context, int seconds) {
        return createDisconnectedWidget(context, WidgetMode.CONNECTING, seconds);
    }

    private static Notification createControlWidget(Context context, WidgetMode mode) {
        String[] buttonIntentParam = {"rewind", "forward", "play_pause", "vol-", "vol+"};
        int[] buttonIds = {R.id.widget_rewind, R.id.widget_forward, R.id.widget_play_pause, R.id.widget_vol_down, R.id.widget_vol_up};

        RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.widget_control);

        for (int i = 0; i < 5; i++) {
            Intent intent = new Intent(WIDGET_BUTTON_CLICKED_INTENT);
            intent.putExtra(WIDGET_BUTTON_NAME, buttonIntentParam[i]);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i, intent, 0);
            widgetView.setOnClickPendingIntent(buttonIds[i], pendingIntent);
        }

        if (mode == WidgetMode.PLAY)
            widgetView.setImageViewResource(R.id.widget_play_pause, R.drawable.ic_play_arrow_black_36dp);
        else if (mode == WidgetMode.PAUSE)
            widgetView.setImageViewResource(R.id.widget_play_pause, R.drawable.ic_pause_black_36dp);

        return new Notification.Builder(context).setSmallIcon(R.drawable.ic_local_movies_white_24dp)
                .setContent(widgetView).build();

    }

    private static Notification createDisconnectedWidget(Context context, WidgetMode mode, int seconds) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        RemoteViews remoteViews;
        if (mode == WidgetMode.DISCONNECTED) {
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_disconnected);
            int reconnectId = R.id.widget_dc;
            String reconnectButtonIntentParam = "reconnect";
            Intent reconnectIntent = new Intent(WIDGET_BUTTON_CLICKED_INTENT);
            reconnectIntent.putExtra(WIDGET_BUTTON_NAME, reconnectButtonIntentParam);
            PendingIntent reconnectClickedPendingIntent = PendingIntent.getBroadcast(context, 6, reconnectIntent, 0);
            remoteViews.setOnClickPendingIntent(reconnectId, reconnectClickedPendingIntent);
        } else {
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_connecting);
            String message = context.getString(R.string.widget_connecting_message);
            remoteViews.setTextViewText(R.id.textView8, message + seconds);
        }

        Intent startActivityIntent = new Intent(context, MainActivity.class);
        startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent startActivityPendingIntent = PendingIntent.getActivity(context, 7, startActivityIntent, 0);

        builder.setCustomContentView(remoteViews);
        builder.setContentIntent(startActivityPendingIntent);

        return builder.setSmallIcon(R.drawable.ic_local_movies_white_24dp).build();
    }
}
