package com.walmart.gshop;

import com.walmart.gshop.models.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by schatu2 on 4/3/16.
 */
public class ProductList {
    public static List<Product> productList = new ArrayList<>(Arrays.asList(
            new Product("Hallmark", "Beautiful pantry, colorful workplace", "Rs. 20", R.drawable.hallmark),
            new Product("Supreme", "Nice and cosy pantry without a foosball table, structurally weak", "Rs. 6", R.drawable.supreme),
            new Product("Taj Mahal", "Majestic marble mausolem surrounded by lush green gardens", "Rs. 150", R.drawable.taj),
            new Product("Eiffel Tower", "Rust free wrought iron tower in the middle of Paris", "Rs. 45", R.drawable.eiffel),
            new Product("Leaning Tower of Pisa", "Beautiful bell tower in Pisa, slightly tilted", "Rs. 80", R.drawable.pisa),
            new Product("Burj Khalifa", "Very tall building surrounded by oil", "Rs. 124", R.drawable.burj),
            new Product("Iron Throne", "Very sought after, made of 1 million swords, 2 Kingsguards free", "Rs. 300", R.drawable.throne)
    ));


    public static List<Product> getProducts(){

        return productList;
    }

    public static Product getProductById(int id){
        if(id < 7){
            return productList.get(id);
        }
        return null;
    }

    public static Product getProductByName(String name){
        for(int i=0;i<productList.size();i++){
            if(productList.get(i).name.equals(name))
                return productList.get(i);
        }
        return null;
    }
}