package valeriya.levinson.favorsbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.Transliterator;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FavorsOpenHelper foh;
    //options dialog
    Dialog d;
    //filtersum dialog
    Dialog filter_amount;
    Button update_dialog;
    Button delete_dialog;
    ArrayList<Favors>listOfFavors;
    ListView lv;
    //contains the favor that was long clicked in the list view
    Favors lastSelected;
    Button addfavor;
    Button Balance;
    //edit text, text view and button in the filtersum dialog
    EditText filter_amount_edittext;
    Button filter_amount_go;
    TextView filter_textview;

//the selected filter (date or sum) convert to string
    String selection;

    favours_adapter FavorsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addfavor = findViewById(R.id.button_add);
        Balance = findViewById(R.id.balanceButton);


        addfavor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this, InsertActiviry.class);
                startActivity(intent);
                //ממסך ההוספה בעזרת אינטנט חוזר מידע על האם ההוספה התרחשה בהצלחה והפונקציה המטפלת במידע החוזר ממסכים אחרים תזהה הגעת מידע ממסך ההוספה לפי קוד 1
                startActivityForResult(intent, 1);

            }
        });

        Balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this, BalanceActivity.class);
                startActivity(intent);
            }
        });


        foh = new FavorsOpenHelper(this);

        foh.open();
//list view object contains the favors in the main antivity
        lv= findViewById(R.id.lv_mainActivity);

         listOfFavors=foh.getAllFavors();
         foh.close();

        Log.i("data", "list size is " + listOfFavors.size());
//למקרה ואין חובות הקפצת הודעה
        if(listOfFavors.size()==0)
        {
             Toast.makeText(this, "no favors yet", Toast.LENGTH_SHORT).show();
        }
//יצירת מוםע של אדאפט- היווצר תאים בהתאם למספר האוביקטים ברשימה ושופך אותם לlist view
           FavorsAdapter=new favours_adapter(this,0,listOfFavors);
           lv.setAdapter(FavorsAdapter);


//למקרה של לחיצה ארוכה על אחת החובות
           lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
               @Override
               public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                   //last selected is a favors type object contains the long clicked favor in the list
                   lastSelected = FavorsAdapter.getItem(position);
                   createOptoinsDialog();
                   delete_dialog.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           foh.open();
                           //delete the favor from database by its id.
                           foh.deleteFavorByRow(lastSelected.getId());
                           foh.close();
                           //remove the favor from the adapter of the list and /////!
                           FavorsAdapter.remove(lastSelected);
                           FavorsAdapter.notifyDataSetChanged();
                           d.dismiss();

                       }
                   });
                   //אם המשתמש בוחר לעדכן את החובה מועבר למסך עדכון והפונקציהonActivityResult מחכה בעזרת intent לתשובה מהמסך אם העדכון התרחש requestcode של פעולה זו הוא 0
                   update_dialog.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           Intent i = new Intent (MainActivity.this, UpdateActivity.class);
                           i.putExtra("favors_id", lastSelected.getId());
                           startActivityForResult(i , 0);
                           d.dismiss();
                       }
                   });

                   return false;
               }
           });


    }
//יצירת התפריט על הlayout שיצרתי עבורו
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_manu, menu);
        return true;
    }
