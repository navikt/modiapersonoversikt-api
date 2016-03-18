package no.nav.sbl.modiabrukerdialog.pip.geografisk.support;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSNAVAnsatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSHentNAVEnhetListeRequest;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNavEnhet;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattEnhetListeFaultGOSYSGeneriskMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattEnhetListeFaultGOSYSNAVAnsattIkkeFunnetMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.*;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.AnsattEnhetUtil;
import no.nav.sbl.modiabrukerdialog.pip.geografisk.EnhetAttributeLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

/**
 * Delegate that retrieves enhet information from NORG and NAVAnsatt services.
 */
public class DefaultEnhetAttributeLocatorDelegate implements EnhetAttributeLocatorDelegate {

    public static final String NAV_VAERNES = "1783";
    private static Logger logger = LoggerFactory.getLogger(EnhetAttributeLocator.class);
    @Inject
    private GOSYSNAVansatt ansattService;
    @Inject
    private GOSYSNAVOrgEnhet enhetService;


    public DefaultEnhetAttributeLocatorDelegate() {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Set<String> getFylkesenheterForAnsatt(String ansattId) {
        Set<String> values = new HashSet<>();

        List<ASBOGOSYSNavEnhet> enheter = hentLokalEnheter(ansattId);
        for (ASBOGOSYSNavEnhet lokalEnhet : enheter) {
            Set<String> enhetIdSet;
            //Spesial enhet
            if ("SPESEN".equalsIgnoreCase(lokalEnhet.getOrgNivaKode())) {
                enhetIdSet = hentUnderenheter(lokalEnhet.getEnhetsId());
            } else {
                enhetIdSet = hentFylkesEnheter(lokalEnhet);
                enhetIdSet.addAll(AnsattEnhetUtil.hentEkstraEnheterForFylke(lokalEnhet.getEnhetsId()));
            }
            enhetIdSet.add(lokalEnhet.getEnhetsId());

            values.addAll(enhetIdSet.stream().collect(toList()));
        }

        return values;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Set<String> getLokalEnheterForAnsatt(String ansattId) {
        Set<String> values = new HashSet<>();
        List<ASBOGOSYSNavEnhet> enheter = hentLokalEnheter(ansattId);
        for (ASBOGOSYSNavEnhet enhet : enheter) {
            String enhetsId = enhet.getEnhetsId();
            values.addAll(AnsattEnhetUtil.hentEnheterForValgtEnhet(enhetsId));
        }
        return values;
    }

    /**
     * Henter enheter for en ansatt.
     *
     * @param ansattId
     * @return list av enhet objekter
     */
    private List<ASBOGOSYSNavEnhet> hentLokalEnheter(String ansattId) {
        try {
            ASBOGOSYSNAVAnsatt ansatt = new ASBOGOSYSNAVAnsatt();
            ansatt.setAnsattId(ansattId);

            List<ASBOGOSYSNavEnhet> enheter = ansattService.hentNAVAnsattEnhetListe(ansatt).getNAVEnheter();
            return enheter;
        } catch (HentNAVAnsattEnhetListeFaultGOSYSNAVAnsattIkkeFunnetMsg | HentNAVAnsattEnhetListeFaultGOSYSGeneriskMsg ex) {
            logger.warn("Exception while calling hentNAVAnsattEnhetListe with ansattId {}.", ansattId, ex);
            return Collections.emptyList();
        }
    }

    /**
     * Henter ID til alle enheter i samme fylke som gitte enheten.
     *
     * @param enhet
     * @return set av enhet id
     */
    private Set<String> hentFylkesEnheter(ASBOGOSYSNavEnhet enhet) {
        Set<String> enhetIdSet = new HashSet<>();

        List<ASBOGOSYSNavEnhet> fylkesEnheter;

        try {
            ASBOGOSYSHentNAVEnhetListeRequest request = new ASBOGOSYSHentNAVEnhetListeRequest();
            request.setNAVEnhet(enhet);
            request.setTypeOrganisertUnder("FYLKE");
            fylkesEnheter = enhetService.hentNAVEnhetListe(request).getNAVEnheter();
        } catch (HentNAVEnhetListeFaultGOSYSGeneriskMsg | HentNAVEnhetListeFaultGOSYSNAVEnhetIkkeFunnetMsg ex) {
            logger.info("Feil ved kall på hentNAVEnhetListe", ex.getMessage());
            return Collections.emptySet();
        }

        for (ASBOGOSYSNavEnhet fylkesEnhet : fylkesEnheter) {
            enhetIdSet.add(fylkesEnhet.getEnhetsId());
            enhetIdSet.addAll(hentUnderenheter(fylkesEnhet.getEnhetsId()));
        }
        //Hvis enheten er selv fylkes enhet, må den legges til.
        if ("FYLKE".equalsIgnoreCase(enhet.getOrgNivaKode())) {
            enhetIdSet.add(enhet.getEnhetsId());
            enhetIdSet.addAll(hentUnderenheter(enhet.getEnhetsId()));
        }

        return enhetIdSet;
    }

    /**
     * Henter undernehet ID-er for en enhet basert på enhetId.
     *
     * @param enhetId
     * @return set av enhet id
     */
    private Set<String> hentUnderenheter(String enhetId) {
        try {
            Set<String> enhetIdSet = new HashSet<>();

            ASBOGOSYSNavEnhet request = new ASBOGOSYSNavEnhet();
            request.setEnhetsId(enhetId);
            List<ASBOGOSYSNavEnhet> enheter = enhetService.hentNAVEnhetGruppeListe(request).getNAVEnheter();

            enhetIdSet.addAll(enheter.stream().map(ASBOGOSYSNavEnhet::getEnhetsId).collect(toList()));
            return enhetIdSet;
        } catch (HentNAVEnhetGruppeListeFaultGOSYSGeneriskMsg | HentNAVEnhetGruppeListeFaultGOSYSNAVEnhetIkkeFunnetMsg ex) {
            logger.warn("Exception while calling hentNAVEnhetGruppeListe with ansattId {}.", enhetId, ex);
            return Collections.emptySet();
        }
    }
}

