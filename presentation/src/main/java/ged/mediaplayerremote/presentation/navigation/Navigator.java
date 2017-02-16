package ged.mediaplayerremote.presentation.navigation;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import ged.mediaplayerremote.presentation.view.activity.FileExplorerActivity;
import ged.mediaplayerremote.presentation.view.activity.HelpActivity;
import ged.mediaplayerremote.presentation.view.activity.ServerFinderActivity;
import ged.mediaplayerremote.presentation.view.activity.SettingsActivity;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Class used to navigate through the application.
 */
@Singleton
public class Navigator {
    public static final int GET_SERVER_REQUEST_CODE = 1;
    public static final int SHOULD_PERFORM_FACTORY_RESET_REQUEST_CODE = 2;

    public static final int FACTORY_RESET_DONE_RESULT_CODE = 3;


    @Inject
    public Navigator() {
    }

    /**
     * Goes to the settings screen.
     *
     * @param context A Context needed to open the activity.
     */
    public void navigateToSettings(Activity context) {
        if (context != null) {
            Intent intentToLaunch = SettingsActivity.getCallingIntent(context);
            context.startActivityForResult(intentToLaunch, SHOULD_PERFORM_FACTORY_RESET_REQUEST_CODE);
        }
    }

    /**
     * Goes to the file explorer screen.
     *
     * @param context A Context needed to open the activity.
     */
    public void navigateToFileExplorer(Context context) {
        if (context != null) {
            Intent intentToLaunch = FileExplorerActivity.getCallingIntent(context);
            context.startActivity(intentToLaunch);
        }
    }

    /**
     * Goes to the help screen.
     *
     * @param context A Context needed to open the activity.
     */
    public void navigateToHelp(Context context) {
        if (context != null) {
            Intent intentToLaunch = HelpActivity.getCallingIntent(context);
            context.startActivity(intentToLaunch);
        }
    }

    /**
     * Goes to the server finder screen.
     *
     * @param fragment A fragment needed to open the activity.
     */
    public void navigateToServerFinder(Fragment fragment) {
        if (fragment != null) {
            Intent intentToLaunch = ServerFinderActivity.getCallingIntent(fragment.getActivity());
            fragment.startActivityForResult(intentToLaunch, GET_SERVER_REQUEST_CODE);
        }
    }
}
