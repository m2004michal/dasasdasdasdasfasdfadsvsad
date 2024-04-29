package model.przesylki;

import model.cennikSekcja.Cennik;
import model.enums.RozmiarPrzesylki;
import model.enums.SposobDostawy;


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


    public double calculatePrice(boolean hasAbonament) {
        //szukamy w cenniku pozycji odpowiadającej parametrom naszej paczki i pobieramy cene dla danej pozycji (gdy brak zwracamy 0)
        double cena = Cennik.pobierzCennik()
                .getListaCen()
                .stream()
                .filter(x -> this.rozmiarPrzesylki == x.getRozmiarPrzesylki() &&
                        this.typPrzesylki.equals(x.getRodzajPrzesylki()) &&
                        x.hasSposobDostawy(this.sposobDostawy))
                .findFirst()
                .map(x -> x.pobierzCeneDlaTypuDostawy(this.getSposobDostawy()))
                .orElse(0D);
        //w przypadku abonamentu zmniejszamy cene o polowe
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
        cena = calculatePrice(this.hasAbonament);
    }

    public double getCena() {
        return cena;
    }

    public boolean getHasAbonament() {
        return hasAbonament;
    }
}
