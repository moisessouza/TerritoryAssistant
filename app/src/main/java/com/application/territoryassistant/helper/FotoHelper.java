package com.application.territoryassistant.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.application.territoryassistant.R;
import com.application.territoryassistant.manager.FotoManager;

import java.io.File;

/**
 * Created by moi09 on 06/03/2016.
 */
public class FotoHelper {

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public interface IResultado {
        void executar();
    }

    public static void showPhoto(Context context, String photoPath, final IResultado resultado){

        if (photoPath == null || photoPath.isEmpty()) {
            return;
        }

        File f = new File(photoPath);
        if (!f.exists()) {
            File fotosDirs = FotoManager.instance().createOrReturnDirectoryPhotos();
            f = new File(fotosDirs, photoPath.substring(photoPath.lastIndexOf(File.separator) + 1));
        }

        if (f.exists()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle(context.getString(R.string.foto_territorio));
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (resultado != null) {
                        resultado.executar();
                    }
                }
            });

            ImageView imageDialog = new ImageView(context);
            Bitmap bitmap = FotoHelper.decodeSampledBitmapFromFile(f.getPath(), 800, 600);

            imageDialog.setImageBitmap(bitmap);
            imageDialog.setAdjustViewBounds(true);
            builder.setView(imageDialog);
            AlertDialog d = builder.create();

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(d.getWindow().getAttributes());
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            d.show();
            d.getWindow().setAttributes(lp);
        }
    }
}