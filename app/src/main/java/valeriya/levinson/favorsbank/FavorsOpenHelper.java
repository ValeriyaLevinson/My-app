package valeriya.levinson.favorsbank;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FavorsOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASENAME="favors2.db";
    public static final String TABLE_FAVORS="tblfavors";
    public static final int DATABASEVERSION=1;

    public static final String COLUMN_ID="Id";
    public static final String COLUMN_FULLNAME="fullName";
    public static final String COLUMN_FIRSTDATE="firstdate";
    public static final String COLUMN_LASTDATE="lastdate";
    public static final String COLUMN_CATEGORY="ctegory";
    public static final String COLUMN_SUM="sum";

    private static final String CREATE_TABLE_FAVORS="CREATE TABLE IF NOT EXISTS " +
            TABLE_FAVORS + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_FULLNAME + " VARCHAR," + COLUMN_FIRSTDATE + " VARCHAR,"
            + COLUMN_LASTDATE + " VARCHAR," + COLUMN_CATEGORY + " VARCHAR," + COLUMN_SUM + " INTEGER " + ");";


    String []allColumns={FavorsOpenHelper.COLUMN_ID, FavorsOpenHelper.COLUMN_FULLNAME,FavorsOpenHelper.COLUMN_FIRSTDATE,
            FavorsOpenHelper.COLUMN_LASTDATE,FavorsOpenHelper.COLUMN_CATEGORY,FavorsOpenHelper.COLUMN_SUM};

    SQLiteDatabase database;

    private String getDateTime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(date);
    }


    public FavorsOpenHelper(Context context) {
        super(context, DATABASENAME, null, DATABASEVERSION);

    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FAVORS);
        Log.i("data", "Table favors created");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORS);
        onCreate(db);
    }

    public void open()
    {
        database=this.getWritableDatabase();
        Log.i("data", "Database connection open");
    }
//הוספת חובה לטבלה
    public Favors createFavor(Favors f)
    {
        ContentValues values=new ContentValues();
        values.put(FavorsOpenHelper.COLUMN_FULLNAME, f.getname());
        values.put(FavorsOpenHelper.COLUMN_FIRSTDATE, f.getDate());
        values.put(FavorsOpenHelper.COLUMN_LASTDATE, f.getLastdate());
        values.put(FavorsOpenHelper.COLUMN_CATEGORY, f.getcategory());
        values.put(FavorsOpenHelper.COLUMN_SUM, f.getSum());

        long insertId=database.insert(FavorsOpenHelper.TABLE_FAVORS, null, values);
        Log.i("data", "favor " + insertId + "insert to database");
        f.setId(insertId);
        return f;


    }
//מציאת חובה לפי id
    public Favors FindbyId (Long Id)
    {
        Favors f;
        Cursor cursor=database.query(FavorsOpenHelper.TABLE_FAVORS, allColumns, null, null, null, null, null);
        if(cursor.getCount()>0) {
            while (cursor.moveToNext()) {
                if (cursor.getLong(cursor.getColumnIndex(FavorsOpenHelper.COLUMN_ID)) == Id) {
                    long id = cursor.getLong(cursor.getColumnIndex(FavorsOpenHelper.COLUMN_ID));
                    String fullname = cursor.getString(cursor.getColumnIndex(FavorsOpenHelper.COLUMN_FULLNAME));
                    String firstdate = cursor.getString(cursor.getColumnIndex(FavorsOpenHelper.COLUMN_FIRSTDATE));
                    String lastdate = cursor.getString(cursor.getColumnIndex(FavorsOpenHelper.COLUMN_LASTDATE));
                    String category = cursor.getString(cursor.getColumnIndex(FavorsOpenHelper.COLUMN_CATEGORY));
                    int sum = cursor.getInt(cursor.getColumnIndex(FavorsOpenHelper.COLUMN_SUM));

                    f = new Favors(id, sum, fullname, category, firstdate, lastdate);
                    return f;
                }
            }
        }
        return null;
    }

