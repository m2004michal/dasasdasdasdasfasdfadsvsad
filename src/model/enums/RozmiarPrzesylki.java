package model.enums;

public enum RozmiarPrzesylki {
    MINI,
    SREDNI,
    MAXI;

    public String getValue(){
        return switch (this){
            case MINI -> "Mini";
            case SREDNI -> "Sredni";
            case MAXI -> "Maxi";
        };
    }

}
