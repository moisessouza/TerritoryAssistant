package com.application.territoryassistant.dirigentes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.application.territoryassistant.R;
import com.application.territoryassistant.bd.DirigenteDBHelper;
import com.application.territoryassistant.dirigentes.vo.DirigentesVO;
import com.application.territoryassistant.helper.ToastHelper;

public class EditarDirigenteActivity extends AppCompatActivity {

    DirigenteDBHelper db = new DirigenteDBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_dirigente);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_editar_dirigente);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        Integer id = extras.getInt("ID");

        buscarDirigente(id);

    }

    private void buscarDirigente(Integer id){

        final DirigentesVO vo = db.buscarDirigente(id);
        final EditText nome = (EditText)findViewById(R.id.txt_nome_dirigente_edicao);
        final EditText email = (EditText)findViewById(R.id.txt_email_dirigente_edicao);

        nome.setText(vo.getNome());
        email.setText(vo.getEmail());

        Button btnGravar = (Button)findViewById(R.id.btn_gravar_edicao);

        btnGravar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String d = nome.getText().toString();
                d = d.trim();
                if (!d.isEmpty()) {
                    String e = email.getText().toString();
                    vo.setNome(d);
                    vo.setEmail(e);
                    db.atualizarDirigente(vo);
                    TextView t = (TextView) findViewById(R.id.lab_mensagem_edicao);
                    t.setText(getString(R.string.dirigente_atualizado, d));
                } else {
                    ToastHelper.toast(EditarDirigenteActivity.this, getString(R.string.nome_obrigatorio));
                }
            }
        });

        Button btnVoltar = (Button)findViewById(R.id.btn_voltar_edicao);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditarDirigenteActivity.this, DirigentesActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

}