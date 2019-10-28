package no.nav.kjerneinfo.domain.person;

public interface Tilleggsadresse {
    public static final String TILLEGGSADRESSETYPE_OFFISIELL_ADRESSE = "Offisiell adresse";
    public static final String TILLEGGSADRESSETYPE_MATRIKKELADRESSE = "Matrikkeladresse";

    String getTilleggsadresseMedType();
    String getTilleggsadresseType();
    String getTilleggsadresse();
}
