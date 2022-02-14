package no.nav.modiapersonoversikt.legacy.sak.service;

import no.nav.modiapersonoversikt.legacy.sak.providerdomain.DokumentMetadata;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.Sakstema;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.resultatwrappere.TjenesteResultatWrapper;
import no.nav.modiapersonoversikt.legacy.sak.service.interfaces.TilgangskontrollService;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangTilTemaData;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll;
import org.slf4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.TRUE;
import static java.util.stream.Collectors.toList;
import static no.nav.modiapersonoversikt.legacy.sak.providerdomain.Feilmelding.*;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.slf4j.LoggerFactory.getLogger;

public class TilgangskontrollServiceImpl implements TilgangskontrollService {

    @Autowired
    private Tilgangskontroll tilgangskontroll;

    public final static String TEMAKODE_BIDRAG = "BID";
    private static final Logger logger = getLogger(TilgangskontrollService.class);

    public TjenesteResultatWrapper harSaksbehandlerTilgangTilDokument(HttpServletRequest request, DokumentMetadata journalpostMetadata, String fnr, String urlTemakode) {
        if (erJournalfortPaAnnetTema(urlTemakode, journalpostMetadata)) {
            return new TjenesteResultatWrapper(JOURNALFORT_ANNET_TEMA, journalfortAnnetTemaEktraFeilInfo(journalpostMetadata.getTemakodeVisning(), fnr));
        } else if (!journalpostMetadata.isErJournalfort()) {
            return new TjenesteResultatWrapper(IKKE_JOURNALFORT, ikkeJournalfortEkstraFeilInfo(fnr));
        } else if (journalpostMetadata.getFeilWrapper().getInneholderFeil()) {
            return new TjenesteResultatWrapper(journalpostMetadata.getFeilWrapper().getFeilmelding());
        }

        return new TjenesteResultatWrapper(TRUE);
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
        Map<String, String> map = new HashMap<>();
        map.put("temanavn", temanavn);
        map.put("fnr", fnr);
        return map;
    }

    private Map ikkeJournalfortEkstraFeilInfo(String fnr) {
        Map<String, String> map = new HashMap<>();
        map.put("fnr", fnr);
        return map;
    }
}
