package ged.mediaplayerremote.data.repository;

import ged.mediaplayerremote.data.repository.datasource.ButtonDataStore;
import ged.mediaplayerremote.domain.repository.ButtonRepository;

import javax.inject.Inject;
import java.util.List;

/**
 * A {@link ButtonRepository} implementation used for retrieving button and tab configuration related data.
 */
public class ButtonDataRepository implements ButtonRepository {
    private ButtonDataStore buttonDataStore;

    @Inject
    public ButtonDataRepository(ButtonDataStore buttonDataStore) {
        this.buttonDataStore = buttonDataStore;
    }

    @Override
    public int getTabCount() {
        return buttonDataStore.getTabCount();
    }

    @Override
    public String getTabName(int tabIndex) {
        return buttonDataStore.getTabName(tabIndex);
    }

    @Override
    public void setTabName(int tabIndex, String tabName) {
        buttonDataStore.setTabName(tabIndex, tabName);
    }

    @Override
    public void addTab(String tabName) {
        buttonDataStore.addTab(tabName);
    }

    @Override
    public void deleteTab(int tabIndex) {
        buttonDataStore.deleteTab(tabIndex);
    }

    @Override
    public void swapTabs(int tabIndex1, int tabIndex2) {
        buttonDataStore.swapTabs(tabIndex1, tabIndex2);
    }

    @Override
    public List<Integer> getButtonCodes(int tabIndex) {
        return buttonDataStore.getButtonCodes(tabIndex);
    }

    @Override
    public int getButtonCode(int tabIndex, int buttonIndex) {
        return buttonDataStore.getButtonCode(tabIndex, buttonIndex);
    }

    @Override
    public void setButtonCode(int tabIndex, int buttonIndex, int buttonCode) {
        buttonDataStore.setButtonCode(tabIndex, buttonIndex, buttonCode);
    }

    @Override
    public void setButtonCodes(int tabIndex, List<Integer> buttonCodes) {
        buttonDataStore.setButtonCodes(tabIndex, buttonCodes);
    }

    @Override
    public String getButtonDescription(int buttonCode) {
        return buttonDataStore.getButtonDescription(buttonCode);
    }
}
