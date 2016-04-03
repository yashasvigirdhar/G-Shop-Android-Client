package com.walmart.gshop.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flipkart.chatheads.ui.ChatHead;
import com.flipkart.chatheads.ui.ChatHeadContainer;
import com.flipkart.chatheads.ui.ChatHeadViewAdapter;
import com.flipkart.chatheads.ui.MaximizedArrangement;
import com.walmart.gshop.MyApplication;
import com.walmart.gshop.R;

/**
 * Created by ygirdha on 4/3/2016.
 */
public class ProductDetailActivity extends AppCompatActivity {
    TextView Name;
    TextView Description;
    TextView Price;
    ImageView photo;
    Button b1, b2;
    String productName;
    String productPrice;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail_layout);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Product Detail");
        productName = getIntent().getExtras().getString("productName");
        String productDescription = getIntent().getExtras().getString("productDescription");
        productPrice = getIntent().getExtras().getString("productPrice");

        Price = (TextView) findViewById(R.id.textView4);
        Description = (TextView) findViewById(R.id.textView5);
        Name = (TextView) findViewById(R.id.textView);
        photo = (ImageView) findViewById(R.id.imageButton);
        b1 = (Button) findViewById(R.id.button2);
        b2 = (Button) findViewById(R.id.button3);
        Name.setText(productName);
        Description.setText(productDescription);
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

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailActivity.this, CheckoutActivity.class);
                intent.putExtra("productName", productName);
                intent.putExtra("productPrice", productPrice);
                startActivity(intent);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        ((MyApplication) getApplication()).chatContainer = (ChatHeadContainer) findViewById(R.id.chat_container1);

        ((MyApplication) getApplication()).chatContainer.setViewAdapter(new ChatHeadViewAdapter() {

            @Override
            public Drawable getPointerDrawable() {
                return null;
            }

            @Override
            public View getTitleView(Object key, ChatHead chatHead) {
                return null;
            }

            @Override
            public FragmentManager getFragmentManager() {
                return getSupportFragmentManager();
            }


            @Override
            public Fragment instantiateFragment(Object key, ChatHead chatHead) {
                // return the fragment which should be shown when the arrangment switches to maximized (on clicking a chat head)
                // you can use the key parameter to get back the object you passed in the addChatHead method.
                // this key should be used to decide which fragment to show.
                if (key == "Channel") {
                    return ChannelListActivity.newInstance();
                } else {
                    return ChatActivity.newInstance(key.toString());
                }
            }

            @Override
            public Drawable getChatHeadDrawable(Object key) {
                // this is where you return a drawable for the chat head itself based on the key. Typically you return a circular shape
                // you may want to checkout circular image library https://github.com/flipkart-incubator/circular-image
                return getResources().getDrawable(R.drawable.group);

            }
        });
        // you can even pass a custom object instead of "head0"

        ((MyApplication) getApplication()).chatContainer.setArrangement(MaximizedArrangement.class, null);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((MyApplication) getApplication()).chatContainer = (ChatHeadContainer) findViewById(R.id.chat_container1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item_main clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.chat) {
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            ((MyApplication) getApplication()).setScreenShareBitmap(bitmap);
            ((MyApplication) getApplication()).setScreenShareLink(productName);
            ((MyApplication) getApplication()).chatContainer.addChatHead("Channel", false, true);
            ((MyApplication) getApplication()).chatContainer.setVisibility(View.VISIBLE);
        }

        return super.onOptionsItemSelected(item);
    }
}
