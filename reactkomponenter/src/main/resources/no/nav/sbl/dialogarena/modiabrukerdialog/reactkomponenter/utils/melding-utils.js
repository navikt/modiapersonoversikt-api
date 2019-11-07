export const MeldingsTyper = {
    SPORSMAL_SKRIFTLIG: 'SPORSMAL_SKRIFTLIG',
    SPORSMAL_SKRIFTLIG_DIREKTE: 'SPORSMAL_SKRIFTLIG_DIREKTE',
    SVAR_SKRIFTLIG: 'SVAR_SKRIFTLIG',
    SVAR_SBL_INNGAAENDE: 'SVAR_SBL_INNGAAENDE',
    DOKUMENT_VARSEL: 'DOKUMENT_VARSEL',
    OPPGAVE_VARSEL: 'OPPGAVE_VARSEL',
    SVAR_OPPMOTE: 'SVAR_OPPMOTE',
    SVAR_TELEFON: 'SVAR_TELEFON',
    DELVIS_SVAR_SKRIFTLIG: 'DELVIS_SVAR_SKRIFTLIG',
    SAMTALEREFERAT_OPPMOTE: 'SAMTALEREFERAT_OPPMOTE',
    SAMTALEREFERAT_TELEFON: 'SAMTALEREFERAT_TELEFON',
    SPORSMAL_MODIA_UTGAAENDE: 'SPORSMAL_MODIA_UTGAAENDE'
};

export const MeldingsTyperTekst = {
    SPORSMAL_SKRIFTLIG: 'Spørsmål fra bruker',
    SPORSMAL_SKRIFTLIG_DIREKTE: 'Spørsmål fra bruker',
    SVAR_SKRIFTLIG: 'Svar fra NAV',
    SVAR_SBL_INNGAAENDE: 'Svar fra bruker',
    DOKUMENT_VARSEL: 'Dokument varsel',
    OPPGAVE_VARSEL: 'Oppgave varsel',
    SVAR_OPPMOTE: 'Svar oppmøte',
    SVAR_TELEFON: 'Svar telefon',
    DELVIS_SVAR_SKRIFTLIG: 'Delsvar',
    SAMTALEREFERAT_OPPMOTE: 'Samtalereferat oppmøte',
    SAMTALEREFERAT_TELEFON: 'Samtalereferat telefon',
    SPORSMAL_MODIA_UTGAAENDE: 'Spørsmål fra NAV'
};

const toNameCase = (navn) => navn.replace(/\b(?!em)\w+?\b/g,
    (txt) => txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase());

export function erInngaaende(melding) {
    return [
        MeldingsTyper.SPORSMAL_SKRIFTLIG,
        MeldingsTyper.SPORSMAL_SKRIFTLIG_DIREKTE,
        MeldingsTyper.SVAR_SBL_INNGAAENDE
    ].indexOf(melding.meldingstype) >= 0;
}

export function hentForfatterIdent(melding) {
    return erInngaaende(melding) ? melding.fnrBruker : melding.navIdent;
}

export function finnMeldingsForfattere(melding) {
    return melding.erDokumentMelding
    || melding.meldingstype === MeldingsTyper.SPORSMAL_SKRIFTLIG
    || melding.meldingstype === MeldingsTyper.SPORSMAL_SKRIFTLIG_DIREKTE
        ? ''
        : `Skrevet av: ${toNameCase(melding.skrevetAv)}`;
}

export function eldsteMeldingForst(melding1, melding2) {
    const d1 = new Date(melding1.opprettetDato);
    const d2 = new Date(melding2.opprettetDato);
    return d1 - d2;
}

export function erDelvisSvar(melding) {
    return melding.meldingstype === MeldingsTyper.DELVIS_SVAR_SKRIFTLIG;
}

export function erSkriftligSvar(melding) {
    return melding.meldingstype === MeldingsTyper.SVAR_SKRIFTLIG;
}

export function erInngaaendeSvar(melding) {
    return melding.meldingstype === MeldingsTyper.SVAR_SBL_INNGAAENDE;
}

export function erOppmoteSvar(melding) {
    return melding.meldingstype === MeldingsTyper.SVAR_OPPMOTE;
}

export function erTelefonSvar(melding) {
    return melding.meldingstype === MeldingsTyper.SVAR_TELEFON;
}

export function getMeldingsTypeTekst(melding) {
    const type = melding.meldingstype;
    const typeTekst = MeldingsTyperTekst[type];
    if (typeTekst !== undefined) {
        return typeTekst;
    }
    return 'Udefinert meldingstype';
}
