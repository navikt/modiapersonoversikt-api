package no.nav.sbl.dialogarena.varsel.service;

import no.nav.sbl.dialogarena.varsel.domain.Varsel;
import no.nav.sbl.dialogarena.varsel.domain.Varsel.VarselMelding;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.binding.BrukervarselV1;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Person;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Varselbestilling;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerRequest;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerResponse;
import org.joda.time.DateTime;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

public class VarslerServiceImpl implements VarslerService {

    private static final Logger log = getLogger(VarslerServiceImpl.class);

    @Inject
    private BrukervarselV1 brukervarsel;

    @Override
    public Optional<List<Varsel>> hentAlleVarsler(String fnr) {
        Person bruker = new Person();
        bruker.setIdent(fnr);

        HentVarselForBrukerRequest request = new HentVarselForBrukerRequest();
        request.setBruker(bruker);

        try {
            HentVarselForBrukerResponse response = brukervarsel.hentVarselForBruker(request);

            List<Varselbestilling> varselbestillingsliste = response.getBrukervarsel().getVarselbestillingListe();

            List<Varsel> varsler = varselbestillingsliste.stream()
                    .map(TIL_VARSEL)
                    .collect(toList());

            if (varsler.size() == 0) {
                return empty();
            }

            return of(varsler);
        } catch (Exception ex) {
            log.error("Feilet ved uthenting av varsler.", ex);
            return empty();
        }

    }

    private static Function<XMLGregorianCalendar, DateTime> TIL_DATETIME = xmlGregorianCalendar -> new DateTime(xmlGregorianCalendar.toGregorianCalendar().getTime());

    private static final Function<no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Varsel, VarselMelding> TIL_VARSEL_MELDING = varsel -> {
        String kanal = varsel.getKanal();
        String innhold = varsel.getVarseltekst();
        String mottakerInformasjon = varsel.getKontaktinfo();
        String statusKode = "";
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

        return new VarselMelding(kanal, innhold, mottakerInformasjon, utsendingsTidpunkt, statusKode, feilbeskrivelse, epostemne, url);

    };


    private static Function<no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Varselbestilling, Varsel> TIL_VARSEL = varselBestilling -> {
        String varselType = varselBestilling.getVarseltypeId();

        DateTime sendtTidspunkt = TIL_DATETIME.apply(varselBestilling.getBestilt());
        if (varselBestilling.getSisteVarselutsendelse() != null) {
            sendtTidspunkt = TIL_DATETIME.apply(varselBestilling.getSisteVarselutsendelse());
        }

        String status = "";

        List<VarselMelding> varselMeldingliste = varselBestilling.getVarselListe().stream()
                .map(TIL_VARSEL_MELDING)
                .filter(varselmelding -> varselmelding.utsendingsTidspunkt != null)
                // TODO Trenger vi å sjekke på statuskode? Hvor skal dette gjøres
//                .filter(varselmelding -> varselmelding.statusKode.equals(STATUSKODE_OK))
                .collect(toList());

        return new Varsel(varselType, sendtTidspunkt, status, varselMeldingliste);
    };
}
