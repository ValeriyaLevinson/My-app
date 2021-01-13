package valeriya.levinson.favorsbank;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

public class InsertActiviry extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    FavorsOpenHelper foh;
    Favors f;
    Spinner spinner;
    List<String> categories;
    EditText fullname;
    Button firstdate;
    Button lastdate;
    EditText sum;
    Button button_plus;
    Button button_minus;
    Button addfavor;

    Button button_pressed;
    String chosen_category ;
    int sumfavor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_activiry);

        //מקשרים את כל הרכיבים שצריך למסך
        foh = new FavorsOpenHelper(this);

        fullname= findViewById(R.id.edittext_name);
        firstdate= findViewById(R.id.button_firstdate);
        lastdate= findViewById(R.id.button_lastdate);
        sum= findViewById(R.id.edittext_sum);
        button_plus=findViewById(R.id.button_plus);
        button_minus=findViewById(R.id.button_minus);
        addfavor=findViewById(R.id.button_add_insertactivity);
        spinner= findViewById(R.id.spinner);

        final Category category= new Category();
        categories=category.getCategories();

//האדפטר לוקח איברים מהlist ושופך אותם לspiner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, categories);
        //מקשרים לספינר את האדאפטר ומממשים אתspiner cliclk listener
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);


//הופעת הדיאלוג של הלוח שנה ומשתמשים בכפתור הנוסף כדי לשנות על כפתורי התאריכים את התאריך שנבחר
        firstdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Showdialog();
                button_pressed=firstdate;

            }
        });

        lastdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Showdialog();
               button_pressed=lastdate;
            }
        });
//שליפת הסכום ובדיקת קלט
        button_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sum.getText().length()>0 && TextUtils.isDigitsOnly(sum.getText()))
                {
                    sumfavor = Integer.valueOf(sum.getText().toString());
                }
                else
                {
                    Toast.makeText(InsertActiviry.this, "invalid input", Toast.LENGTH_SHORT).show();
                }
            }
        });

        button_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sum.getText().length()>0 && TextUtils.isDigitsOnly(sum.getText()))
                {
                    sumfavor = Integer.valueOf(sum.getText().toString()) *(-1);
                }
                else
                {
                    Toast.makeText(InsertActiviry.this, "invalid input", Toast.LENGTH_SHORT).show();
                }
            }
        });
//בדיקת קלט לכל הפרטים והוספת החובה לרשימה בdatabase
        addfavor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name="", date1="", date2="";
                //getting the full name -----------------------------------------------------------
                if(fullname.length()!=0 && !stringContainsNumber(fullname.getText().toString()))
                {
                    name = fullname.getText().toString();
                }
                else
                {
                    Toast.makeText(InsertActiviry.this, "invalid input", Toast.LENGTH_SHORT).show();
                }

                     date1 = firstdate.getText().toString();
                     date2 = lastdate.getText().toString();

                if(name.length()>0 && !date1.equals("first date") && !date2.equals("last date") && sumfavor!=0 && chosen_category.length()>0)
                {
                    long id =-1;
                    f = new Favors(id, sumfavor, name, chosen_category, date1, date2);
                    foh.open();
                    f = foh.createFavor(f);
                    foh.close();
//בשלב הזה נעשתה בדיקה והחובה הוכנסה לכן בעזרת אינטנט מעבירים למסך הראשי הודעה שההוספה התרחשה בהצלחה
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                    Toast.makeText(InsertActiviry.this, "new favor added", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    Toast.makeText(InsertActiviry.this, "check your inputs", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
    //שליפת הקטגוריה שנבחרה מהספינר

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        chosen_category = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public void Showdialog()
    {
        Calendar systemCalender = Calendar.getInstance();
        int year = systemCalender.get(Calendar.YEAR);
        int month = systemCalender.get(Calendar.MONTH);
        int day = systemCalender.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(InsertActiviry.this ,new SetDate(),year,month,day);
        datePickerDialog.show();
        datePickerDialog.setCancelable(true);
    }


    public  class SetDate implements DatePickerDialog.OnDateSetListener
    {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            monthOfYear = monthOfYear +1;

            String str = dayOfMonth + "/" + monthOfYear +"/" + year;
            button_pressed.setText(str);

        }
    }

    public boolean stringContainsNumber( String s )
    {
        return Pattern.compile( "[0-9]" ).matcher( s ).find();
    }
}
