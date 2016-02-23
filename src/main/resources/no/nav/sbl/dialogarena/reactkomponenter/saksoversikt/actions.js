import * as AT from './actionTypes';
import Ajax from './../utils/ajax';
import Q from 'q';

const dataDispatch = (dispatch, type) => (data) => dispatch({ type, data });

export const hentWidgetData = (fnr) => {
    return (dispatch) => {
        const promisedDispatch = dataDispatch.bind(null, dispatch);

        dispatch({ type: AT.LAST_WIDGET_DATA_START });
        return Ajax
            .get('/modiabrukerdialog/rest/saksoversikt/' + fnr + '/temaer')
            .then(promisedDispatch(AT.LAST_WIDGET_DATA_OK))
            .catch(promisedDispatch(AT.LAST_WIDGET_DATA_FEIL));
    }
};

export const hentLerretData = (fnr) => {
    return (dispatch) => {
        const promisedDispatch = dataDispatch.bind(null, dispatch);

        const temaer = Ajax.get('/modiabrukerdialog/rest/saksoversikt/' + fnr + '/temaer');
        const journalposter = Ajax.get('/modiabrukerdialog/rest/saksoversikt/' + fnr + '/journalposter');
        const sakstema = Ajax.get('/modiabrukerdialog/rest/saksoversikt/' + fnr + '/sakstema');
        const tekster = Ajax.get('/modiabrukerdialog/rest/informasjon/tekster');
        const miljovariabler = Ajax.get('/modiabrukerdialog/rest/informasjon/miljovariabler');

        dispatch({ type: AT.LAST_LERRET_DATA_START });
        return Q
            .all([temaer, journalposter, sakstema, tekster, miljovariabler])
            .then(promisedDispatch(AT.LAST_LERRET_DATA_OK))
            .catch(promisedDispatch(AT.LAST_LERRET_DATA_FEIL));
    }
};

export const velgTema = (sak) => ({ type: 'VOID' });
