package com.walmart.gshop.activities;

import android.content.Intent;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.support.v7.widget.Toolbar;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.widget.TextView;

import com.walmart.gshop.R;
import com.walmart.gshop.Wallet;

/**
 * Created by schatu2 on 4/3/16.
 */
public class WalletActivity extends AppCompatActivity{

    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.wallet);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle("GShop");

        TextView textView = (TextView)findViewById(R.id.wallet_text);
        String msg = String.format("Your wallet currently has \n\t Rs. %f", Wallet.amount);
        textView.setText(msg);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item_main clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
//            startActivity(new Intent(ProductListActivity.this, AccountActivity.class));
            startActivity(new Intent(this, AccountActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}