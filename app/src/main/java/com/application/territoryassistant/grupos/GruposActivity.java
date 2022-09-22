package com.application.territoryassistant.grupos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.application.territoryassistant.R;
import com.application.territoryassistant.bd.GrupoDBHelper;
import com.application.territoryassistant.grupos.vo.GrupoVO;
import com.application.territoryassistant.helper.ToastHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GruposActivity extends AppCompatActivity {

    GrupoDBHelper db = new GrupoDBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_grupos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_grupos);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GruposActivity.this, NovoGrupoActivity.class);
                startActivity(intent);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        carregarGrupos();

    }

    private void carregarGrupos(){

        List<GrupoVO> grupoVOs = db.buscarGrupos();

        ListView listView = (ListView)findViewById(R.id.list_grupos);

        GruposArrayAdapter adapter = new GruposArrayAdapter(this,
                android.R.layout.simple_list_item_1, grupoVOs);
        listView.setAdapter(adapter);

    }

    private class GruposArrayAdapter extends ArrayAdapter<GrupoVO> {

        Context context;
        Map<GrupoVO, Integer> idMap = new HashMap<GrupoVO, Integer>();

        public GruposArrayAdapter(Context context, int textViewResourceId,
                                       List<GrupoVO> GrupoVO) {
            super(context, textViewResourceId, GrupoVO);
            this.context = context;

            for (GrupoVO vo:GrupoVO) {
                idMap.put(vo, vo.getId());
            }
        }

        @Override
        public long getItemId(int position) {
            GrupoVO item = getItem(position);
            return idMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            GrupoVO vo = getItem(position);

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.fragment_grupos, parent, false);

            TextView textView = (TextView) rowView.findViewById(R.id.nomes);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.delete);

            imageView.setTag(vo);
            textView.setTag(vo);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView i = (ImageView) v;
                    GrupoVO vo = (GrupoVO) i.getTag();

                    boolean possuiTerritorio = db.possuiTerritorio(vo.getId());
                    if (!possuiTerritorio) {
                        db.deletarGrupo(vo.getId());
                        GruposArrayAdapter.this.remove(vo);
                        GruposArrayAdapter.this.notifyDataSetChanged();
                    } else {
                        ToastHelper.toast(GruposActivity.this, getString(R.string.grupo_associado_territorio));
                    }
                }

            });

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    GrupoVO vo = (GrupoVO) v.getTag();

                    Intent intent = new Intent(GruposActivity.this, EditarGrupoActivity.class);
                    intent.putExtra("ID", vo.getId());
                    startActivity(intent);

                }

            });

            textView.setText(vo.getNome());

            return rowView;
        }

    }

}
