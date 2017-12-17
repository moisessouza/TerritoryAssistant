package com.application.territoryassistant.configuracoes;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.application.territoryassistant.R;
import com.application.territoryassistant.bd.ConfiguracoesDBHelper;
import com.application.territoryassistant.helper.ToastHelper;

public class ConfiguracoesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        configurarAcoes();

    }

    private void configurarAcoes() {

        final ConfiguracoesDBHelper db = new ConfiguracoesDBHelper(this);

        String textoDirigente = db.buscarTextoDirigente();
        Integer numDias = db.buscarNumDiasEsperaTerritorio();

        final TextView txtTextoPadrao = (TextView)findViewById(R.id.txt_texto_padrao);
        txtTextoPadrao.setText(textoDirigente);

        final TextView txtNumDias = (TextView)findViewById(R.id.txt_num_dias);
        txtNumDias.setText(numDias.toString());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_salvar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db.atualizarTextoDirigente(txtTextoPadrao.getText().toString());

                String string = txtNumDias.getText().toString();
                if (!string.isEmpty()) {
                    db.atualizarNumDiasDescanso(Integer.valueOf(string));
                }

                ToastHelper.toast(ConfiguracoesActivity.this,
                        ConfiguracoesActivity.this.getString(R.string.configuracoes_salvas));
               finish();
            }
        });

    }

}
