package ged.mediaplayerremote.presentation.view.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import ged.mediaplayerremote.R;
import ged.mediaplayerremote.presentation.model.ButtonModel;
import ged.mediaplayerremote.presentation.view.adapter.dragndrop.DragAndDropHelperViewHolder;
import ged.mediaplayerremote.presentation.view.adapter.dragndrop.OnStartDragListener;

import java.util.Collections;
import java.util.List;

/**
 * Adapter that manages a list of {@link ButtonModel}.
 */
public class ButtonAdapter extends RecyclerView.Adapter<ButtonAdapter.ButtonViewHolder> {

    private final LayoutInflater layoutInflater;
    private final OnStartDragListener mDragStartListener;
    private boolean editMode = false;
    private int emptyButtonVisibility = View.GONE;
    private int itemHeight = 100;
    private int itemOffset;
    private List<ButtonModel> buttons;
    private OnButtonClickedListener onButtonClickedListener;

    public ButtonAdapter(Context context, OnStartDragListener dragStartListener) {
        this.mDragStartListener = dragStartListener;

        this.layoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.buttons = Collections.emptyList();
        this.itemOffset = context.getResources().getDimensionPixelSize(R.dimen.button_grid_item_offset);
    }

    /**
     * Hide buttons that have no command code assigned.
     */
    public void setEmptyButtonsInvisible() {
        emptyButtonVisibility = View.GONE;
        editMode = false;
        notifyDataSetChanged();
    }

    /**
     * Show buttons that have no command code assigned so they can be clicked.
     */
    public void setEmptyButtonsVisible() {
        emptyButtonVisibility = View.VISIBLE;
        editMode = true;
        notifyDataSetChanged();
    }

    /**
     * Swap position of two buttons.
     */
    public void swapButtons(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(buttons, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(buttons, i, i - 1);
            }
        }
    }

    public void setButtons(List<ButtonModel> buttonsCodes) {
        this.buttons = buttonsCodes;
    }

    @Override
    public ButtonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = layoutInflater.inflate(R.layout.activity_main_button_grid_button, parent, false);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight);
        view.setLayoutParams(params);

        return new ButtonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ButtonViewHolder holder, final int position) {
        int buttonCode = buttons.get(position).getCommandCode();

        holder.buttonDescription.setText(buttons.get(position).getDescription());

        if (buttonCode == 0) {
            holder.myself.setVisibility(emptyButtonVisibility);
        } else {
            holder.myself.setVisibility(View.VISIBLE);
        }

        holder.myself.setOnClickListener(v -> onButtonClickedListener.onButtonClicked(position));
        holder.myself.setOnLongClickListener(v ->
        {
            if (editMode)
                mDragStartListener.onStartDrag(holder);
            else
                onButtonClickedListener.onButtonLongClicked();
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return buttons.size();
    }

    public void setOnButtonClickedListener(OnButtonClickedListener onButtonClickedListener) {
        this.onButtonClickedListener = onButtonClickedListener;
    }

    public void setLayoutHeight(int layoutHeight) {
        itemHeight = getItemHeight(layoutHeight);
        notifyDataSetChanged();
    }

    private int getItemHeight(int layoutHeight) {
        return (layoutHeight - (8 * itemOffset)) / 3;
    }

    public interface OnButtonClickedListener {
        void onButtonClicked(int position);

        void onButtonLongClicked();
    }

    static class ButtonViewHolder extends RecyclerView.ViewHolder implements DragAndDropHelperViewHolder {

        @BindView(R.id.button_desc) TextView buttonDescription;
        @BindView(R.id.button_layout) CardView myself;

        ButtonViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onItemSelected() {
            myself.setScaleX(1.2F);
            myself.setScaleY(1.2F);
        }

        @Override
        public void onItemClear() {
            myself.setScaleX(1.0F);
            myself.setScaleY(1.0F);
        }
    }
}
