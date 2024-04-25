package model.listy;

import model.Klient;
import model.przesylki.Przesylka;

import java.util.ArrayList;

public class ListaZamowien extends KoszykoLista{

    public ListaZamowien(Klient klient) {
        super(new ArrayList<>(), klient);
    }

    public void dodajDoListy(Przesylka przesylka){
        getPrzesylki().add(przesylka);
    }
    public void ustawListe(ArrayList<Przesylka> przesylkiZListy) {
        setPrzesylki(przesylkiZListy);
    }
}
