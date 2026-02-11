package domain;
import domain.tiprata.Inotator;
public class SwimmingDuck extends Duck implements Inotator{
    public SwimmingDuck(Long id, String username, String email, String password, double viteza, double rezistenta) {
        super(id, username, email, password, viteza, rezistenta);
    }
    @Override
    public void inoata() {
        System.out.println(getUsername() + " rata asta inoata");
    }
}
