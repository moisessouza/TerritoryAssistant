package ged.mediaplayerremote.presentation.presenter;

import ged.mediaplayerremote.domain.interactor.DefaultSubscriber;
import ged.mediaplayerremote.domain.interactor.UseCase;
import ged.mediaplayerremote.domain.interactor.UseCaseParametrized;
import ged.mediaplayerremote.domain.model.PlaybackStatus;
import ged.mediaplayerremote.domain.repository.ButtonRepository;
import ged.mediaplayerremote.domain.repository.UserPreferencesRepository;
import ged.mediaplayerremote.presentation.dagger2.PerFragment;
import ged.mediaplayerremote.presentation.model.PlaybackStatusModel;
import ged.mediaplayerremote.presentation.model.mapper.PlaybackStatusModelMapper;
import ged.mediaplayerremote.presentation.util.Toolbox;
import ged.mediaplayerremote.presentation.view.MainView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@PerFragment
public class MainPresenter implements Presenter {

    private MainView mainView;

    private ButtonRepository buttonRepository;
    private UserPreferencesRepository userPreferences;
    private PlaybackStatusModelMapper playbackStatusModelMapper;
    private Subscription volumeBarTimer;

    private UseCase playPauseUseCase;
    private UseCase lowerVolumeUseCase;
    private UseCase raiseVolumeUseCase;
    private UseCase fastForwardUseCase;
    private UseCase rewindUseCase;
    private UseCase editModeListenerUseCase;
    private UseCase getPlaybackStatusUseCase;
    private UseCase getSnapshotUseCase;
    private UseCaseParametrized<Boolean> setEditModeUseCase;
    private UseCaseParametrized<Long> setPositionUseCase;
    private UseCaseParametrized<Integer> setVolumeUseCase;

    private boolean volumeBarLocked = false;
    private boolean positionBarLocked = false;
    private int tabSelected = 0;

    @Inject
    public MainPresenter(
            PlaybackStatusModelMapper playbackStatusModelMapper,
            UserPreferencesRepository userPreferences,
            ButtonRepository buttonRepository,
            @Named("playbackStatus") UseCase getPlaybackStatusUseCase,
            @Named("snapshot") UseCase getSnapshotUseCase,
            @Named("editModeListener") UseCase editModeListenerUseCase,
            @Named("playPause") UseCase playPauseUseCase,
            @Named("fastForward") UseCase fastForwardUseCase,
            @Named("rewind") UseCase rewindUseCase,
            @Named("VolumeDown") UseCase lowerVolumeUseCase,
            @Named("VolumeUp") UseCase raiseVolumeUseCase,
            @Named("setEditMode") UseCaseParametrized<Boolean> setEditModeUseCase,
            @Named("SetPosition") UseCaseParametrized<Long> setPositionUseCase,
            @Named("SetVolume") UseCaseParametrized<Integer> setVolumeUseCase) {

        this.playbackStatusModelMapper = playbackStatusModelMapper;
        this.userPreferences = userPreferences;
        this.buttonRepository = buttonRepository;
        this.getSnapshotUseCase = getSnapshotUseCase;
        this.editModeListenerUseCase = editModeListenerUseCase;
        this.setEditModeUseCase = setEditModeUseCase;
        this.getPlaybackStatusUseCase = getPlaybackStatusUseCase;
        this.buttonRepository = buttonRepository;
        this.playPauseUseCase = playPauseUseCase;
        this.fastForwardUseCase = fastForwardUseCase;
        this.rewindUseCase = rewindUseCase;
        this.lowerVolumeUseCase = lowerVolumeUseCase;
        this.raiseVolumeUseCase = raiseVolumeUseCase;
        this.setPositionUseCase = setPositionUseCase;
        this.setVolumeUseCase = setVolumeUseCase;

    }

    public void setView(MainView mainView) {
        this.mainView = mainView;
    }

    public void initialize() {
        this.initializeViewPager();
    }

    @Override
    public void resume() {
        checkUserPreferences();
        registerEditModeListener();
        registerPlaybackStatusListener();
        if (snapshotEnabled()) {
            showSnapshot();
            registerSnapshotListener();
        } else {
            hideSnapshot();
        }
    }

    @Override
    public void pause() {
        getPlaybackStatusUseCase.unsubscribe();
        getSnapshotUseCase.unsubscribe();
    }

