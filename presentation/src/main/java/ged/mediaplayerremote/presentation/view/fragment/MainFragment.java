package ged.mediaplayerremote.presentation.view.fragment;

import android.app.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ged.mediaplayerremote.R;
import ged.mediaplayerremote.domain.model.PlaybackState;
import ged.mediaplayerremote.presentation.dagger2.components.ScopedComponent;
import ged.mediaplayerremote.presentation.model.PlaybackStatusModel;
import ged.mediaplayerremote.presentation.model.ServerModel;
import ged.mediaplayerremote.presentation.presenter.MainPresenter;
import ged.mediaplayerremote.presentation.util.Toolbox;
import ged.mediaplayerremote.presentation.view.MainView;
import ged.mediaplayerremote.presentation.view.adapter.ButtonTabsFragmentAdapter;

import javax.inject.Inject;
import java.util.List;


/**
 * Fragment that shows main screen of the app. Appbar with fragment pager in the top, navigation drawer on the side and
 * dock on the bottom.
 */
public class MainFragment extends BaseFragment implements MainView, NavigationView.OnNavigationItemSelectedListener,
        PopupMenu.OnMenuItemClickListener {

    /**
     * An interface to inform the host activity about events happening in MainFragment.
     */
    public interface MainFragmentEventListener {

        /**
         * Navigation drawer item has been clicked.
         *
         * @param menuItem which item has been clicked.
         */
        void onMenuItemClicked(MenuItem menuItem);

        /**
         * If navigation drawer is closed, back key should close the app instead of closing the nav drawer.
         */
        void closeApplication();
    }

    @BindView(R.id.tab_layout) TabLayout tabLayout;
    @BindView(R.id.viewpager) ViewPager viewPager;
    @BindView(R.id.drawer_layout) DrawerLayout navDrawerLayout;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.position) TextView positionLabel;
    @BindView(R.id.duration) TextView durationLabel;
    @BindView(R.id.progressBar) SeekBar progressBar;
    @BindView(R.id.snapshotSmall) ImageView snapshotSmall;
    @BindView(R.id.mainButtonPlay) ImageButton mainButton;
    @BindView(R.id.title) TextView title;
    @BindView(R.id.toolbar_layout) RelativeLayout toolbarLayout;
    @BindView(R.id.edit_mode_menu) ImageView editModeMenu;
    @BindView(R.id.volumeSeekBar) SeekBar volumeSeekBar;
    @BindView(R.id.volume) TextView volumeBarTextView;
    @BindView(R.id.volumeIcon) ImageView volumeIcon;
    @BindView(R.id.dock_rewind) ImageButton dockRewind;
    @BindView(R.id.dock_forward) ImageButton dockForward;
    @BindView(R.id.dock_volup) ImageButton dockVolUp;
    @BindView(R.id.dock_voldown) ImageButton dockVolDown;
    @BindView(R.id.volumeBar) LinearLayout volumeBarLayout;
    @BindView(R.id.progressBarLayout) LinearLayout progressBarLayout;
    @BindView(R.id.appbar_upper_layout_standard) RelativeLayout appbarStandardUpperLayout;
    @BindView(R.id.appbar_upper_layout_edit_mode) RelativeLayout appBarEditModeUpperLayout;

    // binded separately, after navDrawerLayout has been inflated.
    TextView hostNameTextView;
    TextView serverIPTextView;
    TextView navBarNowPlaying;
    TextView navBarProgress;
    TextView navBarDuration;
    TextView navBarStatus;
    ImageView snapshotBig;
    TextView snapshotUnavailable;
    RelativeLayout layoutSnapshotBig;

    @Inject MainPresenter mainPresenter;

    private ButtonTabsFragmentAdapter buttonTabsFragmentAdapter;
    private SwitchCompat editModeSwitch;
    private MainFragmentEventListener mainFragmentEventListener;
    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = getActivity();
        if (activity instanceof  MainFragmentEventListener) {
            mainFragmentEventListener = (MainFragmentEventListener) activity;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.getComponent(ScopedComponent.class).inject(this);
        final View fragmentView = inflater.inflate(R.layout.activity_main, container, false);
        this.unbinder = ButterKnife.bind(this, fragmentView);
        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialize();
        this.mainPresenter.setView(this);
        loadButtonPages();
    }



    @Override
    public void onResume() {
        super.onResume();
        mainPresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mainPresenter.pause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mainPresenter.destroy();
        mainFragmentEventListener = null;
    }



    @Override
    public void showEditMenuOnlyTab() {
        showDropDownMenu(ButtonTab.ONLY_TAB);
    }

    @Override
    public void showEditMenuFirstTab() {
        showDropDownMenu(ButtonTab.FIRST_TAB);
    }

    @Override
    public void showEditMenuLastTab() {
        showDropDownMenu(ButtonTab.LAST_TAB);
    }

    @Override
    public void showEditMenuMiddleTab() {
        showDropDownMenu(ButtonTab.MIDDLE_TAB);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        /**  Popup menu in the top-right corner. Visible when edit mode is enabled. */
        String title = item.getTitle().toString();

        if (title.equals(getString(R.string.edit_menu_new_tab))) {
            mainPresenter.onAddTabClicked();
        }
        else if (title.equals(getString(R.string.edit_menu_rename_tab))) {
            mainPresenter.onTabRenameClicked();
        }
        else if (title.equals(getString(R.string.edit_menu_move_right))) {
            mainPresenter.onMoveRightClicked();
        }
        else if (title.equals(getString(R.string.edit_menu_move_left))) {
            mainPresenter.onMoveLeftClicked();
        }
        else if (title.equals(getString(R.string.edit_menu_delete_tab))) {
            mainPresenter.onDeleteTabClicked();
        }
        return false;
    }

    @Override
    public void updateMainButtonIcon(PlaybackStatusModel playbackStatusModel) {
        if (playbackStatusModel == null) {
            mainButton.setImageResource(R.drawable.ic_play_arrow_white_36dp);
        } else {

            if (playbackStatusModel.getPlaybackState() == PlaybackState.PLAYING) {
                mainButton.setImageResource(R.drawable.ic_pause_white_36dp);
            } else if (playbackStatusModel.getPlaybackState() == PlaybackState.PAUSED) {
                mainButton.setImageResource(R.drawable.ic_play_arrow_white_36dp);
            }
        }
    }

    @Override
    public void showNewTabDialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        final EditText input = new EditText(alert.getContext());
        FrameLayout container = new FrameLayout(getActivity());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        params.leftMargin = (40);
        params.rightMargin = 40;
        params.topMargin = 40;
        input.setLayoutParams(params);
        input.setMinimumWidth(400);
        input.setText(getString(R.string.name));
        container.addView(input);
        alert.setTitle(getString(R.string.create_new_tab));

        alert.setView(container);

        alert.setPositiveButton(getString(R.string.create), (dialog, whichButton) -> mainPresenter.onNewTabNameChosen(input.getText().toString()));
        alert.setNegativeButton(getString(R.string.cancel), (dialog, whichButton) -> {
        });

        alert.show();
        input.requestFocus();
    }

    @Override
    public void showRenameTabDialog(String oldName) {
        FrameLayout container = new FrameLayout(getActivity());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = Toolbox.dpToPx(20);
        params.leftMargin = margin;
        params.rightMargin = margin;
        params.topMargin = margin;
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        final EditText input = new EditText(alert.getContext());
        input.setSingleLine();
        input.setLayoutParams(params);
        input.setMinimumWidth(Toolbox.dpToPx(200));
        input.setText(oldName);
        container.addView(input);
        alert.setTitle(getString(R.string.rename_tab));
        alert.setView(container);
        alert.setPositiveButton(getString(R.string.rename), (dialog, whichButton) -> mainPresenter.onTabRenamed(input.getText().toString()));
        alert.setNegativeButton(getString(R.string.cancel), (dialog, whichButton) -> {
        });

        alert.show();
    }

    @Override
    public void showConfirmTabDeleteDialog(String tabName) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle(getString(R.string.dialog_confirm_delete_title));
        String message = getString(R.string.dialog_confirm_delete_message) + tabName + "?";
        alert.setMessage(message);
        alert.setPositiveButton(getString(R.string.dialog_confirm_delete_yes), (dialog, whichButton) -> mainPresenter.onDeleteTabConfirmed());
        alert.setNegativeButton(getString(R.string.cancel), (dialog, whichButton) -> {
        });
        alert.show();
    }

    @Override
    public void renderButtonPages(List<String> pageTitles) {
        buttonTabsFragmentAdapter.setPagesList(pageTitles);
        buttonTabsFragmentAdapter.notifyDataSetChanged();
    }

    @Override
    public void moveToTab(int tab) {
        viewPager.setCurrentItem(tab);
    }

    @Override
    public void updateServerInformation(ServerModel serverModel) {
        if (serverModel == null) {
            this.hostNameTextView.setText("---");
            this.serverIPTextView.setText("---");
        } else {
            this.hostNameTextView.setText(serverModel.getHostName());
            this.serverIPTextView.setText(serverModel.getIp());
        }
    }

    @Override
    public void updateTitle(PlaybackStatusModel playbackStatusModel) {
        if (playbackStatusModel == null) {
            title.setText(getString(R.string.app_name));
        } else {
            title.setText(playbackStatusModel.getFileName());
        }
    }

    @Override
    public void setKeepScreenOn(boolean keepScreenOn) {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        View v = getView();
        if (v != null) {
            v.setKeepScreenOn(keepScreenOn);
        }
    }

    @Override
    public void setVolumeBar(int percent) {
        volumeSeekBar.setProgress(percent);
    }

    @Override
    public void setVolumeLabel(int percent) {
        String percentText = "" + percent + "%";
        volumeBarTextView.setText(percentText);
    }

    @Override
    public void setPositionLabel(String position) {
        if (position == null) {
            positionLabel.setText(getString(R.string.remote_zero_time));
        } else {
            positionLabel.setText(position);
        }
    }

    @Override
    public void setDurationLabel(String duration) {
        if (duration == null) {
            durationLabel.setText(getString(R.string.remote_zero_time));
        } else {
            durationLabel.setText(duration);
        }
    }

    @Override
    public void setPositionBar(int position) {
        this.progressBar.setProgress(position);
    }

    @Override
    public void setPositionBarMax(int maxPosition) {
        this.progressBar.setMax(maxPosition);
    }

    @Override
    public void setVolumeIconHigh() {
        volumeIcon.setImageResource(R.drawable.ic_volume_up_white_24dp);
    }

    @Override
    public void setVolumeIconLow() {
        volumeIcon.setImageResource(R.drawable.ic_volume_down_white_24dp);
    }

    @Override
    public void setVolumeIconMuted() {
        volumeIcon.setImageResource(R.drawable.ic_volume_mute_white_24dp);
    }

    @Override
    public void showEditModeAppBar() {
        appbarStandardUpperLayout.setVisibility(View.GONE);
        appBarEditModeUpperLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showStandardAppBar() {
        appBarEditModeUpperLayout.setVisibility(View.GONE);
        appbarStandardUpperLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNavDrawer() {
        if (!navDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            navDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public void hideNavDrawer() {
        if (navDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            navDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void showNavDrawerSnapshot() {
        layoutSnapshotBig.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNavDrawerSnapshot() {
        layoutSnapshotBig.setVisibility(View.GONE);
    }

    @Override
    public void showPositionBar() {
        progressBarLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hidePositionBar() {
        progressBarLayout.setVisibility(View.GONE);
    }

    @Override
    public void showVolumeBar() {
        volumeBarLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideVolumeBar() {
        volumeBarLayout.setVisibility(View.GONE);
    }

    @Override
    public void updatePreview(byte[] snapshot) {
        if (snapshot == null) {
            snapshotUnavailable.setVisibility(View.VISIBLE);
            snapshotBig.setVisibility(View.GONE);
            snapshotSmall.setVisibility(View.GONE);
        } else {
            Bitmap bitmap = BitmapFactory.decodeByteArray(snapshot, 0, snapshot.length);

            snapshotUnavailable.setVisibility(View.GONE);
            snapshotBig.setVisibility(View.VISIBLE);
            snapshotBig.setImageBitmap(bitmap);
            snapshotBig.setAdjustViewBounds(true);
            snapshotBig.setMinimumHeight(48);
            snapshotBig.setScaleType(ImageView.ScaleType.CENTER_CROP);

            snapshotSmall.setVisibility(View.VISIBLE);
            snapshotSmall.setImageBitmap(bitmap);
            snapshotSmall.setAdjustViewBounds(true);
            snapshotSmall.setMaxHeight(112);
            snapshotSmall.setMinimumHeight(112);
            snapshotSmall.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }

    @Override
    public void updateNavDrawerViews(PlaybackStatusModel playbackStatusModel) {
        if (playbackStatusModel == null) {
            navBarNowPlaying.setText("---");
            navBarProgress.setText(getString(R.string.remote_zero_time));
            navBarDuration.setText(getString(R.string.remote_zero_time));
            navBarStatus.setText(R.string.status_no_file_selected);
        } else {
            navBarNowPlaying.setText(playbackStatusModel.getFileName());
            navBarProgress.setText(playbackStatusModel.getPositionString());
            navBarDuration.setText(playbackStatusModel.getDurationString());

            if (playbackStatusModel.getPlaybackState() == PlaybackState.NO_FILE_SELECTED)
                navBarStatus.setText(R.string.status_stopped);
            else if (playbackStatusModel.getPlaybackState() == PlaybackState.PAUSED)
                navBarStatus.setText(R.string.status_paused);
            else if (playbackStatusModel.getPlaybackState() == PlaybackState.PLAYING)
                navBarStatus.setText(R.string.status_playing);
            else if (playbackStatusModel.getPlaybackState() == PlaybackState.STOPPED)
                navBarStatus.setText(R.string.status_stopped);
        }
    }

    @Override
    public void showMessage(String message) {
        showToastMessage(message);
    }

    @Override
    public void setEditModeSwitch(boolean checked) {
        editModeSwitch.setChecked(checked);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        CharSequence title = item.getTitle();
        String editMode = getString(R.string.edit_mode_switch);

        if (title.equals(editMode)) {
            editModeSwitchOnClick();
        } else
            mainFragmentEventListener.onMenuItemClicked(item);

        return false;
    }

    public void onSettingsReset() {
        mainPresenter.initialize();
    }

    public void onVolumeUpPressed() {
        mainPresenter.onVolumeRaised();
    }

    public void onVolumeDownPressed() {
        mainPresenter.onVolumeLowered();
    }

    public void onBackKeyPressed() {
        if (navDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            hideNavDrawer();
        } else {
            mainFragmentEventListener.closeApplication();
        }
    }

    private void bindNavDrawerViews() {
        View headerView = navigationView.inflateHeaderView(R.layout.activity_main_nav_drawer_header);

        editModeSwitch = (SwitchCompat) navigationView.getMenu().getItem(1).getActionView();
        editModeSwitch.setOnClickListener(v -> editModeSwitchOnClick());
        editModeSwitch.setClickable(false);

        hostNameTextView = (TextView) headerView.findViewById(R.id.navbar_hostname);
        serverIPTextView = (TextView) headerView.findViewById(R.id.navbar_serverIP);

        navBarNowPlaying = (TextView) headerView.findViewById(R.id.navbar_now_playing);
        navBarProgress = (TextView) headerView.findViewById(R.id.navbar_progress);
        navBarDuration = (TextView) headerView.findViewById(R.id.navbar_duration);
        navBarStatus = (TextView) headerView.findViewById(R.id.navbar_status);
        snapshotBig = (ImageView) headerView.findViewById(R.id.snapshotBig);
        snapshotUnavailable = (TextView) headerView.findViewById(R.id.snapshot_not_available);

        layoutSnapshotBig = (RelativeLayout) headerView.findViewById(R.id.layoutSnapshotBig);
    }

    private void showDropDownMenu(ButtonTab tab) {
        PopupMenu popup = new PopupMenu(getActivity(), editModeMenu);
        Menu menu = popup.getMenu();
        menu.add(getString(R.string.edit_menu_new_tab));
        menu.add(getString(R.string.edit_menu_rename_tab));
        if (tab == ButtonTab.FIRST_TAB || tab == ButtonTab.MIDDLE_TAB) {
            menu.add(getString(R.string.edit_menu_move_right));
        }
        if (tab == ButtonTab.LAST_TAB || tab == ButtonTab.MIDDLE_TAB) {
            menu.add(getString(R.string.edit_menu_move_left));
        }
        if (tab != ButtonTab.ONLY_TAB) {
            menu.add(getString(R.string.edit_menu_delete_tab));
        }

        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    private void initialize() {
        setupRecyclerView();
        setupNavigationView();
        setupTitleBar();
        setupDock();
        bindNavDrawerViews();
    }

    private void setupDock() {
        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mainPresenter.onPositionBarMoved(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mainPresenter.onPositionBarTouched();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mainPresenter.onPositionBarChosen(seekBar.getProgress());
            }
        });

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mainPresenter.onVolumeBarMoved(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mainPresenter.onVolumeBarTouched();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mainPresenter.onVolumeBarChosen(seekBar.getProgress());
            }
        });
    }

    @OnClick(R.id.dock_volup)
    void volumeUpClick() {
        mainPresenter.onVolumeRaised();
    }

    @OnClick(R.id.dock_voldown)
    void volumeDownClick() {
        mainPresenter.onVolumeLowered();
    }

    @OnClick(R.id.dock_forward)
    void fastForwardClick() {
        mainPresenter.onFastForward();
    }

    @OnClick(R.id.dock_rewind)
    void rewindClick() {
        mainPresenter.onRewind();
    }

    @OnClick(R.id.mainButtonPlay)
    void mainButtonOnClick() {
        mainPresenter.onPlayPauseClicked();
    }

    @OnClick(R.id.edit_mode_exit_button)
    void editModeExitOnClick() {
        mainPresenter.onStandardModeRequested();
    }

    @OnClick(R.id.edit_mode_menu)
    void editModeMenuOnClick() {
        mainPresenter.onEditModeMenuClicked();
    }

    private void setupTitleBar() {
        toolbarLayout.setOnClickListener(v ->
                mainPresenter.onAppBarClicked());
    }

    private void loadButtonPages() {
        this.mainPresenter.initialize();
    }

    private void setupNavigationView() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), navDrawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        navDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupRecyclerView() {
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        buttonTabsFragmentAdapter = new ButtonTabsFragmentAdapter(getFragmentManager());

        viewPager.setAdapter(buttonTabsFragmentAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mainPresenter.onTabSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setupWithViewPager(viewPager);
    }

    private void editModeSwitchOnClick() {
        if (!editModeSwitch.isChecked()) {
            mainPresenter.onEditModeRequested();
        } else {
            mainPresenter.onStandardModeRequested();
        }
    }

    private enum ButtonTab {
        ONLY_TAB,
        FIRST_TAB,
        MIDDLE_TAB,
        LAST_TAB
    }
}
