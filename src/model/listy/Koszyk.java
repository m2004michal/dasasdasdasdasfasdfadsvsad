package model.listy;

import model.przesylki.Przesylka;

import java.util.ArrayList;

public class Koszyk extends ListaZamowien {
    private boolean czyWlascicielMaAbonament;

    public Koszyk(String nazwaWlascicielaKoszyka, boolean czyWlascicielMaAbonament) {
        super(nazwaWlascicielaKoszyka);
        this.czyWlascicielMaAbonament = czyWlascicielMaAbonament;
    }

    public void ustawKoszyk(ArrayList<Przesylka> przesylkiZListy) {
        ustawListe(przesylkiZListy);
    }

    public boolean getCzyWlascicielMaAbonament() {
        return czyWlascicielMaAbonament;
    }

    public void setCzyWlascicielMaAbonament(boolean czyWlascicielMaAbonament) {
        this.czyWlascicielMaAbonament = czyWlascicielMaAbonament;
    }
}
