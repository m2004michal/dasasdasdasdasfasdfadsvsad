package model.enums;

public enum SposobDostawy {
    PUNKT,
    KURIER,
    AUTOMAT;

    public String getValue(){
        return switch (this){
            case PUNKT -> "w punkcie";
            case KURIER -> "kurier";
            case AUTOMAT -> "automat";
        };
    }
}
