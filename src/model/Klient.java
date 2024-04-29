package model;

import model.enums.RodzajePlatnosci;
import model.enums.RozmiarPrzesylki;
import model.enums.SposobDostawy;
import model.listy.Koszyk;
import model.listy.ListaZamowien;
import model.przesylki.*;

import java.util.ArrayList;


public class Klient {
    private final String nazwa;
    private double kwota;

    private boolean hasAbonament;
    private final ListaZamowien listaZyczen;
    private final Koszyk koszyk;

    private final ListaZamowien poprzednioZamowione;


    public Klient(String nazwa, double kwota, boolean hasAbonament) {
        this.nazwa = nazwa;
        this.kwota = kwota;
        this.hasAbonament = hasAbonament;
        this.listaZyczen = new ListaZamowien(this.getNazwa());
        this.poprzednioZamowione = new ListaZamowien(this.getNazwa());
        this.koszyk = new Koszyk(this.getNazwa(), this.getHasAbonament());
    }

    public void dodaj(Przesylka przesylka) {
        przesylka.setHasAbonament(hasAbonament);
        listaZyczen.dodajDoListy(przesylka);
    }

    private Przesylka stworzPrzesylkeDoZwrotu(RozmiarPrzesylki rozmiarPrzesylki, String zwykly, int i, SposobDostawy sposobDostawy) {
        return switch (rozmiarPrzesylki) {
            case MAXI -> new Maxi(zwykly, i, sposobDostawy);
            case MINI -> new Mini(zwykly, i, sposobDostawy);
            case SREDNI -> new Sredni(zwykly, i, sposobDostawy);
        };
    }

    public void zwroc(RozmiarPrzesylki rozmiarPrzesylki, String zwykly, int i, SposobDostawy sposobDostawy) {
        Przesylka p = stworzPrzesylkeDoZwrotu(rozmiarPrzesylki, zwykly, i, sposobDostawy);
        p.setHasAbonament(this.getHasAbonament());
        p.wyliczAktualnaCene();
        try {
            Przesylka przesylka1 = getPoprzednioZamowione().getPrzesylki()
                    .stream()
                    .filter(przesylka -> przesylka.getTypPrzesylki().equals(p.getTypPrzesylki()) &&
                            przesylka.getSposobDostawy().equals(p.getSposobDostawy()) &&
                            przesylka.getRozmiarPrzesylki().equals(p.getRozmiarPrzesylki()) &&
                            przesylka.getCena() == p.getCena())
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("nie znaleziono podanej przesylki w historii zamowien"));

            koszyk.getPrzesylki().add(przesylka1);
            kwota += przesylka1.getCena();
        } catch (RuntimeException e) {
            System.err.println("nie znaleziono podanej przesylki w historii zamowien");
        }


    }

    public void przepakuj() {
        ArrayList<Przesylka> nowaListaZyczen = new ArrayList<>(listaZyczen.getPrzesylki().stream().filter(x -> x.calculatePrice(this.getHasAbonament()) == 0).toList());
        ArrayList<Przesylka> listaDoKoszyka = new ArrayList<>(listaZyczen.getPrzesylki().stream().filter(x -> x.calculatePrice(this.getHasAbonament()) != 0).toList());
        koszyk.ustawKoszyk(listaDoKoszyka);
        listaZyczen.ustawListe(nowaListaZyczen);
    }

    public void zaplac(RodzajePlatnosci platnosc) {
        this.koszyk.getPrzesylki().sort((x, y) -> (int) (x.calculatePrice(this.getHasAbonament()) - y.calculatePrice(this.getHasAbonament())));
        this.koszyk.getPrzesylki()
                .forEach(x -> {
                    double cena = x.calculatePrice(this.getHasAbonament());
                    if (platnosc == RodzajePlatnosci.KARTA)
                        cena *= 1.01;
                    int il = x.getIlosc();
                    for (int i = 0; i < il; i++) {
                        if (kwota - cena >= 0) {
                            kwota -= cena;
                            x.setIlosc(x.getIlosc() - 1);
                            poprzednioZamowione.dodajDoListy(stworzPrzesylkeDoZwrotu(x.getRozmiarPrzesylki(), x.getTypPrzesylki(), 1, x.getSposobDostawy()));
                        }
                    }
                });
        this.koszyk.setPrzesylki(new ArrayList<>(this.koszyk.getPrzesylki().stream().filter(x -> x.getIlosc() != 0).toList()));
        kwota = (double) Math.round(kwota * 100) / 100;

    }

    public void setHasAbonament(boolean hasAbonament) {
        this.hasAbonament = hasAbonament;
        listaZyczen.getPrzesylki().forEach(przesylka -> przesylka.setHasAbonament(hasAbonament));
        koszyk.getPrzesylki().forEach(przesylka -> {
            przesylka.setHasAbonament(hasAbonament);
            przesylka.wyliczAktualnaCene();
        });
        koszyk.setCzyWlascicielMaAbonament(hasAbonament);
    }

    public ListaZamowien getPoprzednioZamowione() {
        return poprzednioZamowione;
    }

    public String pobierzPortfel() {
        return String.valueOf(kwota);
    }

    public boolean getHasAbonament() {
        return hasAbonament;
    }

    public ListaZamowien pobierzListeZamowien() {
        return listaZyczen;
    }

    public String getNazwa() {
        return nazwa;
    }

    public Koszyk pobierzKoszyk() {
        return koszyk;
    }

}
