package ged.mediaplayerremote.presentation.view.fragment;

import android.app.Fragment;
import android.widget.Toast;
import ged.mediaplayerremote.presentation.dagger2.HasComponent;

/**
 * Base {@link android.app.Fragment} class to be extended by fragments in this app.
 */
public class BaseFragment extends Fragment {

    /**
     * Get a Dagger component for dependency injection.
     */
    @SuppressWarnings("unchecked")
    protected <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((HasComponent<C>) getActivity()).getComponent());
    }

    /**
     * Show a {@link android.widget.Toast} message.
     *
     * @param message An string representing a message to be shown.
     */
    protected void showToastMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

}
