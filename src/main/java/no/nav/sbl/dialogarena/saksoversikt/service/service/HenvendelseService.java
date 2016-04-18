package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Behandling;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.FeilendeBaksystemException;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Soknad;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSSoknad;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.function.Predicate;

import static java.util.stream.Collectors.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.Transformers.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem.HENVENDELSE;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Soknad.HenvendelseStatus.FERDIG;
import static no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSHenvendelseStatus.UNDER_ARBEID;
import static no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSHenvendelseStatus.valueOf;
import static no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSHenvendelseType.SOKNADSINNSENDING;
import static org.slf4j.LoggerFactory.getLogger;

public class HenvendelseService {

    private static final Logger LOGGER = getLogger(HenvendelseService.class);

    @Inject
    @Named("henvendelseSoknaderPortType")
    private HenvendelseSoknaderPortType henvendelse;

    public List<Soknad> hentHenvendelsessoknader(String fnr) {
        try {
            return henvendelse.hentSoknadListe(fnr).stream()
                    .filter(IKKE_PAABEGYNT_ETTERSENDING_FRA_SEND_SOKNAD)
                    .map(wsSoknad ->  transformTilSoknad(wsSoknad))
                    .collect(toList());
        } catch (RuntimeException e) {
            LOGGER.error("Det skjedde en uventet feil mot Henvendelse", e);
            throw new FeilendeBaksystemException(HENVENDELSE);
        }
    }

    public List<Soknad> hentPaabegynteSoknader(String fnr) {
        return hentHenvendelsessoknaderMedStatus(Soknad.HenvendelseStatus.UNDER_ARBEID, fnr);
    }

    public List<Behandling> hentKvitteringer(String fnr) {
        return hentHenvendelsessoknaderMedStatus(FERDIG, fnr).stream()
                .map(SOKNAD_TIL_KVITTERING)
                .collect(toList());
    }

    public List<Soknad> hentHenvendelsessoknaderMedStatus(Soknad.HenvendelseStatus status, String fnr) {
        try {
            return hentHenvendelsessoknader(fnr).stream()
                    .filter(soknad -> soknad.getStatus().equals(status))
                    .sorted((o1, o2) -> o2.getSistendretDato().compareTo(o1.getSistendretDato()))
                    .collect(toList());
        } catch (RuntimeException e) {
            LOGGER.error("Det skjedde en uventet feil mot Henvendelse", e);
            throw new FeilendeBaksystemException(HENVENDELSE);
        }
    }

    private static final Predicate<WSSoknad> IKKE_PAABEGYNT_ETTERSENDING_FRA_SEND_SOKNAD = wsSoknad -> !(wsSoknad.isEttersending()
            && valueOf(wsSoknad.getHenvendelseStatus()).equals(UNDER_ARBEID)
            && wsSoknad.getHenvendelseType().equals(SOKNADSINNSENDING.value()));

}
