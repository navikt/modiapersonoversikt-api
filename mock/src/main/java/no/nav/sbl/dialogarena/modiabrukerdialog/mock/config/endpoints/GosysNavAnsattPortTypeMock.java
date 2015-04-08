package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.ASBOGOSYSFagomradeListe;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSFinnArenaNAVAnsattListeRequest;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSHentNAVAnsattFagomradeListeRequest;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSNAVAnsatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSNAVAnsattListe;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNAVEnhetListe;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNavEnhet;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static java.util.Arrays.asList;

@Configuration
public class GosysNavAnsattPortTypeMock {

    public static final String ANSATT_ID = "u1241";
    public static final List<ASBOGOSYSNavEnhet> NAV_ENHET_LISTE;
    public static final int ANTALL_MOCK_ENHETER = 7;

    static {
        List<ASBOGOSYSNavEnhet> liste = new ArrayList<>();
        liste.addAll(asList(
                lagNavEnhet("0122", "NAV Trøgstad"),
                lagNavEnhet("0100", "NAV Østfold"),
                lagNavEnhet("2960", "NAV Drift og Utvikling - Anskaffelse og økonomi"),
                lagNavEnhet("4303", "NAV Id og fordeling"),
                lagNavEnhet("4403", "NAV Forvaltning Oslo og Akershus"),
                lagNavEnhet("4100", "NAV Kontaktsenter Test"),
                lagNavEnhet("1234", "NAV Mockbrukers Enhet")
        ));

        for (int i = 0,manglende = ANTALL_MOCK_ENHETER - liste.size(); i < manglende; i++) {
            liste.add(lagNavEnhet(lagEnhetId(i), lagEnhetNavn()));
        }

        NAV_ENHET_LISTE = Collections.unmodifiableList(liste);
    }

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
                ASBOGOSYSNAVAnsatt ansatt = new ASBOGOSYSNAVAnsatt();
                ansatt.setAnsattId(ANSATT_ID);
                try {
                    ansatt.getEnheter()
                            .addAll(hentNAVAnsattEnhetListe(null).getNAVEnheter());
                } catch (HentNAVAnsattEnhetListeFaultGOSYSNAVAnsattIkkeFunnetMsg | HentNAVAnsattEnhetListeFaultGOSYSGeneriskMsg ignored) {
                    throw new RuntimeException(ignored);
                }
                return ansatt;
            }

            @Override
            public ASBOGOSYSNAVEnhetListe hentNAVAnsattEnhetListe(ASBOGOSYSNAVAnsatt hentNAVAnsattEnhetListeRequest)
                    throws HentNAVAnsattEnhetListeFaultGOSYSNAVAnsattIkkeFunnetMsg, HentNAVAnsattEnhetListeFaultGOSYSGeneriskMsg {
                ASBOGOSYSNAVEnhetListe enhetListe = new ASBOGOSYSNAVEnhetListe();
                enhetListe.getNAVEnheter()
                        .addAll(NAV_ENHET_LISTE);
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

    private static String lagEnhetId(int id) {
        String base = String.valueOf(id);
        while (base.length() < 4) {
            base = "0"+base;
        }
        return base;
    }
    public static String lagEnhetNavn() {
        String[] type = new String[]{
                "Drift og Utvikling",
                "Id og fordeling",
                "Kontaktsenter",
                "Vanlig-senter",
                "IKT",
                "DevOps",
                "Testing"
        };
        String[] place = new String[]{
                "Trøgstad",
                "Østfold",
                "Anskaffelse og økonomi",
                "Oslo",
                "Akershus",
                "Hedmark",
                "Oppland"
        };
        Random r = new Random();
        String t = type[r.nextInt(type.length)];
        String p = place[r.nextInt(place.length)];

        return "NAV "+t+" - "+p;
    }
}
