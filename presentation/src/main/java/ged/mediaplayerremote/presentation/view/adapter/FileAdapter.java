package ged.mediaplayerremote.presentation.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import ged.mediaplayerremote.R;
import ged.mediaplayerremote.domain.model.FileType;
import ged.mediaplayerremote.presentation.model.RemoteFileModel;

import java.util.Collections;
import java.util.List;

/**
 * Adapter that manages a list of {@link RemoteFileModel}.
 */
public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileModelViewHolder> {

    /**
     * An interface to inform the host fragment that a file has been clicked.
     */
    public interface OnFileClickedListener {
        void onFileEntityClicked(RemoteFileModel remoteFileModel);
    }

    private LayoutInflater layoutInflater;
    private List<RemoteFileModel> files;
    private OnFileClickedListener onFileClickedListener;

    public FileAdapter(Context context) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        files = Collections.emptyList();
    }


    public void setOnFileClickedListener(OnFileClickedListener onFileClickedListener) {
        this.onFileClickedListener = onFileClickedListener;
    }

    public void setFiles(List<RemoteFileModel> files) {
        this.files = files;
    }


    @Override
    public FileModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = layoutInflater.inflate(R.layout.activity_file_explorer_file_entity, parent, false);
        return new FileModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FileModelViewHolder holder, int position) {
        RemoteFileModel file = files.get(position);

        holder.fileName.setText(file.getFileName());
        holder.fileName.setTextSize(16);

        FileType fileType = file.getType();
        switch (fileType) {
        case DIRECTORY:
            holder.fileIcon.setImageResource(R.drawable.ic_folder_white_36dp);
            break;
        case MOVIE:
            holder.fileIcon.setImageResource(R.drawable.ic_movie_white_36dp);
            break;
        case MUSIC:
            holder.fileIcon.setImageResource(R.drawable.ic_music_note_white_36dp);
            break;
        case IMAGE:
            holder.fileIcon.setImageResource(R.drawable.ic_image_white_36dp);
            break;
        case UNKNOWN:
            holder.fileIcon.setImageResource(R.drawable.ic_insert_drive_file_white_36dp);
            break;
        }

        holder.fileEntityLayout.setOnClickListener(v -> onFileClickedListener.onFileEntityClicked(file));
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    static class FileModelViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.file_entity_layout) RelativeLayout fileEntityLayout;
        @BindView(R.id.file_name) TextView fileName;
        @BindView(R.id.file_icon) ImageView fileIcon;

        FileModelViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

}
