package model.cennikSekcja;

import model.enums.RozmiarPrzesylki;

import java.util.ArrayList;

public class Cennik {
    public ArrayList<ProduktWCenniku> getListaCen() {
        return listaCen;
    }

    private final ArrayList<ProduktWCenniku> listaCen;
    private static Cennik INSTANCE;

    private Cennik() {
        this.listaCen = new ArrayList<>();
    }
    public static Cennik getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new Cennik();
        }

        return INSTANCE;
    }

    public static Cennik pobierzCennik() {
        return Cennik.getInstance();
    }

    public void dodaj(RozmiarPrzesylki rozmiarPrzesylki, String rodzajPrzesylki,
                      int cenaDostawy, int cenaOdbioruPunkt, int cenaOdbioruAutomat){
        if(listaCen.stream().filter(x ->
                x.getRozmiarPrzesylki().equals(rozmiarPrzesylki) &&
                        x.getRodzajPrzesylki().equals(rodzajPrzesylki)).toList().isEmpty()) {

            listaCen.add(new ProduktWCenniku(rozmiarPrzesylki, rodzajPrzesylki,
                    cenaDostawy, cenaOdbioruPunkt, cenaOdbioruAutomat));
        }
    }
    public void dodaj(RozmiarPrzesylki rozmiarPrzesylki, String rodzajPrzesylki,
                      int cenaDostawy, int cenaOdbioruPunkt){
        listaCen.add(new ProduktWCenniku(rozmiarPrzesylki, rodzajPrzesylki,
                cenaDostawy, cenaOdbioruPunkt, -1));
    }
    public void dodaj(RozmiarPrzesylki rozmiarPrzesylki, String rodzajPrzesylki,
                      int cenaDostawy){
        listaCen.add(new ProduktWCenniku(rozmiarPrzesylki, rodzajPrzesylki,
                cenaDostawy, -1, -1));
    }

    @Override
    public String toString() {
        return "Cennik{" +
                "listaCen=" + listaCen +
                '}';
    }
}
