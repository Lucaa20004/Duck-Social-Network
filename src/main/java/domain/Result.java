package domain;

import java.util.Arrays;

public class Result {
    private double duratamain;
    private Duck[] lista;
    private Culoar[] listaculoar;
    public Result(double duratamain, Duck[] lisa,  Culoar[] lista1) {
        this.duratamain = duratamain;
        this.lista = lisa;
        this.listaculoar = lista1;
    }

    public double getTimpMinim() {
        return duratamain;
    }

    public Culoar[] getCuloare() {
        return listaculoar;
    }

    public Duck[] getDucks() {
        return lista;
    }



    @Override
    public String toString() {
        return "Result{" +
                "duratamain=" + duratamain +
                ", lisa=" + Arrays.toString(lista) +
                '}';
    }
}
