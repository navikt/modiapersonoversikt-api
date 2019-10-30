package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.*;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GosysNavAnsattPortTypeMock.NAV_ENHET_LISTE;

@Configuration
public class GosysNavOrgEnhetPortTypeMock {

    @Bean
    public GOSYSNAVOrgEnhet orgEnhet() {
        return createGosysNavOrgEnhetPortTypeMock();
    }

    public static GOSYSNAVOrgEnhet createGosysNavOrgEnhetPortTypeMock() {
        return new GOSYSNAVOrgEnhet() {
            @Override
            public ASBOGOSYSNAVEnhetListe finnNAVEnhet(ASBOGOSYSFinnNAVEnhetRequest finnNAVEnhetRequest)
                    throws FinnNAVEnhetFaultGOSYSGeneriskMsg {
                ASBOGOSYSNAVEnhetListe enhetListe = new ASBOGOSYSNAVEnhetListe();
                enhetListe.getNAVEnheter().addAll(NAV_ENHET_LISTE);
                return enhetListe;
            }

            @Override
            public ASBOGOSYSNAVEnhetListe hentNAVEnhetGruppeListe(ASBOGOSYSNavEnhet hentNAVEnhetGruppeListeRequest)
                    throws HentNAVEnhetGruppeListeFaultGOSYSGeneriskMsg, HentNAVEnhetGruppeListeFaultGOSYSNAVEnhetIkkeFunnetMsg {
                return new ASBOGOSYSNAVEnhetListe();
            }

            @Override
            public ASBOGOSYSNAVEnhetListe hentNAVEnhetListe(ASBOGOSYSHentNAVEnhetListeRequest hentNAVEnhetListeRequest)
                    throws HentNAVEnhetListeFaultGOSYSGeneriskMsg, HentNAVEnhetListeFaultGOSYSNAVEnhetIkkeFunnetMsg {
                ASBOGOSYSNAVEnhetListe enhetsListe = new ASBOGOSYSNAVEnhetListe();
                enhetsListe.getNAVEnheter().addAll(NAV_ENHET_LISTE);
                return enhetsListe;
            }

            @Override
            public ASBOGOSYSNAVEnhetListe finnArenaNAVEnhetListe(ASBOGOSYSFinnArenaNAVEnhetListeRequest finnArenaNAVEnhetListeRequest)
                    throws FinnArenaNAVEnhetListeFaultGOSYSGeneriskMsg {
                return new ASBOGOSYSNAVEnhetListe();
            }

            @Override
            public ASBOGOSYSNAVEnhetListe hentSpesialEnhetTilPerson(ASBOGOSYSHentSpesialEnhetTilPersonRequest hentSpesialEnhetTilPersonRequest)
                    throws HentSpesialEnhetTilPersonFaultGOSYSGeneriskMsg, HentSpesialEnhetTilPersonFaultGOSYSPersonIkkeFunnetMsg, HentSpesialEnhetTilPersonFaultGOSYSNAVEnhetIkkeFunnetMsg {
                return new ASBOGOSYSNAVEnhetListe();
            }

            @Override
            public ASBOGOSYSNavEnhet hentNAVEnhet(ASBOGOSYSNavEnhet hentNAVEnhetRequest)
                    throws HentNAVEnhetFaultGOSYSGeneriskMsg, HentNAVEnhetFaultGOSYSNAVEnhetIkkeFunnetaMsg {
                return new ASBOGOSYSNavEnhet();
            }
        };
    }
}
