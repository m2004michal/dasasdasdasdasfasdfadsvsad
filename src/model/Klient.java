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

    //ustawiamy abonament przechowywany w przesylce na aktualny stan abonamentu uzytkownika
    public void dodaj(Przesylka przesylka) {
        przesylka.setHasAbonament(hasAbonament);
        listaZyczen.dodajDoListy(przesylka);
    }

    //na bazie rozmiaru, typu i sposobu dostawy tworzymy przesylke okreslonej subklasy - MAXI/MINI/SREDNIA
    private Przesylka stworzPrzesylkeDoZwrotu(RozmiarPrzesylki rozmiarPrzesylki, String typ, int i, SposobDostawy sposobDostawy) {
        return switch (rozmiarPrzesylki) {
            case MAXI -> new Maxi(typ, i, sposobDostawy);
            case MINI -> new Mini(typ, i, sposobDostawy);
            case SREDNI -> new Sredni(typ, i, sposobDostawy);
        };
    }


    public void zwroc(RozmiarPrzesylki rozmiarPrzesylki, String zwykly, int i, SposobDostawy sposobDostawy) {
        //tworzymy przesylke uzywajac metody stworzPrzesylkeDoZwrotu(w celu stworzenia przesylki z subklasy MAXI/MINI/SREDNIA)
        Przesylka p = stworzPrzesylkeDoZwrotu(rozmiarPrzesylki, zwykly, i, sposobDostawy);
        //ustawiamy abonament stworzonej przesylki na aktualny stan abonamentu uzytkownika
        p.setHasAbonament(this.getHasAbonament());
        // ustawiamy cene przesylki w momencie dodania na cene wyliczoną na bazie aktualnego abonamentu (w celu zapamiętania ceny przesyłki z chwili dodania
        p.wyliczAktualnaCene();

        try {
            //sprawdzamy czy przesylka o danych parametrach widnieje w naszej historii zamowien
            //zapisujemy ją w zmiennej przesylka1
            Przesylka przesylka1 = getPoprzednioZamowione().getPrzesylki()
                    .stream()
                    .filter(przesylka -> przesylka.getTypPrzesylki().equals(p.getTypPrzesylki()) &&
                            przesylka.getSposobDostawy().equals(p.getSposobDostawy()) &&
                            przesylka.getRozmiarPrzesylki().equals(p.getRozmiarPrzesylki()) &&
                            przesylka.getCena() == p.getCena())
                    .findFirst()
                    //podnosimy wyjatek mowiacy nam o braku takiej przeyslki
                    .orElseThrow(() -> new RuntimeException("nie znaleziono podanej przesylki w historii zamowien"));

            //dodajemy znaleziona przesylke do koszyka
            koszyk.getPrzesylki().add(przesylka1);
            //zwracamy klientowi do portfela(kwota) cene przesylki
            kwota += przesylka1.getCena();
            //usuwamy przesylke z historii zamowien
            getPoprzednioZamowione().getPrzesylki().remove(przesylka1);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
    }

    //przerzucamy przesylki z listyZamowien do koszyka ze specyfikacja ze przesylki znajdujace sie w koszyku muszą posiadać swoją pozycje w cenniku
    public void przepakuj() {
        ArrayList<Przesylka> nowaListaZyczen = new ArrayList<>(listaZyczen.getPrzesylki().stream().filter(x -> x.calculatePrice(this.getHasAbonament()) == 0).toList());
        ArrayList<Przesylka> listaDoKoszyka = new ArrayList<>(listaZyczen.getPrzesylki().stream().filter(x -> x.calculatePrice(this.getHasAbonament()) != 0).toList());
        koszyk.ustawKoszyk(listaDoKoszyka);
        listaZyczen.ustawListe(nowaListaZyczen);
    }


    public void zaplac(RodzajePlatnosci platnosc) {
        //sortujemy nasz koszyk bazując na cenie -> od najmniejszej do największej


        this.koszyk.getPrzesylki().sort((x, y) -> (int) (x.calculatePrice(this.getHasAbonament()) - y.calculatePrice(this.getHasAbonament())));
        this.koszyk.getPrzesylki()
                .forEach(x -> {
                    //w zaleznosci od rodzaju platnosci wyliczamy cene
                    double cena = (x.calculatePrice(this.getHasAbonament()) * ((platnosc == RodzajePlatnosci.KARTA) ? 1.01 : 1));
                    int il = x.getIlosc();
                    //iteracyjnie przechodzimy po przesylkach -> jesli stac nas na zakup 1 sztuki zmniejszamy ilosc przesylek o 0 i kwote w portfelu o cene przeyslki oraz dodajemy zakupiona przesylke do listy zamowien
                    for (int i = 0; i < il; i++) {
                        if (kwota - cena >= 0) {
                            kwota -= cena;
                            x.setIlosc(x.getIlosc() - 1);
                            Przesylka przesylka = stworzPrzesylkeDoZwrotu(x.getRozmiarPrzesylki(), x.getTypPrzesylki(), 1, x.getSposobDostawy());
                            przesylka.setHasAbonament(hasAbonament);
                            poprzednioZamowione.dodajDoListy(przesylka);
                        }
                    }
                });
        //dodajemy przesylki ktorych ilosc jest rozna od 0 spowrotem do koszyka
        this.koszyk.ustawListe(new ArrayList<>(this.koszyk.getPrzesylki().stream().filter(x -> x.getIlosc() != 0).toList()));
        //zaokraglamy kwote do 2 miejsc po przecinku
        kwota = (double) Math.round(kwota * 100) / 100;

    }


    //setter abonamentu robi troche wiecej ze wzgledu na to ze przy zmianie stanu abonamentu u uzytkownika chcemy zmienic ten stan takze w podleglym mu koszyku i podleglych mu produktach
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
