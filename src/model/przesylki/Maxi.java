package model.przesylki;

import model.enums.SposobDostawy;

import static model.enums.RozmiarPrzesylki.MAXI;

public class Maxi extends Przesylka {
    public Maxi(String typPrzesylki, int ilosc, SposobDostawy sposobDostawy) {
        super(typPrzesylki, ilosc, sposobDostawy, MAXI);
    }
    public Maxi(Przesylka przesylka){
        super(przesylka.getTypPrzesylki(), przesylka.getIlosc(), przesylka.getSposobDostawy(), MAXI);
    }
}
