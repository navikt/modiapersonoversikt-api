package no.nav.sbl.dialogarena.soknader.service;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.soknader.domain.Soknad;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.util.List;

public class SoknaderServiceDefault implements SoknaderService {

    private final Logger logger = LoggerFactory.getLogger(SoknaderServiceDefault.class);

    @SpringBean
    @Named("sakOgBehandlingPortType")
    private SakOgBehandlingPortType portType;

    @Override
    public List<Soknad> getSoknader(String fnr) {
        logger.error("Soknaderintegrasjon ikke implementert");
        throw new ApplicationException("Soknaderintegrasjon ikke implementert");
    }
}
