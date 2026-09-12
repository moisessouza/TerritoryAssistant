package com.application.territoryassistant.dirigentes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.application.territoryassistant.R;
import com.application.territoryassistant.bd.DirigenteDBHelper;
import com.application.territoryassistant.dirigentes.vo.DirigentesVO;
import com.application.territoryassistant.helper.ToastHelper;
import com.application.territoryassistant.viewmodel.LeaderViewModel;
import com.application.territoryassistant.viewmodel.ViewModelFactory;

public class EditarDirigenteActivity extends AppCompatActivity {

    private LeaderViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_dirigente);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_editar_dirigente);
        setSupportActionBar(toolbar);

        viewModel = new ViewModelProvider(this, new ViewModelFactory(this)).get(LeaderViewModel.class);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        Integer id = extras != null ? extras.getInt("ID") : null;

        if (id != null) {
            buscarDirigente(id);
        }
    }

    private void buscarDirigente(Integer id){

        DirigenteDBHelper db = new DirigenteDBHelper(this);
        final DirigentesVO vo = db.buscarDirigente(id);
        if (vo == null) return;

        final EditText nome = (EditText)findViewById(R.id.txt_nome_dirigente_edicao);
        final EditText email = (EditText)findViewById(R.id.txt_email_dirigente_edicao);

        nome.setText(vo.getNome());
        email.setText(vo.getEmail());

        Button btnGravar = (Button)findViewById(R.id.btn_gravar_edicao);

        btnGravar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String d = nome.getText().toString().trim();
                if (!d.isEmpty()) {
                    String e = email.getText().toString();
                    viewModel.updateLeader(vo.getId(), d, e, success -> {
                        TextView t = (TextView) findViewById(R.id.lab_mensagem_edicao);
                        t.setText(getString(R.string.dirigente_atualizado, d));
                        return null;
                    });
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
