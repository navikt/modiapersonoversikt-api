import {
    erInngaaendeSvar, erOppmoteSvar, erSkriftligSvar, erTelefonSvar
} from './melding-utils';

export function erBesvart(traad) {
    return traad.some(erSkriftligSvar)
        || traad.some(erInngaaendeSvar)
        || traad.some(erOppmoteSvar)
        || traad.some(erTelefonSvar);
}

export function erIkkeBesvart(traad) {
    return !erBesvart(traad);
}

export function filtrerBortDelviseSvar(traad) {
    return traad.filter(melding => !erDelvisSvar(melding));
}