package model.listy;

import model.Klient;
import model.przesylki.Przesylka;

import java.util.ArrayList;

public abstract class KoszykoLista {

    private ArrayList<Przesylka> przesylki;

    public Klient getKlient() {
        return klient;
    }

    private Klient klient;

    public KoszykoLista(ArrayList<Przesylka> przesylki, Klient klient) {
        this.przesylki = przesylki;
        this.klient = klient;
    }

    @Override
    public String toString() {
        StringBuilder przesylkiDoWydruku = new StringBuilder();
        if (przesylki.isEmpty())
            return "-- pusto";
        for (Przesylka przesylka : przesylki) {
            przesylkiDoWydruku.append(przesylka.toString()).append("\n");
        }
        return klient.getNazwa() + "\n" + przesylkiDoWydruku;
    }

    public ArrayList<Przesylka> getPrzesylki() {
        return przesylki;
    }

    public void setPrzesylki(ArrayList<Przesylka> przesylki) {
        this.przesylki = przesylki;
    }
}
