package com.example.corpwatch;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MainScreen extends AppCompatActivity {
    int height_f = 3;
    View f = null;
    RelativeLayout layout;
    Button bt1, bt2, bt3;
    float ActiveTxt = 18f;
    float BaseTxt = 17;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        layout = (RelativeLayout)findViewById(R.id.rel);
        bt1 = findViewById(R.id.button4);
        bt2 = findViewById(R.id.button5);
        bt3 = findViewById(R.id.button6);
        lineUlt(1);
    }
    public void onClickHistory (View v) {
        lineUlt (1);

        bt1.setTextAppearance(android.R.style.TextAppearance_Material_Body2);
        bt2.setTextAppearance(android.R.style.TextAppearance_Material_Body1);
        bt3.setTextAppearance(android.R.style.TextAppearance_Material_Body1);
        bt1.setTextSize(TypedValue.COMPLEX_UNIT_SP,ActiveTxt);
        bt2.setTextSize(TypedValue.COMPLEX_UNIT_SP,BaseTxt);
        bt3.setTextSize(TypedValue.COMPLEX_UNIT_SP,BaseTxt);
    }
    public void onClickReq (View v) {
        lineUlt (2);

        bt1.setTextAppearance(android.R.style.TextAppearance_Material_Body1);
        bt2.setTextAppearance(android.R.style.TextAppearance_Material_Body2);
        bt3.setTextAppearance(android.R.style.TextAppearance_Material_Body1);
        bt2.setTextSize(TypedValue.COMPLEX_UNIT_SP,ActiveTxt);
        bt1.setTextSize(TypedValue.COMPLEX_UNIT_SP,ActiveTxt);
        bt3.setTextSize(TypedValue.COMPLEX_UNIT_SP,BaseTxt);
    }
    public void onClickTest (View v) {
        lineUlt (3);

        bt1.setTextAppearance(android.R.style.TextAppearance_Material_Body1);
        bt2.setTextAppearance(android.R.style.TextAppearance_Material_Body1);
        bt3.setTextAppearance(android.R.style.TextAppearance_Material_Body2);
        bt3.setTextSize(TypedValue.COMPLEX_UNIT_SP,ActiveTxt);
        bt2.setTextSize(TypedValue.COMPLEX_UNIT_SP,BaseTxt);
        bt1.setTextSize(TypedValue.COMPLEX_UNIT_SP,BaseTxt);
    }
    public void lineUlt  (int n) {
        if (f != null) {
            ((ViewManager)f.getParent()).removeView(f);
        }
        View v = new View(this);
        RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height_f);
        //params.setMargins(1, 5, 1, 0);
        v.setLayoutParams(params);
        v.setBackgroundResource(R.color.black);
        v.setId(R.id.reservedNamedId);
        if (n==1) {
            LinearLayout li = (LinearLayout) findViewById(R.id.layln1);
            li.addView(v);
        } else if (n==2) {
            LinearLayout li = (LinearLayout) findViewById(R.id.layln2);
            li.addView(v);
        } else {
            LinearLayout li = (LinearLayout) findViewById(R.id.layln3);
            li.addView(v);
        }
        f = v;
    }
}