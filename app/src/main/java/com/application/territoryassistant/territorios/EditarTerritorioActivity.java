package com.application.territoryassistant.territorios;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.application.territoryassistant.R;
import com.application.territoryassistant.bd.GrupoDBHelper;
import com.application.territoryassistant.bd.TerritorioDBHelper;
import com.application.territoryassistant.fototerritorio.FotoTerritorioActivity;
import com.application.territoryassistant.grupos.vo.GrupoVO;
import com.application.territoryassistant.helper.FotoHelper;
import com.application.territoryassistant.helper.ToastHelper;
import com.application.territoryassistant.manager.FotoManager;
import com.application.territoryassistant.territorios.vo.TerritorioVO;
import com.application.territoryassistant.territorios.vo.TerritorioVizinhoVO;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditarTerritorioActivity extends AppCompatActivity {

    TerritorioDBHelper dbTerritorio = new TerritorioDBHelper(this);
    GrupoDBHelper dbGrupo = new GrupoDBHelper(this);

    private ArrayList<TerritorioVO> territoriosSelecionados = new ArrayList<>();
    private List<TerritorioVO> territorioVOs = new ArrayList<>();

    private List<GrupoVO> grupoVOs = new ArrayList<>();
    private GrupoVO grupoVOSelecionado;

    private TerritorioVO territorioVO;

    private String photoPath;
    private Integer id;

    private boolean vizinhosAlterados = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_territorio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        id = extras.getInt("ID");

        if(savedInstanceState != null){
            photoPath = savedInstanceState.getString("photoPath");
            territoriosSelecionados = (ArrayList<TerritorioVO>)savedInstanceState.getSerializable("territoriosSelecionados");
        }

        buscarDados(id);
        configurarAcoes();

    }

    // Salva estado
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("photoPath", photoPath);
        savedInstanceState.putSerializable("territoriosSelecionados", territoriosSelecionados);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void buscarDados(Integer idTerritorio) {

        territorioVO = dbTerritorio.buscarTerritorio(idTerritorio);
        grupoVOs = dbGrupo.buscarGrupos();

        for (GrupoVO grupoVO:grupoVOs) {
            if(grupoVO.getId().equals(territorioVO.getIdGrupo())){
                grupoVOSelecionado = grupoVO;
                break;
            }
        }

        List<TerritorioVizinhoVO> territorioVizinhoVOs = dbTerritorio.buscarVizinhos(idTerritorio);
        territorioVOs = dbTerritorio.buscarTerritorios(idTerritorio, false, null);

        if (territoriosSelecionados == null || territoriosSelecionados.isEmpty()) {
            if (territorioVizinhoVOs != null && !territorioVizinhoVOs.isEmpty()) {
                for (TerritorioVO terVO : territorioVOs) {
                    for (TerritorioVizinhoVO territorioVizinhoVO : territorioVizinhoVOs) {
                        if (terVO.getId().equals(territorioVizinhoVO.getIdVizinho())) {
                            territoriosSelecionados.add(terVO);
                            continue;
                        }
                    }
                }
            }
        } else {
            List<TerritorioVO> legado = territoriosSelecionados;
            territoriosSelecionados = new ArrayList<>();
            for (TerritorioVO terVO : territorioVOs) {
                for (TerritorioVO territorioVO : legado) {
                    if (terVO.getId().equals(territorioVO.getId())) {
                        territoriosSelecionados.add(terVO);
                        continue;
                    }
                }
            }
        }

    }

    private void configurarAcoes () {

        EditText t = (EditText) findViewById(R.id.txt_cod_territorio);
        t.setText(territorioVO.getCod());

        Spinner spinner = (Spinner)findViewById(R.id.spinner_grupos);
        SpinnerGrupoArrayAdapter adapter = new SpinnerGrupoArrayAdapter(this,
                android.R.layout.simple_spinner_item, grupoVOs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        EditText obs = (EditText) findViewById(R.id.txt_observacoes);
        obs.setText(territorioVO.getObservacoes());

        spinner.setSelection(grupoVOs.indexOf(grupoVOSelecionado));

        CheckBox check = (CheckBox)findViewById(R.id.check_suspenso);
        check.setChecked(territorioVO.getSuspenso());

        ImageView fotoTerritorio = (ImageView)findViewById(R.id.img_foto_territorio);

        if (photoPath == null) {
            photoPath = territorioVO.getFotoPath();
        }

        if (photoPath != null) {

            File f = new File(photoPath);
            if (!f.exists()) {
                File fotosDirs = FotoManager.instance().createOrReturnDirectoryPhotos();
                f = new File(fotosDirs, photoPath.substring(photoPath.lastIndexOf(File.separator) + 1));
                photoPath = f.getPath();
            }

            Bitmap bitmap = FotoHelper.decodeSampledBitmapFromFile(photoPath, 100, 100);
            fotoTerritorio.setImageBitmap(bitmap);

            fotoTerritorio.setOnClickListener(onClickFotoTerritorio());

        }

        FloatingActionButton btnGravar = (FloatingActionButton)findViewById(R.id.fab_gravar);

        btnGravar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText t = (EditText) findViewById(R.id.txt_cod_territorio);
                Spinner s = (Spinner) findViewById(R.id.spinner_grupos);
                CheckBox check = (CheckBox)findViewById(R.id.check_suspenso);
                EditText obs = (EditText) findViewById(R.id.txt_observacoes);

                String cod = t.getText().toString();
                GrupoVO grupoVO = (GrupoVO) s.getSelectedItem();
                Boolean suspenso = check.isChecked();
                String observacoes = obs.getText().toString();

                territorioVO.setCod(cod);
                territorioVO.setIdGrupo(grupoVO.getId());
                territorioVO.setSuspenso(suspenso);
                territorioVO.setObservacoes(observacoes);
                territorioVO.setFotoPath(photoPath);

                if (dbTerritorio.buscarTerritorioPorCod(cod, territorioVO.getId()) == null) {
                    dbTerritorio.atualizarTerritorio(territorioVO);

                    if (territoriosSelecionados != null && !territoriosSelecionados.isEmpty()) {
                        dbTerritorio.gravarVizinhos(territorioVO.getId(), territoriosSelecionados);
                    } else {
                        dbTerritorio.deletarVizinhos(territorioVO.getId());
                    }

                    ToastHelper.toast(getBaseContext(), getString(R.string.territorio_atualizado, cod));
                    finish();

                } else {
                    ToastHelper.toast(getBaseContext(), getString(R.string.territorio_ja_existe, cod));
                }
            }
        });

        Button btnSelecionarVisinhos =  (Button) findViewById(R.id.btn_selecionar_vizinhos);


        btnSelecionarVisinhos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Esconder teclado
                EditText t = (EditText) findViewById(R.id.txt_cod_territorio);
                InputMethodManager imm = (InputMethodManager)EditarTerritorioActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(t.getApplicationWindowToken(), 0);

                AlertDialog.Builder builder = new AlertDialog.Builder(EditarTerritorioActivity.this);

                builder.setTitle(getString(R.string.selecionar_vizinhos));

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (vizinhosAlterados) {
                            ToastHelper.toast(EditarTerritorioActivity.this, getString(R.string.lista_vizinhos_alteradao));
                        }
                    }
                });

                TerritoriosArrayAdapterDialog adapter = new TerritoriosArrayAdapterDialog(EditarTerritorioActivity.this,
                        android.R.layout.simple_list_item_1, territorioVOs);
                builder.setAdapter(adapter, null);

                builder.show();
            }
        });

        List<String> stringTerritorios = new ArrayList<String>();

        for (TerritorioVO vo : territoriosSelecionados) {
            stringTerritorios.add(vo.getCod());
        }

        ImageButton verFoto = (ImageButton)findViewById(R.id.btn_ver_foto);

        verFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditarTerritorioActivity.this, FotoTerritorioActivity.class);
                if (photoPath != null) {
                    intent.putExtra("caminhoArquivo", photoPath);
                }
                startActivityForResult(intent, 1);
            }
        });

    }

    @NonNull
    private View.OnClickListener onClickFotoTerritorio() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            FotoHelper.showPhoto(EditarTerritorioActivity.this, photoPath, null);
            }
        };
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

                    vizinhosAlterados = true;

                }
            });

            vh.checkBox.setTag(vo);
            vh.checkBox.setChecked(territoriosSelecionados.contains(vo));

            return convertView;
        }

    }

    private class SpinnerGrupoArrayAdapter extends ArrayAdapter<GrupoVO> {

        Map<GrupoVO, Integer> idMap = new HashMap<GrupoVO, Integer>();
        Context context;

        public SpinnerGrupoArrayAdapter(Context context, int textViewResourceId,
                                        List<GrupoVO> grupoVOs) {
            super(context, textViewResourceId, grupoVOs);

            this.context = context;

            for (GrupoVO vo:grupoVOs) {
                idMap.put(vo, vo.getId());
            }
        }

        @Override
        public long getItemId(int position) {
            GrupoVO item = getItem(position);
            return idMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            GrupoVO vo = (GrupoVO)getItem(position);

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.spinner_grupos, parent, false);

            TextView textView = (TextView) rowView.findViewById(R.id.nome_grupo);
            textView.setTag(vo);

            textView.setText(vo.getNome());

            return rowView;

        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            GrupoVO vo = (GrupoVO)getItem(position);

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            CheckedTextView textView = (CheckedTextView)inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);;
            textView.setTag(vo);

            textView.setText(vo.getNome());

            return textView;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode== 1) {
            Bundle extras = data.getExtras();
            photoPath = extras.getString("caminhoArquivo");
            ImageView imageView = (ImageView)findViewById(R.id.img_foto_territorio);
            Bitmap bitmap = FotoHelper.decodeSampledBitmapFromFile(photoPath, 100, 100);
            imageView.setImageBitmap(bitmap);
            ToastHelper.toast(this, getString(R.string.foto_territorio_alterada));
        }
    }

}
