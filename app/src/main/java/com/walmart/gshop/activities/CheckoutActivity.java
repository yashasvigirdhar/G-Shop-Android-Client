package com.walmart.gshop.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.walmart.gshop.R;

/**
 * Created by ygirdha on 4/3/2016.
 */

public class CheckoutActivity extends AppCompatActivity {
    TextView Name, Price;
    ImageView photo;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Checkout");


        String productName = getIntent().getExtras().getString("productName");
        String productPrice = getIntent().getExtras().getString("productPrice");

        Name = (TextView) findViewById(R.id.textView6);
        Price = (TextView) findViewById(R.id.textView7);
        photo = (ImageView) findViewById(R.id.imageView);
        Name.setText(productName);
        Price.setText(productPrice);

        if (productName.equals("Hallmark")) {
            photo.setImageResource(R.drawable.hallmark);
        } else if (productName.equals("Supreme")) {
            photo.setImageResource(R.drawable.supreme);
        } else if (productName.equals("Taj Mahal")) {
            photo.setImageResource(R.drawable.taj);
        } else if (productName.equals("Eiffel Tower")) {
            photo.setImageResource(R.drawable.eiffel);
        } else if (productName.equals("Leaning Tower of Pisa")) {
            photo.setImageResource(R.drawable.pisa);
        } else if (productName.equals("Burj Khalifa")) {
            photo.setImageResource(R.drawable.burj);
        } else if (productName.equals("Iron Throne")) {
            photo.setImageResource(R.drawable.throne);
        }
    }
}
