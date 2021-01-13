package valeriya.levinson.favorsbank;

import java.util.Date;

public class Favors extends Basesql {

 //Long id;
 int sum;
 String name, category;
 String date, lastdate;
 //category

    public void Favors(){}

    public Favors(Long id, int sum, String name, String category, String date, String lastdate) {
        super(id);
      //  this.id = id;
        this.sum=sum;
        this.name=name;
        this.category=category;
       // this.comment=comment;
        this.date=date;
        this.lastdate=lastdate;

    }


    public String getLastdate() {
        return lastdate;
    }

    public void setLastdate(String lastdate) {
        this.lastdate = lastdate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getcategory() {
        return category;
    }

    public void setcategory(String category) {
        this.category = category;
    }

    public String getname() {
        return name;
    }

    public void setname(String lastname) {
        this.name = name;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

}
