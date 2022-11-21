package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;

import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    int SpannedLength = 0,chipLength = 4;
    Chip createchip(String data){
        Chip chip=new Chip(this);
        chip.setText(data);
        chip.setCloseIconVisible(true);
        chip.setClickable(false);
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ch.removeView((Chip)v);
            }
        });
        return chip;
    }
    int hashcount=0;
    ChipGroup ch;
    int prev=0;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ch=findViewById(R.id.chipgroup);


        TextInputLayout Phone = findViewById(R.id.outlinedTextField);
        Phone.getEditText().setCursorVisible(false);
        Phone.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence,int start, int before, int count) {
              //Toast.makeText(getApplicationContext(), String.valueOf(charSequence.length())+" "+count, Toast.LENGTH_SHORT).show();
                if(charSequence.length()>0 && String.valueOf(charSequence.charAt(charSequence.length()-1)).equals("#")){
                    int i=charSequence.length()-2;
                    while(i>=0){
                        if(String.valueOf(charSequence.charAt(i)).equals("#")){
                            break;
                        }
                        i--;
                    }
                    if(i!=-1){
                        String data=charSequence.subSequence(i, charSequence.length()-1).toString();
                       ch.addView(createchip(data));
                        String value=charSequence.toString();
                   //   value.replace(data,"");
                        if(i==0){
                            Phone.getEditText().setText("#");
                            Phone.getEditText().setSelection(1);
                        }

                        else{
                            Phone.getEditText().setText(charSequence.subSequence(0, i)+"#");
                            Phone.getEditText().setSelection(i+1);
                        }

                    }

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}