package no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static no.nav.modig.lang.collections.IterUtils.on;

public interface HenvendelseService {

    List<Henvendelse> hentAlleHenvendelser(String aktorId);

    class Default implements HenvendelseService {

        private final HenvendelsePortType henvendelseWS;

        public Default(HenvendelsePortType henvendelseWS) {
            this.henvendelseWS = henvendelseWS;
        }

        @Override
        public List<Henvendelse> hentAlleHenvendelser(String aktorId) {
            Transformer<WSHenvendelse, Henvendelse> somHenvendelse = new Transformer<WSHenvendelse, Henvendelse>() {
				@Override
				@SuppressWarnings("unchecked")
                public Henvendelse transform(WSHenvendelse wsHenvendelse) {
                    String henvendelseType = wsHenvendelse.getHenvendelseType();
                    Henvendelse henvendelse = new Henvendelse(
                            wsHenvendelse.getBehandlingsId(),
                            Henvendelsetype.valueOf(henvendelseType),
                            wsHenvendelse.getTraad());
                    henvendelse.opprettet = wsHenvendelse.getOpprettetDato();
                    henvendelse.tema = wsHenvendelse.getTema();
                    henvendelse.overskrift = ("SPORSMAL".equals(henvendelseType) ? "Bruker:" : "NAV:");
                    henvendelse.setLest(wsHenvendelse.getLestDato() != null);
                    henvendelse.lestDato = wsHenvendelse.getLestDato();

                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, String> behandlingsresultat;
                    try {
                        behandlingsresultat = (Map<String, String>) mapper.readValue(wsHenvendelse.getBehandlingsresultat(), Map.class);
                    } catch (IOException e) {
                        throw new RuntimeException("Kunne ikke lese ut behandlingsresultat", e);
                    }

                    henvendelse.fritekst = behandlingsresultat.get("fritekst");
                    return henvendelse;
                }
            };
            return on(henvendelseWS.hentHenvendelseListe(aktorId, Arrays.asList("SPORSMAL", "SVAR"))).map(somHenvendelse).collect();
        }
    }

    class Mock implements HenvendelseService {

        Map<String, Henvendelse> henvendelser = new HashMap<>();

