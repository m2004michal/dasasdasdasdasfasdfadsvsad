package model.przesylki;

import model.enums.SposobDostawy;

import static model.enums.RozmiarPrzesylki.MINI;

public class Mini extends Przesylka {
    public Mini(String typPrzesylki, int ilosc, SposobDostawy sposobDostawy) {
        super(typPrzesylki, ilosc, sposobDostawy, MINI);
    }
}
