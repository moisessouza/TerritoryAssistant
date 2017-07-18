package com.application.territoryassistant.helper;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by moi09 on 06/03/2016.
 */
public class ToastHelper {

    public static void toast(Context context, String mensagem) {
        Toast toast = Toast.makeText(context, mensagem, Toast.LENGTH_SHORT);
        toast.show();
    }
}
