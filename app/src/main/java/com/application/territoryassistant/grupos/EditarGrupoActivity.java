package com.application.territoryassistant.grupos;

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
import com.application.territoryassistant.bd.GrupoDBHelper;
import com.application.territoryassistant.grupos.vo.GrupoVO;
import com.application.territoryassistant.viewmodel.GroupViewModel;
import com.application.territoryassistant.viewmodel.ViewModelFactory;

public class EditarGrupoActivity extends AppCompatActivity {

    private GroupViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_grupo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewModel = new ViewModelProvider(this, new ViewModelFactory(this)).get(GroupViewModel.class);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        Integer id = extras != null ? extras.getInt("ID") : null;

        if (id != null) {
            buscarGrupo(id);
        }
    }

    private void buscarGrupo(Integer id){

        GrupoDBHelper db = new GrupoDBHelper(this);
        final GrupoVO vo = db.buscarGrupo(id);
        if (vo == null) return;

        final EditText text = (EditText)findViewById(R.id.txt_nome_grupo);

        text.setText(vo.getNome());

        Button btnGravar = (Button)findViewById(R.id.btn_gravar);

        btnGravar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = text.getText().toString();
                viewModel.updateGroup(vo.getId(), nome, success -> {
                    TextView t = (TextView) findViewById(R.id.lab_mensagem);
                    t.setText(getString(R.string.grupo_atualizado, nome));
                    return null;
                });
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
