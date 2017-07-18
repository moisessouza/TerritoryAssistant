package com.application.territoryassistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.application.territoryassistant.R;
import com.application.territoryassistant.grupos.vo.GrupoVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpinnerGrupoArrayAdapter extends ArrayAdapter<GrupoVO> {

        Map<GrupoVO, Integer> idMap = new HashMap<>();
        Context context;

        public SpinnerGrupoArrayAdapter(Context context, int textViewResourceId,
                                       List<GrupoVO> grupoVOs) {
            super(context, textViewResourceId,grupoVOs);

            this.context = context;

            for (GrupoVO vo:grupoVOs) {
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

            GrupoVO vo = (GrupoVO)getItem(position);

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.spinner_grupos, parent, false);

            TextView textView = (TextView) rowView.findViewById(R.id.nome_grupo);
            textView.setTag(vo);

            textView.setText(vo.getNome());

            return rowView;

        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            GrupoVO vo = (GrupoVO)getItem(position);

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            CheckedTextView textView = (CheckedTextView)inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
            textView.setTag(vo);

            textView.setText(vo.getNome());

            return textView;

        }
    }