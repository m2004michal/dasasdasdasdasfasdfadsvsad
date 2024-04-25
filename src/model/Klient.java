package model;

import model.enums.RodzajePlatnosci;
import model.enums.RozmiarPrzesylki;
import model.enums.SposobDostawy;
import model.listy.Koszyk;
import model.listy.ListaZamowien;
import model.przesylki.Maxi;
import model.przesylki.Mini;
import model.przesylki.Przesylka;
import model.przesylki.Sredni;

import java.util.ArrayList;


public class Klient {
    private String nazwa;
    private double kwota;

    public boolean getHasAbonament() {
        return hasAbonament;
    }

    private boolean hasAbonament;
    private ListaZamowien listaZyczen;
    private  Koszyk koszyk;
    public Klient(String nazwa, double kwota, boolean hasAbonament) {
        this.nazwa = nazwa;
        this.kwota = kwota;
        this.hasAbonament = hasAbonament;
        this.listaZyczen = new ListaZamowien(this);
        this.koszyk = new Koszyk(this);
    }

    public void dodaj(Przesylka przesylka){
        przesylka.setHasAbonament(true);
        listaZyczen.dodajDoListy(przesylka);
    }

    public ListaZamowien pobierzListeZamowien(){
        return listaZyczen;
    }

    public String getNazwa() {
        return nazwa;
    }

    public Koszyk pobierzKoszyk() {
        return koszyk;
    }

    public void przepakuj() {
        ArrayList<Przesylka> nowaListaZyczen = new ArrayList<>(listaZyczen.getPrzesylki().stream().filter(x -> x.calculatePrice(this.getHasAbonament()) == 0).toList());
        ArrayList<Przesylka> listaDoKoszyka = new ArrayList<>(listaZyczen.getPrzesylki().stream().filter(x -> x.calculatePrice(this.getHasAbonament())!= 0).toList());
        koszyk.ustawKoszyk(listaDoKoszyka);
        listaZyczen.ustawListe(nowaListaZyczen);
    }

    public void zaplac(RodzajePlatnosci platnosc){
       this.koszyk.getPrzesylki().sort((x, y) -> (int) (x.calculatePrice(this.getHasAbonament()) - y.calculatePrice(this.getHasAbonament())));
        this.koszyk.getPrzesylki().stream().peek(x -> {
            double cena = x.calculatePrice(this.getHasAbonament());
            if (platnosc == RodzajePlatnosci.KARTA)
                cena *= 1.01;
            int il = x.getIlosc();
            for (int i = 0; i < il; i++) {
                if (kwota - cena >= 0) {
                    kwota -= cena;
                    x.setIlosc(x.getIlosc() - 1);
                }
            }
        }).forEach(x -> {});
       this.koszyk.setPrzesylki(new ArrayList<>(this.koszyk.getPrzesylki().stream().filter(x-> x.getIlosc() != 0).toList()));
        kwota = (double) Math.round(kwota * 100) /100;
    }

    public String pobierzPortfel() {
        return String.valueOf(kwota);
    }

    private Przesylka stworzPrzesylkeDoZwrotu(RozmiarPrzesylki rozmiarPrzesylki, String zwykly, int i, SposobDostawy sposobDostawy){
        return switch (rozmiarPrzesylki){
            case MAXI -> new Maxi(zwykly, i, sposobDostawy);
            case MINI -> new Mini(zwykly, i, sposobDostawy);
            case SREDNI -> new Sredni(zwykly, i, sposobDostawy);
        };
    }

    public void zwroc(RozmiarPrzesylki rozmiarPrzesylki, String zwykly, int i, SposobDostawy sposobDostawy) {
        Przesylka p = stworzPrzesylkeDoZwrotu(rozmiarPrzesylki, zwykly, i, sposobDostawy);
        koszyk.getPrzesylki().add(p);
        kwota += p.calculatePrice(this.getHasAbonament()) * i;
    }
}
