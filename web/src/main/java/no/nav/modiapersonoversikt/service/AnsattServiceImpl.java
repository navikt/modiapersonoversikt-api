package no.nav.modiapersonoversikt.service;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSNAVAnsatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNavEnhet;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.*;
import no.nav.common.auth.subject.SubjectHandler;
import no.nav.modiapersonoversikt.api.domain.norg.Ansatt;
import no.nav.modiapersonoversikt.api.domain.norg.AnsattEnhet;
import no.nav.modiapersonoversikt.api.service.norg.AnsattService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class AnsattServiceImpl implements AnsattService {

    private static final Logger LOG = LoggerFactory.getLogger(AnsattServiceImpl.class);
    private final GOSYSNAVansatt ansattWS;

    @Autowired
    public AnsattServiceImpl(GOSYSNAVansatt ansattWS) {
        this.ansattWS = ansattWS;
    }

    public List<AnsattEnhet> hentEnhetsliste() {
        return SubjectHandler.getIdent()
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
