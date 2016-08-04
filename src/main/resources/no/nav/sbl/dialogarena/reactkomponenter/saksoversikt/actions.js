import * as AT from './action-types';
import Ajax from './../utils/ajax';
import Q from 'q';

function rethrow(fn) {
    return (data) => {
        console.error(data);
        fn(data);
    };
}

const dataDispatch = (dispatch, type) => (data) => dispatch({ type, data });

export const hentLerretData = (fnr) => (dispatch) => {
    const promisedDispatch = dataDispatch.bind(null, dispatch);

    const tekster = Ajax.get('/modiabrukerdialog/rest/informasjon/tekster');
    const miljovariabler = Ajax.get('/modiabrukerdialog/rest/informasjon/miljovariabler');
    const sakstema = Ajax.get(`/modiabrukerdialog/rest/saksoversikt/${fnr}/sakstema`);

    dispatch({ type: AT.LAST_LERRET_DATA_START });
    return Q
        .allSettled([tekster, miljovariabler, sakstema])
        .then(promisedDispatch(AT.LAST_LERRET_DATA_OK))
        .catch(rethrow(promisedDispatch(AT.LAST_LERRET_DATA_FEIL)));
};

export const hentDokumentData = (fnr, valgtjournalpost) => (dispatch) => {
    const promisedDispatch = dataDispatch.bind(null, dispatch);

    const journalpostmetadata = Ajax.get(`/modiabrukerdialog/rest/saksoversikt/${fnr}/journalpostmetadata/${valgtjournalpost.journalpostId}?temakode=${valgtjournalpost.temakode}`);

    dispatch({ type: AT.LAST_DOKUMENT_DATA_START });
    return journalpostmetadata
        .then(promisedDispatch(AT.LAST_DOKUMENT_DATA_OK))
        .catch(rethrow(promisedDispatch(AT.LAST_DOKUMENT_DATA_FEIL)));
};

export const velgSak = (sak) => ({ type: AT.VELG_SAK, data: sak });
export const velgJournalpost = (journalpost) => ({ type: AT.VELG_JOURNALPOST, data: journalpost });
export const visSide = (side) => ({ type: AT.VIS_SIDE, data: side });
export const purgeScrollId = () => ({ type: AT.PURGE_SCROLL_ID });

export const velgFiltreringAvsender = (filtreringsvalg) => ({ type: AT.VELG_FILTRERING_AVSENDER, filtreringsvalg });

// Benyttes av Wicketklassen SaksoversiktLerret
export const purgeState = () => ({ type: AT.PURGE_STATE });
export const unmount = () => ({ type: AT.UNMOUNT });
