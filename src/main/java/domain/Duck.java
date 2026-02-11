package domain;

public abstract class Duck extends User{
    //private TipRata tip;
    private double viteza;
    private double rezistenta;

    public Duck(Long id, String username, String email, String password, double viteza, double rezistenta) {
        super(id, username, email, password);
        //this.tip = tip;
        this.viteza = viteza;
        this.rezistenta = rezistenta;
    }



    public double getViteza() {
        return viteza;
    }

    public void setViteza(double viteza) {
        this.viteza = viteza;
    }

    public double getRezistenta() {
        return rezistenta;
    }

    public void setRezistenta(double rezistenta) {
        this.rezistenta = rezistenta;
    }

    @Override
    public String toString() {
        return "Duck{" +

                ", viteza=" + viteza +
                ", rezistenta=" + rezistenta +
                '}';
    }
}
