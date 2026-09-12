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
import com.application.territoryassistant.helper.ToastHelper;
import com.application.territoryassistant.viewmodel.LeaderViewModel;
import com.application.territoryassistant.viewmodel.ViewModelFactory;

public class NovoDirigenteActivity extends AppCompatActivity {

    private LeaderViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_dirigente);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_novo_dirigente);
        setSupportActionBar(toolbar);

        viewModel = new ViewModelProvider(this, new ViewModelFactory(this)).get(LeaderViewModel.class);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        configurarAcoes();
    }

    private void configurarAcoes (){

        Button btnGrabar = (Button)findViewById(R.id.btn_gravar);

        btnGrabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText txtNomeDirigente = (EditText)findViewById(R.id.txt_nome_dirigente);
                EditText txtEmail = (EditText) findViewById(R.id.txt_email_dirigente);
                TextView mensagem = (TextView)findViewById(R.id.lab_mensagem);
                String d = txtNomeDirigente.getText().toString();
                String email = txtEmail.getText().toString();

                if (!d.isEmpty()) {
                    viewModel.addLeader(d, email, success -> {
                        mensagem.setText(getString(R.string.dirigente_inserido, d));
                        txtNomeDirigente.setText(null);
                        txtEmail.setText(null);
                        return null;
                    });
                } else {
                    ToastHelper.toast(NovoDirigenteActivity.this, getString(R.string.nome_obrigatorio));
                }

            }
        });

        Button btnVoltar = (Button)findViewById(R.id.btn_voltar);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NovoDirigenteActivity.this, DirigentesActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

}
