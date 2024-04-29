package service;

import model.przesylki.Maxi;
import model.przesylki.Mini;
import model.przesylki.Przesylka;
import model.przesylki.Sredni;

public class PrzesylkaService {

    public static Przesylka copyPrzesylka(Przesylka przesylka){
        return switch (przesylka.getRozmiarPrzesylki()){
            case MINI -> new Mini(przesylka);
            case SREDNI -> new Sredni(przesylka);
            case MAXI -> new Maxi(przesylka);
        };
    }

}
