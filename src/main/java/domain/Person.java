package domain;
import java.time.LocalDate;
public class Person extends User{
    private String nume;
    private String prenume;
    private LocalDate dataNasterii;
    private String ocupatie;
    private int nivelEmpatie;

    public Person(Long id, String username, String email, String password, String nume, String prenume, LocalDate dataNasterii, String ocupatie, int nivelEmpatie) {
        super(id, username, email, password);
        this.nume = nume;
        this.prenume = prenume;
        this.dataNasterii = dataNasterii;
        this.ocupatie = ocupatie;
        this.nivelEmpatie = nivelEmpatie;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public LocalDate getDataNasterii() {
        return dataNasterii;
    }

    public void setDataNasterii(LocalDate dataNasterii) {
        this.dataNasterii = dataNasterii;
    }

    public String getOcupatie() {
        return ocupatie;
    }

    public void setOcupatie(String ocupatie) {
        this.ocupatie = ocupatie;
    }

    public int getNivelEmpatie() {
        return nivelEmpatie;
    }

    public void setNivelEmpatie(int nivelEmpatie) {
        this.nivelEmpatie = nivelEmpatie;
    }

    @Override
    public String toString() {
        return "Person{" +
                "nume='" + nume + '\'' +
                ", prenume='" + prenume + '\'' +
                ", dataNasterii=" + dataNasterii +
                ", ocupatie='" + ocupatie + '\'' +
                ", nivelEmpatie=" + nivelEmpatie +
                '}';
    }
}
