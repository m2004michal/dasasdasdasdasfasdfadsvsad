package model.cennikSekcja;

import model.enums.RozmiarPrzesylki;

import java.util.ArrayList;

public class Cennik {
    public ArrayList<ProduktWCenniku> getListaCen() {
        return listaCen;
    }

    static final int BRAK_CENY_W_CENNIKU = -1;
    private final ArrayList<ProduktWCenniku> listaCen;
    private static Cennik INSTANCE;

    private Cennik() {
        this.listaCen = new ArrayList<>();
    }

    public static Cennik pobierzCennik() {
        if (INSTANCE == null) {
            INSTANCE = new Cennik();
        }

        return INSTANCE;
    }

    public void dodaj(RozmiarPrzesylki rozmiarPrzesylki, String rodzajPrzesylki,
                      int cenaDostawy, int cenaOdbioruPunkt, int cenaOdbioruAutomat) {
        ProduktWCenniku produktWCenniku = new ProduktWCenniku(rozmiarPrzesylki, rodzajPrzesylki,
                cenaDostawy, cenaOdbioruPunkt, cenaOdbioruAutomat);
        try {
            listaCen.remove(listaCen.stream()
                    .filter(szukanyProduktWCenniku -> szukanyProduktWCenniku.getRozmiarPrzesylki().equals(rozmiarPrzesylki) && szukanyProduktWCenniku.getRodzajPrzesylki().equals(rodzajPrzesylki))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("nie znaleziono pozycji w cenniku")));
            listaCen.add(produktWCenniku);
        } catch (RuntimeException e) {
            listaCen.add(produktWCenniku);
        }

    }

    public void dodaj(RozmiarPrzesylki rozmiarPrzesylki, String rodzajPrzesylki,
                      int cenaDostawy, int cenaOdbioruPunkt) {
        listaCen.add(new ProduktWCenniku(rozmiarPrzesylki, rodzajPrzesylki,
                cenaDostawy, cenaOdbioruPunkt, BRAK_CENY_W_CENNIKU));
    }

    public void dodaj(RozmiarPrzesylki rozmiarPrzesylki, String rodzajPrzesylki,
                      int cenaDostawy) {
        listaCen.add(new ProduktWCenniku(rozmiarPrzesylki, rodzajPrzesylki,
                cenaDostawy, BRAK_CENY_W_CENNIKU, BRAK_CENY_W_CENNIKU));
    }

    @Override
    public String toString() {
        return "Cennik{" +
                "listaCen=" + listaCen +
                '}';
    }
}
