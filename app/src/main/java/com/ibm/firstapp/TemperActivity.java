package com.ibm.firstapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TemperActivity extends AppCompatActivity{
    EditText inp;
    TextView out;
    public double x;
    public double y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temper);

        inp = (EditText)findViewById(R.id.inpTemper);
        out = (TextView)findViewById(R.id.showTemper);

        Button btn = (Button)findViewById(R.id.btnTemper);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = inp.getText().toString();
                x = Double.parseDouble(str);
                y=x*9/5+32;
                out.setText("结果为:"+y);
            }
        });
    }
}
