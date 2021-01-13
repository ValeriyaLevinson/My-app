package valeriya.levinson.favorsbank;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.graphics.BlendMode.COLOR;

public class favours_adapter extends ArrayAdapter<Favors> {
    Context context;
    List<Favors> objects;

    //הפניה לאקטיביתי שבו משתמשים באדפטר ורשימת האוביקטים שיופיעו בlistview
    public favours_adapter(Context context, int resource, List<Favors> objects) {
        super(context, resource, objects);
        this.objects=objects;
        this.context=context;
    }

    private String getDateTime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(date);
    }
//הפונקציה הזו תרוץ כמספר האוביקטים ברשימה כל פעם עם position אחר
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
        // list viewשל ה layout  ה
        View view = layoutInflater.inflate(R.layout.favour_row, parent, false);

//השתלטות על כל הדברים בתא
        TextView tvFulltName =(TextView) view.findViewById(R.id.tvtName);
        TextView tvDate =(TextView) view.findViewById(R.id.tvDate);
        TextView tvLastDate =(TextView) view.findViewById(R.id.tvLastDate);
        TextView tvCategory =(TextView) view.findViewById(R.id.tvcategory);
        TextView tvShoeAmount =(TextView) view.findViewById(R.id.tvshoeamount);
        //שולפת את האיבר מהרשימה במקום הposition
        Favors temp = objects.get(position);
        tvFulltName.setText(String.valueOf(temp.getname()));
        tvDate.setText( temp.date);
        tvLastDate.setText(temp.lastdate);
        tvCategory.setText(String.valueOf(temp.getcategory()));
        tvShoeAmount.setText(String.valueOf(temp.sum));

        if(temp.sum>0)
        {

            tvShoeAmount.setTextColor(Color.GREEN);
        }
        if(temp.getSum()<0)
        {
            tvShoeAmount.setTextColor(Color.RED);
        }
        return view;
    }
}
