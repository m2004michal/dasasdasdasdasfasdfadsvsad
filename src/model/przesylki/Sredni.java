package model.przesylki;

import model.enums.SposobDostawy;

import static model.enums.RozmiarPrzesylki.SREDNI;

public class Sredni extends Przesylka {
    public Sredni(String typPrzesylki, int ilosc, SposobDostawy sposobDostawy) {
        super(typPrzesylki, ilosc, sposobDostawy, SREDNI);
    }
    public Sredni(Przesylka przesylka){
        super(przesylka.getTypPrzesylki(), przesylka.getIlosc(), przesylka.getSposobDostawy(), SREDNI);
    }
}
