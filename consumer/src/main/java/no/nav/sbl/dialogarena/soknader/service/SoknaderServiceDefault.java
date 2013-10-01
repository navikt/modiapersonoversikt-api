package no.nav.sbl.dialogarena.soknader.service;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.modia.widget.panels.InfoPanelVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SoknaderServiceDefault implements SoknaderService {

    private final Logger logger = LoggerFactory.getLogger(SoknaderServiceDefault.class);

    @Override
    public List<InfoPanelVM> getWidgetContent(String fnr) {
        logger.error("Soknaderintegrasjon ikke implementert");
        throw new ApplicationException("Soknaderintegrasjon ikke implementert");
    }
}
