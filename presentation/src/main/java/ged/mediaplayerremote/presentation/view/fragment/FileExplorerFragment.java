package ged.mediaplayerremote.presentation.view.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ged.mediaplayerremote.R;
import ged.mediaplayerremote.domain.model.FileType;
import ged.mediaplayerremote.presentation.dagger2.components.ScopedComponent;
import ged.mediaplayerremote.presentation.model.RemoteFileModel;
import ged.mediaplayerremote.presentation.presenter.FileExplorerPresenter;
import ged.mediaplayerremote.presentation.view.FileExplorerView;
import ged.mediaplayerremote.presentation.view.adapter.FileAdapter;
import ged.mediaplayerremote.presentation.view.adapter.ItemOffsetDecoration;

import javax.inject.Inject;
import java.util.List;

/**
 * Fragment that shows a file explorer.
 */
public class FileExplorerFragment extends BaseFragment implements FileAdapter.OnFileClickedListener, FileExplorerView {

    @Inject FileExplorerPresenter fileExplorerPresenter;

    @BindView(R.id.file_explorer_location_text) TextView location;
    @BindView(R.id.file_explorer_loading) ProgressBar loading;
    @BindView(R.id.file_explorer_recycler) RecyclerView recyclerView;

    private FileAdapter fileAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Unbinder butterKnife;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent(ScopedComponent.class).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View fragmentView = inflater.inflate(R.layout.activity_file_explorer, container, false);
        butterKnife = ButterKnife.bind(this, fragmentView);

        layoutManager = new LinearLayoutManager(container.getContext());
        recyclerView.setLayoutManager(layoutManager);
        ItemOffsetDecoration buttonGridOffsetDecoration = new ItemOffsetDecoration(container.getContext(),
                R.dimen.button_grid_item_offset);
        recyclerView.addItemDecoration(buttonGridOffsetDecoration);

        fileAdapter = new FileAdapter(container.getContext());
        fileAdapter.setOnFileClickedListener(this);
        recyclerView.setAdapter(fileAdapter);

        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fileExplorerPresenter.setView(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        fileExplorerPresenter.initialize();
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
        fileExplorerPresenter.destroy();
    }

    @Override
    public void onFileEntityClicked(RemoteFileModel remoteFileModel) {
        fileExplorerPresenter.onFileEntityClicked(remoteFileModel);
    }

    @Override
    public void renderFiles(List<RemoteFileModel> files) {
        fileAdapter.setFiles(files);
        fileAdapter.notifyDataSetChanged();
        layoutManager.scrollToPosition(0);
    }

    @Override
    public void showLocation(String location) {
        this.location.setText(location);
    }

    @Override
    public void showMessage(String message) {
        showToastMessage(message);
    }

    @Override
    public void showConfirmFileDialog(RemoteFileModel remoteFileModel) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        alertDialog.setTitle(getResources().getString(R.string.file_explorer_dialog_file_chosen));

        String message;
        if (remoteFileModel.getType() == FileType.UNKNOWN) {
            message = remoteFileModel.getFileName() + "\n\n"
                    + getResources().getString(R.string.file_explorer_dialog_unknown_file_type);
        } else {
            message = remoteFileModel.getFileName();
        }
        alertDialog.setMessage(message);

        alertDialog.setPositiveButton(getResources().getString(R.string.file_explorer_dialog_load),
                (dialog, which) -> fileExplorerPresenter.onFileConfirmed(remoteFileModel));
        alertDialog.setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> {});
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    @Override
    public void showViewLoading() {
        if (loading != null)
            loading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideViewLoading() {
        if (loading != null)
            loading.setVisibility(View.GONE);
    }

    @Override
    public void showFileLoadedMessage(RemoteFileModel remoteFileModel) {
        showToastMessage(getResources().getString(R.string.file_explorer_loaded) + remoteFileModel.getFileName());
    }

    @OnClick(R.id.file_explorer_fab)
    public void refreshFloatingButtonOnClick() {
        this.fileExplorerPresenter.onRefreshClicked();
    }

}
