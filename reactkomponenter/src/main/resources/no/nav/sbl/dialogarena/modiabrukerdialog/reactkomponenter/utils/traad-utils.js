import {
    erInngaaendeSvar, erOppmoteSvar, erSkriftligSvar, erTelefonSvar, erDelvisSvar
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
export const TraadMock = {
    meldinger: [{
        id: '02',
        erInngaaende: false,
        fritekst: 'Daniel er en kul kille',
        statusTekst: 'StatusTekst',
        lestStatus: 'LestStatusTekst',
        temagruppeNavn: 'Temagruppenavn',
        journalfortTemanavn: 'JournalførtTemanavn',
        journalfortDatoTekst: 'Datotekst',
        journalfortSaksId: '123',
        journalfortAvNavIdent: 'Aremark',
        visningsDatoTekst: 'Visningsdatotekst',
        erDokumentMelding: false,
        skrevetAv: 'Daniel Winsvold (Z999999)',
        journalfortAv: {
            navn: 'Daniel Journalførersen'
        }
    }],
    traadId: '01',
    statusTekst: 'StatusTekst',
    ikontekst: 'ikontekst',
    antallMeldingerIOpprinneligTraad: 1
};
