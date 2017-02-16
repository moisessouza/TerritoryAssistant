package ged.mediaplayerremote.presentation.dagger2.components;

import dagger.Component;
import ged.mediaplayerremote.presentation.dagger2.PerFragment;
import ged.mediaplayerremote.presentation.dagger2.modules.InteractorModule;
import ged.mediaplayerremote.presentation.view.fragment.ButtonGridFragment;
import ged.mediaplayerremote.presentation.view.fragment.FileExplorerFragment;
import ged.mediaplayerremote.presentation.view.fragment.MainFragment;
import ged.mediaplayerremote.presentation.view.fragment.ServerFinderFragment;
import ged.mediaplayerremote.presentation.view.fragment.SettingsFragment;

@PerFragment
@Component(dependencies = ApplicationComponent.class, modules = {InteractorModule.class})
public interface ScopedComponent {

    void inject(ButtonGridFragment buttonGridFragment);
    void inject(MainFragment remoteFragment);
    void inject(FileExplorerFragment fileExplorerFragment);
    void inject(ServerFinderFragment serverFinderFragment);
    void inject(SettingsFragment settingsFragment);

}
