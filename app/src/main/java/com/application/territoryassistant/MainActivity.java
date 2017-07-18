package com.application.territoryassistant;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.application.territoryassistant.about.AboutActivity;
import com.application.territoryassistant.adapter.DetalhesDesignadosArrayAdapter;
import com.application.territoryassistant.adapter.DetalhesDirigentesArrayAdapter;
import com.application.territoryassistant.adapter.DetalhesUltimaAcoesArrayAdapter;
import com.application.territoryassistant.bd.DBHelper;
import com.application.territoryassistant.bd.DesignacaoDBHelper;
import com.application.territoryassistant.bd.DirigenteDBHelper;
import com.application.territoryassistant.bd.GrupoDBHelper;
import com.application.territoryassistant.bd.TerritorioDBHelper;
import com.application.territoryassistant.bd.UltimaAcoesDBHelper;
import com.application.territoryassistant.configuracoes.ConfiguracoesActivity;
import com.application.territoryassistant.designar.DesignarActivity;
import com.application.territoryassistant.designar.VerDesignadosActivity;
import com.application.territoryassistant.designar.vo.DesignacaoVO;
import com.application.territoryassistant.dirigentes.DirigentesActivity;
import com.application.territoryassistant.dirigentes.vo.DirigentesVO;
import com.application.territoryassistant.grupos.GruposActivity;
import com.application.territoryassistant.helper.ToastHelper;
import com.application.territoryassistant.sugerir.SugerirActivity;
import com.application.territoryassistant.territorios.TerritoriosActivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // -----
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //-------

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_configuracoes) {
            Intent intent = new Intent(MainActivity.this, ConfiguracoesActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dirigentes) {
            goToDirigentes();
        } else if (id == R.id.nav_territorios) {
            goToTerritorios();
        } else if (id == R.id.nav_grupos) {
            goToGrupos();
        } else if (id == R.id.nav_designar_territorios) {
            goToDesignar();
        } else if (id == R.id.nav_territorios_designados) {
            goToVerDesignados();
        } else if (id == R.id.nav_sugerir) {
            goToSugerir();
        } else if(id == R.id.nav_exportar_dados){
            goToExportarDados();
        } else if (id == R.id.nav_importar_dados) {
            goToImortarDados();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void goToExportarDados() {

        try {

            File src = new File("/data/data/com.application.territoryassistant/databases/territories.db");
            File dst = createOrReturnFile();

            DBHelper.copy(src, dst);

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(dst));
            shareIntent.setType("application/octet-stream");
            startActivityForResult(Intent.createChooser(shareIntent, getString(R.string.enviar_dados_para)),2);

        } catch (Exception e) {
            ToastHelper.toast(this, getString(R.string.erro_exportar_dados));
            e.printStackTrace();
        }
    }

    public File createOrReturnFile () throws IOException {
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "TerritoryAssistant" + File.separator);

        if (!folder.exists()){
            folder.mkdir();
        }

        File bkp = new File(folder, "territories.db");

        if (!bkp.exists()) {
            bkp.createNewFile();
        }

        return bkp;
    }


    private void goToImortarDados() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

            if (resultCode == RESULT_OK) {
                try {
                    Uri uri = data.getData();
                    DBHelper db = new DBHelper(this);
                    InputStream is = getContentResolver().openInputStream(uri);
                    db.importDatabase(is);
                    ToastHelper.toast(this, getString(R.string.dados_importados));
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e){
                    ToastHelper.toast(this,getString(R.string.erro_importar_dados));
                    e.printStackTrace();
                }
            }

        } else if (requestCode == 2){
            if (resultCode == RESULT_OK) {
                ToastHelper.toast(this, getString(R.string.dados_exportados));
            }
        } else if (requestCode == 3) {
            if (resultCode == RESULT_OK){
                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);
                mViewPager.setAdapter(mSectionsPagerAdapter);
            }
        }
    }

    private void goToGrupos() {
        Intent intent = new Intent(MainActivity.this, GruposActivity.class);
        startActivity(intent);
    }

    private void goToTerritorios() {

        GrupoDBHelper dbGrupo = new GrupoDBHelper(this);

        boolean possuiGrupos = dbGrupo.possuiGrupo();
        if (possuiGrupos) {
            Intent intent = new Intent(MainActivity.this, TerritoriosActivity.class);
            startActivity(intent);
        } else {
            ToastHelper.toast(this, getString(R.string.necessario_registrar_grupos));
        }
    }

    private void goToDirigentes() {
        Intent intent = new Intent(MainActivity.this, DirigentesActivity.class);
        startActivity(intent);
    }

    private void goToDesignar() {

        TerritorioDBHelper dbTerritorio = new TerritorioDBHelper(this);
        DirigenteDBHelper dbDirigente = new DirigenteDBHelper(this);

        boolean possuiTerritorio = dbTerritorio.possuiTerritoriosCadastrado();
        boolean possuiDirigente = dbDirigente.possuiDirigentesCadastrado();

        if (possuiTerritorio && possuiDirigente) {
            Intent intent = new Intent(MainActivity.this, DesignarActivity.class);
            startActivity(intent);
        } else {
            ToastHelper.toast(this, getString(R.string.necessario_cadastrar_dirigentes_territorios));
        }

    }

    private void goToVerDesignados() {
        Intent intent = new Intent(MainActivity.this, VerDesignadosActivity.class);
        startActivity(intent);
    }

    private void goToSugerir() {

        TerritorioDBHelper dbTerritorio = new TerritorioDBHelper(this);
        DirigenteDBHelper dbDirigente = new DirigenteDBHelper(this);

        boolean possuiTerritorio = dbTerritorio.possuiTerritoriosCadastrado();
        boolean possuiDirigente = dbDirigente.possuiDirigentesCadastrado();

        if (possuiTerritorio && possuiDirigente) {
            Intent intent = new Intent(MainActivity.this, SugerirActivity.class);
            startActivity(intent);
        } else {
            ToastHelper.toast(this, getString(R.string.necessario_cadastrar_dirigentes_territorios));
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Context context;

        public SectionsPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1, context);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return context.getString(R.string.designados);
                case 1:
                    return context.getString(R.string.dirigentes);
                case 2:
                    return context.getString(R.string.ultimas_acoes);
            }
            return null;
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
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, Context context) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView;

            int secao = getArguments().getInt(ARG_SECTION_NUMBER);

            if (secao == 1) {
                rootView = criarTelaDesignados(inflater, container, savedInstanceState);
            } else if (secao == 2) {
                rootView = criarTelaDirigentes(inflater, container, savedInstanceState);
            } else {
                rootView = criarTelaUltimaAcoes(inflater, container, savedInstanceState);
            }
            return rootView;
        }

        private View criarTelaUltimaAcoes(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detalhes_ultimas_acoes, container, false);
            ListView list = (ListView) rootView.findViewById(R.id.list_detalhes_ultima_acoes);

            UltimaAcoesDBHelper dbUltimaAcoes = new UltimaAcoesDBHelper(getContext());

            List<UltimaAcoesDBHelper.UltimaAcaoVO> ultimaAcaoVOs = dbUltimaAcoes.recuperarUltimasAcoes(20);
            list.setAdapter(new DetalhesUltimaAcoesArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, ultimaAcaoVOs));

            return rootView;

        }

        private View criarTelaDesignados(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            final DesignacaoDBHelper dbDesignacao = new DesignacaoDBHelper(getActivity());
            List<DesignacaoVO> listaDesignacao = dbDesignacao.buscarDesignacoesTerritorioAberto(null);

            View rootView = null;

            if (!listaDesignacao.isEmpty()) {

                rootView = inflater.inflate(R.layout.fragment_detalhes_designados, container, false);
                final ListView list = (ListView) rootView.findViewById(R.id.list_detalhes_designados);


                list.setAdapter(new DetalhesDesignadosArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, listaDesignacao));

                EditText filtro = (EditText)rootView.findViewById(R.id.txt_filtro);


                filtro.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        List<DesignacaoVO> listaDesignacao = dbDesignacao.buscarDesignacoesTerritorioAberto(s.toString(), true);
                        list.setAdapter(new DetalhesDesignadosArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, listaDesignacao));
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

            } else {
                rootView = inflater.inflate(R.layout.fragment_detalhes_sem_designados, container, false);
            }

            return rootView;
        }

        private View criarTelaDirigentes(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detalhes_dirigentes, container, false);
            ListView list = (ListView) rootView.findViewById(R.id.list_detalhes_dirigentes);

            DesignacaoDBHelper dbDesignacao = new DesignacaoDBHelper(getActivity());
            List<DesignacaoVO> listaDesignacao = dbDesignacao.buscarDesignacoesTerritorioAberto(null);
            if (listaDesignacao != null && !listaDesignacao.isEmpty()) {
                List<Integer> idsDirigentes = new ArrayList<>();
                for (DesignacaoVO designacaoVO : listaDesignacao) {
                    idsDirigentes.add(designacaoVO.getIdDirigente());
                }

                DirigenteDBHelper dbDirigentes = new DirigenteDBHelper(getActivity());
                List<DirigentesVO> dirigentesVOs = dbDirigentes.buscarDirigentesPorId(idsDirigentes.toArray(new Integer[]{}));

                TerritorioDBHelper dbTerritorio = new TerritorioDBHelper(getActivity());

                for (DirigentesVO dirigentesVO : dirigentesVOs) {
                    List<String> codTerritorios = dbTerritorio.buscarTerritoriosDesignadosParaDirigente(dirigentesVO.getId());
                    StringBuilder builder = new StringBuilder();

                    boolean isFist = true;

                    for (String cod : codTerritorios) {
                        if (isFist) {
                            builder.append(cod);
                            isFist = false;
                        } else {
                            builder.append(",").append(cod);
                        }
                    }

                    dirigentesVO.setCodsTerritorio(builder.toString());

                }

                list.setAdapter(new DetalhesDirigentesArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, dirigentesVOs));
            }

            return rootView;
        }

    }

}
