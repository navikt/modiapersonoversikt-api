import {
    eldsteMeldingForst, erDelvisSvar, erInngaaendeSvar, erOppmoteSvar, erSkriftligSvar, erTelefonSvar,
    MeldingsTyper
} from './melding-utils';

function byggSamletSvar(avsluttendeSvar, delviseSvar) {
    const dobbeltLinjeskift = '\n\u00A0\n';
    avsluttendeSvar.fritekst = delviseSvar.concat(avsluttendeSvar)
        .map(melding => melding.fritekst)
        .join(dobbeltLinjeskift);
}

function byggMeldingsforfattereStreng(avsluttendeSvar, delviseSvar) {
    avsluttendeSvar.skrevetAvFlere = delviseSvar.concat(avsluttendeSvar)
        .map(melding => melding.skrevetAv.navn + ' (' + melding.navIdent + ')')
        .join(' og ');
}

export function flettDelviseSvarInnISkriftligSvar(traad, delviseSvar) {
    const skriftligeSvar = traad.filter(erSkriftligSvar);
    if (skriftligeSvar.length > 0) {
        const avsluttendeSvar = skriftligeSvar.sort(eldsteMeldingForst)[0];
        byggSamletSvar(avsluttendeSvar, delviseSvar);
        byggMeldingsforfattereStreng(avsluttendeSvar, delviseSvar);
    }
}

export function filtrerBortDelviseSvar(traad) {
    return traad.filter(melding => !erDelvisSvar(melding));
}

export function slaaSammenDelviseSvar(traad) {
    const delviseSvar = traad.filter(erDelvisSvar);
    if (delviseSvar.length > 0) {
        flettDelviseSvarInnISkriftligSvar(traad, delviseSvar);
    }
    return filtrerBortDelviseSvar(traad);
}

export function traadInneholderDelvisSvar(traad) {
    if (traad === undefined) {
        return false;
    }
    return traad.reduce((traadInneholderDelviseSvar, melding) =>
        melding.meldingstype === MeldingsTyper.DELVIS_SVAR_SKRIFTLIG || traadInneholderDelviseSvar
        , false);
}

export function erBesvart(traad) {
    return traad.some(erSkriftligSvar)
        || traad.some(erInngaaendeSvar)
        || traad.some(erOppmoteSvar)
        || traad.some(erTelefonSvar);
}

export function erIkkeBesvart(traad) {
    return !erBesvart(traad);
}
