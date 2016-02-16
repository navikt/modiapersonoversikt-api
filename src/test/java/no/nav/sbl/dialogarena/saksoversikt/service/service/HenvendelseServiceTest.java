package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import org.mockito.InjectMocks;
import org.mockito.Mock;

//@RunWith(MockitoJUnitRunner.class)
public class HenvendelseServiceTest {

    @Mock
    private HenvendelsePortType henvendelseSoknaderPortType;

    @InjectMocks
    private HenvendelseService henvendelseService;

//    @BeforeClass
//    public static void systemSetup() {
//        System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
//        System.setProperty("behandlingsstatus.synlig.antallDager", "30");
//    }
//
//    @Before
//    public void setup() {
//
//    }


}
