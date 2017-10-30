package no.nav.sbl.dialogarena.modiabrukerdialog;

import no.nav.brukerdialog.security.domain.OidcCredential;
import no.nav.modig.core.context.TestSubjectHandler;
import no.nav.modig.core.domain.*;

import javax.security.auth.Subject;

public class ModigInternbrukerSubjectHandler extends TestSubjectHandler {
    private Subject subject;
    private static OidcCredential oidcCredential;
    private static String veilederIdent = "Z999999";
    private static String servicebruker = "srvServicebruker";

    public static void setVeilederIdent(String ident) {
        veilederIdent = ident;
    }

    public static void setServicebruker(String bruker) {
        servicebruker = bruker;
    }
    public static void setOidcCredential(OidcCredential credential) {
        oidcCredential = credential;
    }

    @Override
    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    @Override
    public void reset() {
        setSubject(subject);
    }

    @Override
    public Subject getSubject() {
        Subject subject = new Subject();

        subject.getPrincipals().add(new SluttBruker(veilederIdent, IdentType.InternBruker));
        subject.getPrincipals().add(new ConsumerId(servicebruker));
        if (oidcCredential != null) {
            subject.getPublicCredentials().add(new OidcCredential(oidcCredential.getToken()));
        }

        return subject;
    }

    @Override
    public Integer getAuthenticationLevel() {
        return 4;
    }
}
