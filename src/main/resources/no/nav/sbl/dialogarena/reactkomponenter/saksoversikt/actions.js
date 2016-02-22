import * as AT from './actionTypes';
import Ajax from './../utils/ajax';

const dataDispatch = (dispatch, type) => (data) => dispatch({ type, data });

export const hentTemaer = (fnr) => {
    return (dispatch) => {
        const promisedDispatch = dataDispatch.bind(null, dispatch);

        dispatch({ type: AT.LAST_DATA_START });
        return Ajax
            .get('/modiabrukerdialog/rest/saksoversikt/' + fnr + '/temaer')
            .then(promisedDispatch(AT.LAST_DATA_OK))
            .catch(promisedDispatch(AT.LAST_DATA_FEIL));
    }
};