        public Mock() {
            Random random = new Random();

            Henvendelse spsm1 = new Henvendelse("" + random.nextInt(), Henvendelsetype.SPORSMAL, "" + random.nextInt());
            spsm1.opprettet = DateTime.now().minusWeeks(2);
            spsm1.fritekst = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. " +
                    "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum" +
                    " iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto" +
                    " odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis eleifend" +
                    " option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Typi non habent claritatem insitam; est usus legentis in iis qui facit" +
                    " eorum claritatem. Investigationes demonstraverunt lectores legere me lius quod ii legunt saepius. Claritas est etiam processus dynamicus, qui" +
                    " sequitur mutationem consuetudium lectorum. Mirum est notare quam littera gothica, quam nunc putamus parum claram, anteposuerit litterarum formas" +
                    " humanitatis per seacula quarta decima et quinta decima. Eodem modo typi, qui nunc nobis videntur parum clari, fiant sollemnes in futurum.";
            spsm1.overskrift = (Henvendelsetype.SPORSMAL.equals(spsm1.type) ? "Bruker:" : "NAV:");
            spsm1.tema = "UFORE";
            spsm1.markerSomLest();
            spsm1.lestDato = spsm1.opprettet;
            henvendelser.put(spsm1.id, spsm1);

            Henvendelse svar1 = new Henvendelse("" + random.nextInt(), Henvendelsetype.SVAR, spsm1.traadId);
            svar1.opprettet = DateTime.now().minusDays(6);
            svar1.fritekst = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. " +
                    "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum" +
                    " iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto";
            svar1.overskrift = (Henvendelsetype.SPORSMAL.equals(svar1.type) ? "Bruker:" : "NAV:");
            svar1.tema = spsm1.tema;
            svar1.markerSomLest();
            svar1.lestDato = DateTime.now().minusDays(4);
            henvendelser.put(svar1.id, svar1);

            Henvendelse spsm2 = new Henvendelse("" + random.nextInt(), Henvendelsetype.SPORSMAL, spsm1.traadId);
            spsm2.opprettet = DateTime.now().minusDays(2);
            spsm2.fritekst = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. " +
                    "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum" +
                    " iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto" +
                    " odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis eleifend" +
                    " option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Typi non habent claritatem insitam; est usus legentis in iis qui facit" +
                    " eorum claritatem. Investigationes demonstraverunt lectores legere me lius quod ii legunt saepius. Claritas est etiam processus dynamicus, qui" +
                    " sequitur mutationem consuetudium lectorum. Mirum est notare quam littera gothica, quam nunc putamus parum claram, anteposuerit litterarum formas" +
                    " humanitatis per seacula quarta decima et quinta decima. Eodem modo typi, qui nunc nobis videntur parum clari, fiant sollemnes in futurum.";
            spsm2.overskrift = (Henvendelsetype.SPORSMAL.equals(spsm2.type) ? "Bruker:" : "NAV:");
            spsm2.tema = "PENSJON";
            spsm2.markerSomLest();
            spsm2.lestDato = spsm2.opprettet;
            henvendelser.put(spsm2.id, spsm2);

            Henvendelse svar2 = new Henvendelse("" + random.nextInt(), Henvendelsetype.SVAR, spsm1.traadId);
            svar2.opprettet = DateTime.now().minusDays(1);
            svar2.fritekst = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. " +
                    "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum" +
                    " iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto" +
                    " odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis eleifend" +
                    " option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Typi non habent claritatem insitam; est usus legentis in iis qui facit" +
                    " eorum claritatem. Investigationes demonstraverunt lectores legere me lius quod ii legunt saepius. Claritas est etiam processus dynamicus, qui" +
                    " sequitur mutationem consuetudium lectorum. Mirum est notare quam littera gothica, quam nunc putamus parum claram, anteposuerit litterarum formas";
            svar2.tema = spsm2.tema;
            svar2.overskrift = (Henvendelsetype.SPORSMAL.equals(svar2.type) ? "Bruker:" : "NAV:");
            svar2.markerSomLest();
            svar2.lestDato = svar2.opprettet.minusHours(6);
            henvendelser.put(svar2.id, svar2);

            Henvendelse spsm3 = new Henvendelse("" + random.nextInt(), Henvendelsetype.SPORSMAL, "" + random.nextInt());
            spsm3.opprettet = DateTime.now().minusWeeks(12);
            spsm3.fritekst = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. " +
                    "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum";
            spsm3.tema = "PENSJON";
            spsm3.overskrift = (Henvendelsetype.SPORSMAL.equals(spsm3.type) ? "Bruker:" : "NAV:");
            spsm3.markerSomLest();
            spsm3.lestDato = spsm3.opprettet;
            henvendelser.put(spsm3.id, spsm3);

            Henvendelse svar3 = new Henvendelse("" + random.nextInt(), Henvendelsetype.SVAR, spsm3.traadId);
            svar3.opprettet = DateTime.now();
            svar3.fritekst = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. " +
                    "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum" +
                    " iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto" +
                    " odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis eleifend" +
                    " option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Typi non habent claritatem insitam; est usus legentis in iis qui facit" +
                    " eorum claritatem. Investigationes demonstraverunt lectores legere me lius quod ii legunt saepius. Claritas est etiam processus dynamicus, qui" +
                    " sequitur mutationem consuetudium lectorum. Mirum est notare quam littera gothica, quam nunc putamus parum claram, anteposuerit litterarum formas";
            svar3.tema = spsm3.tema;
            svar3.overskrift = (Henvendelsetype.SPORSMAL.equals(svar3.type) ? "Bruker:" : "NAV:");
            henvendelser.put(svar3.id, svar3);

            Henvendelse spsm4 = new Henvendelse("" + random.nextInt(), Henvendelsetype.SPORSMAL, "" + random.nextInt());
            spsm4.opprettet = DateTime.now().minusHours(1);
            spsm4.fritekst = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. " +
                    "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum" +
                    " iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto" +
                    " odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis eleifend" +
                    " option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Typi non habent claritatem insitam; est usus legentis in iis qui facit" +
                    " eorum claritatem. Investigationes demonstraverunt lectores legere me lius quod ii legunt saepius. Claritas est etiam processus dynamicus, qui" +
                    " sequitur mutationem consuetudium lectorum. Mirum est notare quam littera gothica, quam nunc putamus parum claram, anteposuerit litterarum formas";
            spsm4.tema = "SOSIALE_TJENESTER";
            spsm4.overskrift = (Henvendelsetype.SPORSMAL.equals(spsm4.type) ? "Bruker:" : "NAV:");
            spsm4.markerSomLest();
            spsm4.lestDato = spsm4.opprettet;
            henvendelser.put(spsm4.id, spsm4);
        }

        @Override
        public List<Henvendelse> hentAlleHenvendelser(String aktorId) {
            return new ArrayList<>(henvendelser.values());
        }
    }

}
