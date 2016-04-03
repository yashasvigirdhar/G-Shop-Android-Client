package com.walmart.gshop.adapters;

/**
 * Created by schatu2 on 4/2/16.
 */
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.walmart.gshop.R;
import com.walmart.gshop.activities.ProductDetailActivity;
import com.walmart.gshop.models.Product;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ProductViewHolder> implements View.OnClickListener {

    @Override
    public void onClick(View v) {

    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView productName;
        TextView productDescription;
        TextView productPrice;
        ImageView productPhoto;


        ProductViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            productName = (TextView)itemView.findViewById(R.id.product_name);
            productDescription = (TextView)itemView.findViewById(R.id.product_description);
            productPrice = (TextView)itemView.findViewById((R.id.product_price));
            productPhoto = (ImageView)itemView.findViewById(R.id.product_photo);
        }
    }

    List<Product> products;
    Context context;

    public RVAdapter(List<Product> products, Context c)
    {
        this.products = products;
        this.context = c;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_main, viewGroup, false);
        ProductViewHolder pvh = new ProductViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final ProductViewHolder productViewHolder, int i) {
        productViewHolder.productName.setText(products.get(i).name);
        productViewHolder.productDescription.setText(products.get(i).description);
        productViewHolder.productPrice.setText(products.get(i).price);
        productViewHolder.productPhoto.setImageResource(products.get(i).photoId);

        productViewHolder.productDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("productName", productViewHolder.productName.getText().toString());
                intent.putExtra("productDescription", productViewHolder.productDescription.getText().toString());
                intent.putExtra("productPrice", productViewHolder.productPrice.getText().toString());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}