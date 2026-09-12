package com.application.territoryassistant.dirigentes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.application.territoryassistant.R;
import com.application.territoryassistant.bd.DesignacaoDBHelper;
import com.application.territoryassistant.bd.room.DirigenteEntity;
import com.application.territoryassistant.dirigentes.vo.DirigentesVO;
import com.application.territoryassistant.helper.ToastHelper;
import com.application.territoryassistant.viewmodel.LeaderUiState;
import com.application.territoryassistant.viewmodel.LeaderViewModel;
import com.application.territoryassistant.viewmodel.ViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirigentesActivity extends AppCompatActivity {

    private LeaderViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dirigentes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewModel = new ViewModelProvider(this, new ViewModelFactory(this)).get(LeaderViewModel.class);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_dirigentes);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DirigentesActivity.this, NovoDirigenteActivity.class);
                startActivity(intent);
            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        observarDirigentes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadLeaders();
    }

    private void observarDirigentes() {
        ListView listview = (ListView) findViewById(R.id.list_dirigentes);

        viewModel.getUiState().observe(this, state -> {
            if (state instanceof LeaderUiState.Success) {
                LeaderUiState.Success success = (LeaderUiState.Success) state;
                List<DirigentesVO> dirigentesVOs = new ArrayList<>();
                for (DirigenteEntity entity : success.getLeaders()) {
                    int id = entity.getId() != null ? entity.getId() : 0;
                    dirigentesVOs.add(new DirigentesVO(id, entity.getNome(), entity.getEmail()));
                }
                DirigentesArrayAdapter adapter = new DirigentesArrayAdapter(this,
                        android.R.layout.simple_list_item_1, dirigentesVOs);
                listview.setAdapter(adapter);
            }
        });

        viewModel.loadLeaders();
    }

    private class DirigentesArrayAdapter extends ArrayAdapter<DirigentesVO> {

        Context context;
        Map<DirigentesVO, Integer> idMap = new HashMap<DirigentesVO, Integer>();

        public DirigentesArrayAdapter(Context context, int textViewResourceId,
                                      List<DirigentesVO> dirigentesVOs) {
            super(context, textViewResourceId, dirigentesVOs);
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

            DirigentesVO vo = getItem(position);

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.fragment_dirigentes, parent, false);

            TextView textView = (TextView) rowView.findViewById(R.id.nomes);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.delete);

            imageView.setTag(vo);
            textView.setTag(vo);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView i = (ImageView) v;
                    DirigentesVO vo = (DirigentesVO) i.getTag();

                    DesignacaoDBHelper dbDesignacao = new DesignacaoDBHelper(DirigentesActivity.this);
                    boolean jaDesignado = dbDesignacao.dirigenteJaDesignado(vo.getId());
                    if (!jaDesignado) {
                        viewModel.deleteLeader(vo.getId(), success -> {
                            DirigentesArrayAdapter.this.remove(vo);
                            DirigentesArrayAdapter.this.notifyDataSetChanged();
                            return null;
                        });
                    } else {
                        ToastHelper.toast(DirigentesActivity.this, getString(R.string.dirigente_ja_designado));
                    }
                }

            });

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DirigentesVO vo = (DirigentesVO) v.getTag();

                    Intent intent = new Intent(DirigentesActivity.this, EditarDirigenteActivity.class);
                    intent.putExtra("ID", vo.getId());
                    startActivity(intent);
                }

            });

            textView.setText(vo.getNome());

            return rowView;
        }
    }
}
