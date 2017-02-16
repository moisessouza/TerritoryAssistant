package ged.mediaplayerremote.presentation.view;

import ged.mediaplayerremote.presentation.model.PlaybackStatusModel;
import ged.mediaplayerremote.presentation.model.ServerModel;

import java.util.List;

/**
 * Interface representing a View in a model view presenter pattern.
 * This view represents main view of the remote. Note: the middle views (button grids) are represented by different
 * interfaces: {@link ButtonGridView}
 */
public interface MainView extends BaseView
{
    /**
     * Render a list of pages that contain grids of buttons.
     *
     * @param pageTitles List of titles of all pages to be rendered.
     */
    void renderButtonPages(List<String> pageTitles);

    /**
     * Update big preview (displayed in a Navigation Drawer) and small preview (displayed in an appbar) ImageViews with
     * a new image.
     *
     * @param snapshot Image to be displayed.
     */
    void updatePreview(byte[] snapshot);

    /**
     * Update the icon of the main button to match the value of playback state. It will show pause icon if the file is
     * currently playing, and play icon otherwise.
     *
     * @param playbackStatusModel An object the playback state will be taken from.
     */
    void updateMainButtonIcon(PlaybackStatusModel playbackStatusModel);

    /**
     * Set a value to the title view in the app bar.
     *
     * @param playbackStatusModel An object the title will be taken from.
     */
    void updateTitle(PlaybackStatusModel playbackStatusModel);

    /**
     * Set a value to the position label. The value should be a String in a hh:mm:ss format.
     *
     * @param progress a String value to be set.
     */
    void setPositionLabel(String progress);

    /**
     * Set a value to the time duration label. The value should be a String in a hh:mm:ss format.
     *
     * @param duration a String value to be set
     */
    void setDurationLabel(String duration);

    /**
     * Set progress value to the position progress bar.
     *
     * @param progress A value to be set.
     */
    void setPositionBar(int progress);

    /**
     * Set a maximum value to a position bar. This value should correspond to the duration of currently played file.
     *
     * @param maxPosition A value to be set as a maximum of the progress bar.
     */
    void setPositionBarMax(int maxPosition);

    /**
     * Show a view that shows a {@link android.widget.ProgressBar} that represents current progress of the playback in
     * the MPC-HC player, a text label with current position and text label with overall duration of currently loaded
     * file.
     */
    void showPositionBar();

    /**
     * Hide a view that shows a {@link android.widget.ProgressBar} that represents current progress of the playback in
     * the MPC-HC player, a text label with current position and text label with overall duration of currently loaded
     * file.
     */
    void hidePositionBar();

    /**
     * Show a view that shows a {@link android.widget.ProgressBar} with a volume that is currently set in the MPC-HC
     * player, a text label with current volume and an icon that changes according to the volume value.
     */
    void showVolumeBar();

    /**
     * Set a progress of the volume bar to selected value.
     *
     * @param percent What value should the progress bar have.
     */
    void setVolumeBar(int percent);

    /**
     * Show a percentage value next to the volume bar.
     *
     * @param percent What value should be shown.
     */
    void setVolumeLabel(int percent);

    /**
     * Show an icon next to the volume bar with a "volume high" graphic.
     */
    void setVolumeIconHigh();

    /**
     * Show an icon next to the volume bar with a "volume low" graphic.
     */
    void setVolumeIconLow();

    /**
     * Show an icon next to the volume bar with a "volume muted" graphic.
     */
    void setVolumeIconMuted();

    /**
     * Hide a view that shows a {@link android.widget.ProgressBar} with a volume that is currently set in the MPC-HC
     * player, a text label with current volume and an icon that changes with according to volume value.
     */
    void hideVolumeBar();

    /**
     * Update a view that shows an IP and hostname of the server that the app is currently connected to.
     *
     * @param serverModel A server to show.
     */
    void updateServerInformation(ServerModel serverModel);

    /**
     * Show a Navigation Drawer.
     */
    void showNavDrawer();

    /**
     * Hide a Navigation Drawer.
     */
    void hideNavDrawer();

    /**
     * Update all views in Navigation Drawer. This includes: file name, playback state (playing / paused etc), position,
     * duration.
     *
     * @param playbackStatusModel An object from which all corresponding values will be taken from.
     */
    void updateNavDrawerViews(PlaybackStatusModel playbackStatusModel);

    /**
     * Show a view in a Navigation Drawer, that displays an image of what is currently visible in the MPC-HC player.
     */
    void showNavDrawerSnapshot();

    /**
     * Hide a view in a Navigation Drawer, that displays an image of what is currently visible in the MPC-HC player.
     */
    void hideNavDrawerSnapshot();

    /**
     * Prevent the device from turning the screen off and falling asleep.
     *
     * @param keepScreenOn true if screen should be kept on, false otherwise.
     */
    void setKeepScreenOn(boolean keepScreenOn);

    /**
     * Switch the appbar to the edit mode, that switches the button to open a Navigation Drawer into a button to close
     * the edit mode, hides the preview, and changes title of currently played file into an "Edit Mode".
     */
    void showEditModeAppBar();

    /**
     * Switch the appbar to the standard mode, that contains a button to open a Navigation Drawer, a movie preview (if
     * enabled by the user) and a title of currently played file.
     */
    void showStandardAppBar();

    /**
     * Open a drop-down menu that is applicable to a situation when there is more than 1 page with buttons, and
     * currently selected page has lowest index.
     */
    void showEditMenuFirstTab();

    /**
     * Open a drop-down menu that is applicable to a situation when there is more than 1 page with buttons, and
     * currently selected page has neither lowest nor highest index.
     */
    void showEditMenuMiddleTab();

    /**
     * Open a drop-down menu that is applicable to a situation when there is more than 1 page with buttons, and
     * currently selected page has the highest index.
     */
    void showEditMenuLastTab();

    /**
     * Open a drop-down menu that is applicable to a situation when there is only 1 page with buttons left.
     */
    void showEditMenuOnlyTab();

    /**
     * Switch to selected button grid page in a {@link android.support.v4.view.ViewPager}
     *
     * @param tab Index of a page to be opened.
     */
    void moveToTab(int tab);

    /**
     * Show a dialog that asks user to type the name for a new tab.
     */
    void showNewTabDialog();

    /**
     * Show a dialog that asks the user to type new name of the current tab.
     *
     * @param oldName Current name of the tab.
     */
    void showRenameTabDialog(String oldName);

    /**
     * Show a dialog that asks the user to confirm deletion of the current tab.
     *
     * @param tabName A name of the tab to be deleted
     */
    void showConfirmTabDeleteDialog(String tabName);

    /**
     * Switch the state of edit mode {@link android.support.v7.widget.SwitchCompat} in the Navigation Drawer.
     *
     * @param checked New state of the switch.
     */
    void setEditModeSwitch(boolean checked);
}
