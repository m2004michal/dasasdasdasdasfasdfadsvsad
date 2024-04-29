import model.Klient;
import model.cennikSekcja.Cennik;
import model.listy.Koszyk;
import model.listy.ListaZamowien;
import model.przesylki.Maxi;
import model.przesylki.Mini;
import model.przesylki.Sredni;
import service.CenaService;

import static model.enums.RodzajePlatnosci.KARTA;
import static model.enums.RodzajePlatnosci.PRZELEW;
import static model.enums.RozmiarPrzesylki.*;
import static model.enums.SposobDostawy.*;
import static service.CenaService.calculatePrice;

public class Main {

    static int cena(Koszyk k, String typ) {
        //pobieramy te elementy z listy ktorych typ rowny jest podanemu po czym pobieramy tylko ich ceny * ilosc a następnie dodajemy je do zmiennej cena
        double cena = k.getPrzesylki()
                .stream()
                .filter(x -> x.getTypPrzesylki().equals(typ))
                .map(przesylka -> calculatePrice(przesylka, k.getCzyWlascicielMaAbonament()) * przesylka.getIlosc()).mapToDouble(cenaPrzesylek -> cenaPrzesylek).sum();

        return (int) cena;
    }

    public static void main(String[] args) {

        // cennik
        Cennik cennik = Cennik.pobierzCennik();

        // dodawanie nowych cen do cennika
        cennik.dodaj(MINI, "zwykly", 12, 10, 8); 	// jeśli klient odbierze w punkcie: 10 zł/przesyłkę
        // jeśli klient odbierze w automacie: 8 zł/przesyłkę
        // jeśli kurier dostarczy przesyłkę: 12 zł/przesyłkę

        cennik.dodaj(MINI, "ekspres", 16, 14, 12);	// jak wyżej


        cennik.dodaj(SREDNI, "ubezp", 20, 15);		// jeśli kurier dostarczy przesyłkę: 20 zł/przesyłkę
        // jeśli klient odbierze w punkcie: 15 zł/przesyłkę
        // nie ma możliwości odbioru w automacie

        cennik.dodaj(MAXI, "ekspres", 20);		// przesyłka dostarczona tylko przez kuriera: 20 zł/przesyłkę


        // Klient Blyskawica deklaruje kwotę 70 zł na zamówienia przesyłek
        // true oznacza, że klient posiada abonament
        Klient blyskawica = new Klient("Błyskawica", 70, true);

        // Klient Błyskawica dodaje do listy zamówień różne przesyłki:
        // Przesyłka mini - zwykły, ilość: 4, sposób odbioru: w punkcie odbioru
        // Przesyłka średni - zwykly, ilość: 3, sposób odbioru: przez kuriera
        // Przesyłka maxi - ekspres, ilość: 2, sposób odbioru: w automacie
        // Przesyłka mini - ekspres, ilość: 4, sposób odbioru: przez kuriera
        blyskawica.dodaj(new Mini("zwykly", 4, PUNKT));
        blyskawica.dodaj(new Sredni("zwykly", 3, KURIER));
        blyskawica.dodaj(new Maxi("ekspres", 2, KURIER));
        blyskawica.dodaj(new Mini("ekspres", 4, AUTOMAT));

        // Lista zamówień klienta Błyskawica
        ListaZamowien listaBlyskawicy = blyskawica.pobierzListeZamowien();

        System.out.println("Lista zamówień klienta " + listaBlyskawicy);

        // Przed płaceniem, klient przepakuje przesyłki z listy zamówień do koszyka.
        // Możliwe, że na liście zamówień są rodzaje przesyłek niemające ceny w cenniku,
        // w takim przypadku nie trafiłyby do koszyka
        Koszyk koszykBlyskawicy = blyskawica.pobierzKoszyk();
        blyskawica.przepakuj();

        // Co jest na liście zamówień klienta Błyskawica
        System.out.println("Po przepakowaniu, lista zamówień klienta " + blyskawica.pobierzListeZamowien());

        // Co jest w koszyku klienta Błyskawica
        System.out.println("Po przepakowaniu, koszyk klienta " + koszykBlyskawicy);

        // Ile wynosi cena wszystkich przesyłek typu ekspresowego w koszyku klienta Błyskawica
        System.out.println("Przesyłki ekspresowe w koszyku klienta Błyskawica kosztowały:  " + cena(koszykBlyskawicy, "ekspres") + "\n");

        // Klient zapłaci...
        // w przypadku posiadania abonamentu klient dostanie 50% rabatu od każdej przesyłki
        blyskawica.zaplac(KARTA);	// płaci kartą płatniczą, prowizja 1%

        // Ile klientowi Błyskawica zostało pieniędzy?
        System.out.println("Po zapłaceniu, klientowi Błyskawica zostało: " + blyskawica.pobierzPortfel() + " zł" + "\n");

        // Mogło klientowi zabraknąć środków, wtedy aplikacja odłoży niektóre przesyłki w koszyku
        // wpp. koszyk jest pusty po zapłaceniu
        System.out.println("Po zapłaceniu, koszyk klienta " + blyskawica.pobierzKoszyk());
        System.out.println("Po zapłaceniu, koszyk klienta " + koszykBlyskawicy + "\n");

        // Teraz przychodzi klient Żółwik,
        // deklaruje 130 zł na zamówienia
        Klient zolwik = new Klient("Żółwik", 130, false);

        // Zamówił za dużo jak na tę kwotę
        zolwik.dodaj(new Mini("zwykly", 4, KURIER));
        zolwik.dodaj(new Maxi("ekspres", 5, KURIER));

        // Co klient Żółwik ma na swojej liście zamówień
        System.out.println("Lista zamówień klienta " + zolwik.pobierzListeZamowien());

        Koszyk koszykZolwika = zolwik.pobierzKoszyk();
        zolwik.przepakuj();

        // Co jest na liście zamówień klienta Żółwik
        System.out.println("Po przepakowaniu, lista zamówień klienta " + zolwik.pobierzListeZamowien() + "\n");

        // A co jest w koszyku klienta Żółwik
        System.out.println("Po przepakowaniu, koszyk klienta " + zolwik.pobierzKoszyk());

        // klient Żółwik płaci
        zolwik.zaplac(PRZELEW);	// płaci przelewem, bez prowizji

        // Ile klientowi Żółwik zostało pieniędzy?
        System.out.println("Po zapłaceniu, klientowi Żółwik zostało: " + zolwik.pobierzPortfel() + " zł" + "\n");

        // Co zostało w koszyku klienta Żółwik (za mało pieniędzy miał)
        System.out.println("Po zapłaceniu, koszyk klienta " + koszykZolwika);

        zolwik.zwroc(MINI, "zwykly", 1, KURIER);	// zwrot (do koszyka) 1 przesyłki mini zwykłej (z dostawą od kuriera) z ostatniej transakcji

        // Ile klientowi Zółwik zostało pieniędzy?
        System.out.println("Po zwrocie, klientowi Zółwik zostało: " + zolwik.pobierzPortfel() + " zł" + "\n" );

        // Co zostało w koszyku klienta Żółwik
        System.out.println("Po zwrocie, koszyk klienta " + koszykZolwika);


    }
}