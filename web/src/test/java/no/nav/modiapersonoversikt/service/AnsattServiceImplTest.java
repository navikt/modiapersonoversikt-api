package no.nav.modiapersonoversikt.service;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSNAVAnsatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSNAVAnsattListe;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNAVEnhetListe;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNavEnhet;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.*;
import no.nav.modiapersonoversikt.api.domain.norg.Ansatt;
import no.nav.modiapersonoversikt.api.domain.norg.AnsattEnhet;
import no.nav.modiapersonoversikt.api.utils.http.SubjectHandlerUtil;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

class AnsattServiceImplTest {

    private GOSYSNAVansatt ansattWS = mock(GOSYSNAVansatt.class);
    private AnsattServiceImpl ansattServiceImpl = new AnsattServiceImpl(ansattWS);

    @Test
    void skalHenteAlleAnsattaForEnEnhet() throws Exception {
        ASBOGOSYSNAVEnhetListe asbogosysnavEnhetListe = new ASBOGOSYSNAVEnhetListe();
        List<ASBOGOSYSNavEnhet> enhetsliste = asbogosysnavEnhetListe.getNAVEnheter();
        enhetsliste.add(lagNavEnhet("111", "testEnhet"));
        enhetsliste.add(lagNavEnhet("222", "testEnhet2"));
        enhetsliste.add(lagNavEnhet("333", "testEnhet3"));

        when(ansattWS.hentNAVAnsattEnhetListe(any(ASBOGOSYSNAVAnsatt.class))).thenReturn(asbogosysnavEnhetListe);

        List<AnsattEnhet> enheter = SubjectHandlerUtil.withIdent("Z999999", () -> ansattServiceImpl.hentEnhetsliste());
        List<AnsattEnhet> refEnheter = asbogogsysEnhetListeTilAnsattEnhetListe(asbogosysnavEnhetListe);

        verify(ansattWS).hentNAVAnsattEnhetListe(any(ASBOGOSYSNAVAnsatt.class));

        assertThat(enheter.get(0).enhetId, is(refEnheter.get(0).enhetId));
        assertThat(enheter.get(1).enhetId, is(refEnheter.get(1).enhetId));
        assertThat(enheter.get(2).enhetId, is(refEnheter.get(2).enhetId));
    }

    @Test
    void skalKunneHenteNavnAnsatt() throws HentNAVAnsattFaultGOSYSGeneriskfMsg, HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg {

        when(ansattWS.hentNAVAnsatt(any(ASBOGOSYSNAVAnsatt.class))).thenReturn(lagNavAnsatt("Kalle", "Karlsson", "111"));

        String navn = ansattServiceImpl.hentAnsattNavn("123");

        assertThat(navn, is("Kalle Karlsson"));
    }

    @Test
    void skalKunneHenteAnsatteForEnhet() throws HentNAVAnsattListeFaultGOSYSGeneriskMsg, HentNAVAnsattListeFaultGOSYSNAVEnhetIkkeFunnetMsg {
        ASBOGOSYSNAVAnsattListe asbogosysnavAnsattListe = new ASBOGOSYSNAVAnsattListe();
        List<ASBOGOSYSNAVAnsatt> ansattListe = asbogosysnavAnsattListe.getNAVAnsatte();
        ansattListe.add(lagNavAnsatt("Kalle", "Karlsson", "111"));
        ansattListe.add(lagNavAnsatt("Klara", "Svensson", "222"));
        ansattListe.add(lagNavAnsatt("Knut", "Larsson", "333"));
        ansattListe.add(lagNavAnsatt("Kristina", "Johansson", "444"));

        when(ansattWS.hentNAVAnsattListe(any(ASBOGOSYSNavEnhet.class))).thenReturn(asbogosysnavAnsattListe);

        List<Ansatt> ansatte = ansattServiceImpl.ansatteForEnhet(new AnsattEnhet("123", "testEnhet"));
        List<Ansatt> refListeAnsatte = asbogogsysAnsatteListeTilAnsatteListe(asbogosysnavAnsattListe);

        verify(ansattWS).hentNAVAnsattListe((any(ASBOGOSYSNavEnhet.class)));
        assertThat(ansatte.get(0).ident, is(refListeAnsatte.get(0).ident));
        assertThat(ansatte.get(1).ident, is(refListeAnsatte.get(1).ident));
        assertThat(ansatte.get(2).ident, is(refListeAnsatte.get(2).ident));

    }

    private ASBOGOSYSNAVAnsatt lagNavAnsatt(String fornavn, String etternavn, String id) {
        ASBOGOSYSNAVAnsatt navAnsatt = new ASBOGOSYSNAVAnsatt();
        navAnsatt.setAnsattNavn(fornavn + " " + etternavn);
        navAnsatt.setFornavn(fornavn);
        navAnsatt.setEtternavn(etternavn);
        navAnsatt.setAnsattId(id);
        return navAnsatt;
    }

    private ASBOGOSYSNavEnhet lagNavEnhet(String enhetsId, String enhetsNavn) {
        ASBOGOSYSNavEnhet navEnhet = new ASBOGOSYSNavEnhet();
        navEnhet.setEnhetsId(enhetsId);
        navEnhet.setEnhetsNavn(enhetsNavn);
        return navEnhet;
    }

    private List<AnsattEnhet> asbogogsysEnhetListeTilAnsattEnhetListe(ASBOGOSYSNAVEnhetListe liste) {
        List<AnsattEnhet> ansattEnhetList = new LinkedList<>();
        for (ASBOGOSYSNavEnhet asbogosysNavEnhet : liste.getNAVEnheter()) {
            ansattEnhetList.add(new AnsattEnhet(asbogosysNavEnhet.getEnhetsId(), asbogosysNavEnhet.getEnhetsNavn()));
        }
        return ansattEnhetList;
    }

    private List<Ansatt> asbogogsysAnsatteListeTilAnsatteListe(ASBOGOSYSNAVAnsattListe asbogosysnavAnsattListe) {
        List<Ansatt> ansattList = new LinkedList<>();
        for (ASBOGOSYSNAVAnsatt ansatt : asbogosysnavAnsattListe.getNAVAnsatte()) {
            ansattList.add(new Ansatt(ansatt.getFornavn(), ansatt.getEtternavn(), ansatt.getAnsattId()));
        }
        return ansattList;
    }

}
