package model.przesylki;

import model.cennikSekcja.Cennik;
import model.cennikSekcja.ProduktWCenniku;
import model.enums.RozmiarPrzesylki;
import model.enums.SposobDostawy;

import java.util.List;


public abstract class Przesylka {
    private RozmiarPrzesylki rozmiarPrzesylki;
    private String typPrzesylki;
    private int ilosc;
    private SposobDostawy sposobDostawy;
    private boolean hasAbonament;
    public int getIlosc() {
        return ilosc;
    }

    public void setIlosc(int ilosc) {
        this.ilosc = ilosc;
    }

    public void setHasAbonament(boolean hasAbonament) {
        this.hasAbonament = hasAbonament;
    }

    public Przesylka(String typPrzesylki, int ilosc, SposobDostawy sposobDostawy, RozmiarPrzesylki rozmiarPrzesylki) {
        this.typPrzesylki = typPrzesylki;
        this.ilosc = ilosc;
        this.sposobDostawy = sposobDostawy;
        this.rozmiarPrzesylki = rozmiarPrzesylki;
    }
    public double calculatePrice(boolean hasAbonament){
        List<ProduktWCenniku> produkty = Cennik.getInstance()
                .getListaCen()
                .stream()
                .filter(x -> this.rozmiarPrzesylki == x.getRozmiarPrzesylki() &&
                        this.typPrzesylki.equals(x.getRodzajPrzesylki()) &&
                        x.hasSposobDostawy(this.sposobDostawy))
                .toList();

        if (produkty.isEmpty())
            return 0;
        double cena = produkty.get(0).pobierzCeneDlaTypuDostawy(this.sposobDostawy);
        if (hasAbonament)
            cena /= 2;

        return cena;
    }

    @Override
    public String toString() {
        return rozmiarPrzesylki.getValue() +
                ", typ: " + typPrzesylki +
                ", ile: " + ilosc +
                ", odbiór: " + sposobDostawy.getValue() +
                ", cena: " + calculatePrice(hasAbonament);
    }

    public String getTypPrzesylki() {
        return typPrzesylki;
    }
}