    @Override
    public void destroy() {
        getSnapshotUseCase.unsubscribe();

        mainView = null;
    }


    public void onAppBarClicked() {
        mainView.showNavDrawer();
    }

    public void onStandardModeRequested() {
        setEditModeUseCase.execute(new DefaultSubscriber(), false);
    }

    public void onEditModeRequested() {
        setEditModeUseCase.execute(new DefaultSubscriber(), true);
    }

    public void onVolumeRaised() {
        raiseVolumeUseCase.execute(new DefaultSubscriber());
        volumeChanged();
    }

    public void onVolumeLowered() {
        lowerVolumeUseCase.execute(new DefaultSubscriber());
        volumeChanged();
    }

    public void onFastForward() {
        fastForwardUseCase.execute(new DefaultSubscriber());
        mainView.hideVolumeBar();
        mainView.showPositionBar();
    }

    public void onRewind() {
        rewindUseCase.execute(new DefaultSubscriber());
        mainView.hideVolumeBar();
        mainView.showPositionBar();
    }

    public void onPlayPauseClicked() {
        playPauseUseCase.execute(new DefaultSubscriber());
    }

    /**
     * Lock the position progress bar so that incoming PlaybackStatus update will not change the bar value while user is
     * manually manipulating it.
     */
    public void onPositionBarTouched() {
        positionBarLocked = true;
    }

    public void onPositionBarChosen(int position) {
        positionBarLocked = false;
        setPositionUseCase.execute(new DefaultSubscriber(), (long) position);
    }

    public void onPositionBarMoved(int position) {
        String positionString = Toolbox.positionIntToString(position);
        this.mainView.setPositionLabel(positionString);
    }

    /**
     * Lock the volume progress bar so that incoming PlaybackStatus update will not change the bar value while user is
     * manually manipulating it.
     */
    public void onVolumeBarTouched() {
        volumeBarLocked = true;
    }

    public void onVolumeBarChosen(int volume) {
        resetVolumeBarVisibilityTimer();
        volumeBarLocked = false;
        setVolumeUseCase.execute(new DefaultSubscriber(), volume);
    }

    public void onVolumeBarMoved(int volume) {
        resetVolumeBarVisibilityTimer();

        setVolumeBarView(volume);
    }

    public void onEditModeMenuClicked() {
        int tabCount = buttonRepository.getTabCount();
        if (tabSelected == 0 && tabCount > 1)
            mainView.showEditMenuFirstTab();
        else if (tabSelected > 0 && tabSelected < tabCount - 1)
            mainView.showEditMenuMiddleTab();
        else if (tabSelected == tabCount - 1 && tabCount > 1)
            mainView.showEditMenuLastTab();
        else if (tabSelected == 0 && tabCount == 1)
            mainView.showEditMenuOnlyTab();
    }

    public void onAddTabClicked() {
        mainView.showNewTabDialog();
    }

    public void onNewTabNameChosen(String name) {
        buttonRepository.addTab(name);
        showButtonPages();
        mainView.moveToTab(buttonRepository.getTabCount());
    }

    public void onTabRenameClicked() {
        mainView.showRenameTabDialog(buttonRepository.getTabName(tabSelected));
    }

    public void onTabRenamed(String newName) {
        buttonRepository.setTabName(tabSelected, newName);
        showButtonPages();
    }

    public void onMoveRightClicked() {
        buttonRepository.swapTabs(tabSelected, tabSelected + 1);
        showButtonPages();
        mainView.moveToTab(tabSelected + 1);
    }

    public void onMoveLeftClicked() {
        buttonRepository.swapTabs(tabSelected, tabSelected - 1);
        showButtonPages();
        mainView.moveToTab(tabSelected - 1);
    }

    public void onDeleteTabClicked() {
        mainView.showConfirmTabDeleteDialog(buttonRepository.getTabName(tabSelected));
    }

    public void onDeleteTabConfirmed() {
        buttonRepository.deleteTab(tabSelected);
        showButtonPages();
        mainView.moveToTab(tabSelected);
    }

    public void onTabSelected(int tab) {
        tabSelected = tab;
    }

    private void volumeChanged() {
        mainView.hidePositionBar();
        mainView.showVolumeBar();
        resetVolumeBarVisibilityTimer();
    }

