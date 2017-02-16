package ged.mediaplayerremote.data.repository.datasource;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import ged.mediaplayerremote.data.R;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link ButtonDataStore} implementation based on {@link SharedPreferences}
 */
public class SharedPrefsButtonDataStore implements ButtonDataStore {
    private Resources resources;
    private SharedPreferences preferences;
    private String packageName;

    @Inject
    public SharedPrefsButtonDataStore(Context context) {
        this.resources = context.getResources();
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.packageName = context.getPackageName();

    }

    @Override
    public int getTabCount() {
        return preferences.getInt(getTabCountPrefKey(), getTabCountDefaultValue());
    }

    @Override
    public String getTabName(int tabIndex) {
        return preferences.getString(getTabNamePrefKey(tabIndex), getTabNameDefaultValue(tabIndex));
    }

    @Override
    public void setTabName(int tabIndex, String tabName) {
        preferences.edit().putString(getTabNamePrefKey(tabIndex), tabName).apply();
    }

    @Override
    public void addTab(String tabName) {
        SharedPreferences.Editor editor = preferences.edit();
        int tabCount = getTabCount();
        editor.putInt(getTabCountPrefKey(), tabCount);
        editor.putString(getTabNamePrefKey(tabCount), tabName);
        editor.apply();
        setTabCount(tabCount + 1);
    }

    @Override
    public void deleteTab(int tabIndex) {
        for (int i = tabIndex; i < getTabCount() - 1; i++) {
            swapTabs(i, i + 1);
        }
        for (int i = 0; i < 9; i++){
            setButtonCode(getTabCount() - 1, i, 0);
        }
        preferences.edit().remove(getTabNamePrefKey(getTabCount() - 1)).apply();
        setTabCount(getTabCount() - 1);

    }

    @Override
    public void swapTabs(int tabIndex, int tabIndex2) {
        String tempTabName = getTabName(tabIndex);
        setTabName(tabIndex, getTabName(tabIndex2));
        setTabName(tabIndex2, tempTabName);

        List<Integer> tempCodes = new ArrayList<>(getButtonCodes(tabIndex));
        setButtonCodes(tabIndex, getButtonCodes(tabIndex2));
        setButtonCodes(tabIndex2, tempCodes);
    }

    @Override
    public List<Integer> getButtonCodes(int tabIndex) {
        List<Integer> buttonCodesList = new ArrayList<>(9);

        for (int i = 0; i < 9; i++) {
            buttonCodesList.add(getButtonCode(tabIndex, i));
        }
        return buttonCodesList;
    }

    @Override
    public int getButtonCode(int tabIndex, int buttonIndex) {
        String key = getButtonCodePrefKey(tabIndex, buttonIndex);
        int def = getButtonCodeDefaultValue(tabIndex, buttonIndex);
        return preferences.getInt(key, def);
    }

    @Override
    public void setButtonCode(int tabIndex, int buttonIndex, int buttonCode) {
        preferences.edit().putInt(getButtonCodePrefKey(tabIndex, buttonIndex), buttonCode).apply();
    }

    @Override
    public void setButtonCodes(int tabIndex, List<Integer> buttonCodes) {
        for (int i = 0; i < buttonCodes.size(); i++) {
            setButtonCode(tabIndex, i, buttonCodes.get(i));
        }
    }

    @Override
    public String getButtonDescription(int buttonCode) {
        int id = resources.getIdentifier("description_" + buttonCode, "string", packageName);
        return resources.getString(id);
    }

    private String getTabNamePrefKey(int tabIndex) {
        return "Name of Tab#" + tabIndex;
    }

    private String getTabNameDefaultValue(int tabIndex) {
        String preferenceDefaultValueString = "pref_tab" + tabIndex + "_name_default";
        int defaultValueId = resources.getIdentifier(preferenceDefaultValueString, "string", packageName);
        if (defaultValueId == 0)
            return "";
        else
            return resources.getString(defaultValueId);
    }

    private void setTabCount(int count) {
        preferences.edit().putInt(getTabCountPrefKey(), count).apply();
    }

    private String getTabCountPrefKey() {
        return resources.getString(R.string.pref_tab_count_key);
    }

    private int getTabCountDefaultValue() {
        return resources.getInteger(R.integer.pref_tab_count_default);
    }

    private String getButtonCodePrefKey(int tabIndex, int buttonIndex) {
        return "Tab #" + tabIndex + " Button #" + buttonIndex;
    }

    private int getButtonCodeDefaultValue(int tabIndex, int buttonIndex) {
        String preferenceDefaultValueString = "pref_tab" + tabIndex + "_button" + buttonIndex + "_default";
        int defaultValueId = resources.getIdentifier(preferenceDefaultValueString, "integer", packageName);

        if (defaultValueId == 0)
            return 0;
        else
            return resources.getInteger(defaultValueId);
    }

}
