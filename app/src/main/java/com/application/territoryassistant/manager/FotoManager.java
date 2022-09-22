package com.application.territoryassistant.manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.application.territoryassistant.MainActivity;
import com.application.territoryassistant.helper.FotoHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by moi09 on 09/03/2016.
 */
public class FotoManager {

    private static FotoManager instance;

    Map<String, Bitmap> thumbsMap = new HashMap<>();

    static {
        instance = new FotoManager();
    }

    private FotoManager (){}

    public Bitmap getRawImage(String rawPath){
        return BitmapFactory.decodeFile(rawPath);
    }

    public Bitmap getThumbImage(String rawPath) {

        try {

            if (thumbsMap.containsKey(rawPath)) {
                return thumbsMap.get(rawPath);
            } else {

                File thumbDirs = createOrReturnDirectoryThumb();
                File thumbPath = new File(thumbDirs, rawPath.substring(rawPath.lastIndexOf(File.separator) + 1));

                Bitmap thumb = null;

                if (thumbPath.exists()){
                    thumb = BitmapFactory.decodeFile(thumbPath.getAbsolutePath());
                } else {
                    thumb = null;

                    File f = new File(rawPath);
                    if (f.exists()) {
                        thumb = FotoHelper.decodeSampledBitmapFromFile(rawPath, 50, 50);
                    } else {
                        File fotosDirs = createOrReturnDirectoryPhotos();
                        File fotoPath = new File(fotosDirs, rawPath.substring(rawPath.lastIndexOf(File.separator) + 1));
                        if (fotoPath.exists()){
                            thumb = FotoHelper.decodeSampledBitmapFromFile(fotoPath.getPath(), 50, 50);
                        }
                    }
                    if (thumb != null) {
                        thumb.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(thumbPath));
                    }
                }

                thumbsMap.put(rawPath, thumb);
                return thumb;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static FotoManager instance() {
        return instance;
    }

    private File createOrReturnDirectoryThumb () throws IOException {

        File folder = new File(MainActivity.getAppContext().getExternalFilesDir("TerritoryAssistant"), "Thumbs");

        if (!folder.exists()){
            folder.mkdirs();
        }

        return folder;

    }

    public File createOrReturnDirectoryPhotos () {

        File folder = new File(MainActivity.getAppContext().getExternalFilesDir("TerritoryAssistant"),"Fotos");

        if (!folder.exists()){
            folder.mkdirs();
        }

        return folder;

    }

}
