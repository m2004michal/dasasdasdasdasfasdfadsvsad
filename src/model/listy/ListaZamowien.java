package model.listy;

import model.przesylki.Przesylka;

import java.util.ArrayList;

public class ListaZamowien {

    private ArrayList<Przesylka> przesylki;
    private final String nazwaWlascicielaKoszyka;

    public ListaZamowien(String nazwaWlascicielaKoszyka) {
        przesylki = new ArrayList<>();
        this.nazwaWlascicielaKoszyka = nazwaWlascicielaKoszyka;
    }

    //przy dodawaniu do listy sprawdzamy czy identyczny element juz w tej liscie widnieje(jesli tak zwiekszamy jego ilosc o ilosc dodawanej przesylki)(jesli nie - dodajemy produkt do listy)
    public void dodajDoListy(Przesylka przesylkaDoDodania) {
        Przesylka przesylkaDoListy = przesylki.stream()
                .filter(przesylka -> przesylka.getTypPrzesylki().equals(przesylkaDoDodania.getTypPrzesylki()) &&
                        przesylka.getSposobDostawy().equals(przesylkaDoDodania.getSposobDostawy()) &&
                        przesylka.getRozmiarPrzesylki().equals(przesylkaDoDodania.getRozmiarPrzesylki()) &&
                        przesylka.getHasAbonament() == przesylkaDoDodania.getHasAbonament())
                .findFirst()
                .map(przesylka -> {
                    przesylka.setIlosc(przesylka.getIlosc() + przesylkaDoDodania.getIlosc());
                    przesylki.remove(przesylka);
                    return przesylka;
                })
                .orElse(przesylkaDoDodania);

        przesylki.add(przesylkaDoListy);

    }

    @Override
    public String toString() {
        StringBuilder przesylkiDoWydruku = new StringBuilder();
        if (przesylki.isEmpty())
            return "-- pusto";
        for (Przesylka przesylka : przesylki) {
            przesylkiDoWydruku.append(przesylka.toString()).append("\n");
        }
        return getNazwaWlascicielaKoszyka() + "\n" + przesylkiDoWydruku;
    }

    public ArrayList<Przesylka> getPrzesylki() {
        return przesylki;
    }

    public String getNazwaWlascicielaKoszyka() {
        return nazwaWlascicielaKoszyka;
    }

    public void ustawListe(ArrayList<Przesylka> nowaListaZyczen) {
        this.przesylki = nowaListaZyczen;
    }

}
