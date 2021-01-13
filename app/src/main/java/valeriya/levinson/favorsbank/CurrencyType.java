package valeriya.levinson.favorsbank;

import java.util.ArrayList;

public class CurrencyType
{
    ArrayList<String> currency;


    public CurrencyType()
    {
        currency= new ArrayList<>();
        currency.add("Dollar");
        currency.add("yuan");
        currency.add("euro");
        currency.add("new shekel");
        currency.add("yen");
        currency.add("zloty");
        currency.add("ruble");
        currency.add("riyal");
        currency.add("pound sterling");
        currency.add("lira");
        currency.add("Real");
        currency.add("Cupon");
        currency.add("Dinero");
        currency.add("Dong");
        currency.add("Yang");
        currency.add("Won");

    }

    public ArrayList<String> getCurrency() {
        return currency;
    }
}
