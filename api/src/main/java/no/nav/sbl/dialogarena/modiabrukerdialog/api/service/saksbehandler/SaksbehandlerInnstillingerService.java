package no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler;

public interface SaksbehandlerInnstillingerService {

    public String getSaksbehandlerValgtEnhet();

    public void setSaksbehandlerValgtEnhetCookie(String valgtEnhet);

    public boolean saksbehandlerInnstillingerErUtdatert();

    public boolean valgtEnhetErKontaktsenter();

}