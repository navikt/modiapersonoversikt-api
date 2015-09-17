package no.nav.sbl.modiabrukerdialog.pip.geografisk.support;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSNAVAnsatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSHentNAVEnhetListeRequest;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNavEnhet;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattEnhetListeFaultGOSYSGeneriskMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattEnhetListeFaultGOSYSNAVAnsattIkkeFunnetMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.*;
import no.nav.sbl.modiabrukerdialog.pip.geografisk.EnhetAttributeLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;

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
    private final Map<String, List<String>> kontorhierarki;


    public DefaultEnhetAttributeLocatorDelegate() {
        kontorhierarki = new HashMap<>();
        kontorhierarki.put(NAV_VAERNES, Arrays.asList("1664", "1665", "1711", "1714", "1717"));
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
                if (lokalEnhet.getEnhetsId().startsWith("17")) {
                    values.addAll(kontorhierarki.get(NAV_VAERNES));
                }
            }
            enhetIdSet.add(lokalEnhet.getEnhetsId());

            for (String enhetId : enhetIdSet) {
                values.add(enhetId);
            }
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
            values.add(enhetsId);
            if (kontorhierarki.containsKey(enhetsId)) {
                values.addAll(kontorhierarki.get(enhetsId));
            }
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

            for (ASBOGOSYSNavEnhet enhet : enheter) {
                enhetIdSet.add(enhet.getEnhetsId());
            }
            return enhetIdSet;
        } catch (HentNAVEnhetGruppeListeFaultGOSYSGeneriskMsg | HentNAVEnhetGruppeListeFaultGOSYSNAVEnhetIkkeFunnetMsg ex) {
            logger.warn("Exception while calling hentNAVEnhetGruppeListe with ansattId {}.", enhetId, ex);
            return Collections.emptySet();
        }
    }
}

