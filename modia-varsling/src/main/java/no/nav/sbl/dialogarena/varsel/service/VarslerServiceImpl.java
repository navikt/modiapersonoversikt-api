package no.nav.sbl.dialogarena.varsel.service;

import no.nav.sbl.dialogarena.varsel.domain.Varsel;
import no.nav.sbl.dialogarena.varsel.domain.Varsel.VarselMelding;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.BrukervarselV1;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSPerson;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSVarsel;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSVarselbestilling;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.WSHentVarselForBrukerRequest;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.WSHentVarselForBrukerResponse;
import org.joda.time.DateTime;
import org.slf4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

public class VarslerServiceImpl implements VarslerService {

    private static final Logger log = getLogger(VarslerServiceImpl.class);

    @Autowired
    private BrukervarselV1 brukervarsel;

    @Override
    public Optional<List<Varsel>> hentAlleVarsler(String fnr) {
        WSHentVarselForBrukerRequest request = new WSHentVarselForBrukerRequest()
                .withBruker(new WSPerson().withIdent(fnr));

        try {
            WSHentVarselForBrukerResponse response = brukervarsel.hentVarselForBruker(request);

            List<WSVarselbestilling> varselbestillingsliste = response.getBrukervarsel().getVarselbestillingListe();

            List<Varsel> varsler = varselbestillingsliste.stream()
                    .map(TIL_VARSEL)
                    .collect(toList());

            if (varsler.size() == 0) {
                return of(emptyList());
            }

            return of(varsler);
        } catch (Exception ex) {
            log.error("Feilet ved uthenting av varsler.", ex);
            return empty();
        }

    }

    private static Function<XMLGregorianCalendar, DateTime> TIL_DATETIME = (xmlGregorianCalendar) -> new DateTime(xmlGregorianCalendar.toGregorianCalendar().getTime());

    private static final Function<WSVarsel, VarselMelding> TIL_VARSEL_MELDING = (varsel) -> {
        String kanal = varsel.getKanal();
        String innhold = varsel.getVarseltekst();
        String mottakerInformasjon = varsel.getKontaktinfo();
        XMLGregorianCalendar sendtDato = varsel.getSendt();
        XMLGregorianCalendar distribuertDato = varsel.getDistribuert();
        DateTime utsendingsTidpunkt = null;
        if (sendtDato != null){
            utsendingsTidpunkt = TIL_DATETIME.apply(sendtDato);
        }
        if (distribuertDato != null) {
            utsendingsTidpunkt = TIL_DATETIME.apply(distribuertDato);
        }
        String feilbeskrivelse = "";
        String epostemne = varsel.getVarseltittel();
        String url = varsel.getVarselURL();
        boolean erRevarsel = varsel.isReVarsel();

        return new VarselMelding(kanal, innhold, mottakerInformasjon, utsendingsTidpunkt, feilbeskrivelse, epostemne, url, erRevarsel);

    };


    private static Function<WSVarselbestilling, Varsel> TIL_VARSEL = (varselBestilling) -> {
        String varselType = varselBestilling.getVarseltypeId();

        DateTime sendtTidspunkt = TIL_DATETIME.apply(varselBestilling.getBestilt());
        if (varselBestilling.getSisteVarselutsendelse() != null) {
            sendtTidspunkt = TIL_DATETIME.apply(varselBestilling.getSisteVarselutsendelse());
        }

        List<VarselMelding> varselMeldingliste = varselBestilling.getVarselListe().stream()
                .map(TIL_VARSEL_MELDING)
                .collect(toList());

        boolean erRevarsling = varselMeldingliste.stream()
                .filter(varselMelding -> varselMelding.erRevarsel)
                .findAny()
                .isPresent();

        return new Varsel(varselType, sendtTidspunkt, varselMeldingliste, erRevarsling);
    };
}
