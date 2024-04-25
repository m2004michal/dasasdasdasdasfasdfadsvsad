package model.listy;

import model.Klient;
import model.przesylki.Przesylka;

import java.util.ArrayList;

public class Koszyk extends KoszykoLista{
    public Koszyk(Klient klient) {
        super(new ArrayList<>(), klient);
    }
    public void ustawKoszyk(ArrayList<Przesylka> przesylkiZListy){
        setPrzesylki(przesylkiZListy);
    }

}
