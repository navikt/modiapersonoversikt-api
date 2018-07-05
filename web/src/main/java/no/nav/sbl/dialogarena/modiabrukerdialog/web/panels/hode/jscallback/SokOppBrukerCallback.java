package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.hode.jscallback;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.exception.AuthorizationWithSikkerhetstiltakException;
import no.nav.kjerneinfo.consumer.fim.person.to.HentSikkerhetstiltakRequest;
import no.nav.kjerneinfo.domain.person.fakta.Sikkerhetstiltak;
import no.nav.modig.core.exception.AuthorizationException;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.hode.Hode;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class SokOppBrukerCallback implements HodeCallback<String> {
    private Logger logger = LoggerFactory.getLogger(SokOppBrukerCallback.class);
    public static final String JSON_SIKKERHETTILTAKS_BESKRIVELSE = "sikkerhettiltaksbeskrivelse";
    public static final String JSON_ERROR_TEXT = "errortext";
    public static final String JSON_SOKT_FNR = "soektfnr";
    private final PersonKjerneinfoServiceBi personKjerneinfoServiceBi;
    private final Component component;

    public SokOppBrukerCallback(
            PersonKjerneinfoServiceBi personKjerneinfoServiceBi,
            Component component) {
        this.personKjerneinfoServiceBi = personKjerneinfoServiceBi;
        this.component = component;
    }

    @Override
    public void onCallback(AjaxRequestTarget target, Hode hode, String fnr) {
        try {
            hode.handlePerson(target, hode, fnr);
        } catch (AuthorizationException ex) {
            logger.warn("AuthorizationException ved kall på getPersonKjerneinfo", ex.getMessage());
            String message = component.getString(ex.getMessage());
            HentSikkerhetstiltakRequest sikkerhetstiltakRequest = new HentSikkerhetstiltakRequest(fnr);
            Sikkerhetstiltak sikkerhetstiltak = personKjerneinfoServiceBi.hentSikkerhetstiltak(sikkerhetstiltakRequest);
            if ( sikkerhetstiltak != null && sikkerhetstiltak.getSikkerhetstiltaksbeskrivelse() != null ) {
                message = byggSikkerhetstiltakMelding(message, sikkerhetstiltak.getSikkerhetstiltaksbeskrivelse());
             }
            if (isBlank(message)) {
                message = ex.getMessage();
            }
            target.appendJavaScript(hode.getUpdateScript(message));
        }
    }

    private String byggSikkerhetstiltakMelding(String message, String sikkerhetsbeskrivelse) {
        if(isBlank(message)) {
            return String.format("Sikkerhetstiltak: %s", sikkerhetsbeskrivelse);
        }

        if(message.endsWith(".")) {
            return String.format("%s Sikkerhetstiltak: %s", message, sikkerhetsbeskrivelse);
        }
        return String.format("%s. Sikkerhetstiltak: %s", message, sikkerhetsbeskrivelse);
    }

}
