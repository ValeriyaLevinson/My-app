package valeriya.levinson.favorsbank;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class BalanceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //הרכיבים על המסך
    TextView benefit;
    TextView loss;
    TextView balance;
    //הקלאס שמטפל בכסף
    Manage_money mng;
    int balance_money;
    //רשימת החובות כדי לשלוח לקלאס שמטפל בכסף
    FavorsOpenHelper foh;
    ArrayList<Favors> allFavors;
    //בחירת סוג המטבע
    Dialog Currency_dialog;
    Button choosecurrency;
    ArrayList<String>coinstype;
    CurrencyType class_currencytype;
    Spinner spinner;
    Button ok;
    String Choosen_type;
    //שמירת סוג המטבעות
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        benefit = findViewById(R.id.profit);
        loss = findViewById(R.id.loss);
        balance = findViewById(R.id.totalbalance);
        choosecurrency = findViewById(R.id.button_currency);
        sp = getSharedPreferences("details", 0);
        //הוצאת סוג מטבעות למקרה ויש
        Choosen_type = sp.getString("choosen_currency", null);

        choosecurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//הוצאת הרשימה של המטבעות מהקלאס שבו הם נמצאים
                class_currencytype = new CurrencyType();
                coinstype = class_currencytype.getCurrency();

                createDialogCurrency();
                //אדאפטר שופך לספינר את רשימת המטבעות
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(BalanceActivity.this, android.R.layout.simple_list_item_single_choice, coinstype);
                spinner.setAdapter(dataAdapter);
                //spiner click listener
                spinner.setOnItemSelectedListener(BalanceActivity.this);
                //בדיקה אם יש סוג מטבעות שכבר נבחר, אם כן הספינר יראה אותו
                if (Choosen_type!= null)
                {
                    spinner.setSelection(((ArrayAdapter)spinner.getAdapter()).getPosition(Choosen_type));
                }
//בחירת סוג מטבע חדש
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("choosen_currency", Choosen_type);
                        editor.commit();
                        //להראות את היתרה עם הסוג החדש שנבחר
                        ShowBalance(Choosen_type);
                        Currency_dialog.dismiss();
                    }
                });


            }
        });

//להראות את היתרה עם הסוג השמור
        ShowBalance(Choosen_type);

    }



    public void ShowBalance( String Choosen_type1)
    {

        foh = new FavorsOpenHelper(this);
        foh.open();
        allFavors = foh.getAllFavors();
        foh.close();
        //---------------------------------------------
        mng = new Manage_money(allFavors);
        balance_money = mng.Find_balance();
//למקרה והמשתמש לא בחר בסוג מטבע
        if(Choosen_type1 == null) {
            benefit.setText(String.valueOf(mng.getPositive_balance()));
            loss.setText(String.valueOf(mng.getNegative_balance()));


            balance.setText(String.valueOf(balance_money));
        }
//למקרה ובחר בסוג מטבע כלשהו
        else if (Choosen_type1 != null)
        {
            String result = String.valueOf(mng.getPositive_balance());
            benefit.setText(result + " " + Choosen_type1);

            result = String.valueOf(mng.getNegative_balance());
            loss.setText(result + " " + Choosen_type1);

            result = String.valueOf(balance_money);
            balance.setText(result + " " + Choosen_type1);

        }
        if (mng.Find_balance() < 0) {
            balance.setTextColor(Color.RED);
        }

        if (mng.Find_balance() > 0) {
            balance.setTextColor(Color.GREEN);
        }

    }
//דיאלוג לבחירת סוג מטבע עם ספינר
    public void createDialogCurrency()
    {

        Currency_dialog= new Dialog(this);
        Currency_dialog.setContentView(R.layout.currency_dialog);
        Currency_dialog.setTitle("currency type");
        Currency_dialog.setCancelable(true);
        spinner = Currency_dialog.findViewById(R.id.dialod_currency_spinner);
        ok = Currency_dialog.findViewById(R.id.dialog_currency_ok);
        Currency_dialog.show();


    }
//הבחירה בספינר נשמרת
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Choosen_type = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
