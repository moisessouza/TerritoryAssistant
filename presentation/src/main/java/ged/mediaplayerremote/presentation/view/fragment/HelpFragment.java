package ged.mediaplayerremote.presentation.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ged.mediaplayerremote.R;

/**
 * Fragment that shows a view with a user guide.
 */
public class HelpFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_help, container, false);
    }
}
