package com.set;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import set.work.utils.ApplicationUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ApplicationUtil.openApplicationByPackageName(MainActivity.this,"com.mandalat.inscriptiondashun");

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button btn1 = (Button)findViewById(R.id.btn_1);
        Button btn2 = (Button)findViewById(R.id.btn_2);
        Button btn3 = (Button)findViewById(R.id.btn_3);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickS();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickL();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickP();
            }
        });
    }


    @Override
    public void onClick(View v) {

    }



    public void onClickS(){
                Intent intent  = new Intent("com.mandalat.inscriptiondashun.receiver.dashun_start");
                sendBroadcast(intent);
    }

    public void onClickL(){
        Intent intent  = new Intent("com.mandalat.inscriptiondashun.receiver.dashun_login");
        intent.putExtra("username","guest1");
        intent.putExtra("password","1");
        sendBroadcast(intent);
    }

    public void onClickP(){
        Bundle bundle = new Bundle();
        bundle.putString("patient_id","00320475");
        bundle.putString("username","guest1");
        bundle.putString("password","1");
        ApplicationUtil.openAppActivity(MainActivity.this,"com.mandalat.inscriptiondashun","com.mandalat.inscriptiondashun.activity.PatientMainActivity",bundle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
