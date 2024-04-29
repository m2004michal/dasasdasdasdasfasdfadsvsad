package model.przesylki;

import model.enums.RozmiarPrzesylki;
import model.enums.SposobDostawy;

import static service.CenaService.calculatePrice;


public abstract class Przesylka {
    private final RozmiarPrzesylki rozmiarPrzesylki;
    private final String typPrzesylki;
    private int ilosc;
    private final SposobDostawy sposobDostawy;
    private boolean hasAbonament;

    private double cena;


    public Przesylka(String typPrzesylki, int ilosc, SposobDostawy sposobDostawy, RozmiarPrzesylki rozmiarPrzesylki) {
        this.typPrzesylki = typPrzesylki;
        this.ilosc = ilosc;
        this.sposobDostawy = sposobDostawy;
        this.rozmiarPrzesylki = rozmiarPrzesylki;
        wyliczAktualnaCene();
    }


    @Override
    public String toString() {
        return rozmiarPrzesylki.getValue() +
                ", typ: " + typPrzesylki +
                ", ile: " + ilosc +
                ", odbiór: " + sposobDostawy.getValue() +
                ", cena: " + calculatePrice(this, hasAbonament);
    }

    public String getTypPrzesylki() {
        return typPrzesylki;
    }

    public int getIlosc() {
        return ilosc;
    }

    public void setIlosc(int ilosc) {
        this.ilosc = ilosc;
    }

    public void setHasAbonament(boolean hasAbonament) {
        this.hasAbonament = hasAbonament;
    }

    public RozmiarPrzesylki getRozmiarPrzesylki() {
        return rozmiarPrzesylki;
    }

    public SposobDostawy getSposobDostawy() {
        return sposobDostawy;
    }

    public void wyliczAktualnaCene() {
        cena = calculatePrice(this, this.hasAbonament);
    }

    public double getCena() {
        return cena;
    }

    public boolean getHasAbonament() {
        return hasAbonament;
    }

}
