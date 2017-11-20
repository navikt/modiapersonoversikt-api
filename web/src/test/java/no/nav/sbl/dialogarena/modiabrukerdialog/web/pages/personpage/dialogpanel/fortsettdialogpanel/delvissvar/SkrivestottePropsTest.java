package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.delvissvar;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.GrunnInfo;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

class SkrivestottePropsTest {
    private final GrunnInfo.Bruker bruker  = new GrunnInfo.Bruker("10108000398", "testesen", "testfamilien", "NAV Aremark");
    private final GrunnInfo.Saksbehandler saksbehandler = new GrunnInfo.Saksbehandler("0118", "F_z123456", "E_z123456");
    private final GrunnInfo grunnInfo = new GrunnInfo(bruker, saksbehandler);



    @Test
    void skrivestottepropsForKontaktsenter() {
        SkrivestotteProps skrivestotteProps = new SkrivestotteProps(grunnInfo, "4110");

        assertEquals(skrivestotteProps.size(),2);
        assertEquals(skrivestotteProps.get("autofullfor"), grunnInfo);
        assertEquals(skrivestotteProps.get("knagger"), asList("ks"));


    }

    @Test
    void fofAndre() {
        SkrivestotteProps skrivestotteProps = new SkrivestotteProps(grunnInfo, "5110");
        assertEquals(skrivestotteProps.size(),1);
        assertEquals(skrivestotteProps.get("autofullfor"), grunnInfo);
    }
}