package service;

import model.cennikSekcja.Cennik;
import model.cennikSekcja.ProduktWCenniku;
import model.enums.SposobDostawy;
import model.przesylki.Przesylka;

public class CenaService {

    public static double calculatePrice(Przesylka przesylka, boolean hasAbonament) {
        //szukamy w cenniku pozycji odpowiadającej parametrom naszej paczki i pobieramy cene dla danej pozycji (gdy brak zwracamy 0)
        double cena = Cennik.pobierzCennik()
                .getListaCen()
                .stream()
                .filter(x -> przesylka.getRozmiarPrzesylki() == x.getRozmiarPrzesylki() &&
                        przesylka.getTypPrzesylki().equals(x.getRodzajPrzesylki()) &&
                        hasSposobDostawy(x, przesylka.getSposobDostawy()))
                .findFirst()
                .map(x -> x.pobierzCeneDlaTypuDostawy(przesylka.getSposobDostawy()))
                .orElse(0D);
        //w przypadku abonamentu zmniejszamy cene o polowe
        if (hasAbonament)
            cena /= 2;

        return cena;
    }

    public static boolean hasSposobDostawy(ProduktWCenniku produktWCenniku, SposobDostawy sposobDostawy) {
        switch (sposobDostawy) {
            case AUTOMAT -> {
                if (produktWCenniku.getCenaOdbiorAutomat() > 0) return true;
            }
            case KURIER -> {
                if (produktWCenniku.getCenaDostawa() > 0) return true;
            }
            case PUNKT -> {
                if (produktWCenniku.getCenaOdbiorPunkt() > 0) return true;
            }
        }
        return false;
    }

}
