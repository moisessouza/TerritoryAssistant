package com.application.territoryassistant.territorios;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.application.territoryassistant.R;
import com.application.territoryassistant.adapter.SpinnerGrupoArrayAdapter;
import com.application.territoryassistant.bd.GrupoDBHelper;
import com.application.territoryassistant.bd.TerritorioDBHelper;
import com.application.territoryassistant.fototerritorio.FotoTerritorioActivity;
import com.application.territoryassistant.grupos.vo.GrupoVO;
import com.application.territoryassistant.helper.FotoHelper;
import com.application.territoryassistant.helper.ToastHelper;
import com.application.territoryassistant.territorios.vo.TerritorioVO;
import com.application.territoryassistant.territorios.vo.TerritorioVizinhoVO;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NovoTerritorioActivity extends AppCompatActivity {

    TerritorioDBHelper dbTerritorio = new TerritorioDBHelper(this);
    GrupoDBHelper dbGrupo = new GrupoDBHelper(this);

    private ArrayList<TerritorioVO> territoriosSelecionados = new ArrayList<>();
    private List<TerritorioVO> territorioVOs = new ArrayList<>();

    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_territorio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState != null){
            photoPath = savedInstanceState.getString("photoPath");
            territoriosSelecionados = (ArrayList<TerritorioVO>)savedInstanceState.getSerializable("territoriosSelecionados");
        }

        configurarAcoes();

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("photoPath", photoPath);
        savedInstanceState.putSerializable("territoriosSelecionados", territoriosSelecionados);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void configurarAcoes () {

        List<GrupoVO> grupoVOs = dbGrupo.buscarGrupos();

        territorioVOs = dbTerritorio.buscarTerritorios();

        if (territoriosSelecionados != null){
            List<TerritorioVO> legado = territoriosSelecionados;
            territoriosSelecionados = new ArrayList<>();
            for (TerritorioVO terVO : territorioVOs) {
                for (TerritorioVO territorioVO : legado) {
                    if (terVO.getId().equals(territorioVO.getId())) {
                        territoriosSelecionados.add(terVO);
                    }
                }
            }
        }

        if (photoPath != null) {
            ImageView fotoTerritorio = (ImageView)findViewById(R.id.img_foto_territorio);
            Bitmap bitmap = FotoHelper.decodeSampledBitmapFromFile(photoPath, 100, 100);
            fotoTerritorio.setImageBitmap(bitmap);

            fotoTerritorio.setOnClickListener(onClickFotoTerritorio());

        }

        Spinner spinner = (Spinner)findViewById(R.id.spinner_grupos);
        SpinnerGrupoArrayAdapter adapter = new SpinnerGrupoArrayAdapter(this,
                android.R.layout.simple_spinner_item, grupoVOs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        FloatingActionButton btnGravar = (FloatingActionButton)findViewById(R.id.fab_gravar);

        btnGravar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText txt_cod = (EditText) findViewById(R.id.txt_cod_territorio);
                Spinner s = (Spinner) findViewById(R.id.spinner_grupos);
                EditText obs = (EditText) findViewById(R.id.txt_observacoes);
                CheckBox check = (CheckBox) findViewById(R.id.check_suspenso);

                String cod = txt_cod.getText().toString();
                String observacoes = obs.getText().toString();
                GrupoVO grupoVO = (GrupoVO) s.getSelectedItem();
                Boolean suspenso = check.isChecked();

                if (cod.isEmpty()){
                    ToastHelper.toast(getBaseContext(), getString(R.string.cod_error));

                }else {

                    if (dbTerritorio.buscarTerritorioPorCod(cod) == null) {

                        Long idTerritorio = dbTerritorio.gravarTerritorio(cod, grupoVO.getId(), observacoes, photoPath, suspenso);
                        if (idTerritorio != -1 && territoriosSelecionados != null && !territoriosSelecionados.isEmpty()) {
                            dbTerritorio.gravarVizinhos(Long.valueOf(idTerritorio).intValue(), territoriosSelecionados);
                        }

                        ToastHelper.toast(getBaseContext(), getString(R.string.territorio_inserido, cod));

                        territorioVOs = dbTerritorio.buscarTerritorios();
                        territoriosSelecionados.clear();

                        finish();

                    } else {
                        ToastHelper.toast(getBaseContext(), getString(R.string.territorio_ja_existe, cod));
                    }

                }


            }
        });

        Button btnSelecionarVisinhos =  (Button) findViewById(R.id.btn_selecionar_vizinhos);

        btnSelecionarVisinhos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Esconder teclado
                EditText t = (EditText) findViewById(R.id.txt_cod_territorio);
                InputMethodManager imm = (InputMethodManager) NovoTerritorioActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(t.getApplicationWindowToken(), 0);

                AlertDialog.Builder builder = new AlertDialog.Builder(NovoTerritorioActivity.this);

                builder.setTitle(getString(R.string.selecionar_vizinhos));

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Blank
                    }
                });
                TerritoriosArrayAdapterDialog adapter = new TerritoriosArrayAdapterDialog(NovoTerritorioActivity.this,
                        android.R.layout.simple_list_item_1, territorioVOs);
                builder.setAdapter(adapter, null);

                builder.show();
            }
        });

        ImageButton verFoto = (ImageButton)findViewById(R.id.btn_ver_foto);

        verFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NovoTerritorioActivity.this, FotoTerritorioActivity.class);
                if (photoPath != null){
                    intent.putExtra("caminhoArquivo", photoPath);
                }
                startActivityForResult(intent, 1);
            }
        });


    }

    private class TerritoriosArrayAdapterDialog extends ArrayAdapter<TerritorioVO> {

        Context context;
        Map<TerritorioVO, Integer> idMap = new HashMap<TerritorioVO, Integer>();

        public TerritoriosArrayAdapterDialog(Context context, int textViewResourceId,
                                             List<TerritorioVO> territorioVOs) {
            super(context, textViewResourceId, territorioVOs);
            this.context = context;

            for (TerritorioVO vo:territorioVOs) {
                idMap.put(vo, vo.getId());
            }
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
            TextView mDataFim;
            CheckBox checkBox;

            final View rowView;

            public ViewHolder (ViewGroup parent){

                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                rowView = inflater.inflate(R.layout.dialog_selecionar_territorios, parent, false);

                labUltimaData = (TextView)rowView.findViewById(R.id.lab_ultima_data);

                mCodigo = (TextView) rowView.findViewById(R.id.codigos);
                mDataFim = (TextView) rowView.findViewById(R.id.ultima_data);
                checkBox = (CheckBox)rowView.findViewById(R.id.check_territorios);

            }

            public View getRowView(){
                return rowView;
            }

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder vh = null;
            if (convertView == null){
                vh = new ViewHolder(parent);
                convertView = vh.getRowView();
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder)convertView.getTag();
            }

            TerritorioVO vo = getItem(position);

            if (vo.getUltimaDataFim() != null && vo.getUltimaDataFim() != 0){
                DateFormat df = DateFormat.getDateInstance();
                String data = df.format(new Date(vo.getUltimaDataFim()));
                vh.mDataFim.setText(data);
            } else {
                vh.mDataFim.setText(null);
            }

            vh.mCodigo.setText(vo.getCod());
            vh.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CheckBox checkView = (CheckBox) v;

                    if (checkView.isChecked()) {
                        territoriosSelecionados.add((TerritorioVO) v.getTag());
                    } else {
                        territoriosSelecionados.remove((TerritorioVO) v.getTag());
                    }

                }
            });

            vh.checkBox.setTag(vo);
            vh.checkBox.setChecked(territoriosSelecionados.contains(vo));

            return convertView;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1) {
            Bundle extras = data.getExtras();
            photoPath = extras.getString("caminhoArquivo");
            ImageView imageView = (ImageView)findViewById(R.id.img_foto_territorio);
            Bitmap bitmap = FotoHelper.decodeSampledBitmapFromFile(photoPath, 100, 100);
            imageView.setImageBitmap(bitmap);
            imageView.setOnClickListener(onClickFotoTerritorio());
        }
    }

    @NonNull
    private View.OnClickListener onClickFotoTerritorio() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            FotoHelper.showPhoto(NovoTerritorioActivity.this, photoPath, null);
            }
        };
    }
}
