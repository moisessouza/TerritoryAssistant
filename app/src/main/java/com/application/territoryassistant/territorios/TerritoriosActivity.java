package com.application.territoryassistant.territorios;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.application.territoryassistant.R;
import com.application.territoryassistant.adapter.SpinnerGrupoArrayAdapter;
import com.application.territoryassistant.bd.DesignacaoDBHelper;
import com.application.territoryassistant.bd.DirigenteDBHelper;
import com.application.territoryassistant.bd.GrupoDBHelper;
import com.application.territoryassistant.bd.TerritorioDBHelper;
import com.application.territoryassistant.designar.DesignarActivity;
import com.application.territoryassistant.grupos.vo.GrupoVO;
import com.application.territoryassistant.helper.FotoHelper;
import com.application.territoryassistant.helper.ToastHelper;
import com.application.territoryassistant.historico.HistoricoActivity;
import com.application.territoryassistant.manager.FotoManager;
import com.application.territoryassistant.territorios.vo.TerritorioVO;
import com.application.territoryassistant.territorios.vo.TerritorioVizinhoVO;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TerritoriosActivity extends AppCompatActivity {

    TerritorioDBHelper db = new TerritorioDBHelper(this);
    GrupoDBHelper dbGrupo = new GrupoDBHelper(this);

    @Override
    protected void onResume() {
        super.onResume();
        carregarTerritorios();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_territorios);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_territorios);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TerritoriosActivity.this, NovoTerritorioActivity.class);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void carregarTerritorios(){

        List<TerritorioVO> territorioVOs = db.buscarTerritorios();
        List<GrupoVO> grupoVOs = new ArrayList<>();

        grupoVOs.add(new GrupoVO(-1, getString(R.string.todos_os_grupos)));
        grupoVOs.addAll(dbGrupo.buscarGrupos());

        final ListView listView = (ListView)findViewById(R.id.list_territorios);

        TerritoriosArrayAdapter adapter = new TerritoriosArrayAdapter(this,
                android.R.layout.simple_list_item_1, territorioVOs);
        listView.setAdapter(adapter);

        final Spinner spinnerGrupos = (Spinner)findViewById(R.id.spinner_grupos);

        SpinnerGrupoArrayAdapter grupoAdapter = new SpinnerGrupoArrayAdapter(this,
                android.R.layout.simple_spinner_item, grupoVOs);

        spinnerGrupos.setAdapter(grupoAdapter);

        final CheckBox checkOrdenar = (CheckBox)findViewById(R.id.check_ordernar);
        final CheckBox checkNaoDesignados = (CheckBox) findViewById(R.id.check_nao_designados);

        checkNaoDesignados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TerritoriosActivity.this.carregarTerritorios(spinnerGrupos, checkNaoDesignados, checkOrdenar, listView);
            }
        });

        checkOrdenar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TerritoriosActivity.this.carregarTerritorios(spinnerGrupos, checkNaoDesignados, checkOrdenar, listView);
            }
        });

        spinnerGrupos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TerritoriosActivity.this.carregarTerritorios(spinnerGrupos, checkNaoDesignados, checkOrdenar, listView);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }

    private void carregarTerritorios(Spinner spinnerGrupos, CheckBox checkNaoDesignados, CheckBox checkOrdenar, ListView listView) {

        GrupoVO grupoVO = (GrupoVO) spinnerGrupos.getSelectedItem();

        Integer idGrupo = grupoVO.getId();
        if (idGrupo == -1) {
            idGrupo = null;
        }

        if (!checkNaoDesignados.isChecked()) {
            List<TerritorioVO> territorioVOs = db.buscarTerritorios(null, checkOrdenar.isChecked(), idGrupo);
            TerritoriosArrayAdapter adapter = new TerritoriosArrayAdapter(TerritoriosActivity.this,
                    android.R.layout.simple_list_item_1, territorioVOs);
            listView.setAdapter(adapter);
        } else {
            List<TerritorioVO> territorioVOs = db.buscarTerritoriosNaoDesignados(checkOrdenar.isChecked(), idGrupo);
            TerritoriosArrayAdapter adapter = new TerritoriosArrayAdapter(TerritoriosActivity.this,
                    android.R.layout.simple_list_item_1, territorioVOs);
            listView.setAdapter(adapter);
        }
    }

    private class TerritoriosArrayAdapter extends ArrayAdapter<TerritorioVO> {

        Context context;
        Map<TerritorioVO, Integer> idMap = new HashMap<TerritorioVO, Integer>();
        Boolean isPopup;

        public TerritoriosArrayAdapter(Context context, int textViewResourceId,
                                      List<TerritorioVO> territorioVOs) {
            super(context, textViewResourceId, territorioVOs);
            this.context = context;

            for (TerritorioVO vo:territorioVOs) {
                idMap.put(vo, vo.getId());
            }

            this.isPopup = Boolean.FALSE;

        }

        public TerritoriosArrayAdapter(Context context, int textViewResourceId,
                                       List<TerritorioVO> territorioVOs, Boolean isPopup) {
            super(context, textViewResourceId, territorioVOs);
            this.context = context;

            for (TerritorioVO vo:territorioVOs) {
                idMap.put(vo, vo.getId());
            }

            this.isPopup = isPopup;
        }

        @Override
        public long getItemId(int position) {
            TerritorioVO item = getItem(position);
            return idMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        public class ViewHolder {

            TextView labUltimaData;

            TextView mCodigo;
            ImageView mFoto;
            TextView mDataFim;

            final View rowView;

            String currentPath;

            public ViewHolder (ViewGroup parent){

                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                rowView = inflater.inflate(R.layout.fragment_territorios, parent, false);

                labUltimaData = (TextView)rowView.findViewById(R.id.lab_ultima_data);

                mCodigo = (TextView) rowView.findViewById(R.id.codigos);
                mDataFim = (TextView) rowView.findViewById(R.id.ultima_data);
                mFoto = (ImageView)rowView.findViewById(R.id.img_foto_territorio);

                mFoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TerritorioVO vo = (TerritorioVO)v.getTag();
                        if (vo.getFotoPath() != null && !vo.getFotoPath().isEmpty()) {
                            FotoHelper.showPhoto(TerritoriosActivity.this, vo.getFotoPath(), null);
                        }
                    }
                });

                mCodigo.setOnClickListener(new LinhaClickListener(TerritoriosArrayAdapter.this, isPopup));
                mDataFim.setOnClickListener(new LinhaClickListener(TerritoriosArrayAdapter.this, isPopup));
                labUltimaData.setOnClickListener(new LinhaClickListener(TerritoriosArrayAdapter.this, isPopup));

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

            TerritorioVO vo = getItem(position);

            if (vo.getUltimaDataFim() != null && vo.getUltimaDataFim() != 0){
                DateFormat df = DateFormat.getDateInstance();
                String data = df.format(new Date(vo.getUltimaDataFim()));
                viewHolder.mDataFim.setText(data);
            } else {
                viewHolder.mDataFim.setText(null);
            }

            viewHolder.mCodigo.setText(vo.getCod());
            viewHolder.mCodigo.setTag(vo);
            viewHolder.mDataFim.setTag(vo);
            viewHolder.labUltimaData.setTag(vo);

            viewHolder.currentPath = vo.getFotoPath();
            viewHolder.mFoto.setTag(vo);
            viewHolder.buscarFoto();

            return convertView;

        }

    }

    class LinhaClickListener implements View.OnClickListener {

        TerritoriosArrayAdapter adapter;
        Boolean isPopup;

        public LinhaClickListener (TerritoriosArrayAdapter adapter, Boolean isPopup){
            this.adapter = adapter;
            this.isPopup = isPopup != null ? isPopup : Boolean.FALSE;
        }

        @Override
        public void onClick(View v) {

            final TerritorioVO vo = (TerritorioVO) v.getTag();
            final TerritorioDBHelper dbTerritorio = new TerritorioDBHelper(TerritoriosActivity.this);
            final DesignacaoDBHelper dbDesignacao = new DesignacaoDBHelper(TerritoriosActivity.this);

            PopupMenu popup = new PopupMenu(TerritoriosActivity.this, v);

            // This activity implements OnMenuItemClickListener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    List<TerritorioVO> territorioVOs;

                    switch (item.getItemId()) {
                        case R.id.menu_editar:
                            Intent intentEditar = new Intent(TerritoriosActivity.this, EditarTerritorioActivity.class);
                            intentEditar.putExtra("ID", vo.getId());
                            startActivity(intentEditar);
                            return true;
                        case R.id.menu_deletar:

                            boolean possuiDesignacao = dbDesignacao.possuiDesignacao(vo.getId());
                            if (!possuiDesignacao) {
                                new AlertDialog.Builder(TerritoriosActivity.this)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setMessage(getString(R.string.certeza_deletar_territorio))
                                        .setPositiveButton(getString(R.string.sim), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                db.deletarTerritorio(vo.getId());
                                                adapter.remove(vo);
                                                adapter.notifyDataSetChanged();
                                            }

                                        })
                                        .setNegativeButton(R.string.nao, null)
                                        .show();
                            } else {
                                Toast toast = Toast.makeText(TerritoriosActivity.this, getString(R.string.territorio_ja_designado), Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            return true;
                        case R.id.menu_historico:
                            Intent intentHistorico = new Intent(TerritoriosActivity.this, HistoricoActivity.class);
                            intentHistorico.putExtra("TerritorioVO", vo);
                            startActivity(intentHistorico);
                            return true;
                        case R.id.menu_ver_vizinhos:

                            List<TerritorioVizinhoVO> territorioVizinhoVOs = dbTerritorio.buscarVizinhos(vo.getId());
                            List<Integer> idVizinhos = new ArrayList<Integer>();
                            for (TerritorioVizinhoVO voVizinho : territorioVizinhoVOs) {
                                idVizinhos.add(voVizinho.getIdVizinho());
                            }

                            territorioVOs = dbTerritorio.buscarTerritoriosPorId(idVizinhos.toArray(new Integer[]{}));

                            if (!territorioVOs.isEmpty()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(TerritoriosActivity.this);
                                builder.setTitle(getString(R.string.territorios_vizinhos));
                                builder.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Blank
                                    }
                                });
                                TerritoriosArrayAdapter adapter = new TerritoriosArrayAdapter(TerritoriosActivity.this,
                                        android.R.layout.simple_list_item_1, territorioVOs, true);
                                builder.setAdapter(adapter, null);
                                builder.show();
                            } else {
                                ToastHelper.toast(TerritoriosActivity.this, getString(R.string.sem_territorios_vizinhos));
                            }

                            return true;

                        case R.id.menu_designar:
                            DirigenteDBHelper dbDirigente = new DirigenteDBHelper(TerritoriosActivity.this);
                            if (dbDirigente.possuiDirigentesCadastrado()) {
                                Intent intent = new Intent(TerritoriosActivity.this, DesignarActivity.class);
                                territorioVOs = dbTerritorio.buscarTerritoriosPorId(vo.getId());
                                intent.putExtra("listaSugerir", territorioVOs.toArray(new TerritorioVO[]{}));
                                startActivity(intent);
                            } else {
                                ToastHelper.toast(TerritoriosActivity.this, getString(R.string.cadastre_um_dirigente));
                            }
                            return true;
                        default:
                            return false;
                    }

                }
            });

            popup.inflate(R.menu.list_territorio_menu);
            Menu menu = popup.getMenu();

            boolean existeDesignacaoAberto = dbDesignacao.existeDesignacaoAberto(vo.getId());
            boolean territorioSuspenso = dbTerritorio.territorioSuspenso(vo.getId());

            if (existeDesignacaoAberto || territorioSuspenso) {
                menu.findItem(R.id.menu_designar).setEnabled(false);
            }

            if (isPopup){
                menu.findItem(R.id.menu_ver_vizinhos).setVisible(false);
            }

            popup.show();

        }

    }


}
