package ged.mediaplayerremote.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import ged.mediaplayerremote.data.R;
import ged.mediaplayerremote.domain.repository.UserPreferencesRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 *  An {@link UserPreferencesRepository} implementation based on {@link SharedPreferences} mechanism.
 */
@Singleton
public class SharedPreferencesRepositoryImpl implements UserPreferencesRepository {

    private SharedPreferences prefs;
    private Resources resources;

    @Inject
    public SharedPreferencesRepositoryImpl(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        resources = context.getResources();
    }

    @Override
    public String getIP() {
        String key = resources.getString(R.string.userpref_custom_ip_key);
        String def = resources.getString(R.string.userpref_custom_ip_default);
        return prefs.getString(key, def);
    }

    @Override
    public String getPort() {
        String key = resources.getString(R.string.userpref_port_key);
        String def = resources.getString(R.string.userpref_port_default);
        return prefs.getString(key, def);
    }

    @Override
    public int getConnectionTimeout() {
        String key = resources.getString(R.string.userpref_connection_timeout_key);
        String def = resources.getString(R.string.userpref_connection_timeout_default);
        return Integer.parseInt(prefs.getString(key, def));
    }

    @Override
    public int getVolumeJumpValue() {
        String key = resources.getString(R.string.userpref_vol_jump_key);
        int def = resources.getInteger(R.integer.userpref_vol_jump_default);
        return prefs.getInt(key, def);
    }

    @Override
    public int getTimeJumpValue() {
        String key = resources.getString(R.string.userpref_time_jump_key);
        int def = resources.getInteger(R.integer.userpref_time_jump_default);
        return prefs.getInt(key, def);
    }

    @Override
    public boolean usePhysicalVolumeButtons() {
        String key = resources.getString(R.string.userpref_physical_buttons_key);
        boolean def = resources.getBoolean(R.bool.userpref_physical_buttons_default);
        return prefs.getBoolean(key, def);
    }

    @Override
    public boolean keepScreenOn() {
        String key = resources.getString(R.string.userpref_keep_screen_on_key);
        boolean def = resources.getBoolean(R.bool.userpref_keep_screen_on_default);
        return prefs.getBoolean(key, def);
    }

    @Override
    public boolean adjustScreenBrightness() {
        String key = resources.getString(R.string.userpref_brighness_key);
        boolean def = resources.getBoolean(R.bool.userpref_brighness_default);
        return prefs.getBoolean(key, def);
    }

    @Override
    public boolean showWidget() {
        String key = resources.getString(R.string.userpref_widget_key);
        boolean def = resources.getBoolean(R.bool.userpref_widget_default);
        return prefs.getBoolean(key, def);
    }

    @Override
    public boolean showSnapshot() {
        String key = resources.getString(R.string.userpref_snapshot_key);
        boolean def = resources.getBoolean(R.bool.userpref_snapshot_default);
        return prefs.getBoolean(key, def);
    }

    @Override
    public int getSnapshotUpdateInterval() {
        String key = resources.getString(R.string.userpref_snapshot_update_key);
        int def = resources.getInteger(R.integer.userpref_snapshot_update_default);
        return prefs.getInt(key, def);
    }
}