//הגדרת פעולה לכל אפשרות בתפריט
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch(item.getItemId())
        {
            case R.id.action_allfavors:
                foh = new FavorsOpenHelper(MainActivity.this);
                foh.open();
                listOfFavors = foh.getAllFavors();
                foh.close();
                refreshadapter();
                break;

            case R.id.action_bycategory:
//פונקציה היוצרת את הדיאלוג של הסינון
                createfilterAmountDialog();
                filter_amount_edittext.setHint("category");
                filter_textview.setText("enter category");
                filter_amount_go.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        foh = new FavorsOpenHelper(MainActivity.this);
                        foh.open();
                        selection = filter_amount_edittext.getText().toString();
                        listOfFavors = foh.getAllFavorsByFIlter("ctegory='" + selection + "'" , "sum ASC");
                        foh.close();
                        refreshadapter();
                        //סגירת הדיאלוג
                        filter_amount.dismiss();
                    }
                });

               break;

            case R.id.action_byname:
                createfilterAmountDialog();
                filter_amount_edittext.setHint("name");
                filter_textview.setText("enter the name");
                filter_amount_go.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        foh = new FavorsOpenHelper(MainActivity.this);
                        foh.open();
                        selection = filter_amount_edittext.getText().toString();
                        listOfFavors = foh.getAllFavorsByFIlter("fullName='" + selection + "'" , "sum ASC");
                        foh.close();
                        refreshadapter();
                        filter_amount.dismiss();
                    }
                });

                break;

            case R.id.action_byamounth:
                createfilterAmountDialog();
                filter_amount_go.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        foh = new FavorsOpenHelper(MainActivity.this);
                        foh.open();
                        selection = filter_amount_edittext.getText().toString();
                        listOfFavors = foh.getAllFavorsByFIlter("sum=" + selection , "sum ASC");
                        foh.close();
                        refreshadapter();
                        filter_amount.dismiss();
                    }
                });
            break;

            case R.id.action_lessthen:
                createfilterAmountDialog();
                filter_amount_go.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        foh = new FavorsOpenHelper(MainActivity.this);
                        foh.open();
                        selection = filter_amount_edittext.getText().toString();
                        listOfFavors = foh.getAllFavorsByFIlter("sum<" + selection , "sum ASC");
                        foh.close();
                        refreshadapter();
                        filter_amount.dismiss();
                    }
                });
            break;

            case R.id.action_morethan:
                createfilterAmountDialog();
                filter_amount_go.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        foh = new FavorsOpenHelper(MainActivity.this);
                        foh.open();
                        selection = filter_amount_edittext.getText().toString();
                        listOfFavors = foh.getAllFavorsByFIlter("sum>" + selection , "sum ASC");
                        foh.close();
                        refreshadapter();
                        filter_amount.dismiss();
                    }
                });
                break;

            case R.id.action_byFirstDate:
                createfilterAmountDialog();
                //יצירת הדיאלוג של הלוח שנה
                Showdialog();
                filter_amount_edittext.setHint("date");
                filter_textview.setText("first date");
                filter_amount_go.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        foh = new FavorsOpenHelper(MainActivity.this);
                        foh.open();
                        selection = filter_amount_edittext.getText().toString();
                        listOfFavors = foh.listOfsameDates(selection);
                        foh.close();
                        refreshadapter();
                        filter_amount.dismiss();
                    }
                });

                break;

            case R.id.action_byLastDate:
                createfilterAmountDialog();
                Showdialog();
                filter_amount_edittext.setHint("date");
                filter_textview.setText("last date");
                filter_amount_go.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        foh = new FavorsOpenHelper(MainActivity.this);
                        foh.open();
                        selection = filter_amount_edittext.getText().toString();
                        listOfFavors = foh.listOfLastsameDates(filter_amount_edittext.getText().toString());
                        foh.close();
                        refreshadapter();
                        filter_amount.dismiss();
                    }
                });

                break;

            case R.id.action_firstDateBefore:
                    createfilterAmountDialog();
                    Showdialog();
                filter_amount_edittext.setHint("date");
                filter_textview.setText("first date before-");
                filter_amount_go.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        foh = new FavorsOpenHelper(MainActivity.this);
                        foh.open();
                        selection = filter_amount_edittext.getText().toString();
                        listOfFavors = foh.listOfBeforeFirstDate(filter_amount_edittext.getText().toString());
                        foh.close();
                        refreshadapter();
                        filter_amount.dismiss();
                    }
                });
                break;

            case R.id.action_lastDateBefore:
                createfilterAmountDialog();
                Showdialog();
                filter_amount_edittext.setHint("date");
                filter_textview.setText("last date before-");
                filter_amount_go.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        foh = new FavorsOpenHelper(MainActivity.this);
                        foh.open();
                        selection = filter_amount_edittext.getText().toString();
                        listOfFavors = foh.listOfBeforeLastDate(filter_amount_edittext.getText().toString());
                        foh.close();
                        refreshadapter();
                        filter_amount.dismiss();
                    }
                });
                break;

            case R.id.action_firstDateafter:
                createfilterAmountDialog();
                Showdialog();
                filter_amount_edittext.setHint("date");
                filter_textview.setText("first date after-");
                filter_amount_go.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        foh = new FavorsOpenHelper(MainActivity.this);
                        foh.open();
                        selection = filter_amount_edittext.getText().toString();
                        listOfFavors = foh.listOfAfterFirstDate(filter_amount_edittext.getText().toString());
                        foh.close();
                        refreshadapter();
                        filter_amount.dismiss();
                    }
                });
                break;


            case R.id.action_lastDateafter:
                createfilterAmountDialog();
                Showdialog();
                filter_amount_edittext.setHint("date");
                filter_textview.setText("last date after-");
                filter_amount_go.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        foh = new FavorsOpenHelper(MainActivity.this);
                        foh.open();
                        selection = filter_amount_edittext.getText().toString();
                        listOfFavors = foh.listOfAfterLastDate(filter_amount_edittext.getText().toString());
                        foh.close();
                        refreshadapter();
                        filter_amount.dismiss();
                    }
                });
                break;
            }




        return true;
    }
