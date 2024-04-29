package model.cennikSekcja;

import model.enums.RozmiarPrzesylki;
import model.enums.SposobDostawy;

public class ProduktWCenniku {


    private final RozmiarPrzesylki rozmiarPrzesylki;
    private final String rodzajPrzesylki;
    private final int cenaOdbiorPunkt;
    private final int cenaOdbiorAutomat;
    private final int cenaDostawa;

    public ProduktWCenniku(RozmiarPrzesylki rozmiarPrzesylki, String rodzajPrzesylki, int cenaDostawa, int cenaOdbiorPunkt, int cenaOdbiorAutomat) {
        this.rozmiarPrzesylki = rozmiarPrzesylki;
        this.rodzajPrzesylki = rodzajPrzesylki;
        this.cenaDostawa = cenaDostawa;
        this.cenaOdbiorPunkt = cenaOdbiorPunkt;
        this.cenaOdbiorAutomat = cenaOdbiorAutomat;
    }

    public double pobierzCeneDlaTypuDostawy(SposobDostawy sposobDostawy) {
        return switch (sposobDostawy) {
            case AUTOMAT -> this.getCenaOdbiorAutomat();
            case KURIER -> this.getCenaDostawa();
            case PUNKT -> this.getCenaOdbiorPunkt();
        };
    }



    public RozmiarPrzesylki getRozmiarPrzesylki() {
        return rozmiarPrzesylki;
    }

    public String getRodzajPrzesylki() {
        return rodzajPrzesylki;
    }

    public int getCenaOdbiorPunkt() {
        return cenaOdbiorPunkt;
    }

    public int getCenaOdbiorAutomat() {
        return cenaOdbiorAutomat;
    }

    public int getCenaDostawa() {
        return cenaDostawa;
    }

    @Override
    public String toString() {
        return "ProduktWCenniku{" +
                "rozmiarPrzesylki=" + rozmiarPrzesylki +
                ", rodzajPrzesyłki='" + rodzajPrzesylki + '\'' +
                ", cenaOdbiorPunkt=" + cenaOdbiorPunkt +
                ", cenaOdbiorAutomat=" + cenaOdbiorAutomat +
                ", cenaDostawa=" + cenaDostawa +
                '}';
    }


}
