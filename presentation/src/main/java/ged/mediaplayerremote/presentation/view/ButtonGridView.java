package ged.mediaplayerremote.presentation.view;

import ged.mediaplayerremote.presentation.model.ButtonModel;

import java.util.List;

/**
 * Interface representing a View in a model view presenter pattern.
 * This view represents grid of 9 reprogrammable buttons with MPC-HC commands.
 */
public interface ButtonGridView {

    /**
     * Render all buttons on this page.
     *
     * @param buttons List of buttons that will be shown.
     */
    void renderButtonTab(List<ButtonModel> buttons);

    /**
     * Swap position of two buttons.
     *
     * @param fromButton first button to be swapped.
     * @param toButton second button to be swapped.
     */
    void swapButtons(int fromButton, int toButton);

    /**
     * Make unassigned buttons visible.
     */
    void showEmptyButtons();

    /**
     * Make unassigned buttons invisible.
     */
    void hideEmptyButtons();

    /**
     * Show a dialog with list of commands that can be assigned to this button.
     *
     * @param buttonIndex index of the button to be reassigned.
     */
    void showButtonAssignDialog(int buttonIndex);



}
