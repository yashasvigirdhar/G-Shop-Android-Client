package com.walmart.gshop.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.walmart.gshop.Constants;
import com.walmart.gshop.MyApplication;
import com.walmart.gshop.R;


/**
 * Created by dev-ygirdha on 4/2/2016.
 */
public class LoginActivity extends AppCompatActivity{
    Button b1,b2;
    EditText ed1,ed2;

    TextView tx1;
    int counter = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        b1=(Button)findViewById(R.id.button);
        ed1=(EditText)findViewById(R.id.editText);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Redirecting...",Toast.LENGTH_SHORT).show();
                    SharedPreferences sp = getSharedPreferences(Constants.CHAT_PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putString(Constants.CHAT_USERNAME, ed1.getText().toString());
                     edit.apply();

                    MyApplication.getmPubNub().setUUID(ed1.getText().toString());

                    Intent i = new Intent(LoginActivity.this, ProductListActivity.class);
                    startActivity(i);
            }
        });
    }

   // @Override
  /*  public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item_main) {
        // Handle action bar item_main clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item_main.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item_main);
    }*/
}
