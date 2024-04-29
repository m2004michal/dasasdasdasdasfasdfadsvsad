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
import service.PrzesylkaService;

import java.util.ArrayList;
import java.util.Optional;

import static service.CenaService.calculatePrice;


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

        //sprawdzamy czy przesylka o danych parametrach widnieje w naszej historii zamowien
        //zapisujemy ją w zmiennej przesylka1(jesli istnieje)
        Optional<Przesylka> przesylka1 = getPoprzednioZamowione().getPrzesylki()
                .stream()
                .filter(przesylka -> przesylka.getTypPrzesylki().equals(p.getTypPrzesylki()) &&
                        przesylka.getSposobDostawy().equals(p.getSposobDostawy()) &&
                        przesylka.getRozmiarPrzesylki().equals(p.getRozmiarPrzesylki()) &&
                        przesylka.getCena() == p.getCena() &&
                        przesylka.getIlosc() >= i)
                .findFirst();
        //jezeli znajdziemy przesyłkę
        przesylka1.ifPresentOrElse(przesylka -> {
                    int iloscDoZostawieniaWHistorii = przesylka.getIlosc() - i;
                    getPoprzednioZamowione().getPrzesylki().remove(przesylka);
                    przesylka.setIlosc(i);
                    koszyk.getPrzesylki().add(przesylka);
                    kwota += przesylka.getCena();
                    Przesylka przesylkaCopy = PrzesylkaService.copyPrzesylka(przesylka);
                    przesylkaCopy.setIlosc(iloscDoZostawieniaWHistorii);
                    if (przesylkaCopy.getIlosc() > 0)
                        getPoprzednioZamowione().getPrzesylki().add(przesylkaCopy);
                }, //jesli nie znajdziemy przesylki
                () -> System.out.println("NIE ZNALEZIONO PRZESYLKI O PODANYCH PARAMETRACH LUB PODANA" +
                        "DO ZWROTU ILOSC JEST WIEKSZA NIZ ILOSC ZAKUPIONYCH PRZESYLEK PODANEGO TYPI"));
    }

    //przerzucamy przesylki z listyZamowien do koszyka ze specyfikacja ze przesylki znajdujace sie w koszyku muszą posiadać swoją pozycje w cenniku
    public void przepakuj() {
        ArrayList<Przesylka> nowaListaZyczen = new ArrayList<>(listaZyczen.getPrzesylki().stream().filter(x -> calculatePrice(x, this.getHasAbonament()) == 0).toList());
        ArrayList<Przesylka> listaDoKoszyka = new ArrayList<>(listaZyczen.getPrzesylki().stream().filter(x -> calculatePrice(x, this.getHasAbonament()) != 0).toList());
        koszyk.ustawKoszyk(listaDoKoszyka);
        listaZyczen.ustawListe(nowaListaZyczen);
    }


    public void zaplac(RodzajePlatnosci platnosc) {
        //sortujemy nasz koszyk bazując na cenie -> od najmniejszej do największej

        this.koszyk.getPrzesylki().sort((x, y) -> (int) (calculatePrice(x, this.getHasAbonament()) - calculatePrice(y, this.getHasAbonament())));
        this.koszyk.getPrzesylki()
                .forEach(x -> {
                    double cena = (calculatePrice(x, this.getHasAbonament()) * ((platnosc == RodzajePlatnosci.KARTA) ? 1.01 : 1));
                    int il = x.getIlosc();
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
        this.koszyk.ustawListe(new ArrayList<>(this.koszyk.getPrzesylki().stream().filter(x -> x.getIlosc() != 0).toList()));
        kwota = (double) Math.round(kwota * 100) / 100;

    }

    //w wypadku gdyby stan abonamentu klienta mial ulec zmianie (mozna usunac badz stestowac w mainie dzialanie metod po zmianei abonamentu)
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
