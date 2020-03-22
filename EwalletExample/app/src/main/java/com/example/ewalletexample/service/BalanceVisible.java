package com.example.ewalletexample.service;

public class BalanceVisible {
    private static boolean balanceVisible = true;

    public static void SwitchBalanceVisible(){
        balanceVisible = !balanceVisible;
    }

    public static boolean IsBalanceVisible(){
        return balanceVisible;
    }
}
