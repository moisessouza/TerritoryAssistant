package ged.mediaplayerremote.data.repository.datasource;

import java.util.List;

/**
 * Interface that represents a data store where button configuration is saved and retrieved from.
 */
public interface ButtonDataStore
{
    /**
     * Get the amount of button tabs.
     */
    int getTabCount();

    /**
     * Get a text description of a tab.
     *
     * @param tabIndex Index of tab used to get description.
     */
    String getTabName(int tabIndex);

    /**
     * Set a new text description to a tab.
     *
     * @param tabIndex Index of a tab to receive new description.
     * @param tabName New description to be used.
     */
    void setTabName(int tabIndex, String tabName);

    /**
     * Add a new tab. New tab is placed after the last tab.
     *
     * @param tabName String to be used as new tab's description.
     */
    void addTab(String tabName);

    /**
     * Delete a tab.
     *
     * @param tabIndex Index of a tab to be deleted.
     */
    void deleteTab(int tabIndex);

    /**
     * Swap position of two tabs.
     *
     * @param tabIndex1 First tab to be swapped.
     * @param tabIndex2 Second tab to be swapped.
     */
    void swapTabs(int tabIndex1, int tabIndex2);

    /**
     * Get a MPC-HC api command code assigned to a button.
     *
     * @param tabIndex Index of a tab where the button is located.
     * @param buttonIndex Index of a button to read the code from.
     */
    int getButtonCode(int tabIndex, int buttonIndex);

    /**
     * Get a list of all MPC-HC api command codes assigned to buttons in a tab.
     *
     * @param tabIndex Index of a tab to read all button codes from.
     */
    List<Integer> getButtonCodes(int tabIndex);

    /**
     * Assign a new MPC-HC api command code to a button.
     *
     * @param tabIndex Index of a bat where the button is located.
     * @param buttonIndex Index of a button to be assigned a new command code.
     * @param buttonCode New command code to be assigned.
     */
    void setButtonCode(int tabIndex, int buttonIndex, int buttonCode);

    /**
     * Assign a MPC-HC api command code to every button in a tab.
     *
     * @param tabIndex Index of a tab to be assigned new codes.
     * @param buttonCodes List of codes to be assigned to buttons.
     */
    void setButtonCodes(int tabIndex, List<Integer> buttonCodes);

    /**
     * Get a text description of the MPC-HC api command code.
     *
     * @param buttonCode command code used to get description.
     */
    String getButtonDescription(int buttonCode);


}
