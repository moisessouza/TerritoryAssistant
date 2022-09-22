package com.application.territoryassistant.grupos;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.application.territoryassistant.R;
import com.application.territoryassistant.bd.GrupoDBHelper;

public class NovoGrupoActivity extends AppCompatActivity {

    GrupoDBHelper db = new GrupoDBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_grupo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        configurarAcoes();

    }

    public void configurarAcoes(){

        Button btnGrabar = (Button)findViewById(R.id.btn_gravar);

        btnGrabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText txtNomeGrupo = (EditText)findViewById(R.id.txt_nome_grupo);
                TextView mensagem = (TextView)findViewById(R.id.lab_mensagem);
                String nome = txtNomeGrupo.getText().toString();
                db.gravarGrupo(nome);
                mensagem.setText(getString(R.string.grupo_inserido, nome));
                txtNomeGrupo.setText(null);

            }
        });

        Button btnVoltar = (Button)findViewById(R.id.btn_voltar);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NovoGrupoActivity.this, GruposActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

}
