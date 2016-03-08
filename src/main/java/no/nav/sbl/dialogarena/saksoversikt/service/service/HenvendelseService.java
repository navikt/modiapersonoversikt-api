package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Kvittering;
import no.nav.sbl.dialogarena.saksoversikt.service.utils.FeilendeBaksystemException;
import no.nav.sbl.dialogarena.saksoversikt.service.utils.comparator.OmvendtKronologiskSistEndretDatoComparator;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSSoknad;
import org.apache.commons.collections15.Predicate;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

import static java.util.stream.Collectors.*;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.sbl.dialogarena.saksoversikt.service.utils.Transformers.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.utils.Transformers.SOKNAD;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem.HENVENDELSE;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad.HenvendelseStatus.FERDIG;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad.STATUS;
import static no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSHenvendelseStatus.UNDER_ARBEID;
import static no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSHenvendelseStatus.valueOf;
import static no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSHenvendelseType.SOKNADSINNSENDING;
import static org.slf4j.LoggerFactory.getLogger;

public class HenvendelseService {

    private static final Logger LOGGER = getLogger(HenvendelseService.class);

    @Inject
    @Named("henvendelseSoknaderPortType")
    private HenvendelseSoknaderPortType henvendelse;

    public List<Record<Soknad>> hentHenvendelsessoknader(String fnr) {
        try {
            return on(henvendelse.hentSoknadListe(fnr)).filter(IKKE_PAABEGYNT_ETTERSENDING_FRA_SEND_SOKNAD).map(SOKNAD).collect();
        } catch (RuntimeException e) {
            LOGGER.error("Det skjedde en uventet feil mot Henvendelse", e);
            throw new FeilendeBaksystemException(HENVENDELSE);
        }
    }

    public List<Record<Kvittering>> hentKvitteringer(String fnr) {
        return hentHenvendelsessoknaderMedStatus(FERDIG, fnr).stream().map(SOKNAD_TIL_KVITTERING::transform).collect(toList());
    }

    public List<Record<Soknad>> hentHenvendelsessoknaderMedStatus(Soknad.HenvendelseStatus status, String fnr) {
        try {
            return on(hentHenvendelsessoknader(fnr)).filter(where(STATUS, equalTo(status))).collect(new OmvendtKronologiskSistEndretDatoComparator());
        } catch (RuntimeException e) {
            LOGGER.error("Det skjedde en uventet feil mot Henvendelse", e);
            throw new FeilendeBaksystemException(HENVENDELSE);
        }
    }

    private static final Predicate<WSSoknad> IKKE_PAABEGYNT_ETTERSENDING_FRA_SEND_SOKNAD = wsSoknad -> !(wsSoknad.isEttersending()
            && valueOf(wsSoknad.getHenvendelseStatus()).equals(UNDER_ARBEID)
            && wsSoknad.getHenvendelseType().equals(SOKNADSINNSENDING.value()));

}