    private void setVolumeBarView(int volume) {

        mainView.setVolumeBar(volume);
        mainView.setVolumeLabel(volume);
        if (volume >= 50) {
            mainView.setVolumeIconHigh();
        } else if (volume < 50 && volume > 0) {
            mainView.setVolumeIconLow();
        } else {
            mainView.setVolumeIconMuted();
        }


    }

    private void resetVolumeBarVisibilityTimer() {
        if (volumeBarTimer != null && !volumeBarTimer.isUnsubscribed()) {
            volumeBarTimer.unsubscribe();
        }
        Observable<Long> observable = Observable.timer(3, TimeUnit.SECONDS, Schedulers.io());
        volumeBarTimer = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (mainView != null) {
                        mainView.hideVolumeBar();
                        mainView.showPositionBar();
                    }

                });
    }

    private boolean snapshotEnabled() {
        return userPreferences.showSnapshot();
    }

    private void checkUserPreferences() {
        mainView.setKeepScreenOn(userPreferences.keepScreenOn());

    }

    private void initializeViewPager() {
        this.showButtonPages();
    }

    private void showToastMessage(String message) {
        mainView.showMessage(message);
    }

    private void registerSnapshotListener() {
        getSnapshotUseCase.execute(new DefaultSubscriber<byte[]>() {
            @Override
            public void onNext(byte[] image) {
                mainView.updatePreview(image);
            }
        });
    }

    private void showSnapshot() {
        mainView.showNavDrawerSnapshot();
    }

    private void hideSnapshot() {
        mainView.hideNavDrawerSnapshot();
        mainView.updatePreview(null);
    }

    private void registerPlaybackStatusListener() {
        getPlaybackStatusUseCase.execute(new PlaybackStatusSubscriber());
    }

    private void registerEditModeListener() {
        editModeListenerUseCase.execute(new DefaultSubscriber<Boolean>() {
            @Override
            public void onNext(Boolean editMode) {
                if (editMode) {
                    enableEditMode();
                } else {
                    disableEditMode();
                }
            }
        });
    }

    private void showButtonPages() {
        List<String> buttonTabs = new ArrayList<>();

        for (int i = 0; i < buttonRepository.getTabCount(); i++) {
            buttonTabs.add(buttonRepository.getTabName(i));
        }
        mainView.renderButtonPages(buttonTabs);
    }

    private void enableEditMode() {
        mainView.setEditModeSwitch(true);
        mainView.showEditModeAppBar();
    }

    private void disableEditMode() {
        mainView.showStandardAppBar();
        mainView.setEditModeSwitch(false);
    }

    private class PlaybackStatusSubscriber extends DefaultSubscriber<PlaybackStatus> {

        private int nullStatusCounter = 0;

        @Override
        public void onNext(PlaybackStatus playbackStatus) {

            if (playbackStatus == null) {
                nullStatusCounter++;
            } else {
                nullStatusCounter = 0;
                PlaybackStatusModel playbackStatusModel = playbackStatusModelMapper.transform(playbackStatus);
                updateStatusViews(playbackStatusModel);
            }

            /**
             * For smoother UX, do not clear all playback status views after single timed out response. Wait for 3
             * consecutive null statuses.
             */
            if (nullStatusCounter > 3) {
                updateStatusViews(null);
            }
        }

        private void updateStatusViews(PlaybackStatusModel playbackStatusModel) {
            mainView.updateNavDrawerViews(playbackStatusModel);
            mainView.updateTitle(playbackStatusModel);
            mainView.updateMainButtonIcon(playbackStatusModel);
            if (playbackStatusModel != null) {
                if (!positionBarLocked) {
                    mainView.setPositionBar((int) playbackStatusModel.getPosition());
                    mainView.setPositionLabel(playbackStatusModel.getPositionString());
                    mainView.setPositionBarMax((int) playbackStatusModel.getDuration());
                    mainView.setDurationLabel(playbackStatusModel.getDurationString());
                }
                if (!volumeBarLocked) {
                    setVolumeBarView(playbackStatusModel.getVolume());
                }
            }
            else {
                mainView.setPositionBar(0);
                mainView.setPositionLabel(null);
                mainView.setPositionBarMax(0);
                mainView.setDurationLabel(null);
                mainView.setVolumeBar(0);
            }
        }
    }
}
