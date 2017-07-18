package com.application.territoryassistant.dirigentes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.territoryassistant.R;
import com.application.territoryassistant.bd.DesignacaoDBHelper;
import com.application.territoryassistant.bd.DirigenteDBHelper;
import com.application.territoryassistant.dirigentes.vo.DirigentesVO;
import com.application.territoryassistant.helper.ToastHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirigentesActivity extends AppCompatActivity {

    DirigenteDBHelper db = new DirigenteDBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dirigentes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_dirigentes);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DirigentesActivity.this, NovoDirigenteActivity.class);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        carregarDirigentes();

    }

    private void carregarDirigentes(){

        List<DirigentesVO> dirigentesVOs = db.buscarDirigentes();

        ListView listview = (ListView) findViewById(R.id.list_dirigentes);

        DirigentesArrayAdapter adapter = new DirigentesArrayAdapter(this,
                android.R.layout.simple_list_item_1, dirigentesVOs);
        listview.setAdapter(adapter);
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
                        db.deletarDirigente(vo.getId());
                        DirigentesArrayAdapter.this.remove(vo);
                        DirigentesArrayAdapter.this.notifyDataSetChanged();
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
