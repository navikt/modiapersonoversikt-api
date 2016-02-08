package no.nav.sbl.modiabrukerdialog.pip.journalforing.support;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.ASBOGOSYSFagomrade;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSHentNAVAnsattFagomradeListeRequest;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFagomradeListeFaultGOSYSGeneriskMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFagomradeListeFaultGOSYSNAVAnsattIkkeFunnetMsg;
import no.nav.sbl.modiabrukerdialog.pip.journalforing.JournalfortTemaAttributeLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.*;

public class DefaultJournalfortTemaAttributeLocatorDelegate implements JournalfortTemaAttributeLocatorDelegate {

    private static Logger logger = LoggerFactory.getLogger(JournalfortTemaAttributeLocator.class);

    @Inject
    private GOSYSNAVansatt ansattService;

    public DefaultJournalfortTemaAttributeLocatorDelegate() {
    }

    @Override
    public Set<String> getTemagrupperForAnsattesValgteEnhet(String ansattId, String valgtEnhet) {
        try {
            ASBOGOSYSHentNAVAnsattFagomradeListeRequest ansattFagomraderRequest = new ASBOGOSYSHentNAVAnsattFagomradeListeRequest();
            ansattFagomraderRequest.setAnsattId(ansattId);
            ansattFagomraderRequest.setEnhetsId(valgtEnhet);

            List<ASBOGOSYSFagomrade> fagomrader = ansattService.hentNAVAnsattFagomradeListe(ansattFagomraderRequest).getFagomrader();

            Set<String> temaSet = fagomrader.stream().map(ASBOGOSYSFagomrade::getFagomradeKode).collect(toSet());

            return temaSet;
        } catch (HentNAVAnsattFagomradeListeFaultGOSYSGeneriskMsg e) {
            logger.warn("Feil oppsto under henting av ansatt fagområdeliste for enhet med enhetsId {}.", valgtEnhet, e);
            return emptySet();
        } catch (HentNAVAnsattFagomradeListeFaultGOSYSNAVAnsattIkkeFunnetMsg e) {
            logger.warn("Fant ikke ansatt med ident {}.", ansattId, e);
            return emptySet();
        }
    }
}
