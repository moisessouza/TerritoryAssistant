package com.application.territoryassistant.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.application.territoryassistant.R;
import com.application.territoryassistant.bd.ConfiguracoesDBHelper;
import com.application.territoryassistant.bd.TerritorioDBHelper;
import com.application.territoryassistant.bd.UltimaAcoesDBHelper;
import com.application.territoryassistant.designar.FecharDesignacaoActivity;
import com.application.territoryassistant.designar.vo.DesignacaoVO;
import com.application.territoryassistant.dirigentes.vo.DirigentesVO;
import com.application.territoryassistant.territorios.vo.TerritorioVO;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetalhesDirigentesArrayAdapter extends ArrayAdapter<DirigentesVO> {

    Context context;
    Map<DirigentesVO, Integer> idMap = new HashMap<>();

    public DetalhesDirigentesArrayAdapter(Context context, int textViewResourceId,
                                          List<DirigentesVO> dirigentesVOs) {
        super(context, textViewResourceId, dirigentesVOs);
        this.context = context;

        for (DirigentesVO vo : dirigentesVOs) {
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

        View rowView = inflater.inflate(R.layout.list_detalhes_dirigentes, parent, false);
        rowView.setTag(vo);

        TextView nomeDirigente = (TextView) rowView.findViewById(R.id.txt_nome_dirigente);
        TextView codTerritorio = (TextView) rowView.findViewById(R.id.txt_territorios);

        nomeDirigente.setText(vo.getNome());
        codTerritorio.setText(vo.getCodsTerritorio());

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarCodigoTerritorios((DirigentesVO)v.getTag());
            }
        });

        return rowView;
    }

    private void enviarCodigoTerritorios(DirigentesVO vo) {

        ConfiguracoesDBHelper db = new ConfiguracoesDBHelper(getContext());

        String codTerritorios = vo.getCodsTerritorio();

        StringBuilder builder  = new StringBuilder();
        builder.append(db.buscarTextoDirigente()).append("\n");
        builder.append(context.getString(R.string.codigos));
        builder.append(":\n").append(codTerritorios.replace(",","\n"));

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, builder.toString());

        getContext().startActivity(Intent.createChooser(shareIntent, context.getString(R.string.enviar_dados_para)));

    }

}