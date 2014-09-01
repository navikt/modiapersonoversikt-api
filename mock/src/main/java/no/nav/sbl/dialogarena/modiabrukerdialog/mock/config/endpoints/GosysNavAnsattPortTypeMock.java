package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.ASBOGOSYSFagomradeListe;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSFinnArenaNAVAnsattListeRequest;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSHentNAVAnsattFagomradeListeRequest;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSNAVAnsatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSNAVAnsattListe;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNAVEnhetListe;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNavEnhet;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.FinnArenaNAVAnsattListeFaultGOSYSGeneriskMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.FinnArenaNAVAnsattListeFaultGOSYSNAVEnhetIkkeFunnetMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattEnhetListeFaultGOSYSGeneriskMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattEnhetListeFaultGOSYSNAVAnsattIkkeFunnetMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFagomradeListeFaultGOSYSGeneriskMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFagomradeListeFaultGOSYSNAVAnsattIkkeFunnetMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFaultGOSYSGeneriskfMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattListeFaultGOSYSGeneriskMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattListeFaultGOSYSNAVEnhetIkkeFunnetMsg;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.Arrays.asList;

@Configuration
public class GosysNavAnsattPortTypeMock {

    @Bean
    public GOSYSNAVansatt navAnsatt() {
        return createGosysNavAnsattPortTypeMock();
    }

    public static GOSYSNAVansatt createGosysNavAnsattPortTypeMock() {
        return new GOSYSNAVansatt() {
            @Override
            public ASBOGOSYSNAVAnsattListe finnArenaNAVAnsattListe(ASBOGOSYSFinnArenaNAVAnsattListeRequest finnArenaNAVAnsattListeRequest)
                    throws FinnArenaNAVAnsattListeFaultGOSYSNAVEnhetIkkeFunnetMsg, FinnArenaNAVAnsattListeFaultGOSYSGeneriskMsg {
                return new ASBOGOSYSNAVAnsattListe();
            }

            @Override
            public ASBOGOSYSNAVAnsattListe hentNAVAnsattListe(ASBOGOSYSNavEnhet hentNAVAnsattListeRequest)
                    throws HentNAVAnsattListeFaultGOSYSNAVEnhetIkkeFunnetMsg, HentNAVAnsattListeFaultGOSYSGeneriskMsg {
                return new ASBOGOSYSNAVAnsattListe();
            }

            @Override
            public ASBOGOSYSFagomradeListe hentNAVAnsattFagomradeListe(ASBOGOSYSHentNAVAnsattFagomradeListeRequest hentNAVAnsattFagomradeListeRequest)
                    throws HentNAVAnsattFagomradeListeFaultGOSYSNAVAnsattIkkeFunnetMsg, HentNAVAnsattFagomradeListeFaultGOSYSGeneriskMsg {
                return new ASBOGOSYSFagomradeListe();
            }

            @Override
            public ASBOGOSYSNAVAnsatt hentNAVAnsatt(ASBOGOSYSNAVAnsatt hentNAVAnsattRequest)
                    throws HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg, HentNAVAnsattFaultGOSYSGeneriskfMsg {
                return new ASBOGOSYSNAVAnsatt();
            }

            @Override
            public ASBOGOSYSNAVEnhetListe hentNAVAnsattEnhetListe(ASBOGOSYSNAVAnsatt hentNAVAnsattEnhetListeRequest)
                    throws HentNAVAnsattEnhetListeFaultGOSYSNAVAnsattIkkeFunnetMsg, HentNAVAnsattEnhetListeFaultGOSYSGeneriskMsg {
                ASBOGOSYSNAVEnhetListe enhetListe = new ASBOGOSYSNAVEnhetListe();
                enhetListe.getNAVEnheter()
                        .addAll(asList(
                                lagNavEnhet("0122", "NAV Trøgstad"),
                                lagNavEnhet("0100", "NAV Østfold"),
                                lagNavEnhet("2960", "NAV Drift og Utvikling - Anskaffelse og økonomi"),
                                lagNavEnhet("4303", "NAV Id og fordeling"),
                                lagNavEnhet("4403", "NAV Forvaltning Oslo og Akershus"),
                                lagNavEnhet("4100", "NAV Kontaktsenter Test")
                        ));
                return enhetListe;
            }
        };
    }

    private static ASBOGOSYSNavEnhet lagNavEnhet(String enhetId, String enhetNavn) {
        ASBOGOSYSNavEnhet navEnhet = new ASBOGOSYSNavEnhet();
        navEnhet.setEnhetsId(enhetId);
        navEnhet.setEnhetsNavn(enhetNavn);
        return navEnhet;
    }
}
