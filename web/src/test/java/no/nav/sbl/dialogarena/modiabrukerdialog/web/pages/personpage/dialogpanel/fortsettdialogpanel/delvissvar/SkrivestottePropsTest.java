package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.delvissvar;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.GrunnInfo;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

class SkrivestottePropsTest {
    public static final String KONTAKTSENTER_ENHET_NR = "4110";
    public static final String IKKE_KONTAKTSENTER_ENHET_NR = "5110";
    private final GrunnInfo.Bruker bruker  = new GrunnInfo.Bruker("10108000398", "testesen", "testfamilien", "1234", "NAV Aremark", "1234", "", "K");
    private final GrunnInfo.Saksbehandler saksbehandler = new GrunnInfo.Saksbehandler("0118", "F_z123456", "E_z123456");
    private final GrunnInfo grunnInfo = new GrunnInfo(bruker, saksbehandler);



    @Test
    void skrivestottepropsForKontaktsenter() {
        SkrivestotteProps skrivestotteProps = new SkrivestotteProps(grunnInfo, KONTAKTSENTER_ENHET_NR);

        assertEquals(skrivestotteProps.size(),2);
        assertEquals(skrivestotteProps.get("autofullfor"), grunnInfo);
        assertEquals(skrivestotteProps.get("knagger"), asList("ks"));


    }

    @Test
    void forIkkeKontaktsenter() {
        SkrivestotteProps skrivestotteProps = new SkrivestotteProps(grunnInfo, IKKE_KONTAKTSENTER_ENHET_NR);
        assertEquals(skrivestotteProps.size(),1);
        assertEquals(skrivestotteProps.get("autofullfor"), grunnInfo);
    }
}