package ged.mediaplayerremote.presentation.view.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ged.mediaplayerremote.R;
import ged.mediaplayerremote.presentation.dagger2.components.ScopedComponent;
import ged.mediaplayerremote.presentation.model.ServerModel;
import ged.mediaplayerremote.presentation.presenter.ServerFinderPresenter;
import ged.mediaplayerremote.presentation.view.ServerFinderView;
import ged.mediaplayerremote.presentation.view.adapter.ServerAdapter;

import javax.inject.Inject;

/**
 * Fragment that shows a server finder.
 */
public class ServerFinderFragment extends BaseFragment implements ServerFinderView {

    /**
     * Interface for a parent activity to listen to server selection.
     */
    public interface ServerListener {
        void onServerChosen(String ip);
    }

    @Inject ServerFinderPresenter serverFinderPresenter;
    @Inject ServerAdapter serverAdapter;

    @BindView(R.id.server_finder_loading) ProgressBar loading;
    @BindView(R.id.server_finder_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.server_finder_fab) FloatingActionButton fab;

    private Unbinder butterKnife;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getComponent(ScopedComponent.class).inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.serverFinderPresenter.resume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.activity_server_finder, container, false);
        butterKnife = ButterKnife.bind(this, fragmentView);
        setupRecyclerView();
        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.serverFinderPresenter.setView(this);
        this.serverFinderPresenter.initialize();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView.setAdapter(null);
        butterKnife.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serverFinderPresenter.destroy();
    }

    @Override
    public void showMessage(String message) {
        showToastMessage(message);
    }

    @Override
    public void showViewLoading(int maxProgress) {
        if (loading != null) {
            loading.setMax(maxProgress);
            loading.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideViewLoading() {
        if (loading != null){
            loading.setVisibility(View.GONE);
        }
    }

    @Override
    public void addServer(ServerModel serverModel) {
        serverAdapter.addServer(serverModel);
    }

    @Override
    public void clearServers() {
        serverAdapter.removeServers();
    }

    @Override
    public void selectServer(String ip) {
        Activity parentActivity = getActivity();
        if (parentActivity instanceof ServerListener) {
            ServerListener serverListener = (ServerListener) parentActivity;
            serverListener.onServerChosen(ip);
        }
    }

    @Override
    public void addProgress() {
        loading.setProgress(loading.getProgress() + 1);
    }

    @Override
    public void clearProgress() {
        loading.setProgress(0);
    }

    private void setupRecyclerView() {
        ServerAdapter.OnItemClickListener onItemClickListener =
                serverModel -> serverFinderPresenter.onServerChosen(serverModel.getIp());
        serverAdapter.setOnItemClickListener(onItemClickListener);
        recyclerView.setAdapter(serverAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    @OnClick(R.id.server_finder_fab)
    void fabOnClick() {
        this.serverFinderPresenter.onFloatingActionButtonClicked();
    }






}
