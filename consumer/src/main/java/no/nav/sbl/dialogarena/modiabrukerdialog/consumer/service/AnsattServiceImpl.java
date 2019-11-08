package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSNAVAnsatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNavEnhet;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.*;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.Ansatt;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.Collections.emptyList;
import static no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler;

public class AnsattServiceImpl implements AnsattService {

    private final GOSYSNAVansatt ansattWS;

    @Inject
    public AnsattServiceImpl(GOSYSNAVansatt ansattWS) {
        this.ansattWS = ansattWS;
    }

    public List<AnsattEnhet> hentEnhetsliste() {
        String ident = getSubjectHandler().getUid();
        if (ident == null || ident.isEmpty()) {
            return emptyList();
        }
        
        ASBOGOSYSNAVAnsatt hentNAVAnsattEnhetListeRequest = new ASBOGOSYSNAVAnsatt();
        hentNAVAnsattEnhetListeRequest.setAnsattId(ident);
        try {
            return ansattWS.hentNAVAnsattEnhetListe(hentNAVAnsattEnhetListeRequest)
                    .getNAVEnheter()
                    .stream()
                    .map(TIL_ANSATTENHET)
                    .collect(toList());

        } catch (HentNAVAnsattEnhetListeFaultGOSYSNAVAnsattIkkeFunnetMsg | HentNAVAnsattEnhetListeFaultGOSYSGeneriskMsg e) {
            throw new RuntimeException(e);
        }
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
            throw new RuntimeException(e);
        }
    }

    protected static final Function<ASBOGOSYSNavEnhet, AnsattEnhet> TIL_ANSATTENHET = asbogosysNavEnhet ->
            new AnsattEnhet(asbogosysNavEnhet.getEnhetsId(), asbogosysNavEnhet.getEnhetsNavn());
}
