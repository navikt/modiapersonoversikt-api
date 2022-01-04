package no.nav.modiapersonoversikt.service;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.ASBOGOSYSFagomrade;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSHentNAVAnsattFagomradeListeRequest;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSNAVAnsatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNavEnhet;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.*;
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils;
import no.nav.modiapersonoversikt.legacy.api.domain.norg.Ansatt;
import no.nav.modiapersonoversikt.legacy.api.domain.norg.AnsattEnhet;
import no.nav.modiapersonoversikt.legacy.api.service.norg.AnsattService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toList;

public class AnsattServiceImpl implements AnsattService {

    private static final Logger LOG = LoggerFactory.getLogger(AnsattServiceImpl.class);
    private final GOSYSNAVansatt ansattWS;

    @Autowired
    public AnsattServiceImpl(GOSYSNAVansatt ansattWS) {
        this.ansattWS = ansattWS;
    }

    public List<AnsattEnhet> hentEnhetsliste() {
        return AuthContextUtils.getIdent()
                .map((ident) -> {
                    try {
                        ASBOGOSYSNAVAnsatt request = new ASBOGOSYSNAVAnsatt();
                        request.setAnsattId(ident);
                        return ansattWS.hentNAVAnsattEnhetListe(request)
                                .getNAVEnheter()
                                .stream()
                                .map(TIL_ANSATTENHET)
                                .collect(toList());
                    } catch (HentNAVAnsattEnhetListeFaultGOSYSNAVAnsattIkkeFunnetMsg | HentNAVAnsattEnhetListeFaultGOSYSGeneriskMsg e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElse(emptyList());

    }

    public String hentAnsattNavn(String ident) {
        try {
            ASBOGOSYSNAVAnsatt ansattRequest = new ASBOGOSYSNAVAnsatt();
            ansattRequest.setAnsattId(ident);
            return ansattWS.hentNAVAnsatt(ansattRequest).getAnsattNavn();
        } catch (HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg | HentNAVAnsattFaultGOSYSGeneriskfMsg e) {
            throw new RuntimeException("Noe gikk galt ved henting av ansatt med ident " + ident, e);
        }
    }

    public @NotNull
    Set<String> hentAnsattFagomrader(@NotNull String ident, @NotNull String enhet) {
        try {
            ASBOGOSYSHentNAVAnsattFagomradeListeRequest request = new ASBOGOSYSHentNAVAnsattFagomradeListeRequest();
            request.setAnsattId(ident);
            request.setEnhetsId(enhet);

            return ansattWS
                    .hentNAVAnsattFagomradeListe(request)
                    .getFagomrader()
                    .stream()
                    .map(ASBOGOSYSFagomrade::getFagomradeKode)
                    .collect(Collectors.toSet());
        } catch (HentNAVAnsattFagomradeListeFaultGOSYSNAVAnsattIkkeFunnetMsg e) {
            LOG.warn("Fant ikke ansatt med ident {}.", ident, e);
            return emptySet();
        } catch (HentNAVAnsattFagomradeListeFaultGOSYSGeneriskMsg e) {
            LOG.warn("Feil oppsto under henting av ansatt fagområdeliste for enhet med enhetsId {}.", enhet, e);
            return emptySet();
        } catch (Exception e) {
            LOG.warn("Ukjent feil ved henting av fagområdelsite for ident {} enhet {}.", ident, enhet, e);
            return emptySet();
        }
    }

    @Override
    public List<Ansatt> ansatteForEnhet(AnsattEnhet enhet) {
        ASBOGOSYSNavEnhet request = new ASBOGOSYSNavEnhet();
        request.setEnhetsId(enhet.enhetId);
        request.setEnhetsNavn(enhet.enhetNavn);
        try {
            return ansattWS.hentNAVAnsattListe(request)
                    .getNAVAnsatte()
                    .stream()
                    .map(ansatt -> new Ansatt(ansatt.getFornavn(), ansatt.getEtternavn(), ansatt.getAnsattId()))
                    .collect(toList());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return emptyList();
        }
    }

    protected static final Function<ASBOGOSYSNavEnhet, AnsattEnhet> TIL_ANSATTENHET = asbogosysNavEnhet ->
            new AnsattEnhet(asbogosysNavEnhet.getEnhetsId(), asbogosysNavEnhet.getEnhetsNavn());
}
