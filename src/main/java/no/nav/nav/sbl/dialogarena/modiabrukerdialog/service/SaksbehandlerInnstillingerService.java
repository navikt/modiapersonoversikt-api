package no.nav.nav.sbl.dialogarena.modiabrukerdialog.service;

public interface SaksbehandlerInnstillingerService {

    public String getSaksbehandlerValgtEnhet();

    public void setSaksbehandlerValgtEnhetCookie(String valgtEnhet);

    public boolean saksbehandlerInnstillingerErUtdatert();

    public boolean valgtEnhetErKontaktsenter();

}