//רשימה של כל החובות
    public ArrayList<Favors>getAllFavors()
    {

        ArrayList<Favors> l = new ArrayList<Favors>();
        Cursor cursor=database.query(FavorsOpenHelper.TABLE_FAVORS, allColumns, null, null, null, null, null);
        if(cursor.getCount()>0)
        {
            while(cursor.moveToNext())
            {
                long id=cursor.getLong(cursor.getColumnIndex(FavorsOpenHelper.COLUMN_ID));
                String fullname=cursor.getString(cursor.getColumnIndex(FavorsOpenHelper.COLUMN_FULLNAME));
                String firstdate=cursor.getString(cursor.getColumnIndex(FavorsOpenHelper.COLUMN_FIRSTDATE));
                String lastdate=cursor.getString(cursor.getColumnIndex(FavorsOpenHelper.COLUMN_LASTDATE));
                String category=cursor.getString(cursor.getColumnIndex(FavorsOpenHelper.COLUMN_CATEGORY));
                int sum=cursor.getInt(cursor.getColumnIndex(FavorsOpenHelper.COLUMN_SUM));
//                Date firstDate=null;
//                Date lastDate=null;
//                try {
//                     firstDate=new SimpleDateFormat("dd/MM/yyyy").parse(firstdate);
//                     lastDate=new SimpleDateFormat("dd/MM/yyyy").parse(lastdate);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
                Favors f =new Favors(id, sum , fullname, category, firstdate, lastdate);
                l.add(f);
            }
        }
        return l;
    }


    public long deleteAll()
    {
        return database.delete(FavorsOpenHelper.TABLE_FAVORS, null, null);

    }
    //מחיקת חובה לפי id
    public long deleteFavorByRow(long rowId)
    {
        return database.delete(FavorsOpenHelper.TABLE_FAVORS, FavorsOpenHelper.COLUMN_ID + "=" + rowId, null);
    }
//עדכון חובה לפי id
    public long updateByRow(Favors f)
    {
        ContentValues values=new ContentValues();
        values.put(FavorsOpenHelper.COLUMN_ID, f.getId());
        values.put(FavorsOpenHelper.COLUMN_FULLNAME, f.getname());
        values.put(FavorsOpenHelper.COLUMN_FIRSTDATE, f.getDate());
        values.put(FavorsOpenHelper.COLUMN_LASTDATE, f.getLastdate());
        values.put(FavorsOpenHelper.COLUMN_CATEGORY, f.getcategory());
        values.put(FavorsOpenHelper.COLUMN_SUM, f.getSum());

        return database.update(FavorsOpenHelper.TABLE_FAVORS, values, FavorsOpenHelper.COLUMN_ID +"=" + f.getId(), null);


    }
//סינון
    public ArrayList<Favors>getAllFavorsByFIlter(String selection,String OrderBy)
    {
        Cursor cursor=database.query(FavorsOpenHelper.TABLE_FAVORS, allColumns, selection, null, null, null, OrderBy);
        ArrayList<Favors>l=convertCurserToList(cursor);
        return  l;
    }


    private ArrayList<Favors> convertCurserToList(Cursor cursor) {
        ArrayList<Favors>l=new ArrayList<Favors>();

        if(cursor.getCount()>0)
        {
            while(cursor.moveToNext())
            {
                long id=cursor.getLong(cursor.getColumnIndex(FavorsOpenHelper.COLUMN_ID));
                String fullname=cursor.getString(cursor.getColumnIndex(FavorsOpenHelper.COLUMN_FULLNAME));
                String firstdate=cursor.getString(cursor.getColumnIndex(FavorsOpenHelper.COLUMN_FIRSTDATE));
                String lastdate=cursor.getString(cursor.getColumnIndex(FavorsOpenHelper.COLUMN_LASTDATE));
                String category=cursor.getString(cursor.getColumnIndex(FavorsOpenHelper.COLUMN_CATEGORY));
                int  sum=cursor.getInt(cursor.getColumnIndex(FavorsOpenHelper.COLUMN_SUM));
                Favors f=new Favors(id,sum,fullname,category,firstdate,lastdate);
                l.add(f);
            }

        }
        return l;
    }
//רשימה של חובות עם אותו תאריך שהפוקציה מקבלת
    public ArrayList<Favors> listOfsameDates (String date)
    {
        ArrayList<Favors> Result = new ArrayList<Favors>();
        Date dated = turnStringtoDate(date);
        Date listdate;
        ArrayList<Favors> l= getAllFavors();

        for (int i = 0; i < l.size(); i++)
        {
            listdate = turnStringtoDate(l.get(i).getDate());
            if (dated.equals(listdate) )
                Result.add(l.get(i));
        }

        return Result;
    }
