package no.nav.modiapersonoversiktproxy.consumer.infotrygd.pleiepenger.mapping;

import no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.pleiepenger.Periode;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.pleiepenger.Vedtak;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.informasjon.WSPeriode;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.informasjon.WSVedtak;

class VedtaksMapper extends Mapper {

    VedtaksMapper() {
        registererVedtaksMapper();
        registrerPeriodeMapper();
    }

    private void registererVedtaksMapper() {
        registerMapper(WSVedtak.class, Vedtak.class, (vedtak) ->
                new Vedtak()
                        .withPeriode(map(vedtak.getVedtak()))
                        .withKompensasjonsgrad(vedtak.getKompensasjonsgrad())
                        .withUtbetalingsgrad(vedtak.getUtbetalingsgrad().intValue())
                        .withPleiepengegrad(vedtak.getPleiepengegrad())
                        .withAnvistUtbetaling(map(vedtak.getAnvistUtbetaling()))
                        .withBruttoBelop(vedtak.getBruttobeloep())
                        .withDagsats(vedtak.getDagsats()));
    }

    private void registrerPeriodeMapper() {
        registerMapper(WSPeriode.class, Periode.class, (vedtak) ->
                new Periode(map(vedtak.getFom()),
                        map(vedtak.getTom())));
    }

}
