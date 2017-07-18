package com.application.territoryassistant.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.territoryassistant.R;
import com.application.territoryassistant.helper.FotoHelper;
import com.application.territoryassistant.manager.FotoManager;
import com.application.territoryassistant.territorios.vo.TerritorioVO;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by moi09 on 25/03/2016.
 */
public class ListaTerritoriosArrayAdapter extends ArrayAdapter<TerritorioVO> {

    TerritorioVO [] territorioVOs;
    Context context;

    public ListaTerritoriosArrayAdapter(Context context, int resource, TerritorioVO[] territorioVOs) {
        super(context, resource, territorioVOs);
        this.territorioVOs = territorioVOs;
        this.context = context;
    }

    class ViewHolder {

        TextView mCodigo;
        ImageView mFoto;
        TextView mDataFim;

        final View rowView;

        String fotoPath;

        public ViewHolder (ViewGroup parent){

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            rowView = inflater.inflate(R.layout.fragment_territorios, parent, false);

            mCodigo = (TextView) rowView.findViewById(R.id.codigos);
            mDataFim = (TextView) rowView.findViewById(R.id.ultima_data);
            mFoto = (ImageView)rowView.findViewById(R.id.img_foto_territorio);

            mFoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TerritorioVO vo = (TerritorioVO)v.getTag();
                    if (vo.getFotoPath() != null && !vo.getFotoPath().isEmpty()) {
                        fotoPath = vo.getFotoPath();
                        FotoHelper.showPhoto(context, fotoPath, null);
                    }
                }
            });
        }

        public View getRowView() {
            return rowView;
        }

        public void buscarFoto (){
            new AsyncTask<ViewHolder, Integer, Bitmap>(){

                ViewHolder vh = null;

                @Override
                protected Bitmap doInBackground(ViewHolder... params) {
                    vh = params[0];

                    if (vh.fotoPath != null) {
                        Bitmap thumb = FotoManager.instance().getThumbImage(vh.fotoPath);
                        return thumb;
                    }

                    return  null;

                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    if (bitmap != null) {
                        vh.mFoto.setImageBitmap(bitmap);
                    } else {
                        vh.mFoto.setImageResource(android.R.drawable.ic_menu_camera);
                    }
                }
            }.execute(this);

        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder(parent);
            convertView = viewHolder.getRowView();
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        TerritorioVO item = getItem(position);

        viewHolder.mCodigo.setText(item.getCod());

        if (item.getUltimaDataFim() != null && item.getUltimaDataFim() != 0){
            DateFormat df = DateFormat.getDateInstance();
            String data = df.format(new Date(item.getUltimaDataFim()));
            viewHolder.mDataFim.setText(data);
        } else {
            viewHolder.mDataFim.setText(null);
        }

        viewHolder.fotoPath = item.getFotoPath();
        viewHolder.mFoto.setTag(item);
        viewHolder.buscarFoto();

        return convertView;

    }
}
