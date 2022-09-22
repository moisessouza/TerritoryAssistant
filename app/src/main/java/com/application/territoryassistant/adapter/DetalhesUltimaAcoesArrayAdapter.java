package com.application.territoryassistant.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.core.content.FileProvider;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.application.territoryassistant.R;
import com.application.territoryassistant.bd.DesignacaoDBHelper;
import com.application.territoryassistant.bd.TerritorioDBHelper;
import com.application.territoryassistant.bd.UltimaAcoesDBHelper;
import com.application.territoryassistant.designar.vo.DesignacaoVO;
import com.application.territoryassistant.helper.ToastHelper;
import com.application.territoryassistant.manager.FotoManager;
import com.application.territoryassistant.territorios.vo.TerritorioVO;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetalhesUltimaAcoesArrayAdapter extends ArrayAdapter<UltimaAcoesDBHelper.UltimaAcaoVO> {

        Context context;
        Map<UltimaAcoesDBHelper.UltimaAcaoVO, Integer> idMap = new HashMap<>();

        final DesignacaoDBHelper dbDesignacao;
        final TerritorioDBHelper dbTerritorio;

        public DetalhesUltimaAcoesArrayAdapter(Context context, int textViewResourceId,
                                               List<UltimaAcoesDBHelper.UltimaAcaoVO> ultimaAcaoVOs) {
            super(context, textViewResourceId, ultimaAcaoVOs);
            this.context = context;

            dbDesignacao = new DesignacaoDBHelper(context);
            dbTerritorio = new TerritorioDBHelper(context);

            for (UltimaAcoesDBHelper.UltimaAcaoVO vo:ultimaAcaoVOs) {
                idMap.put(vo, vo.getId());
            }
        }

        @Override
        public long getItemId(int position) {
            UltimaAcoesDBHelper.UltimaAcaoVO item = getItem(position);
            return idMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final UltimaAcoesDBHelper.UltimaAcaoVO vo = getItem(position);

            final String codAcao = vo.getCodAcao();
            String codTerritorios = vo.getCodTerritorios();
            String nome = vo.getNome();
            String dataInicioStr = vo.getDataInicioStr();
            String dataFimStr = vo.getDataFimStr();

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.list_detalhes_ultima_acao, parent, false);
            rowView.setTag(vo);

            TextView v = (TextView)rowView.findViewById(R.id.txt_nome_dirigente);
            v.setText(nome);

            v = (TextView)rowView.findViewById(R.id.txt_territorios);
            v.setText(codTerritorios);

            v = (TextView)rowView.findViewById(R.id.txt_acao);
            if ("D".equals(codAcao)){
                v.setText(context.getString(R.string.designacao));
                v = (TextView)rowView.findViewById(R.id.lab_data);
                v.setText(context.getString(R.string.data_inicio));
                v = (TextView)rowView.findViewById(R.id.txt_data);
                v.setText(dataInicioStr);
            } else {
                v.setText(context.getString(R.string.devolucao));
                v = (TextView)rowView.findViewById(R.id.lab_data);
                v.setText(context.getString(R.string.data_fim));
                v = (TextView)rowView.findViewById(R.id.txt_data);
                v.setText(dataFimStr);
            }

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupMenu popup = new PopupMenu(context, v);
                    popup.inflate(R.menu.list_ultimas_acoes_menu);

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {
                                case R.id.menu_enviar_mensagem:
                                    if ("D".equals(codAcao)) {
                                        enviarDesignacao(vo);
                                    } else {
                                        enviarDevolucao(vo);
                                    }
                                    return true;
                                case R.id.menu_marcar_registros:
                                    List<TerritorioVO> territorioVOs = dbTerritorio.buscarTerritoriosPorCod(vo.getCodTerritorios());
                                    for (TerritorioVO territorio : territorioVOs) {
                                        List<DesignacaoVO> designacaoVOs = dbDesignacao.buscarDesignacoesTerritorioAberto(territorio.getCod());
                                        for (DesignacaoVO designacao : designacaoVOs) {
                                            dbDesignacao.marcarRegistro(designacao);
                                        }
                                    }
                                    ToastHelper.toast(context, context.getString(R.string.registros_marcados_aba_designados));
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });

                    popup.show();

                }
            });

            return rowView;
        }

    private void enviarDevolucao(UltimaAcoesDBHelper.UltimaAcaoVO vo) {

        TerritorioDBHelper dbTerritorio = new TerritorioDBHelper(getContext());

        List<TerritorioVO> territorioVOs = dbTerritorio.buscarTerritoriosPorCod(vo.getCodTerritorios());
        StringBuilder builder = new StringBuilder();

        builder.append(context.getString(R.string.territorio_devolvido)).append(":\n");
        builder.append(context.getString(R.string.codigo)).append(":").append("\n");

        for (TerritorioVO territorioVO : territorioVOs){
            builder.append(territorioVO.getCod()).append("\n");
        }

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, builder.toString());

        getContext().startActivity(Intent.createChooser(shareIntent, context.getString(R.string.enviar_dados_para)));

    }

    private void enviarDesignacao(UltimaAcoesDBHelper.UltimaAcaoVO vo) {

        TerritorioDBHelper dbTerritorio = new TerritorioDBHelper(getContext());

        List<TerritorioVO> territorioVOs = dbTerritorio.buscarTerritoriosPorCod(vo.getCodTerritorios());

        ArrayList<Uri> fotos = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        StringBuilder obs = new StringBuilder();

        builder.append(context.getString(R.string.territorios_designados_data));
        builder.append(":").append(vo.getDataInicioStr()).append("\n");
        builder.append(context.getString(R.string.codigos)).append(":").append("\n");

        for (TerritorioVO territorioVO : territorioVOs){

            builder.append(territorioVO.getCod()).append("\n");

            if (territorioVO.getFotoPath() != null &&
                    !territorioVO.getFotoPath().isEmpty()){

                String fotoPath = territorioVO.getFotoPath();
                File fileIn = new File(fotoPath);
                if (!fileIn.exists()){
                    File fotosDirs = FotoManager.instance().createOrReturnDirectoryPhotos();
                    fileIn = new File(fotosDirs, fotoPath.substring(fotoPath.lastIndexOf(File.separator) + 1));
                }

                if (fileIn.exists()) {
                    Uri u = FileProvider.getUriForFile(getContext(), "com.example.myapp.fileprovider", fileIn);
                    fotos.add(u);
                }
            }

            if (territorioVO.getObservacoes() != null &&
                    !territorioVO.getObservacoes().isEmpty()) {
                obs.append(territorioVO.getCod()).append(":").append("\n");
                obs.append(territorioVO.getObservacoes()).append("\n\n");
            }
        }

        if (!obs.toString().isEmpty()){
            builder.append("\n").append(context.getString(R.string.observacoes)).append(":").append("\n");
            builder.append(obs);
        }

        Intent shareIntent = new Intent();
        if (!fotos.isEmpty()){
            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fotos);
            shareIntent.setType("image/jpeg");
        } else {
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
        }

        shareIntent.putExtra(Intent.EXTRA_TEXT, builder.toString());

        getContext().startActivity(Intent.createChooser(shareIntent, context.getString(R.string.enviar_dados_para)));

    }

}