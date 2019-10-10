package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.delvissvar;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.GrunnInfo;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Fritekst;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.delvissvar.LeggTilbakeDelvisSvarPanel.START_NY_DIALOG_CALLBACK_ID;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.delvissvar.LeggTilbakeDelvisSvarPanel.SVAR_DELVIS_CALLBACK_ID;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LeggTilbakeDelvisSvarPropsTest {

    private static final String BEHANDLINGS_ID = "BEHANDLINGS_ID";
    private static final String FRITEKST = "FRITEKST";
    private static final String FODSELSNUMMER = "10108000398";
    private static final String TRAAD_ID = "1337";
    private static final String OPPGAVE_ID = "OPPGAVE_ID";
    private static final String FORVENTET_TEMAGRUPPE = "Arbeid";
    private static final DateTime OPPRETTETDATO = new DateTime("2017-09-28T11:53:32.470+02:00");
    private static final String FORVENTET_OPPRETTETDATO = "28.09.2017 kl 11:53";
    private static Map<Temagruppe, String> TEMAGRUPPE_MAP = new HashMap<>();
    private final Map<Temagruppe, String> FORVENTET_TEMAGRUPPE_MAP = new HashMap<>(TEMAGRUPPE_MAP);
    private final List<Melding> traad = new ArrayList<>();
    private final GrunnInfo.Bruker bruker  = new GrunnInfo.Bruker("10108000398", "testesen", "testfamilien", "NAV Aremark", "0122", "", "M");
    private final GrunnInfo.Saksbehandler saksbehandler = new GrunnInfo.Saksbehandler("0118", "F_z123456", "E_z123456");
    private final GrunnInfo grunnInfo = new GrunnInfo(bruker, saksbehandler);
    private final SkrivestotteProps skrivestotteProps = new SkrivestotteProps(grunnInfo, "5110");

    static{
        HashMap<Temagruppe, String> map = new HashMap<>();
        map.put(ARBD, "Arbeid");
        map.put(FMLI, "Familie");
        map.put(PLEIEPENGERSY, "Pleiepenger sykt barn");
        map.put(HJLPM,  "Hjelpemidler");
        map.put(BIL, "Hjelpemidler Bil");
        map.put(ORT_HJE, "Helsetjenester og ortopediske hjelpemidler");
        map.put(UTLAND, "Utland");
        map.put(OVRG, "Øvrig");
        map.put(PENS, "Pensjon");
        map.put(UFRT, "Uføretrygd");
        TEMAGRUPPE_MAP =  map;
    }

    @BeforeEach
    void before() {
       traad.add(lagMelding());
    }

    @Test
    @DisplayName("Lager korrekte props til reactkomponenten")
    void lagerPropsSomForventet() {
        LeggTilbakeDelvisSvarProps leggTilbakeDelvisSvarProps = new LeggTilbakeDelvisSvarProps(BEHANDLINGS_ID, TEMAGRUPPE_MAP, traad, skrivestotteProps, false);
        FORVENTET_TEMAGRUPPE_MAP.remove(ARBD);

        assertAll("props",
                () -> assertEquals(BEHANDLINGS_ID, leggTilbakeDelvisSvarProps.get("behandlingskjedeId")),
                () -> assertEquals(FODSELSNUMMER, leggTilbakeDelvisSvarProps.get("fodselsnummer")),
                () -> assertEquals(TRAAD_ID, leggTilbakeDelvisSvarProps.get("traadId")),
                () -> assertEquals(SVAR_DELVIS_CALLBACK_ID, leggTilbakeDelvisSvarProps.get("svarDelvisCallbackId")),
                () -> assertEquals(START_NY_DIALOG_CALLBACK_ID, leggTilbakeDelvisSvarProps.get("startNyDialogId")),
                () -> assertEquals(OPPGAVE_ID, leggTilbakeDelvisSvarProps.get("oppgaveId")),
                () -> assertEquals(FORVENTET_TEMAGRUPPE, leggTilbakeDelvisSvarProps.get("temagruppe")),
                () -> assertEquals(FRITEKST, leggTilbakeDelvisSvarProps.get("sporsmal")),
                () -> assertEquals(FORVENTET_OPPRETTETDATO, leggTilbakeDelvisSvarProps.get("opprettetDato")),
                () -> assertEquals(FORVENTET_TEMAGRUPPE_MAP, leggTilbakeDelvisSvarProps.get("temagruppeMapping")),
                () -> assertEquals(skrivestotteProps, leggTilbakeDelvisSvarProps.get("skrivestotteprops"))
                );
    }

    private Melding lagMelding() {
        return new Melding()
                .withOppgaveId(OPPGAVE_ID)
                .withFnr(FODSELSNUMMER)
                .withTraadId(TRAAD_ID)
                .withFritekst(new Fritekst(FRITEKST, new Saksbehandler("Jan", "Saksbehandler", "ident"), DateTime.now()))
                .withTemagruppe(ARBD.name())
                .withFerdigstiltDato(OPPRETTETDATO);
    }
}