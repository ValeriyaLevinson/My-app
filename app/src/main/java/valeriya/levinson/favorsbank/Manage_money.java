package valeriya.levinson.favorsbank;

import java.util.ArrayList;

public class Manage_money {

    private ArrayList<Favors> AllFavors;
    private int Positive_balance;
    private int Negative_balance;
    private int balance;

    public Manage_money(ArrayList<Favors> favors) {
        AllFavors = favors;
        Positive_balance= 0;
        Negative_balance= 0;
        balance = 0;

    }

    public int getBalance() {
        return balance;
    }


    public int getNegative_balance() {
        return Negative_balance;
    }

    public int getPositive_balance() {
        return Positive_balance;
    }

    public ArrayList<Favors> getFavors() {
        return AllFavors;
    }

    public  int Find_balance()
    {
       for (int i=0; i<AllFavors.size(); i++) {
           if (this.AllFavors.get(i).getSum() > 0)
           {
               this.Positive_balance += this.AllFavors.get(i).getSum();
           }

           if (this.AllFavors.get(i).getSum() < 0)
           {
               this.Negative_balance += this.AllFavors.get(i).getSum();
           }
       }

       this.balance=this.Negative_balance+this.Positive_balance;

        return balance;
    }

}
