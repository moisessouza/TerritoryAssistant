package ged.mediaplayerremote.presentation.presenter;

import ged.mediaplayerremote.domain.interactor.DefaultSubscriber;
import ged.mediaplayerremote.domain.interactor.UseCase;
import ged.mediaplayerremote.domain.interactor.UseCaseParametrized;
import ged.mediaplayerremote.domain.repository.ButtonRepository;
import ged.mediaplayerremote.presentation.dagger2.PerFragment;
import ged.mediaplayerremote.presentation.model.ButtonModel;
import ged.mediaplayerremote.presentation.view.ButtonGridView;
import ged.mediaplayerremote.presentation.view.adapter.dragndrop.ItemMovedListener;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link Presenter} that controls communication with {@link ButtonGridView}
 */
@PerFragment
public class ButtonGridPresenter implements Presenter, ItemMovedListener {

    private ButtonGridView buttonGridView;

    private ButtonRepository buttonRepository;

    private UseCaseParametrized<Map<String, String>> sendCommandUseCase;
    private UseCaseParametrized<Boolean> setEditModeUseCase;
    private UseCase editModeListenerUseCase;

    private boolean editMode = false;
    private int page;


    @Inject
    public ButtonGridPresenter(ButtonRepository buttonRepository,
                               @Named("sendCommand") UseCaseParametrized<Map<String, String>> sendCommandUseCase,
                               @Named("setEditMode") UseCaseParametrized<Boolean> setEditModeUseCase,
                               @Named("editModeListener") UseCase editModeListenerUseCase) {

        this.buttonRepository = buttonRepository;
        this.sendCommandUseCase = sendCommandUseCase;
        this.editModeListenerUseCase = editModeListenerUseCase;
        this.setEditModeUseCase = setEditModeUseCase;
    }

    @Override
    public void resume() {
        registerEditModeListener();
    }

    @Override
    public void pause() {
        unregisterEditModeListener();
    }

    @Override
    public void destroy() {
    }

    /**
     * Initializes the presenter by retrieving button grid configuration.
     */
    public void initialize(int page) {
        this.page = page;
        showButtonGrid();
    }

    public void setView(ButtonGridView buttonGridView) {
        this.buttonGridView = buttonGridView;
    }

    /**
     * Send assigned command to the server, if edit mode is disabled. If edit mode is enabled it will show a dialog with
     * list of commands that can be assigned instead.
     *
     * @param buttonIndex index of the button clicked.
     */
    public void onButtonClicked(int buttonIndex) {
        if (editMode)
            buttonGridView.showButtonAssignDialog(buttonIndex);
        else {
            int code = buttonRepository.getButtonCode(page, buttonIndex);
            sendCommand(code);
        }
    }

    public void onButtonLongClicked() {
        if (!editMode) {
            setEditModeUseCase.execute(new DefaultSubscriber(), true);
        }
    }

    public void onButtonReassigned(int buttonIndex, int code) {
        buttonRepository.setButtonCode(page, buttonIndex, code);
        showButtonGrid();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        List<Integer> buttonCodes = buttonRepository.getButtonCodes(page);

        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(buttonCodes, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(buttonCodes, i, i - 1);
            }
        }
        buttonRepository.setButtonCodes(page, buttonCodes);
        buttonGridView.swapButtons(fromPosition, toPosition);
    }

    private void sendCommand(int code) {
        Map<String, String> command = new HashMap<>(1);
        command.put("wm_command", "" + code);
        sendCommandUseCase.execute(new DefaultSubscriber(), command);
    }

    private void showButtonGrid() {
        List<Integer> buttonCodes = buttonRepository.getButtonCodes(page);
        List<ButtonModel> buttonList = new ArrayList<>();
        for (int i = 0; i < buttonCodes.size(); i++) {
            int buttonCode = buttonCodes.get(i);
            ButtonModel buttonModel = new ButtonModel(buttonCode, buttonRepository.getButtonDescription(buttonCode));
            buttonList.add(buttonModel);
        }
        this.buttonGridView.renderButtonTab(buttonList);
    }

    /**
     * Register to get edit mode status, in case it was enabled in other view of the application.
     */
    private void registerEditModeListener() {
        editModeListenerUseCase.execute(new EditModeSubscriber());
    }

    private void unregisterEditModeListener() {
        editModeListenerUseCase.unsubscribe();
    }

    private class EditModeSubscriber extends DefaultSubscriber<Boolean> {
        @Override
        public void onNext(Boolean editMode) {
            if (editMode) {
                buttonGridView.showEmptyButtons();
                ButtonGridPresenter.this.editMode = true;
            } else {
                buttonGridView.hideEmptyButtons();
                ButtonGridPresenter.this.editMode = false;
            }
        }
    }

}
