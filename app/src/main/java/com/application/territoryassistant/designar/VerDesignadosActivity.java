package com.application.territoryassistant.designar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.application.territoryassistant.R;
import com.application.territoryassistant.bd.DesignacaoDBHelper;
import com.application.territoryassistant.bd.TerritorioDBHelper;
import com.application.territoryassistant.designar.vo.DesignacaoVO;
import com.application.territoryassistant.historico.HistoricoActivity;
import com.application.territoryassistant.territorios.vo.TerritorioVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VerDesignadosActivity extends AppCompatActivity {

    private DesignacaoDBHelper dbDesignacao = new DesignacaoDBHelper(this);
    private TerritorioDBHelper dbTerritorio = new TerritorioDBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_designados);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        configurarAcoes();
    }

    private void configurarAcoes() {

        List<TerritorioVO> territorioVOs = dbTerritorio.buscarTerritorios();
        ListView listView = (ListView)findViewById(R.id.list_territorios);

        TerritoriosArrayAdapter adapter = new TerritoriosArrayAdapter(this, android.R.layout.simple_list_item_1, territorioVOs);
        listView.setAdapter(adapter);

    }

    private class TerritoriosArrayAdapter extends ArrayAdapter<TerritorioVO> {

        Context context;
        Map<TerritorioVO, Integer> idMap = new HashMap<TerritorioVO, Integer>();

        public TerritoriosArrayAdapter(Context context, int textViewResourceId,
                                       List<TerritorioVO> territorioVOs) {
            super(context, textViewResourceId, territorioVOs);
            this.context = context;

            for (TerritorioVO vo:territorioVOs) {
                idMap.put(vo, vo.getId());
            }
        }

        @Override
        public long getItemId(int position) {
            TerritorioVO item = getItem(position);
            return idMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TerritorioVO vo = getItem(position);

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            TextView textView = (TextView)inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            textView.setTag(vo);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TerritorioVO vo = (TerritorioVO)v.getTag();
                    Intent intent = new Intent(VerDesignadosActivity.this, HistoricoActivity.class);
                    intent.putExtra("TerritorioVO", vo);
                    startActivity(intent);
                }

            });

            textView.setText(vo.getCod());
            return textView;
        }

    }

}