//dialog after the long click
    public void createOptoinsDialog()
    {

        d= new Dialog(this);
        d.setContentView(R.layout.options_layout);
        d.setTitle("options");
        d.setCancelable(true);
        update_dialog = d.findViewById(R.id.options_update_button);
        delete_dialog = d.findViewById(R.id.options_delete_button);
        d.show();

    }
//dialog after you click option in the menu
    public void createfilterAmountDialog()
    {

        filter_amount= new Dialog(this);
        filter_amount.setContentView(R.layout.filtersum_dialog);
        filter_amount.setTitle("filter");
        filter_amount.setCancelable(true);
        filter_amount_edittext = filter_amount.findViewById(R.id.dialogfilter_amount);
        filter_amount_go = filter_amount.findViewById(R.id.dialogfilter_amount_go);
        filter_textview = filter_amount.findViewById(R.id.textview_dialogfilter);
        filter_amount.show();

    }

    //refresh adapter-----------------------------------------------------------------------
    public void refreshadapter ()
    {
        FavorsAdapter = new favours_adapter(this,0, listOfFavors);
        lv.setAdapter(FavorsAdapter);
    }
    // אם העדכון ההוספה והמחיקה התרחשו בהצלחה אינטנט מעביר מידע למסך הזה על כך אז שולפים את הרשימה אחרי השינויים ומעדכני אדאפטר
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            foh.open();
            listOfFavors = foh.getAllFavors();
            refreshadapter();
            foh.close();

        }
    }
//יצירת הדיאלוג של הלוח שנה
    public void Showdialog()
    {
        //הפנייה ללוח שנה של המערכת
        Calendar systemCalender = Calendar.getInstance();
        int year = systemCalender.get(Calendar.YEAR);
        int month = systemCalender.get(Calendar.MONTH);
        int day = systemCalender.get(Calendar.DAY_OF_MONTH);
        //  יצירת הדיאלוג שליחה של ,context קלאס שיטפל בו ואת כל מה ששלפנו מהלוח שנה כדי שהוא יפתח על התאריכים האלו
        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this ,new SetDate(),year,month,day);
        datePickerDialog.show();
        datePickerDialog.setCancelable(true);
    }

//מקבל את התאריך שנבחר
    public  class SetDate implements DatePickerDialog.OnDateSetListener
    {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            monthOfYear = monthOfYear +1;

            String str = dayOfMonth + "/" + monthOfYear +"/" + year;
            filter_amount_edittext.setText(str);

        }
    }


}








