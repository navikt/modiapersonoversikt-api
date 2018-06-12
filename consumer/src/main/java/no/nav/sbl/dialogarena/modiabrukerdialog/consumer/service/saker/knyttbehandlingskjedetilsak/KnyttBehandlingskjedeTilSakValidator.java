package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.knyttbehandlingskjedetilsak;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KnyttBehandlingskjedeTilSakValidator {

    private static final List<KnyttBehandlingskjedeTilSakRegel> valideringsRegler = new ArrayList<KnyttBehandlingskjedeTilSakRegel>() {{
        add(enhetRule());
        add(behandlingskjedeRule());
        add(fnrRule());
    }};

    public static void validate(String fnr, String behandlingskjede, Sak sak, String enhet) {
        for (KnyttBehandlingskjedeTilSakRegel regel: valideringsRegler) {
            regel.validate(fnr, behandlingskjede, sak, enhet);
        }
    }

    private static KnyttBehandlingskjedeTilSakRegel enhetRule() {
        return (fnr, behandlingskjede, sak, enhet) -> {
            if (StringUtils.isEmpty(enhet)) {
                String saksId = getSaksId(sak);
                String behandlingskjedeString = getBehandlingskjedeId(behandlingskjede);
                throw new EnhetIkkeSatt(String.format("Enhet-parameter må være tilstede for å kunne knytte behandlingskjede %s til sak %s.", behandlingskjedeString, saksId));
            }
        };
    }

    private static KnyttBehandlingskjedeTilSakRegel behandlingskjedeRule() {
        return (fnr, behandlingskjede, sak, enhet) -> {
            if (StringUtils.isEmpty(behandlingskjede)) {
                String saksId = getSaksId(sak);
                throw new IllegalArgumentException(String.format("Behandlingskjede-parameter må være tilstede for å kunne knytte behandlingskjeden til sak %s.", saksId));
            }
        };
    }

    private static KnyttBehandlingskjedeTilSakRegel fnrRule() {
        return (fnr, behandlingskjede, sak, enhet) -> {
            if (StringUtils.isEmpty(fnr)) {
                String saksId = getSaksId(sak);
                String behandlingskjedeString = getBehandlingskjedeId(behandlingskjede);
                throw new IllegalArgumentException(String.format("fnr-parameter må være tilstede for å kunne knytte behandlingskjede %s til sak %s.", behandlingskjedeString, saksId));
            }
        };
    }

    private static String getSaksId(Sak sak) {
        return Optional.ofNullable(sak)
                            .map(sak1 -> sak1.saksId.orElse("INGEN SAKSID"))
                            .orElse("INGEN SAKSID");
    }

    private static String getBehandlingskjedeId(String behandlingskjede) {
        return Optional.ofNullable(behandlingskjede).orElse("INGEN BEHANDLINGSKJEDEID");
    }
}
