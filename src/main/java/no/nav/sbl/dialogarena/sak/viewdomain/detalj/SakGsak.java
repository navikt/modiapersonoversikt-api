package no.nav.sbl.dialogarena.sak.viewdomain.detalj;

import org.joda.time.DateTime;

import java.util.Optional;

public class SakGsak {

    public Optional<String> saksId = Optional.empty();
    public Optional<String> fagsystemSaksId = Optional.empty();
    public String temaKode, temaNavn, fagsystemKode, fagsystemNavn, sakstype;
    public DateTime opprettetDato;
    public Boolean finnesIGsak = false, finnesIPsak = false;

}
