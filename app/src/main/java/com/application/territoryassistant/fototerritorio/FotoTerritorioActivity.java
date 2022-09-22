package com.application.territoryassistant.fototerritorio;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;

import com.application.territoryassistant.R;
import com.application.territoryassistant.helper.FotoHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FotoTerritorioActivity extends AppCompatActivity {

    private static final int LER_ARQUIVO = 1;
    private static final int ESCREVER_ARQUIVO = 2;
    private static final int CAMERA = 1;

    private String caminhoCameraFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto_territorio);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    LER_ARQUIVO);

        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    ESCREVER_ARQUIVO);
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA);
        }

        if (savedInstanceState != null){
            caminhoCameraFoto = savedInstanceState.getString("caminhoCameraFoto");
        }

        ImageView img  = (ImageView)findViewById(R.id.img_foto_territorio);

        Bundle extras = getIntent().getExtras();
        if (extras  != null) {
            String caminhoArquivo = extras.getString("caminhoArquivo");
            if (caminhoArquivo != null) {
                File photoFile = new File(caminhoArquivo);
                if (photoFile.exists()) {
                    Bitmap bitmap = FotoHelper.decodeSampledBitmapFromFile(caminhoArquivo, 800, 600);
                    img.setImageBitmap(bitmap);
                }
            }
        }

        FloatingActionButton fabTirarFoto = (FloatingActionButton) findViewById(R.id.fab_tirar_foto);
        fabTirarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPickImageIntent(FotoTerritorioActivity.this);
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("caminhoCameraFoto", caminhoCameraFoto);
        super.onSaveInstanceState(outState);
    }

    private void getPickImageIntent(Context context) {
        Intent chooserIntent = null;

        List<Intent> intentList = new ArrayList<>();

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File arquivo = criarArquivo();
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    FileProvider.getUriForFile(getApplicationContext(), "com.example.myapp.fileprovider", arquivo));

            caminhoCameraFoto = arquivo.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent contentIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentIntent.setType("image/*");

        intentList = addIntentsToList(context, intentList, contentIntent);
        intentList = addIntentsToList(context, intentList, takePhotoIntent);

        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),
                    getString(R.string.selecione));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }

        startActivityForResult(chooserIntent, 1);
    }

    private static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
        }
        return list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK) {

            if (data == null) {
                Intent result = new Intent();
                result.putExtra("caminhoArquivo", caminhoCameraFoto);
                setResult(RESULT_OK, result);
                finish();
            } else if (data.getData() != null) {
                InputStream is = null;
                try {
                    Bitmap imageBitmap = null;
                    Uri uri = data.getData();
                    is = getContentResolver().openInputStream(uri);
                    imageBitmap = BitmapFactory.decodeStream(is);
                    String caminhoArquivo = salvarImagem(imageBitmap, caminhoCameraFoto);
                    Intent result = new Intent();
                    result.putExtra("caminhoArquivo", caminhoArquivo);
                    setResult(RESULT_OK, result);
                    finish();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (is != null){
                        try {
                            is.close();
                        } catch (IOException e){}
                    }
                }

            } else if (data.getExtras() != null) {

                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                String caminhoArquivo = salvarImagem(imageBitmap, caminhoCameraFoto);
                Intent result = new Intent();
                result.putExtra("caminhoArquivo", caminhoArquivo);
                setResult(RESULT_OK, result);
                finish();

            }
        }

    }

    private String salvarImagem(Bitmap imageBitmap, String caminhoCameraFoto){
        FileOutputStream out = null;
        String caminho = null;

        try {

            File file = null;

            if (caminhoCameraFoto == null) {
                file = criarArquivo();
            } else {
                file = new File(caminhoCameraFoto);
            }

            caminho = file.getAbsolutePath();
            out = new FileOutputStream(file);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return caminho;

    }

    public File createOrReturnDirectory () throws IOException {

        File folder = new File(getExternalFilesDir("TerritoryAssistant"), "Fotos");

        if (!folder.exists()){
            folder.mkdirs();
        }

        return folder;
    }

    private File criarArquivo() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TERRITORIO_" + timeStamp + "_";
        File storageDir = createOrReturnDirectory();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }


}
