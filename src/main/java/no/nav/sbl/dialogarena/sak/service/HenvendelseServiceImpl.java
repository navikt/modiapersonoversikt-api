package no.nav.sbl.dialogarena.sak.service;

import no.nav.modig.core.exception.SystemException;
import no.nav.sbl.dialogarena.sak.service.interfaces.HenvendelseService;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSSoknad;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.sbl.dialogarena.sak.transformers.HenvendelseTransformers.INNSENDT;

import static no.nav.modig.lang.collections.IterUtils.on;

public class HenvendelseServiceImpl implements HenvendelseService {

    @Inject
    private HenvendelseSoknaderPortType henvendelseSoknaderPortType;

    @Override
    public List<WSSoknad> hentInnsendteSoknader(String fnr) {
        try {
            return on(henvendelseSoknaderPortType.hentSoknadListe(fnr)).filter(where(INNSENDT, equalTo(true))).collect();
        } catch (Exception e) {
            throw new SystemException("Feil ved kall til henvendelse", e);
        }
    }

}
