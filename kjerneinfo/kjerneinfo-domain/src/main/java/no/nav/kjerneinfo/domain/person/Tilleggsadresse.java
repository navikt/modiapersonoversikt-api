package no.nav.kjerneinfo.domain.person;

public interface Tilleggsadresse {
    String TILLEGGSADRESSETYPE_OFFISIELL_ADRESSE = "Offisiell adresse";
    String TILLEGGSADRESSETYPE_MATRIKKELADRESSE = "Matrikkeladresse";

    String getTilleggsadresseMedType();
    String getTilleggsadresseType();
    String getTilleggsadresse();
}
