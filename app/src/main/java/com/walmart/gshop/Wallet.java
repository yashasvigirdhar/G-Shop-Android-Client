package com.walmart.gshop;


/**
 * Created by schatu2 on 4/3/16.
 */
public class Wallet {
    public static double amount = 100000;

    public double deductMoney(double money){
        if(amount >= money){
            amount -= money;
            return amount;
        }
        else {
            return -1;
        }

    }

    public double addMoney(double money){
        amount += money;
        return amount;
    }
}