import model.cennikSekcja.Cennik;
import model.Klient;
import model.listy.Koszyk;
import model.listy.ListaZamowien;
import model.przesylki.Maxi;
import model.przesylki.Mini;
import model.przesylki.Sredni;


import static model.enums.RodzajePlatnosci.KARTA;
import static model.enums.RodzajePlatnosci.PRZELEW;
import static model.enums.RozmiarPrzesylki.*;
import static model.enums.SposobDostawy.*;

public class Main {

    static int cena(Koszyk k, String typ) {
        //pobieramy te elementy z listy ktorych typ rowny jest podanemu po czym pobieramy tylko ich ceny * ilosc a następnie dodajemy je do zmiennej cena
        double cena = k.getPrzesylki()
                .stream()
                .filter(x -> x.getTypPrzesylki().equals(typ))
                .map(przesylka -> przesylka.calculatePrice(k.getCzyWlascicielMaAbonament()) * przesylka.getIlosc()).mapToDouble(cenaPrzesylek -> cenaPrzesylek).sum();

        return (int) cena;
    }

    public static void main(String[] args) {
        Cennik cennik = Cennik.pobierzCennik();
        cennik.dodaj(MINI, "zwykly", 12, 10, 8);
        cennik.dodaj(MINI, "ekspres", 16, 14, 12);
        cennik.dodaj(MINI, "ekspres", 16, 14, 14);
        cennik.dodaj(SREDNI, "ubezp", 20, 15);
        cennik.dodaj(MAXI, "ekspres", 20);

        System.out.println(cennik);

        Klient blyskawica = new Klient("Błyskawica", 70, true);

        blyskawica.dodaj(new Mini("zwykly", 2, PUNKT));
        blyskawica.dodaj(new Sredni("zwykly", 3, KURIER));
        blyskawica.dodaj(new Maxi("ekspres", 2, KURIER));
        blyskawica.dodaj(new Mini("ekspres", 4, AUTOMAT));
        blyskawica.dodaj(new Mini("ekspres", 4, AUTOMAT));

        ListaZamowien listaBlyskawicy = blyskawica.pobierzListeZamowien();

        System.out.println("Lista zamówień klienta " + listaBlyskawicy);

        Koszyk koszykBlyskawicy = blyskawica.pobierzKoszyk();


        //dodatkowe sprawdzenie czy cena wyliczana jest poprawnie przy pozniejszej zmianie statusu abonamentu klienta
        blyskawica.setHasAbonament(false);
        System.out.println("Lista zamówień klienta " + listaBlyskawicy);

        blyskawica.setHasAbonament(true);


        blyskawica.przepakuj();
        System.out.println("Po przepakowaniu, lista zamówień klienta " + blyskawica.pobierzListeZamowien());

        System.out.println("Po przepakowaniu, koszyk klienta " + koszykBlyskawicy);

        System.out.println("Przesyłki ekspresowe w koszyku klienta Błyskawica kosztowały:  " + cena(koszykBlyskawicy, "ekspres"));


        blyskawica.zaplac(KARTA);

        System.out.println("Po zapłaceniu, klientowi Błyskawica zostało: " + blyskawica.pobierzPortfel() + " zł");

        System.out.println("Po zapłaceniu, koszyk klienta " + blyskawica.pobierzKoszyk());
        System.out.println("Po zapłaceniu, koszyk klienta " + koszykBlyskawicy);

        // Teraz przychodzi klient Żółwik,
        // deklaruje 130 zł na zamówienia
        Klient zolwik = new Klient("Żółwik", 130, true);

        // Zamówił za dużo jak na tę kwotę
        zolwik.dodaj(new Mini("zwykly", 4, KURIER));

        // Co klient Żółwik ma na swojej liście zamówień
        System.out.println("Lista zamówień klienta " + zolwik.pobierzListeZamowien());

        Koszyk koszykZolwika = zolwik.pobierzKoszyk();
        zolwik.przepakuj();

        // Co jest na liście zamówień klienta Żółwik
        System.out.println("Po przepakowaniu, lista zamówień klienta " + zolwik.pobierzListeZamowien());

        // A co jest w koszyku klienta Żółwik
        System.out.println("Po przepakowaniu, koszyk klienta " + zolwik.pobierzKoszyk());

        // klient Żółwik płaci
        zolwik.zaplac(PRZELEW);    // płaci przelewem, bez prowizji
        // Ile klientowi Żółwik zostało pieniędzy?
        System.out.println("Po zapłaceniu, klientowi Żółwik zostało: " + zolwik.pobierzPortfel() + " zł");

        zolwik.setHasAbonament(false);
        System.out.println("LISTA POPRZEDNICH ZAMOWIEN " + zolwik.getPoprzednioZamowione());
        zolwik.dodaj(new Mini("zwykly", 4, KURIER));
        zolwik.przepakuj();

        System.out.println("Po przepakowaniu222222222, koszyk klienta " + zolwik.pobierzKoszyk());
        zolwik.zaplac(PRZELEW);
        System.out.println("Po zapłaceniu2, klientowi Żółwik zostało: " + zolwik.pobierzPortfel() + " zł");

        //sprawdzamy liste zrealizowanych oplaconych zamowien
        System.out.println("LISTA POPRZEDNICH ZAMOWIEN " + zolwik.getPoprzednioZamowione());


        // Co zostało w koszyku klienta Żółwik (za mało pieniędzy miał)
        System.out.println("Po zapłaceniu, koszyk klienta " + koszykZolwika);


        zolwik.zwroc(MINI, "zwykly", 1, KURIER);
        zolwik.zwroc(MINI, "zwyklyy", 1, KURIER);    // zwrot (do koszyka) 1 przesyłki mini zwykłej (z dostawą od kuriera) z ostatniej transakcji

        // Ile klientowi Zółwik zostało pieniędzy?
        System.out.println("Po zwrocie, klientowi Zółwik zostało: " + zolwik.pobierzPortfel() + " zł");

        // Co zostało w koszyku klienta Żółwik
        System.out.println("Po zwrocie, koszyk klienta " + koszykZolwika);
    }
}