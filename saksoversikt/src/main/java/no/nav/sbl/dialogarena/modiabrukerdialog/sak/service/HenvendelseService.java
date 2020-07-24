package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service;

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Behandling;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.FeilendeBaksystemException;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Soknad;
import no.nav.sbl.util.EnvironmentUtils;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSSoknad;
import org.joda.time.DateTime;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Baksystem.HENVENDELSE;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Soknad.HenvendelseStatus.FERDIG;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.transformers.Transformers.SOKNAD_TIL_KVITTERING;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.transformers.Transformers.transformTilSoknad;
import static no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSHenvendelseStatus.UNDER_ARBEID;
import static no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSHenvendelseStatus.valueOf;
import static no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSHenvendelseType.SOKNADSINNSENDING;
import static org.slf4j.LoggerFactory.getLogger;

public class HenvendelseService {

    private static final Logger LOGGER = getLogger(HenvendelseService.class);

    @Inject
    private HenvendelseSoknaderPortType henvendelse;

    private Predicate<Soknad> fjernSoknaderInnsendtForHL4I2014 = soknad ->
            soknad.getSistendretDato().isAfter(new DateTime(EnvironmentUtils.getRequiredProperty("FJERN_SOKNADER_FOR_DATO")));

    private List<Soknad> hentHenvendelsessoknader(String fnr) {
        try {
            return henvendelse.hentSoknadListe(fnr).stream()
                    .filter(IKKE_PAABEGYNT_ETTERSENDING_FRA_SEND_SOKNAD)
                    .map(wsSoknad -> transformTilSoknad(wsSoknad))
                    .collect(toList());
        } catch (RuntimeException e) {
            LOGGER.error("Det skjedde en uventet feil mot Henvendelse", e);
            throw new FeilendeBaksystemException(HENVENDELSE);
        }
    }

    public List<Soknad> hentPaabegynteSoknader(String fnr) {
        return hentHenvendelsessoknaderMedStatus(Soknad.HenvendelseStatus.UNDER_ARBEID, fnr);
    }

    public List<Soknad> hentInnsendteSoknader(String fnr) {
        return hentHenvendelsessoknaderMedStatus(Soknad.HenvendelseStatus.FERDIG, fnr);
    }

    public List<Behandling> hentKvitteringer(String fnr) {
        return hentHenvendelsessoknaderMedStatus(FERDIG, fnr).stream()
                .map(SOKNAD_TIL_KVITTERING)
                .collect(toList());
    }

    private List<Soknad> hentHenvendelsessoknaderMedStatus(Soknad.HenvendelseStatus status, String fnr) {
        return hentHenvendelsessoknader(fnr).stream()
                .filter(soknad -> soknad.getStatus().equals(status))
                .filter(fjernSoknaderInnsendtForHL4I2014)
                .sorted((o1, o2) -> o2.getSistendretDato().compareTo(o1.getSistendretDato()))
                .collect(toList());
    }

    private static final Predicate<WSSoknad> IKKE_PAABEGYNT_ETTERSENDING_FRA_SEND_SOKNAD = wsSoknad -> !(wsSoknad.isEttersending()
            && valueOf(wsSoknad.getHenvendelseStatus()).equals(UNDER_ARBEID)
            && wsSoknad.getHenvendelseType().equals(SOKNADSINNSENDING.value()));

}
