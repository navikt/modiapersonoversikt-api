package no.nav.kjerneinfo.consumer.fim.person.to;


import no.nav.common.auth.SubjectHandler;

public class HentKjerneinformasjonRequest {

    private String ident;
    private boolean begrunnet;

    public HentKjerneinformasjonRequest(String ident) {
        this.ident = ident;
    }

    public String getIdent() {
        return ident;
    }

    public boolean isBegrunnet() {
        return begrunnet;
    }

    public void setBegrunnet(boolean begrunnet) {
        this.begrunnet = begrunnet;
    }

    public String generateRequestId() {
        String subjectHandlerIdent = SubjectHandler.getIdent().orElse("-");
        return subjectHandlerIdent + ident + begrunnet;
    }
}
