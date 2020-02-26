package com.curiocodes.i_volunteer.AddOn;

import android.content.Context;
import android.widget.Toast;

public class General {
    public static void toast(Context context, String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
}
