import * as AT from './actionTypes';
import Ajax from './../utils/ajax';
import Q from 'q';

function rethrow(fn) {
    return (data) => {
        console.error(data);

        fn(data);
    }
}

const dataDispatch = (dispatch, type) => (data) => dispatch({ type, data });

export const hentWidgetData = (fnr) => {
    return (dispatch) => {
        const promisedDispatch = dataDispatch.bind(null, dispatch);

        const tekster = Ajax.get('/modiabrukerdialog/rest/informasjon/tekster');
        const temaer = Ajax.get(`/modiabrukerdialog/rest/saksoversikt/${fnr}/temaer`);

        dispatch({ type: AT.LAST_WIDGET_DATA_START });

        return tekster
            .then(promisedDispatch(AT.LAST_WIDGET_DATA_TEKSTER_OK))
            .then(() => temaer)
            .then(promisedDispatch(AT.LAST_WIDGET_DATA_OK))
            .catch(rethrow(promisedDispatch(AT.LAST_WIDGET_DATA_FEIL)));
    }
};

export const hentLerretDataInit = () => {
    return (dispatch) => {
        const promisedDispatch = dataDispatch.bind(null, dispatch);

        const tekster = Ajax.get('/modiabrukerdialog/rest/informasjon/tekster');
        const miljovariabler = Ajax.get('/modiabrukerdialog/rest/informasjon/miljovariabler');

        dispatch({ type: AT.LAST_LERRET_DATA_START });

        return Q
            .all([tekster, miljovariabler])
            .then(promisedDispatch(AT.LAST_LERRET_DATA_INIT_OK))
            .catch(rethrow(promisedDispatch(AT.LAST_LERRET_DATA_FEIL)));
    }
};

export const hentLerretDataSakstema = (fnr)=> {
    return (dispatch) => {
        const promisedDispatch = dataDispatch.bind(null, dispatch);

        const sakstema = Ajax.get(`/modiabrukerdialog/rest/saksoversikt/${fnr}/sakstema`);
        return sakstema
            .then(promisedDispatch(AT.LAST_LERRET_DATA_ALLE_SAKER_OK))
            .catch(rethrow(promisedDispatch(AT.LAST_LERRET_DATA_FEIL)));
    }
};

export const hentDokumentData = (fnr, valgtjournalpost) => {
    return (dispatch) => {
        const promisedDispatch = dataDispatch.bind(null, dispatch);

        const journalpostmetadata = Ajax.get(`/modiabrukerdialog/rest/saksoversikt/${fnr}/journalpostmetadata/${valgtjournalpost.journalpostId}?temakode=${valgtjournalpost.temakode}`);

        dispatch({ type: AT.LAST_DOKUMENT_DATA_START });
        return journalpostmetadata
            .then(promisedDispatch(AT.LAST_DOKUMENT_DATA_OK))
            .catch(rethrow(promisedDispatch(AT.LAST_DOKUMENT_DATA_FEIL)));
    }
};

export const velgDefaultFiltreringAvsender = () => {
    return (dispatch) => velgFiltreringAvsender(event.target.value)
};


export const velgSak = (sak) => ({ type: AT.VELG_SAK, data: sak });
export const velgJournalpost = (journalpost) => ({ type: AT.VELG_JOURNALPOST, data: journalpost });
export const visSide = (side) => ({ type: AT.VIS_SIDE, data: side });

export const velgFiltreringAvsender = (filtreringsvalg) => ({ type: AT.VELG_FILTRERING_AVSENDER, filtreringsvalg });

//Benyttes av Wicketklassen SaksoversiktLerret
export const visTema = (tema) => ({ type: AT.VIS_TEMA, data: tema });
export const purgeState = () => ({ type: AT.PURGE_STATE });