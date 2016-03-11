import React from 'react';
import { velgFiltreringAvsender } from './../../../../../actions';
import { connect } from 'react-redux';
import { injectIntl } from 'react-intl';
import { ALLE, NAV, BRUKER, ANDRE } from './FiltreringAvsenderValg';

const _onSelect = (dispatch) => (event) => dispatch(velgFiltreringAvsender(event.target.value));

const FiltrerAvsender = ({ dispatch, intl: { formatMessage } }) => {
    return (
        <select className="select-container" onChange={_onSelect(dispatch)}>
            <option className="placeholder" selected disabled>{ formatMessage({ id: 'dokumentliste.filtrering.avsender' }) }</option>
            <option value={ALLE}>{ formatMessage({ id: 'dokumentliste.filtrering.alle' }) }</option>
            <option value={NAV}>{ formatMessage({ id: 'dokumentliste.filtrering.nav' }) }</option>
            <option value={BRUKER}>{ formatMessage({ id: 'dokumentliste.filtrering.bruker' }) }</option>
            <option value={ANDRE}>{ formatMessage({ id: 'dokumentliste.filtrering.andre' }) }</option>
        </select>
    );
};

export default injectIntl(connect(({ filtreringsvalg }) => ({ filtreringsvalg }))(FiltrerAvsender));