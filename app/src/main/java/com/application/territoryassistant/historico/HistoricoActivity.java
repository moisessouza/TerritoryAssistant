package com.application.territoryassistant.historico;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.application.territoryassistant.R;
import com.application.territoryassistant.bd.DesignacaoDBHelper;
import com.application.territoryassistant.bd.TerritorioDBHelper;
import com.application.territoryassistant.designar.vo.DesignacaoVO;
import com.application.territoryassistant.territorios.vo.TerritorioVO;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

public class HistoricoActivity extends AppCompatActivity  {

    final TerritorioDBHelper dbTerritorio = new TerritorioDBHelper(this);

    private SectionsPagerAdapterHistorico mSectionsPagerAdapterHistorico;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        configurarAcoes();

    }

    private Integer buscarPosicaoTerritorio(List<TerritorioVO> territorioVOs, TerritorioVO territorioVOInicial){

        Integer posicao = 0;

        if (territorioVOs != null && !territorioVOs.isEmpty()){
            for (int p = 0; p < territorioVOs.size(); p++){
                if (territorioVOInicial.getId().equals(territorioVOs.get(p).getId())){
                    posicao = p;
                    break;
                }
            }
        }

        return posicao;
    }

    private void configurarAcoes() {
        List<TerritorioVO> territorioVOs = dbTerritorio.buscarTerritorios();
        TerritorioVO territorioVOInicial = (TerritorioVO)getIntent().getExtras().getSerializable("TerritorioVO");

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapterHistorico = new SectionsPagerAdapterHistorico(getSupportFragmentManager(), territorioVOs);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapterHistorico);

        mViewPager.setCurrentItem(buscarPosicaoTerritorio(territorioVOs, territorioVOInicial));
    }

    public class SectionsPagerAdapterHistorico extends FragmentStatePagerAdapter {

        final List<TerritorioVO> territorioVOs;

        public SectionsPagerAdapterHistorico(FragmentManager fm, List<TerritorioVO> territorioVOs) {
            super(fm);
            this.territorioVOs = territorioVOs;
        }

        @Override
        public Fragment getItem(int position) {
            TerritorioVO territorioVO = territorioVOs.get(position);
            return PlaceholderFragment.newInstance(territorioVO, getBaseContext());
        }

        @Override
        public int getCount() {
            return territorioVOs.size();
        }

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_TERRITORIO = "section_territorio";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(TerritorioVO territorioVO, Context context) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putSerializable(ARG_SECTION_TERRITORIO, territorioVO);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            TerritorioVO territorioVO = (TerritorioVO)getArguments().getSerializable(ARG_SECTION_TERRITORIO);
            return criarTelaHistorico(territorioVO, inflater, container, savedInstanceState);
        }

        private View criarTelaHistorico(TerritorioVO territorioVO, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_historico, container, false);

            TextView mCodTerritorio =(TextView)rootView.findViewById(R.id.txt_cod_territorio);
            mCodTerritorio.setText(territorioVO.getCod());

            DesignacaoDBHelper dbDesignacao = new DesignacaoDBHelper(getContext());
            List<DesignacaoVO> designacaoVOs = dbDesignacao.buscarDesignacaoPorIdTerritorio(territorioVO.getId());

            ListView list = (ListView)rootView.findViewById(R.id.list_historico);
            list.setAdapter(new HistoricoArrayAdapter(getContext(), 0, designacaoVOs));

            return rootView;
        }

    }

    private static class HistoricoArrayAdapter extends ArrayAdapter<DesignacaoVO> {

        List<DesignacaoVO> designacaoVOs;
        Context context;

        final DesignacaoDBHelper dbDesignacao = new DesignacaoDBHelper(getContext());

        public HistoricoArrayAdapter(Context context, int resource, List<DesignacaoVO> designacaoVOs) {
            super(context, resource, designacaoVOs);

            this.designacaoVOs = designacaoVOs;
            this.context = context;
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final DesignacaoVO designacaoVO = getItem(position);

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.list_historico, parent, false);

            TextView tipo = (TextView)view.findViewById(R.id.txt_tipo);
            TextView nomeDirigente = (TextView)view.findViewById(R.id.txt_nome_dirigente);
            TextView dataInicio = (TextView)view.findViewById(R.id.txt_data_inicio);
            TextView dataFim = (TextView)view.findViewById(R.id.txt_data_fim);

            String tipoStr = designacaoVO.getTipo();

            if ("M".equals(tipoStr)){
                tipoStr = getContext().getString(R.string.type1);
            } else {
                tipoStr = getContext().getString(R.string.type2);
            }

            tipo.setText(tipoStr);
            nomeDirigente.setText(designacaoVO.getNomeDirigente());
            dataInicio.setText(converterData(designacaoVO.getDataInicio()));
            dataFim.setText(converterData(designacaoVO.getDataFim()));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupMenu popupMenu = new PopupMenu(getContext(), v);
                    popupMenu.inflate(R.menu.list_historico_menu);

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {
                                case R.id.menu_deletar:
                                    new AlertDialog.Builder(getContext())
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setMessage(getContext().getString(R.string.tem_certeza_deletar_registro))
                                            .setPositiveButton(getContext().getString(R.string.sim), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dbDesignacao.deletarDesignacao(designacaoVO.getId());
                                                    HistoricoArrayAdapter.this.designacaoVOs.remove(designacaoVO);
                                                    HistoricoArrayAdapter.this.notifyDataSetChanged();
                                                }

                                            })
                                            .setNegativeButton(getContext().getString(R.string.nao), null)
                                            .show();
                                    return true;
                            }

                            return false;
                        }
                    });
                    popupMenu.show();

                }
            });

            return view;
        }

        public String converterData(Long dataLong){

            if (dataLong != null) {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(dataLong);

                DateFormat df = DateFormat.getDateInstance();
                String data = df.format(c.getTime());

                return data;
            } else {
                return null;
            }
        }

    }

}