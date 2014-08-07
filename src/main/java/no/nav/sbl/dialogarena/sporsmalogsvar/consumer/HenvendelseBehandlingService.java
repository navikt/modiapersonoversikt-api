package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLJournalfortInformasjon;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SPORSMAL;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.TIL_MELDING;

public class HenvendelseBehandlingService {

    @Inject
    private HenvendelsePortType henvendelsePortType;
    @Inject
    protected BehandleHenvendelsePortType behandleHenvendelsePortType;

    public List<Melding> hentMeldinger(String fnr) {
        List<String> typer = Arrays.asList(SPORSMAL.name(), SVAR.name(), REFERAT.name());
        return on(henvendelsePortType.hentHenvendelseListe(new WSHentHenvendelseListeRequest().withFodselsnummer(fnr).withTyper(typer)).getAny()).map(TIL_MELDING).collect();
    }

    public void oppdaterJournalfortInformasjonIHenvendelse(Sak sak, String journalpostId, Melding melding) {
        behandleHenvendelsePortType.oppdaterJournalfortInformasjon(melding.id,
                new XMLJournalfortInformasjon()
                        .withJournalfortTema(sak.tema)
                        .withJournalfortDato(DateTime.now())
                        .withJournalpostId(journalpostId)
                        .withJournalfortSaksId(sak.saksId)
                        .withJournalforerNavIdent(getSubjectHandler().getUid())
        );
    }

}
