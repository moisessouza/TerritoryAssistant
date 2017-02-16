package ged.mediaplayerremote.presentation.view.fragment;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ged.mediaplayerremote.AndroidApplication;
import ged.mediaplayerremote.R;
import ged.mediaplayerremote.presentation.dagger2.components.DaggerScopedComponent;
import ged.mediaplayerremote.presentation.dagger2.modules.InteractorModule;
import ged.mediaplayerremote.presentation.model.ButtonModel;
import ged.mediaplayerremote.presentation.presenter.ButtonGridPresenter;
import ged.mediaplayerremote.presentation.view.ButtonGridView;
import ged.mediaplayerremote.presentation.view.adapter.ButtonAdapter;
import ged.mediaplayerremote.presentation.view.adapter.ItemOffsetDecoration;
import ged.mediaplayerremote.presentation.view.adapter.dragndrop.OnStartDragListener;
import ged.mediaplayerremote.presentation.view.adapter.dragndrop.SimpleItemTouchHelperCallback;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that shows a view with a grid of 9 reprogrammable buttons.
 */
public class ButtonGridFragment extends BaseFragment implements ButtonGridView, OnStartDragListener,
        ButtonAdapter.OnButtonClickedListener {

    public static ButtonGridFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        ButtonGridFragment fragment = new ButtonGridFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static final String ARG_PAGE = "ARG_PAGE";

    @Inject ButtonGridPresenter buttonGridPresenter;

    @BindView(R.id.recycler) RecyclerView recyclerView;

    private int mPage;
    private ButtonAdapter buttonAdapter;
    private ItemTouchHelper itemTouchHelper;
    private Unbinder butterKnife;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        this.initializeInjector();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.activity_main_button_grid_page, container, false);
        butterKnife = ButterKnife.bind(this, fragmentView);

        buttonAdapter = new ButtonAdapter(container.getContext(), this);

        fragmentView.post(() -> {
            if (recyclerView == null) {
                return;
            }
            buttonAdapter.setLayoutHeight(fragmentView.getMeasuredHeight());
            buttonAdapter.setOnButtonClickedListener(this);
            recyclerView.setAdapter(buttonAdapter);
        });

        ItemOffsetDecoration itemDecoration =
                new ItemOffsetDecoration(container.getContext(), R.dimen.button_grid_item_offset);
        recyclerView.addItemDecoration(itemDecoration);

        GridLayoutManager manager = new GridLayoutManager(container.getContext(), 3) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView.setLayoutManager(manager);

        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonGridPresenter.setView(this);
        buttonGridPresenter.initialize(mPage);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(buttonGridPresenter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        buttonGridPresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        buttonGridPresenter.pause();
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
        buttonGridPresenter.destroy();
    }

    @Override
    public void renderButtonTab(List<ButtonModel> buttonCodes) {
        buttonAdapter.setButtons(buttonCodes);
        buttonAdapter.notifyDataSetChanged();
    }

    @Override
    public void showButtonAssignDialog(int buttonIndex) {
        createChooserDialog(buttonIndex).show();
    }

    @Override
    public void showEmptyButtons() {
        buttonAdapter.setEmptyButtonsVisible();
    }

    @Override
    public void hideEmptyButtons() {
        buttonAdapter.setEmptyButtonsInvisible();
    }

    @Override
    public void swapButtons(int fromButton, int toButton) {
        buttonAdapter.swapButtons(fromButton, toButton);
        buttonAdapter.notifyItemMoved(fromButton, toButton);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onButtonLongClicked() {
        buttonGridPresenter.onButtonLongClicked();
    }

    @Override
    public void onButtonClicked(int position) {
        buttonGridPresenter.onButtonClicked(position);
    }

    private AlertDialog createChooserDialog(int buttonIndex) {
        Resources res = getResources();

        int[] commandCodes = res.getIntArray(R.array.commandCodeArray);

        ArrayList<String> commands = new ArrayList<>(commandCodes.length);

        String packageName = getActivity().getApplicationContext().getPackageName();
        for (int commandCode : commandCodes) {
            int id = res.getIdentifier("description_" + commandCode, "string", packageName);
            String description = res.getString(id);
            commands.add(description);
        }

        ArrayAdapter<String> commandDescriptionAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.activity_main_button_grid_command_list_adapter_, commands);

        ListView commandListView = new ListView(getActivity());
        commandListView.setAdapter(commandDescriptionAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(res.getString(R.string.dialog_choose_function));
        builder.setNegativeButton(res.getString(R.string.cancel), (dialog, which) -> {
        });
        builder.setAdapter(commandDescriptionAdapter,
                (dialog, which) -> buttonGridPresenter.onButtonReassigned(buttonIndex, commandCodes[which]));

        AlertDialog alertDialog = builder.create();

        ListView listView = alertDialog.getListView();
        listView.setDivider(new ColorDrawable(Color.WHITE));
        listView.setDividerHeight(1);

        return alertDialog;
    }

    private void initializeInjector() {
        AndroidApplication application = (AndroidApplication) getActivity().getApplication();
        DaggerScopedComponent.builder().applicationComponent(application.getApplicationComponent())
                .interactorModule(new InteractorModule()).build().inject(this);
    }
}