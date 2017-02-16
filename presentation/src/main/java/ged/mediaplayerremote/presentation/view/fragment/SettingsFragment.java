package ged.mediaplayerremote.presentation.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import ged.mediaplayerremote.AndroidApplication;
import ged.mediaplayerremote.R;
import ged.mediaplayerremote.domain.controller.ServerSettingsChangedListener;
import ged.mediaplayerremote.domain.repository.UserPreferencesRepository;
import ged.mediaplayerremote.presentation.dagger2.components.ApplicationComponent;
import ged.mediaplayerremote.presentation.dagger2.components.DaggerScopedComponent;
import ged.mediaplayerremote.presentation.dagger2.components.ScopedComponent;
import ged.mediaplayerremote.presentation.navigation.Navigator;
import ged.mediaplayerremote.presentation.sharedpreferences.NumberPickerPreference;
import ged.mediaplayerremote.presentation.view.activity.BaseActivity;
import ged.mediaplayerremote.presentation.view.widget.WidgetStatusListener;

import javax.inject.Inject;

/**
 * Fragment that shows settings.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener,
        Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    /**
     * Interface to inform parent activity about user clicking Server Finder.
     */
    public interface ServerFinderListener {
        /**
         * Method to call when user clicks Server Finder preference.
         */
        void onServerFinderClicked();
    }

    @Inject ServerSettingsChangedListener serverSettingsChangedListener;
    @Inject WidgetStatusListener widgetStatusListener;
    @Inject UserPreferencesRepository userPreferencesRepository;


    private boolean factoryResetPerformed = false;
    private int xmlId;
    private SharedPreferences sharedPreferences;
    private ServerFinderListener serverFinderListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() instanceof ServerFinderListener) {
            this.serverFinderListener = (ServerFinderListener) getActivity();
        }
        xmlId = R.xml.preferences_main_screen;
        changePrefScreen(R.xml.preferences_main_screen, R.string.settings);
        sharedPreferences = getPreferenceScreen().getSharedPreferences();
        updateSummaries(getPreferenceScreen());
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Switch preference screens.
     *
     * @param xmlId link to the preference XML
     * @param titleId link to a string that should be used as a title in the appbar.
     */
    public void changePrefScreen(int xmlId, int titleId) {
        this.xmlId = xmlId;
        PreferenceScreen prefScreen = getPreferenceScreen();
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getActivity().getString(titleId));
        }
        if (prefScreen != null) {
            prefScreen.removeAll();
        }

        addPreferencesFromResource(xmlId);
        updateSummaries(prefScreen);

        if (xmlId == R.xml.preferences_server_screen) {
            registerServerFinderListener();
        }
        else if (xmlId == R.xml.preferences_main_screen) {
            registerFactoryResetListener();
        }

    }

    /**
     * A method to by called by parent activity whenever user has pressed back button on the bottom of device, or back
     * arrow in the appbar.
     */
    public void onUpButton() {
        if (xmlId == R.xml.preferences_main_screen) {
            if (factoryResetPerformed) {
                getActivity().setResult(Navigator.FACTORY_RESET_DONE_RESULT_CODE);
            }
            getActivity().finish();
        } else {
            changePrefScreen(R.xml.preferences_main_screen, R.string.settings);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();

        if (key.equals(getString(R.string.userpref_server_screen_key))) {
            changePrefScreen(R.xml.preferences_server_screen, R.string.visible_pref_server_settings);
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeInjector();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        if (key.equals(getString(R.string.userpref_brighness_key))) {
            BaseActivity baseActivity = (BaseActivity) getActivity();
            baseActivity.adjustBrightness();
        } else if (key.equals(getString(R.string.userpref_widget_key))) {
            
            if (userPreferencesRepository.showWidget()) {
                widgetStatusListener.onWidgetEnabled();
            } else {
                widgetStatusListener.onWidgetDisabled();
            }
        } else if (key.equals(getString(R.string.userpref_custom_ip_key))
                || key.equals(getString(R.string.userpref_port_key))
                || key.equals(getString(R.string.userpref_connection_timeout_key))) {
            serverSettingsChangedListener.onServerSettingsChanged();
            if (userPreferencesRepository.showWidget()) {
                widgetStatusListener.onWidgetDisabled();
                widgetStatusListener.onWidgetEnabled();
            }
        }

        updatePrefSummary(preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        if (key.equals(getString(R.string.userpref_factory_reset_key))) {
            factoryResetPerformed = true;
            serverSettingsChangedListener.onServerSettingsChanged();
            if (userPreferencesRepository.showWidget()) {
                widgetStatusListener.onWidgetDisabled();
                widgetStatusListener.onWidgetEnabled();
            }
            changePrefScreen(R.xml.preferences_main_screen, R.string.settings);
        }
        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (key.equals(getString(R.string.userpref_server_finder_key))) {
            serverFinderListener.onServerFinderClicked();
        }
        return false;
    }

    /**
     * If {@link ged.mediaplayerremote.presentation.view.activity.ServerFinderActivity} returns an IP, set the value to
     * the corresponding preference.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Navigator.GET_SERVER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("SERVER_IP");
                sharedPreferences.edit().putString(getString(R.string.userpref_custom_ip_key), result).apply();
                EditTextPreference customIP = (EditTextPreference) findPreference(getString(R.string.userpref_custom_ip_key));
                customIP.setText(result);
                updatePrefSummary(findPreference(getString(R.string.userpref_custom_ip_key)));
                serverSettingsChangedListener.onServerSettingsChanged();
            }
       }
    }

    /**
     * Recursively update text summary of all preference groups and preferences.
     *
     * @param pref root preference group or preference.
     */
    private void updateSummaries(Preference pref) {
        if (pref instanceof PreferenceGroup) {
            PreferenceGroup prefGroup = (PreferenceGroup) pref;
            for (int i = 0; i < prefGroup.getPreferenceCount(); i++) {
                updateSummaries(prefGroup.getPreference(i));
            }
        } else {
            updatePrefSummary(pref);
        }
    }

    private void updatePrefSummary(Preference preference) {
        if (preference instanceof EditTextPreference) {
            updateEditTextPreferenceSummary((EditTextPreference) preference);
        } else if (preference instanceof NumberPickerPreference) {
            updateNumberPickerPreferenceSummary((NumberPickerPreference) preference);
        }
    }

    private void updateEditTextPreferenceSummary(EditTextPreference editTextPreference) {
        String preferenceKey = editTextPreference.getKey();

        if (preferenceKey.equals(getString(R.string.userpref_connection_timeout_key))) {
            editTextPreference.setSummary(editTextPreference.getText() + "ms");
        } else if (preferenceKey.equals(getString(R.string.userpref_time_jump_key))) {
            editTextPreference.setSummary(editTextPreference.getText() + "s");
        } else if (preferenceKey.equals(getString(R.string.userpref_snapshot_update_key))) {
            editTextPreference.setSummary(editTextPreference.getText() + "s");
        } else {
            editTextPreference.setSummary(editTextPreference.getText());
        }
    }

    private void updateNumberPickerPreferenceSummary(NumberPickerPreference numberPickerPreference) {
        numberPickerPreference.setSummary(numberPickerPreference.getValue() + numberPickerPreference.getUnit());
    }
    
    private void registerFactoryResetListener() {
        Preference factoryReset = findPreference(getString(R.string.userpref_factory_reset_key));
        if (factoryReset != null) {
            factoryReset.setOnPreferenceChangeListener(this);
        }
    }

    private void registerServerFinderListener() {
        Preference factoryReset = findPreference(getString(R.string.userpref_server_finder_key));
        if (factoryReset != null) {
            factoryReset.setOnPreferenceClickListener(this);
        }
    }

    private void initializeInjector() {
        AndroidApplication androidApplication = (AndroidApplication) getActivity().getApplication();
        ApplicationComponent appComponent = androidApplication.getApplicationComponent();
        ScopedComponent scopedComponent = DaggerScopedComponent.builder().applicationComponent(appComponent).build();
        scopedComponent.inject(this);
    }
}