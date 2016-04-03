package com.walmart.gshop.activities;

/**
 * Created by schatu2 on 4/2/16.
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.flipkart.chatheads.ui.ChatHead;
import com.flipkart.chatheads.ui.ChatHeadContainer;
import com.flipkart.chatheads.ui.ChatHeadViewAdapter;
import com.flipkart.chatheads.ui.MaximizedArrangement;
import com.walmart.gshop.Constants;
import com.walmart.gshop.MyApplication;
import com.walmart.gshop.ProductList;
import com.walmart.gshop.R;
import com.walmart.gshop.adapters.RVAdapter;
import com.walmart.gshop.models.Product;

import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private List<Product> products;
    private RecyclerView rv;
    int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product_list);


        ((MyApplication) getApplication()).chatContainer = (ChatHeadContainer) findViewById(R.id.chat_container);

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


        rv = (RecyclerView) findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Product List");

        initializeAdapter();
    }


    private void initializeAdapter() {
        RVAdapter adapter = new RVAdapter(ProductList.productList, this);
        rv.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((MyApplication) getApplication()).chatContainer = (ChatHeadContainer) findViewById(R.id.chat_container);
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
            startActivity(new Intent(this, AccountActivity.class));
            return true;
        }

        if (id == R.id.chat) {
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            ((MyApplication) getApplication()).setScreenShareBitmap(bitmap);
            ((MyApplication) getApplication()).setScreenShareLink(Constants.PRODUCTS_LIST);
            ((MyApplication) getApplication()).chatContainer.addChatHead("Channel", false, true);
            ((MyApplication) getApplication()).chatContainer.setVisibility(View.VISIBLE);
        }

        return super.onOptionsItemSelected(item);
    }


}
