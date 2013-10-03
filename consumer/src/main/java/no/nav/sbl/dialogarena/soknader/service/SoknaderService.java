package no.nav.sbl.dialogarena.soknader.service;

import no.nav.modig.modia.widget.panels.InfoPanelVM;

import java.io.Serializable;
import java.util.List;

public interface SoknaderService extends Serializable {

    String SOKNAD = "soknader";

    List<InfoPanelVM> getWidgetContent(String fnr);
    List<Soknad> getSoknadListe(String fnr);
}
