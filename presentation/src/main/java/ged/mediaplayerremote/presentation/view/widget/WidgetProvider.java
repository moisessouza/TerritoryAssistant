package ged.mediaplayerremote.presentation.view.widget;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import ged.mediaplayerremote.presentation.presenter.WidgetPresenter;
import ged.mediaplayerremote.presentation.view.WidgetView;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * A class used to publish notification that works as a widget to control the player from outside the app.
 */
@Singleton
public class WidgetProvider implements WidgetView, WidgetStatusListener {

    public static final int WIDGET_NOTIFICATION_ID = 1;

    private Context context;
    private NotificationManager notificationManager;
    private BroadcastReceiver widgetBroadcastReceiver;
    private WidgetPresenter widgetPresenter;
    private boolean isEnabled = false;

    @Inject
    public WidgetProvider(Context context, WidgetPresenter widgetPresenter) {

        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.widgetPresenter = widgetPresenter;
    }

    public void enableWidget() {
        isEnabled = true;
        widgetPresenter.setView(this);
        widgetPresenter.initialize();
        registerWidgetReceiver();
    }

    public void disableWidget() {
        isEnabled = false;
        widgetPresenter.destroy();
        unregisterWidgetReceiver();
        hideWidget();
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void onPause() {
        widgetPresenter.pause();
    }

    public void onResume() {
        widgetPresenter.resume();
    }

    @Override
    public void showPauseButton() {
        Notification widget;
        widget = WidgetFactory.getWidgetPause(context);
        notificationManager.notify(WIDGET_NOTIFICATION_ID, widget);
    }

    @Override
    public void showPlayButton() {
        Notification widget;
        widget = WidgetFactory.getWidgetPlay(context);
        notificationManager.notify(WIDGET_NOTIFICATION_ID, widget);
    }

    @Override
    public void showViewDisconnected() {
        Notification widget;
        widget = WidgetFactory.getWidgetDisconnected(context);
        notificationManager.notify(WIDGET_NOTIFICATION_ID, widget);
    }

    @Override
    public void showViewConnecting(int seconds) {
        Notification widget;
        widget = WidgetFactory.getWidgetConnecting(context, seconds);
        notificationManager.notify(WIDGET_NOTIFICATION_ID, widget);
    }

    @Override
    public void onWidgetEnabled() {
        enableWidget();
    }

    @Override
    public void onWidgetDisabled() {
        disableWidget();
    }

    private void registerWidgetReceiver() {
        if (widgetBroadcastReceiver == null) {
            widgetBroadcastReceiver = new WidgetButtonClickReceiver();
        }
        context.registerReceiver(widgetBroadcastReceiver, new IntentFilter(WidgetFactory.WIDGET_BUTTON_CLICKED_INTENT));
    }

    private void unregisterWidgetReceiver() {
        context.unregisterReceiver(this.widgetBroadcastReceiver);
    }

    private void hideWidget() {
        notificationManager.cancel(WIDGET_NOTIFICATION_ID);
    }

    private class WidgetButtonClickReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String buttonName = intent.getStringExtra(WidgetFactory.WIDGET_BUTTON_NAME);

            switch (buttonName) {
                case "play_pause": widgetPresenter.onMainButtonClicked();
                    break;
                case "rewind":
                    widgetPresenter.onRewindClicked();
                    break;
                case "forward":
                    widgetPresenter.onFastForwardClicked();
                    break;
                case "vol-":
                    widgetPresenter.onVolumeDownClicked();
                    break;
                case "vol+":
                    widgetPresenter.onVolumeUpClicked();
                    break;
                case "reconnect":
                    widgetPresenter.onReconnectClicked();
                    break;
            }
        }
    }
}
