package no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.personoppslag;

import java.util.Objects;

public class KontaktpersonMedIdNummerSomAdressat {
    private Long idNummer;

    public Long getIdNummer() {
        return idNummer;
    }

    public void setIdNummer(Long idNummer) {
        this.idNummer = idNummer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KontaktpersonMedIdNummerSomAdressat that = (KontaktpersonMedIdNummerSomAdressat) o;
        return Objects.equals(getIdNummer(), that.getIdNummer());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getIdNummer());
    }
}
