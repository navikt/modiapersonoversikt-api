package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service;

import no.nav.common.auth.SubjectHandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Sakstema;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere.TjenesteResultatWrapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.interfaces.TilgangskontrollService;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangTilTemaData;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.TRUE;
import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils.hentValgtEnhet;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Feilmelding.*;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.slf4j.LoggerFactory.getLogger;

public class TilgangskontrollServiceImpl implements TilgangskontrollService {

    @Inject
    private Tilgangskontroll tilgangskontroll;
    @Inject
    private AnsattService ansattService;

    public final static String TEMAKODE_BIDRAG = "BID";
    private static final Logger logger = getLogger(TilgangskontrollService.class);

    public TjenesteResultatWrapper harSaksbehandlerTilgangTilDokument(HttpServletRequest request, DokumentMetadata journalpostMetadata, String fnr, String urlTemakode) {
        String temakode = journalpostMetadata.getTemakode();

        if (temakodeErBidrag(temakode)) {
            return new TjenesteResultatWrapper(TEMAKODE_ER_BIDRAG);
        } else if (erJournalfortPaAnnetTema(urlTemakode, journalpostMetadata)) {
            return new TjenesteResultatWrapper(JOURNALFORT_ANNET_TEMA, journalfortAnnetTemaEktraFeilInfo(journalpostMetadata.getTemakodeVisning(), fnr));
        } else if (!journalpostMetadata.isErJournalfort()) {
            return new TjenesteResultatWrapper(IKKE_JOURNALFORT, ikkeJournalfortEkstraFeilInfo(fnr));
        } else if (journalpostMetadata.getFeilWrapper().getInneholderFeil()) {
            return new TjenesteResultatWrapper(journalpostMetadata.getFeilWrapper().getFeilmelding());
        }

        return new TjenesteResultatWrapper(TRUE);
    }

    public boolean harGodkjentEnhet(HttpServletRequest request) {
        String valgtEnhet = hentValgtEnhet(request);
        List<String> enhetsListe = ansattService.hentEnhetsliste().stream().map(ansattEnhet -> ansattEnhet.enhetId).collect(toList());

        valgtEnhet = settEnhetDersomCookieIkkeErSatt(valgtEnhet, enhetsListe);

        if (!enhetsListe.contains(valgtEnhet)) {
            String ident = SubjectHandler.getIdent().orElseThrow(() -> new RuntimeException("Fant ikke ident"));
            logger.warn("{} har ikke tilgang til enhet {}.", ident, valgtEnhet);
            return false;
        }
        return true;
    }

    private String settEnhetDersomCookieIkkeErSatt(String valgtEnhet, List<String> enhetsListe) {
        if ("".equals(valgtEnhet)) {
            valgtEnhet = enhetsListe.get(0);
        }
        return valgtEnhet;
    }

    public boolean harEnhetTilgangTilTema(String temakode, String valgtEnhet) {
        if (isNotBlank(temakode)) {
            return tilgangskontroll
                    .check(Policies.tilgangTilTema.with(new TilgangTilTemaData(valgtEnhet, temakode)))
                    .getDecision()
                    .isPermit();
        }
        return true;
    }

    public void markerIkkeJournalforte(List<Sakstema> sakstemaList) {
        sakstemaList
                .forEach(sakstema -> sakstema.dokumentMetadata
                        .stream()
                        .filter(dokumentMetadata -> !dokumentMetadata.isErJournalfort())
                        .map(dokumentMetadata -> dokumentMetadata.withFeilWrapper(IKKE_JOURNALFORT))
                        .collect(toList()));
    }

    private boolean temakodeErBidrag(String temakode) {
        return TEMAKODE_BIDRAG.equals(temakode);
    }

    private boolean erJournalfortPaAnnetTema(String temakode, DokumentMetadata dokumentMetadata) {
        return temakode != null && !dokumentMetadata.getTemakode().equals(temakode);
    }

    private Map journalfortAnnetTemaEktraFeilInfo(String temanavn, String fnr) {
        Map map = new HashMap<>();
        map.put("temanavn", temanavn);
        map.put("fnr", fnr);
        return map;
    }

    private Map ikkeJournalfortEkstraFeilInfo(String fnr) {
        Map map = new HashMap<>();
        map.put("fnr", fnr);
        return map;
    }
}
