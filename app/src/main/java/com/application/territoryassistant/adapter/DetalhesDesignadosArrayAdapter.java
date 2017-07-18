package com.application.territoryassistant.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.application.territoryassistant.R;
import com.application.territoryassistant.bd.DesignacaoDBHelper;
import com.application.territoryassistant.designar.FecharDesignacaoActivity;
import com.application.territoryassistant.designar.vo.DesignacaoVO;
import com.application.territoryassistant.helper.FotoHelper;
import com.application.territoryassistant.manager.FotoManager;
import com.application.territoryassistant.territorios.vo.TerritorioVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetalhesDesignadosArrayAdapter extends ArrayAdapter<DesignacaoVO> {

        Context context;
        Map<DesignacaoVO, Integer> idMap = new HashMap<>();

        final DesignacaoDBHelper dbDesignacao;

        public DetalhesDesignadosArrayAdapter(Context context, int textViewResourceId,
                                       List<DesignacaoVO> designacaoVOs) {
            super(context, textViewResourceId, designacaoVOs);
            this.context = context;
            dbDesignacao = new DesignacaoDBHelper(context);

            for (DesignacaoVO vo:designacaoVOs) {
                idMap.put(vo, vo.getId());
            }
        }

        @Override
        public long getItemId(int position) {
            DesignacaoVO item = getItem(position);
            return idMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        class ViewHolder {

            TextView mCodigo;
            TextView mDiaSemana;
            TextView mData;
            ImageView mFoto;
            TextView mNomeDirigente;
            ImageButton mFechar;
            View mMarcado;

            // Labels
            private TextView lNomeDirigente;

            final View rowView;

            String currentPath;

            public ViewHolder (ViewGroup parent){

                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                rowView = inflater.inflate(R.layout.list_detalhes_designados, parent, false);
                mCodigo = (TextView) rowView.findViewById(R.id.txt_codigo_territorio);
                mData = (TextView) rowView.findViewById(R.id.txt_data_inicio);
                mDiaSemana = (TextView) rowView.findViewById(R.id.txt_dia_semana);
                mFoto = (ImageView) rowView.findViewById(R.id.img_foto_territorio);
                mNomeDirigente = (TextView) rowView.findViewById(R.id.txt_nome_dirigente);

                lNomeDirigente = (TextView)rowView.findViewById(R.id.lab_nome_dirigente);

                mFechar = (ImageButton) rowView.findViewById(R.id.btn_fechar);

                mMarcado = rowView.findViewById(R.id.bar_marcado);

                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DesignacaoVO vo = (DesignacaoVO)v.getTag();
                        fecharRegistro(vo);
                    }
                };

                View.OnClickListener marcarListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final DesignacaoVO vo = (DesignacaoVO)v.getTag();

                        PopupMenu popup = new PopupMenu(context, v);
                        popup.inflate(R.menu.list_designados_menu);

                        // This activity implements OnMenuItemClickListener
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                switch (item.getItemId()) {
                                    case R.id.menu_marcar:
                                        marcarDesmarcarRegistro(vo);
                                        return true;
                                    case R.id.menu_desmarcar:
                                        marcarDesmarcarRegistro(vo);
                                        return true;
                                    case R.id.menu_fechar:
                                        fecharRegistro(vo);
                                        return true;
                                    default:
                                        return false;
                                }


                            }
                        });

                        Menu menu = popup.getMenu();

                        Integer marcado = vo.getMarcado();
                        if (marcado == null || marcado == 0) {
                            menu.findItem(R.id.menu_marcar).setVisible(true);
                            menu.findItem(R.id.menu_desmarcar).setVisible(false);
                        } else {
                            menu.findItem(R.id.menu_marcar).setVisible(false);
                            menu.findItem(R.id.menu_desmarcar).setVisible(true);
                        }

                        popup.show();
                    }
                };

                mCodigo.setOnClickListener(marcarListener);
                mData.setOnClickListener(marcarListener);
                mDiaSemana.setOnClickListener(marcarListener);
                mNomeDirigente.setOnClickListener(marcarListener);
                lNomeDirigente.setOnClickListener(marcarListener);
                mFechar.setOnClickListener(listener);

                mFoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DesignacaoVO vo = (DesignacaoVO)v.getTag();
                        if (vo.getFotoPath() != null && !vo.getFotoPath().isEmpty()) {
                            FotoHelper.showPhoto(getContext(), vo.getFotoPath(), null);
                        }
                    }
                });

            }
            
            private void marcarDesmarcarRegistro(DesignacaoVO vo) {
                Integer marcado = vo.getMarcado();
                if (marcado == null || marcado == 0){
                    dbDesignacao.marcarRegistro(vo);
                    mMarcado.setBackgroundColor(context.getResources().getColor(R.color.primary));
                    vo.setMarcado(1);
                } else {
                    dbDesignacao.desmarcarRegistro(vo);
                    mMarcado.setBackgroundColor(0);
                    vo.setMarcado(0);
                }

            }

            private void fecharRegistro(DesignacaoVO vo) {
                Intent intent = new Intent(context, FecharDesignacaoActivity.class);
                intent.putExtra("ID", vo.getId());
                ((Activity) context).startActivityForResult(intent, 3);
            }

            public void buscarFoto(){

                new AsyncTask<ViewHolder, Integer, Bitmap>(){

                    ViewHolder vh = null;

                    @Override
                    protected Bitmap doInBackground(ViewHolder... params) {
                        vh = params[0];

                        if (vh.currentPath != null) {
                            Bitmap thumb = FotoManager.instance().getThumbImage(vh.currentPath);
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

            public View getRowView(){
                return rowView;
            }

            public void setVO(DesignacaoVO vo) {
                mCodigo.setTag(vo);
                mData.setTag(vo);
                mDiaSemana.setTag(vo);
                mNomeDirigente.setTag(vo);
                lNomeDirigente.setTag(vo);
                mFoto.setTag(vo);
                mFechar.setTag(vo);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder vh = null;
            if (convertView == null) {
                vh = new ViewHolder(parent);
                convertView = vh.getRowView();
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder)convertView.getTag();
            }

            DesignacaoVO vo = getItem(position);

            vh.mCodigo.setText(vo.getCodTerritorio());
            vh.mNomeDirigente.setText(vo.getNomeDirigente());
            vh.mData.setText(vo.getDataDesignacao());
            vh.mDiaSemana.setText(vo.getDiaSemana());

            vh.setVO(vo);

            Integer marcado = vo.getMarcado();
            if (marcado != null && marcado  == 1){
                vh.mMarcado.setBackgroundColor(context.getResources().getColor(R.color.primary));
            } else {
                vh.mMarcado.setBackgroundColor(0);
            }

            vh.currentPath = vo.getFotoPath();
            vh.buscarFoto();

            return convertView;
        }

    }