package com.application.territoryassistant.grupos;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.application.territoryassistant.R;
import com.application.territoryassistant.bd.GrupoDBHelper;
import com.application.territoryassistant.grupos.vo.GrupoVO;
import com.application.territoryassistant.territorios.TerritoriosActivity;
import com.application.territoryassistant.territorios.vo.TerritorioVO;

public class EditarGrupoActivity extends AppCompatActivity {

    GrupoDBHelper db = new GrupoDBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_grupo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        Integer id = extras.getInt("ID");

        buscarGrupo(id);

    }

    private void buscarGrupo(Integer id){

        final GrupoVO vo = db.buscarGrupo(id);
        final EditText text = (EditText)findViewById(R.id.txt_nome_grupo);

        text.setText(vo.getNome());

        Button btnGravar = (Button)findViewById(R.id.btn_gravar);

        btnGravar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = text.getText().toString();
                vo.setNome(nome);
                db.atualizarGrupo(vo);
                TextView t = (TextView) findViewById(R.id.lab_mensagem);
                t.setText(getString(R.string.grupo_atualizado, nome));
            }
        });

        Button btnVoltar = (Button)findViewById(R.id.btn_voltar);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditarGrupoActivity.this, GruposActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

}
