package com.application.territoryassistant.designar;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.application.territoryassistant.MainActivity;
import com.application.territoryassistant.R;
import com.application.territoryassistant.adapter.ListaTerritoriosArrayAdapter;
import com.application.territoryassistant.bd.DesignacaoDBHelper;
import com.application.territoryassistant.bd.DirigenteDBHelper;
import com.application.territoryassistant.bd.TerritorioDBHelper;
import com.application.territoryassistant.bd.UltimaAcoesDBHelper;
import com.application.territoryassistant.designar.vo.DesignacaoVO;
import com.application.territoryassistant.dirigentes.vo.DirigentesVO;
import com.application.territoryassistant.helper.ToastHelper;
import com.application.territoryassistant.territorios.vo.TerritorioVO;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DesignarActivity extends AppCompatActivity {


    private DesignacaoDBHelper dbDesignacao = new DesignacaoDBHelper(this);
    private DirigenteDBHelper dbDirigentes = new DirigenteDBHelper(this);
    private TerritorioDBHelper dbTerritorio = new TerritorioDBHelper(this);
    private UltimaAcoesDBHelper dbUltimaAcoes = new UltimaAcoesDBHelper(this);

    public String type1;
    public String type2;

    private List<DirigentesVO> dirigentesVOs = new ArrayList<>();

    private List<TerritorioVO> territorioVOs = new ArrayList<>();
    private ArrayList<TerritorioVO> territoriosSelecionados = new ArrayList<>();

    private Long dataInicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        type1 = getString(R.string.type1);
        type2 = getString(R.string.type2);

        setContentView(R.layout.activity_designar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null){
            territoriosSelecionados = (ArrayList<TerritorioVO>)savedInstanceState.getSerializable("territoriosSelecionados");
        }

        configurarAcoes();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("territoriosSelecionados", territoriosSelecionados);
        super.onSaveInstanceState(outState);
    }

    private void configurarAcoes() {

        dirigentesVOs = dbDirigentes.buscarDirigentes();
        territorioVOs = dbTerritorio.buscarTerritoriosNaoDesignados();

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null){
            Bundle extras = intent.getExtras();

            Object [] arrayTerritorioVOs = (Object[])extras.getSerializable("listaSugerir");

            if (territoriosSelecionados == null || territoriosSelecionados.isEmpty()) {
                for (TerritorioVO vo : territorioVOs) {
                    for (Object objectVO : arrayTerritorioVOs) {
                        if (objectVO instanceof TerritorioVO) {
                            TerritorioVO arrayVO = (TerritorioVO) objectVO;
                            if (vo.getId().equals(arrayVO.getId())) {
                                territoriosSelecionados.add(vo);
                            }
                        }
                    }

                }
            } else {
                ArrayList<TerritorioVO> legado = territoriosSelecionados;
                territoriosSelecionados = new ArrayList<>();
                for (TerritorioVO vo : territorioVOs) {
                    for (Object objectVO : legado) {
                        if (objectVO instanceof TerritorioVO) {
                            TerritorioVO arrayVO = (TerritorioVO) objectVO;
                            if (vo.getId().equals(arrayVO.getId())) {
                                territoriosSelecionados.add(vo);
                            }
                        }
                    }
                }
            }

            ListView listView = (ListView) DesignarActivity.this.findViewById(R.id.list_territorios);
            listView.setAdapter(new ListaTerritoriosArrayAdapter(DesignarActivity.this,
                    android.R.layout.simple_list_item_1, territoriosSelecionados.toArray(new TerritorioVO[]{})));

        }

        //Forcar nao abrir teclado
        View view = (TextView) findViewById(R.id.txt_data_inicio);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        Spinner spinner = (Spinner)findViewById(R.id.spinner_dirigente);
        SpinnerDirigentesArrayAdapter adapter = new SpinnerDirigentesArrayAdapter(this,
                android.R.layout.simple_spinner_item, dirigentesVOs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        ImageView img = (ImageView)findViewById(R.id.img_data_inicio);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        Calendar c = Calendar.getInstance();
                        c.set(year, monthOfYear, dayOfMonth);
                        c.set(Calendar.HOUR_OF_DAY, 0);
                        c.set(Calendar.MINUTE, 0);
                        c.set(Calendar.SECOND, 0);

                        int day = c.get(Calendar.DAY_OF_WEEK);

                        Spinner s = (Spinner)findViewById(R.id.spinner_type);
                        if (day != 7){
                            s.setSelection(0);
                        } else {
                            s.setSelection(1);
                        }

                        DateFormat df = DateFormat.getDateInstance();
                        Date date = c.getTime();

                        String data = df.format(date);

                        dataInicio = c.getTimeInMillis();
                        TextView d = (TextView) findViewById(R.id.txt_data_inicio);
                        d.setText(data);

                    }
                };

                DatePickerDialog dialog = new DatePickerDialog(DesignarActivity.this, listener, year, month, day);
                dialog.show();
            }
        });

        List<String> ofertaRevista = new ArrayList<>();
        ofertaRevista.add(type1);
        ofertaRevista.add(type2);

        spinner = (Spinner)findViewById(R.id.spinner_type);
        ArrayAdapter<String> stringAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, ofertaRevista);
        spinner.setAdapter(stringAdapter);

        Button btnSelecionarTerritorios = (Button)findViewById(R.id.btn_selecionar_territorios);

        btnSelecionarTerritorios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(DesignarActivity.this);

                builder.setTitle(getString(R.string.selecionar_territorios));

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ListView listView = (ListView) DesignarActivity.this.findViewById(R.id.list_territorios);
                        listView.setAdapter(new ListaTerritoriosArrayAdapter(DesignarActivity.this,
                                android.R.layout.simple_list_item_1, territoriosSelecionados.toArray(new TerritorioVO[]{})));

                    }
                });

                TerritoriosArrayAdapterDialog adapter = new TerritoriosArrayAdapterDialog(DesignarActivity.this,
                        android.R.layout.simple_list_item_1, territorioVOs);
                builder.setAdapter(adapter, null);

                builder.show();

            }
        });


        Button btnSalvar = (Button)findViewById(R.id.btn_salvar);

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(dataInicio == null){
                    ToastHelper.toast(DesignarActivity.this, getString(R.string.preencha_data_inicio));
                    return;
                }

                if (territoriosSelecionados != null &&
                        !territoriosSelecionados.isEmpty()){

                    List<DesignacaoVO> designacaoVOs = new ArrayList<DesignacaoVO>();

                    Spinner s = (Spinner)findViewById(R.id.spinner_type);

                    String result = (String)s.getSelectedItem();
                    String tipo;

                    if(type1.equals(result)){
                        tipo = "M";
                    } else {
                        tipo = "T";
                    }

                    s = (Spinner)findViewById(R.id.spinner_dirigente);

                    DirigentesVO dirigentesVO = (DirigentesVO)s.getSelectedItem();

                    StringBuilder builder = new StringBuilder();
                    boolean isFirst = true;

                    for (TerritorioVO territorioVO: territoriosSelecionados) {

                        if (isFirst){
                            builder.append(territorioVO.getCod());
                            isFirst = false;
                        } else {
                            builder.append(",").append(territorioVO.getCod());
                        }

                        DesignacaoVO vo = new DesignacaoVO(territorioVO.getId(), dirigentesVO.getId(), tipo, dataInicio, null);
                        designacaoVOs.add(vo);
                    }

                    dbDesignacao.gravarDesignacoes(designacaoVOs);

                    gravarUltimaAcoes(dirigentesVO, builder);

                    Intent intent = new Intent(DesignarActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    ToastHelper.toast(DesignarActivity.this, getString(R.string.selecione_territorios));
                    return;
                }

            }

            private void gravarUltimaAcoes(DirigentesVO dirigentesVO, StringBuilder builder) {
                UltimaAcoesDBHelper.UltimaAcaoVO vo = new UltimaAcoesDBHelper.UltimaAcaoVO("D", builder.toString(), dirigentesVO.getId(), dataInicio, null);
                dbUltimaAcoes.gravarUltimaAcoes(vo);
            }

        });

    }

    private class SpinnerDirigentesArrayAdapter extends ArrayAdapter<DirigentesVO> {

        Map<DirigentesVO, Integer> idMap = new HashMap<>();
        Context context;

        public SpinnerDirigentesArrayAdapter(Context context, int textViewResourceId,
                                             List<DirigentesVO> dirigentesVOs) {
            super(context, textViewResourceId,dirigentesVOs);

            this.context = context;

            for (DirigentesVO vo:dirigentesVOs) {
                idMap.put(vo, vo.getId());
            }
        }

        @Override
        public long getItemId(int position) {
            DirigentesVO item = getItem(position);
            return idMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            DirigentesVO vo = (DirigentesVO)getItem(position);

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            TextView rowView = (TextView)inflater.inflate(android.R.layout.simple_spinner_item, parent, false);

            rowView.setText(vo.getNome());
            rowView.setTag(vo);

            return rowView;

        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            DirigentesVO vo = (DirigentesVO)getItem(position);

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            CheckedTextView textView = (CheckedTextView)inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
            textView.setTag(vo);

            textView.setText(vo.getNome());

            return textView;

        }
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


}