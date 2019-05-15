package com.ibm.firstapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    TextView out;
    EditText inp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        out = (TextView)findViewById(R.id.showText);
        inp = (EditText)findViewById(R.id.inpText);

        Button btn = (Button)findViewById(R.id.btn);
        //btn.setOnClickListener(this);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("main", "onClick called...");
            }
        });
    }

    @Override
    public void onClick(View v) {
        Log.i("click", "onClick: ");

        //TextView tv = (TextView)findViewById(R.id.showText);

        //EditText inp = (EditText)findViewById(R.id.inpText);
        String str = inp.getText().toString();

        out.setText(str);
    }
}
