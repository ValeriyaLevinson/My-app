package valeriya.levinson.favorsbank;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
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

public class UpdateActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText fullname;
    Button firstdate, lastdate;
    FavorsOpenHelper foh;
    Favors f;
    Spinner spinner_update;
    List<String> categories;
    EditText sum;
    Button button_plus;
    Button button_minus;
    Button update;
    Long Id;

    Button button_pressed;
    String chosen_category ;
    int sumfavor;
    //אם מעדכנים חובה עם סכום שלילי
    Boolean check = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        fullname= findViewById(R.id.edittext_update_name);
        firstdate= findViewById(R.id.button_update_firstdate);
        lastdate= findViewById(R.id.button_update_lastdate);
        sum= findViewById(R.id.edittext_update_sum);
        button_plus=findViewById(R.id.button_update_plus);
        button_minus=findViewById(R.id.button_update_minus);
        update=findViewById(R.id.button_update_updateactivity);
        spinner_update= findViewById(R.id.spinner_update);

        final Category category= new Category();
        categories=category.getCategories();

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, categories);
        spinner_update.setAdapter(dataAdapter);
        spinner_update.setOnItemSelectedListener(this);
//בעזרת אינטנט מהמסך הראשי מקבלים את הid של החובה שרוצים לעדכן
        Intent intent = getIntent();
        Id = intent.getExtras().getLong("favors_id");
//הוצאת פרטי החובה שרוצים לעדכן ושיבוץ בכפתורים ובתיבות טקסט במסך
        foh = new FavorsOpenHelper(this);
        foh.open();
        f = foh.FindbyId(Id);
        foh.close();

        fullname.setText(f.getname());
        firstdate.setText(f.getDate());
        lastdate.setText(f.getLastdate());
        sum.setText(String.valueOf(f.getSum()));
        //-------------------------------------------------
        sumfavor=Integer.valueOf(sum.getText().toString());
        spinner_update.setSelection(((ArrayAdapter)spinner_update.getAdapter()).getPosition(f.getcategory()));
//אותו תהליך כמו במסך ההוספה רק בשביל שינוי
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
//משתנה בוליאני לבדיקה אם הסכום תקין
        button_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sum.getText().length()>0 && TextUtils.isDigitsOnly(sum.getText()))
                {
                    sumfavor = Integer.valueOf(sum.getText().toString());
                    check = true;
                }
                else
                {
                    Toast.makeText(UpdateActivity.this, "invalid input", Toast.LENGTH_SHORT).show();
                    check = false;
                }
            }
        });

        button_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sum.getText().length()>0 && TextUtils.isDigitsOnly(sum.getText()))
                {
                    sumfavor = Integer.valueOf(sum.getText().toString()) *(-1);
                    check = true;
                }
                else
                {
                    Toast.makeText(UpdateActivity.this, "invalid input", Toast.LENGTH_SHORT).show();
                    check = false;
                }
            }
        });
//בדיקה ועדכון ב database לפי הid של החובה
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name="", date1="", date2="";
               // sumfavor= Integer.valueOf(sum.getText().toString());
                //getting the full name -----------------------------------------------------------
                if(fullname.length()!=0 && !stringContainsNumber(fullname.getText().toString()))
                {
                    name = fullname.getText().toString();
                }
                else
                {
                    Toast.makeText(UpdateActivity.this, "invalid input", Toast.LENGTH_SHORT).show();
                }

                date1 = firstdate.getText().toString();
                date2 = lastdate.getText().toString();

                if(name.length()>0 && !date1.equals("first date") && !date2.equals("last date") && sumfavor!=0 && check && chosen_category.length()>0)
                {
                    f = new Favors(Id, sumfavor, name, chosen_category, date1, date2);
                    foh.open();
                    foh.updateByRow(f) ;
                    foh.close();
//החזרת תשובה למסך הראשי שהעדכון בוצע
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                    Toast.makeText(UpdateActivity.this, "the favor was updated", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    Toast.makeText(UpdateActivity.this, "check your inputs", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

//שליפת הקטגוריות שנבחרה
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        chosen_category =  parent.getItemAtPosition(position).toString();
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
        DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateActivity.this ,new UpdateActivity.SetDate(),year,month,day);
        datePickerDialog.show();
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