//רשימה של חובות עם אותו תאריך אחרון שהפוקציה מקבלת
    public ArrayList<Favors> listOfLastsameDates (String date)
    {
        ArrayList<Favors> Result = new ArrayList<Favors>();
        Date dated = turnStringtoDate(date);
        Date listdate;
        ArrayList<Favors> l= getAllFavors();

        for (int i = 0; i < l.size(); i++)
        {
            listdate = turnStringtoDate(l.get(i).getLastdate());
            if (dated.equals(listdate) )
                Result.add(l.get(i));
        }

        return Result;
    }
//רשימה שהתאריך הראשון בחובות מגיע לפני התאריך שהפונקציה מקבלת
    public ArrayList<Favors> listOfBeforeFirstDate (String date)
    {
        ArrayList<Favors> Result = new ArrayList<Favors>();
        Date dated = turnStringtoDate(date);
        Date listdate;
        ArrayList<Favors> l= getAllFavors();

        for (int i = 0; i < l.size(); i++)
        {
            listdate = turnStringtoDate(l.get(i).getDate());
            if (listdate.before(dated) )
                Result.add(l.get(i));
        }

        return Result;

    }
//חובות שהתאריך האחרון שלהן מגיע לפני התאריך שהפונקציה מקבלת
    public ArrayList<Favors> listOfBeforeLastDate (String date) {
        ArrayList<Favors> Result = new ArrayList<Favors>();
        Date dated = turnStringtoDate(date);
        Date listdate;
        ArrayList<Favors> l = getAllFavors();

        for (int i = 0; i < l.size(); i++) {
            listdate = turnStringtoDate(l.get(i).getLastdate());
            if (listdate.before(dated))
                Result.add(l.get(i));
        }

        return Result;
    }
//חובות שהתאריך הראשון שלהן מגיע אחרי התאריך שהפונקציה מקבלת
    public ArrayList<Favors> listOfAfterFirstDate (String date)
    {
        ArrayList<Favors> Result = new ArrayList<Favors>();
        Date dated = turnStringtoDate(date);
        Date listdate;
        ArrayList<Favors> l= getAllFavors();

        for (int i = 0; i < l.size(); i++)
        {
            listdate = turnStringtoDate(l.get(i).getDate());
            if (listdate.after(dated) )
                Result.add(l.get(i));
        }

        return Result;

    }
//חובות שהתאריך האחרון שלהן מגיע אחרי התאריך שהפונקציה מקבלת
    public ArrayList<Favors> listOfAfterLastDate (String date)
    {
        ArrayList<Favors> Result = new ArrayList<Favors>();
        Date dated = turnStringtoDate(date);
        Date listdate;
        ArrayList<Favors> l= getAllFavors();

        for (int i = 0; i < l.size(); i++)
        {
            listdate = turnStringtoDate(l.get(i).getLastdate());
            if (listdate.after(dated) )
                Result.add(l.get(i));
        }

        return Result;

    }
// מספר החובות שהתאריך שלהם עבר
    public int getExpired (ArrayList<Favors> listOfFavors)
    {
        Calendar systemCalender = Calendar.getInstance();
        int year = systemCalender.get(Calendar.YEAR);
        int month = systemCalender.get(Calendar.MONTH);
        int day = systemCalender.get(Calendar.DAY_OF_MONTH);



        month = month +1;

        String currentDate =  day + "/" + month +"/" + year;


        Date current = turnStringtoDate(currentDate);
        Date listDate;
        int result = 0;
        for (int i = 0; i < listOfFavors.size(); i++)
        {
            listDate = turnStringtoDate(listOfFavors.get(i).getLastdate());
            if (current.after(listDate))
            {
                result++;
            }
        }

        return result;
    }






    public Date turnStringtoDate (String date) {
        Date dated=null;

        try {

            dated = new SimpleDateFormat("dd/MM/yyyy").parse(date);

       } catch (ParseException e) {
           e.printStackTrace();
       }

        return dated;
    }


}
