package com.application.territoryassistant.sugerir;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.application.territoryassistant.R;
import com.application.territoryassistant.adapter.ListaTerritoriosArrayAdapter;
import com.application.territoryassistant.adapter.SpinnerGrupoArrayAdapter;
import com.application.territoryassistant.bd.ConfiguracoesDBHelper;
import com.application.territoryassistant.bd.DesignacaoDBHelper;
import com.application.territoryassistant.bd.GrupoDBHelper;
import com.application.territoryassistant.bd.TerritorioDBHelper;
import com.application.territoryassistant.designar.DesignarActivity;
import com.application.territoryassistant.designar.vo.DesignacaoVO;
import com.application.territoryassistant.grupos.vo.GrupoVO;
import com.application.territoryassistant.helper.ToastHelper;
import com.application.territoryassistant.territorios.vo.TerritorioVO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SugerirActivity extends AppCompatActivity {

    TerritorioDBHelper dbTerritorio = new TerritorioDBHelper(this);
    DesignacaoDBHelper dbDesignacao = new DesignacaoDBHelper(this);
    GrupoDBHelper dbGrupo = new GrupoDBHelper(this);

    private ArrayList<TerritorioVO> territorioSelecionados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugerir);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null){
            territorioSelecionados = (ArrayList<TerritorioVO>)savedInstanceState.getSerializable("territorioSelecionados");
        }

        configurarAcoes();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putSerializable("territorioSelecionados", territorioSelecionados);

        super.onSaveInstanceState(outState);
    }

    private void configurarAcoes() {

        List<GrupoVO> grupoVOs = dbGrupo.buscarGrupos();

        Spinner spinner = (Spinner)findViewById(R.id.spinner_grupo);
        SpinnerGrupoArrayAdapter adapter = new SpinnerGrupoArrayAdapter(this,
                android.R.layout.simple_spinner_item, grupoVOs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        final TextView text = (TextView)findViewById(R.id.txt_num_territorios);

        Button btnSugerir = (Button)findViewById(R.id.btn_sugerir);

        btnSugerir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Esconder teclado
                InputMethodManager imm = (InputMethodManager)SugerirActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(text.getApplicationWindowToken(), 0);

                territorioSelecionados.clear();

                TextView text = (TextView) findViewById(R.id.txt_num_territorios);
                String num = text.getText().toString();
                Integer numero = null;

                if (num != null && !num.isEmpty()) {
                    numero = Integer.valueOf(num);
                } else {
                    ToastHelper.toast(SugerirActivity.this, SugerirActivity.this.getString(R.string.quantos_territorios_deseja_no_campo));
                    return;
                }

                Spinner s = (Spinner) findViewById(R.id.spinner_grupo);
                GrupoVO grupoVO = (GrupoVO) s.getSelectedItem();

                List<Integer> notIds = new ArrayList<>();
                List<DesignacaoVO> designacaoVOs = dbDesignacao.buscarDesignacoesEmAberto();

                if (!designacaoVOs.isEmpty()) {
                    for (DesignacaoVO designacaoVO : designacaoVOs) {
                        notIds.add(designacaoVO.getIdTerritorio());
                    }
                }

                CheckBox somenteVizinhos = (CheckBox)findViewById(R.id.check_somente_vizinhos);

                CheckBox respeitarDataLimite = (CheckBox)findViewById(R.id.check_data_limite);
                Long dataLimite = null;

                if (respeitarDataLimite.isChecked()) {

                    ConfiguracoesDBHelper dbConfiguracoes = new ConfiguracoesDBHelper(getBaseContext());
                    Integer numDias = dbConfiguracoes.buscarNumDiasEsperaTerritorio();

                    dataLimite = calcularDataLimite(numDias);
                }

                if (!somenteVizinhos.isChecked()){
                    selecionarTerritorios(numero, grupoVO, notIds, dataLimite);
                } else {
                    selecionarTerritoriosSomenteVizinhos(numero, grupoVO, notIds, dataLimite);
                }
                exibirSugestoes();

                if (territorioSelecionados.size() < numero) {
                    ToastHelper.toast(SugerirActivity.this, SugerirActivity.this.getString(R.string.nao_possivel_selecionar_numero_terriorios));
                }
            }
        });

        Button btnContinuar = (Button)findViewById(R.id.btn_continuar);

        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (territorioSelecionados != null && !territorioSelecionados.isEmpty()) {

                    Spinner s = (Spinner) findViewById(R.id.spinner_grupo);

                    Intent intent = new Intent(SugerirActivity.this, DesignarActivity.class);
                    intent.putExtra("listaSugerir", territorioSelecionados.toArray(new TerritorioVO[]{}));

                    startActivity(intent);
                    finish();

                } else {
                    ToastHelper.toast(SugerirActivity.this, SugerirActivity.this.getString(R.string.clique_antes_sugerir_terriorios));
                }

            }
        });

        exibirSugestoes();

    }

    private Long calcularDataLimite(Integer numDias) {

        Long dataLimite = null;

        if (numDias != 0) {
            Integer numDiasNegativo = (numDias * -1);
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, 23);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.add(Calendar.DAY_OF_MONTH, numDiasNegativo);
            dataLimite = c.getTimeInMillis();
        }
        return dataLimite;
    }

    private void selecionarTerritorios(Integer numero, GrupoVO grupoVO, List<Integer> notIds, Long dataLimite) {

        List<Integer> idsVizinhos = new ArrayList<>();

        Integer total = 0;

        while (total < numero) {

            if (total == 0) {
                TerritorioVO territorioVO = dbTerritorio.buscarTerritorioMaisTempoTrabalhado(grupoVO.getId(), null, notIds.toArray(new Integer[]{}), dataLimite);

                if(territorioVO == null){
                    ToastHelper.toast(SugerirActivity.this, SugerirActivity.this.getString(R.string.nao_mais_territorios_serem_designados));
                    break;
                }

                territorioSelecionados.add(territorioVO);
                notIds.add(territorioVO.getId());
                idsVizinhos.add(territorioVO.getId());
                total++;
            } else {
                TerritorioVO territorioVO = dbTerritorio.buscarTerritorioMaisTempoTrabalhado(grupoVO.getId(), idsVizinhos.toArray(new Integer[]{}),
                        notIds.toArray(new Integer[]{}), dataLimite);

                if (territorioVO == null) {
                    territorioVO = dbTerritorio.buscarTerritorioMaisTempoTrabalhado(grupoVO.getId(), null, notIds.toArray(new Integer[]{}), dataLimite);
                }

                if (territorioVO != null) {
                    territorioSelecionados.add(territorioVO);
                    notIds.add(territorioVO.getId());
                    idsVizinhos.add(territorioVO.getId());
                }

                total++;

            }

        }
    }

    private Integer selecionarTerritoriosSomenteVizinhos(Integer numero, GrupoVO grupoVO, List<Integer> notIds, Long dataLimite){

        List<Integer> idsVizinhos = new ArrayList<>();

        ArrayList<TerritorioVO> selecao = new ArrayList<>();
        List<Integer> notIdsInterno = new ArrayList<>(notIds);

        Integer total = 0;

        TerritorioVO primeiroTerritorioVO = null;

        while (total < numero) {

            if (total == 0) {
                primeiroTerritorioVO = dbTerritorio.buscarTerritorioMaisTempoTrabalhado(grupoVO.getId(), null, notIdsInterno.toArray(new Integer[]{}), dataLimite);

                if(primeiroTerritorioVO == null){
                    break;
                }

                selecao.add(primeiroTerritorioVO);
                notIdsInterno.add(primeiroTerritorioVO.getId());
                idsVizinhos.add(primeiroTerritorioVO.getId());
                total++;
            } else {
                TerritorioVO territorioVO = dbTerritorio.buscarTerritorioMaisTempoTrabalhado(grupoVO.getId(), idsVizinhos.toArray(new Integer[]{}), notIdsInterno.toArray(new Integer[]{}), dataLimite);

                if (territorioVO != null) {
                    selecao.add(territorioVO);
                    notIdsInterno.add(territorioVO.getId());
                    idsVizinhos.add(territorioVO.getId());
                }

                total++;

            }

        }

        if (selecao.size() < numero) {
            if (primeiroTerritorioVO != null) {
                notIds.add(primeiroTerritorioVO.getId());
                Integer numTerritorios = selecionarTerritoriosSomenteVizinhos(numero, grupoVO, notIds, dataLimite);
                if (numTerritorios <= selecao.size()) {
                    territorioSelecionados = new ArrayList<>(selecao);
                }
            }
        } else {
            territorioSelecionados = new ArrayList<>(selecao);
        }

        return territorioSelecionados.size();

    }

    private void exibirSugestoes() {

        ListView listView = (ListView) SugerirActivity.this.findViewById(R.id.list_sugerir);
        listView.setAdapter(new ListaTerritoriosArrayAdapter(SugerirActivity.this,
                android.R.layout.simple_list_item_1, territorioSelecionados.toArray(new TerritorioVO[]{})));

    }


}