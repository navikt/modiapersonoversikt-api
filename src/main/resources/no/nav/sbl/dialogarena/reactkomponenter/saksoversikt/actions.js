import * as AT from './actionTypes';
import Ajax from './../utils/ajax';
import Q from 'q';

function rethrow(fn) {
    return (data) => {
        debugger;
        console.error(data);

        fn(data);
    }
}

const dataDispatch = (dispatch, type) => (data) => dispatch({type, data});

export const hentWidgetData = (fnr) => {
    return (dispatch) => {
        const promisedDispatch = dataDispatch.bind(null, dispatch);

        dispatch({type: AT.LAST_WIDGET_DATA_START});
        return Ajax
            .get('/modiabrukerdialog/rest/saksoversikt/' + fnr + '/temaer')
            .then(promisedDispatch(AT.LAST_WIDGET_DATA_OK))
            .catch(rethrow(promisedDispatch(AT.LAST_WIDGET_DATA_FEIL)));
    }
};

export const hentLerretData = (fnr) => {
    return (dispatch) => {
        const promisedDispatch = dataDispatch.bind(null, dispatch);

        const temaer = Ajax.get('/modiabrukerdialog/rest/saksoversikt/' + fnr + '/temaer');
        const sakstema = Ajax.get('/modiabrukerdialog/rest/saksoversikt/' + fnr + '/sakstema');
        const tekster = Ajax.get('/modiabrukerdialog/rest/informasjon/tekster');
        const miljovariabler = Ajax.get('/modiabrukerdialog/rest/informasjon/miljovariabler');

        dispatch({type: AT.LAST_LERRET_DATA_START});
        return Q
            .all([temaer, sakstema, tekster, miljovariabler, fnr])
            .then(promisedDispatch(AT.LAST_LERRET_DATA_OK))
            .catch(rethrow(promisedDispatch(AT.LAST_LERRET_DATA_FEIL)));
    }
};

export const velgSak = (sak) => ({type: AT.VELG_SAK, data: sak});

//Benyttes av Wicketklassen SaksoversiktLerret
export const visTema = (tema) => ({type: AT.VIS_TEMA, data: tema});