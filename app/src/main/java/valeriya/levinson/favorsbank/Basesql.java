package valeriya.levinson.favorsbank;

public class Basesql
{
    protected Long id;

    public Basesql(Long id)
    {
        this.id=id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
