package no.nav.sbl.dialogarena.modiabrukerdialog.consumer;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.psak.PsakService;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.UUID;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.option.Optional.optional;

public class PsakServiceImpl implements PsakService {
    @Override
    public Collection<? extends Sak> hentSakerFor(String fnr) {
        return asList(lagPsak());
    }

    private static Sak lagPsak() {
        Sak sak = new Sak();
        sak.fagsystemSaksId = optional("Psak");
        sak.temaKode = "PEN";
        sak.temaNavn = "Pensjon";
        sak.fagsystemKode = "psak";
        sak.fagsystemNavn = "PSAK";
        sak.sakstype = "PENS";
        sak.saksId = optional(UUID.randomUUID().toString());
        sak.opprettetDato = DateTime.now();
        sak.finnesIGsak = false;
        return sak;
    }
}
