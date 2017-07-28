package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

import static java.util.Arrays.asList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/hode")
@Produces(APPLICATION_JSON)
public class HodeController {

    class Me {
        public final String ident, navn, fornavn, etternavn;

        public Me(String ident, String fornavn, String etternavn) {
            this.ident = ident;
            this.fornavn = fornavn;
            this.etternavn = etternavn;
            this.navn = fornavn + " " + etternavn;
        }
    }

    class Enhet {
        public final String enhetId, navn;

        public Enhet(String enhetId, String navn) {
            this.enhetId = enhetId;
            this.navn = navn;
        }
    }

    class Enheter {
        public final String ident;
        public final List<Enhet> enhetliste;

        public Enheter(String ident, List<Enhet> enhetliste) {
            this.ident = ident;
            this.enhetliste = enhetliste;
        }
    }

    @GET
    @Path("/me")
    public Me hentSammensatteSaker() {
        return new Me("Z990322", "F_Z990322", "F_Z990322");
    }

    @GET
    @Path("/enheter")
    public Enheter hentPensjonSaker() {
        return new Enheter("Z990322", asList(new Enhet("0219", "NAV Bærum"), new Enhet("0709", "NAV Larvik")));
    }
}
