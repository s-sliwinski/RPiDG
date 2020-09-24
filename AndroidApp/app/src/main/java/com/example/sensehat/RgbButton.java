package com.example.sensehat;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class RgbButton extends androidx.appcompat.widget.AppCompatButton {
    public RgbButton(Context context, int x, int y) {
        super(context);
        this.x=x;
        this.y=y;
    }
    public int x;
    public int y;

}
