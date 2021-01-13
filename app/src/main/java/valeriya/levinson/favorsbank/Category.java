package valeriya.levinson.favorsbank;

import java.util.ArrayList;
import java.util.Arrays;

public class Category {

    ArrayList<String> categories;



    public Category()
    {
        categories= new ArrayList<>();
        categories.add("car");
        categories.add("food");
        categories.add("driving lessons");
        categories.add("studies");
        categories.add("phone");
        categories.add("emergency");
        categories.add("furniture");
        categories.add("electronic devices");
        categories.add("private lessons");
        categories.add("books");
        categories.add("makeup");
        categories.add("other");

    }



    public ArrayList<String> getCategories() {
        return categories;
    }

}
