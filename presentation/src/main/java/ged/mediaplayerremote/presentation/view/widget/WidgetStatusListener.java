package ged.mediaplayerremote.presentation.view.widget;

public interface WidgetStatusListener {

    /**
     * A method to call after user has enabled the widget_control in the settings.
     */
    void onWidgetEnabled();

    /**
     * A method to call after user has disabled the widget_control in the settings.
     */
    void onWidgetDisabled();
}
