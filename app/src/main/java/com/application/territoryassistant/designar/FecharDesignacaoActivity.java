package com.application.territoryassistant.designar;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.territoryassistant.MainActivity;
import com.application.territoryassistant.R;
import com.application.territoryassistant.bd.DesignacaoDBHelper;
import com.application.territoryassistant.bd.DirigenteDBHelper;
import com.application.territoryassistant.bd.TerritorioDBHelper;
import com.application.territoryassistant.bd.UltimaAcoesDBHelper;
import com.application.territoryassistant.designar.vo.DesignacaoVO;
import com.application.territoryassistant.dirigentes.vo.DirigentesVO;
import com.application.territoryassistant.helper.ToastHelper;
import com.application.territoryassistant.territorios.vo.TerritorioVO;

import java.text.DateFormat;
import java.util.Calendar;

public class FecharDesignacaoActivity extends AppCompatActivity {

    TerritorioDBHelper dbTerritorio = new TerritorioDBHelper(this);
    DesignacaoDBHelper dbDesignacao = new DesignacaoDBHelper(this);
    DirigenteDBHelper dbDirigente = new DirigenteDBHelper(this);
    UltimaAcoesDBHelper dbUltimaAcoes = new UltimaAcoesDBHelper(this);
    
    private DesignacaoVO designacaoVO;
    private TerritorioVO territorioVO;
    private DirigentesVO dirigentesVO;

    private Long dataFim;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fechar_designacao);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        Integer idDesignacao = extras.getInt("ID");
        
        buscarDados(idDesignacao);
        
        configurarAcoes();
        
    }

    private void buscarDados(Integer idDesignacao) {
        
        designacaoVO = dbDesignacao.buscarDesignacao(idDesignacao);
        territorioVO = dbTerritorio.buscarTerritorio(designacaoVO.getIdTerritorio());
        dirigentesVO = dbDirigente.buscarDirigente(designacaoVO.getIdDirigente());
        
    }


    private void configurarAcoes() {
        
        TextView t = (TextView)findViewById(R.id.txt_cod_territorio);
        t.setText(territorioVO.getCod());
        
        t = (TextView)findViewById(R.id.txt_nome_dirigente);
        t.setText(dirigentesVO.getNome());
        
        t = (TextView)findViewById(R.id.txt_tipo);

        String ti = designacaoVO.getTipo();

        if ("O".equals(ti)){
            t.setText(getString(R.string.oferta));
        } else {
            t.setText(getString(R.string.revista));
        }

        Long dataInicio = designacaoVO.getDataInicio();

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(dataInicio);

        DateFormat df = DateFormat.getDateInstance();
        String data = df.format(c.getTime());

        t = (TextView)findViewById(R.id.txt_data_inicio);
        t.setText(data);

        ImageView i = (ImageView)findViewById(R.id.img_data_fim);

        i.setOnClickListener(new View.OnClickListener() {
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
                        c.set(Calendar.HOUR_OF_DAY, 23);
                        c.set(Calendar.MINUTE,59);
                        c.set(Calendar.SECOND, 59);

                        DateFormat df = DateFormat.getDateInstance();
                        String data = df.format(c.getTime());

                        dataFim = c.getTimeInMillis();
                        TextView d = (TextView) findViewById(R.id.txt_data_fim);
                        d.setText(data);

                    }
                };

                DatePickerDialog dialog = new DatePickerDialog(FecharDesignacaoActivity.this, listener, year, month, day);
                dialog.show();
            }
        });

        Button btnFechar = (Button) findViewById(R.id.btn_fechar);

        btnFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dataFim == null){
                    ToastHelper.toast(FecharDesignacaoActivity.this, getString(R.string.preencha_data_fim));
                    return;
                }

                Long dataInicio = designacaoVO.getDataInicio();

                if (dataFim <= dataInicio){
                    ToastHelper.toast(FecharDesignacaoActivity.this, getString(R.string.data_fim_maior_igual_data_inicio));
                    return;
                }

                designacaoVO.setDataFim(dataFim);
                dbDesignacao.atualizarDesignacao(designacaoVO);

                //Gravar ultima data fim
                territorioVO.setUltimaDataFim(dataFim);
                dbTerritorio.atualizarTerritorio(territorioVO);

                gravarUltimaAcoes();
                setResult(RESULT_OK);
                finish();
            }

            private void gravarUltimaAcoes() {
                UltimaAcoesDBHelper.UltimaAcaoVO vo = new UltimaAcoesDBHelper.UltimaAcaoVO("F", territorioVO.getCod(), dirigentesVO.getId(), null, dataFim);
                dbUltimaAcoes.gravarUltimaAcoes(vo);
            }

        });

        Button btnDeletar = (Button) findViewById(R.id.btn_deletar);

        btnDeletar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            new AlertDialog.Builder(FecharDesignacaoActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(getString(R.string.tem_certeza_deletar_designacao))
                    .setPositiveButton(getString(R.string.sim), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dbDesignacao.deletarDesignacao(designacaoVO.getId());
                            setResult(RESULT_OK);
                            finish();
                        }

                    })
                    .setNegativeButton(getString(R.string.nao), null)
                    .show();

            }
        });

    }
}
