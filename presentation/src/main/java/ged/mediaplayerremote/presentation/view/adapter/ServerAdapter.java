package ged.mediaplayerremote.presentation.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import ged.mediaplayerremote.R;
import ged.mediaplayerremote.presentation.model.ServerModel;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter that manages a list of {@link ServerModel}.
 */
public class ServerAdapter extends RecyclerView.Adapter<ServerAdapter.ServerViewHolder> {

    /**
     * An interface to inform host fragment that a {@link ServerModel} has been selected.
     */
    public interface OnItemClickListener {
        void onServerClicked(ServerModel serverModel);
    }

    private List<ServerModel> mServerList;
    private LayoutInflater layoutInflater;
    private OnItemClickListener onItemClickListener;

    @Inject
    public ServerAdapter(Context context) {
        this.layoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mServerList = new ArrayList<>();
    }

    @Override
    public ServerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = layoutInflater.inflate(R.layout.activity_server_finder_server_model, parent, false);
        return new ServerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ServerViewHolder holder, int position) {
        final ServerModel serverModel = mServerList.get(position);

        holder.hostname.setText(serverModel.getHostName());
        holder.ip.setText(serverModel.getIp());
        holder.itemView.setOnClickListener(view -> onItemClickListener.onServerClicked(serverModel));

    }

    @Override
    public int getItemCount() {
        return mServerList.size();
    }

    public void addServer(ServerModel serverModel) {
        mServerList.add(serverModel);
        this.notifyDataSetChanged();
    }

    public void removeServers() {
        mServerList.clear();
        this.notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    static class ServerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rv_server_ip) TextView ip;
        @BindView(R.id.rv_hostname) TextView hostname;
        @BindView(R.id.rv_icon) ImageView icon;


        ServerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
