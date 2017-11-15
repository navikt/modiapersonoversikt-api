export const MeldingsTyper = {
    SPORSMAL_SKRIFTLIG: 'SPORSMAL_SKRIFTLIG',
    SVAR_SKRIFTLIG: 'SVAR_SKRIFTLIG',
    DELVIS_SVAR: 'DELVIS_SVAR_SKRIFTLIG',
    SVAR_SBL_INNGAAENDE: 'SVAR_SBL_INNGAAENDE'
};

export function eldsteMeldingForst(melding1, melding2) {
    const d1 = new Date(melding1.opprettetDato);
    const d2 = new Date(melding2.opprettetDato);
    if (d1 > d2) {
        return 1;
    } else if (d1 < d2) {
        return -1;
    }
    return 0;
}

export function erDelvisSvar(melding) {
    return melding.meldingstype === MeldingsTyper.DELVIS_SVAR;
}

export function erSkriftligSvar(melding) {
    return melding.meldingstype === MeldingsTyper.SVAR_SKRIFTLIG;
}

export function erInngaaende(melding) {
    return [MeldingsTyper.SPORSMAL_SKRIFTLIG, MeldingsTyper.SVAR_SBL_INNGAAENDE].indexOf(melding.meldingstype) >= 0;
}

export function fraBruker(melding) {
    return erInngaaende(melding) ? melding.fnrBruker : melding.navIdent;
}
