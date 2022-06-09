package no.nav.modiapersonoversikt.consumer.dkif;

import com.github.benmanes.caffeine.cache.Cache;
import no.nav.common.types.identer.Fnr;
import no.nav.modiapersonoversikt.infrastructure.cache.CacheUtils;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.DigitalKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSEpostadresse;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSKontaktinformasjon;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSMobiltelefonnummer;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.WSHentDigitalKontaktinformasjonRequest;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.WSHentDigitalKontaktinformasjonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class DkifServiceImpl implements Dkif.Service {

    private static final Logger logger = LoggerFactory.getLogger(DkifServiceImpl.class);
    private final DigitalKontaktinformasjonV1 dkifPorttype;
    private final Cache<Fnr, Dkif.DigitalKontaktinformasjon> cache;

    public DkifServiceImpl(DigitalKontaktinformasjonV1 dkifPorttype) {
        this.dkifPorttype = dkifPorttype;
        this.cache = CacheUtils.createDefaultCache();
    }

    @Override
    public Dkif.DigitalKontaktinformasjon hentDigitalKontaktinformasjon(String ident) {
        return Objects.requireNonNull(cache.get(new Fnr(ident), this::hentKontaktinfoFraApi));
    }

    private Dkif.DigitalKontaktinformasjon hentKontaktinfoFraApi(Fnr ident) {
        try {
            WSHentDigitalKontaktinformasjonResponse response = dkifPorttype.hentDigitalKontaktinformasjon(
                new WSHentDigitalKontaktinformasjonRequest().withPersonident(ident.get())
            );
            return DkifSoapExtentions.responseFromDTO(response);
        } catch (HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet e) {
            logger.info("Kunne ikke hente kontaktinformasjon fra dkif, ", e);
            return DkifSoapExtentions.responseFromDTO(lagTomDigitalKontaktinformasjonResponse());
        } catch (Exception e) {
            logger.error("Feil ved henting fra dkif, ", e.getMessage());
            throw new RuntimeException("Feil ved henting fra dkif ", e);
        }
    }

    private WSHentDigitalKontaktinformasjonResponse lagTomDigitalKontaktinformasjonResponse() {
        WSKontaktinformasjon digitalKontaktinformasjon = new WSKontaktinformasjon();
        digitalKontaktinformasjon.setMobiltelefonnummer(new WSMobiltelefonnummer().withValue(""));
        digitalKontaktinformasjon.setEpostadresse(new WSEpostadresse().withValue(""));
        digitalKontaktinformasjon.setReservasjon("");

        return new WSHentDigitalKontaktinformasjonResponse()
                .withDigitalKontaktinformasjon(digitalKontaktinformasjon);
    }

}
