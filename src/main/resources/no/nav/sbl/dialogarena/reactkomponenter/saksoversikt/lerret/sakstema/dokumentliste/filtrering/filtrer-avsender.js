import React, { PropTypes as pt } from 'react';
import { velgFiltreringAvsender } from './../../../../actions';
import { connect } from 'react-redux';
import { FormattedMessage, injectIntl } from 'react-intl';
import { ALLE, NAV, BRUKER, ANDRE } from './filtrering-avsender-valg';

const _onSelect = (dispatch) => (event) => dispatch(velgFiltreringAvsender(event.target.value));

const FiltrerAvsender = ({ valgtValg, dispatch, intl: { formatMessage } }) => (
        <div className="filtrering-container">
            <FormattedMessage id={'dokumentliste.filtrering.forklaring'}/>
            <select className="select-container" value={valgtValg} onChange={_onSelect(dispatch)}>
                <option value={ALLE}>{ formatMessage({ id: 'dokumentliste.filtrering.alle' }) }</option>
                <option value={NAV}>{ formatMessage({ id: 'dokumentliste.filtrering.nav' }) }</option>
                <option value={BRUKER}>{ formatMessage({ id: 'dokumentliste.filtrering.bruker' }) }</option>
                <option value={ANDRE}>{ formatMessage({ id: 'dokumentliste.filtrering.andre' }) }</option>
            </select>
        </div>
);

FiltrerAvsender.propTypes = {
    valgtValg: pt.string.isRequired
};

export default injectIntl(connect(({ filtreringsvalg }) => ({ filtreringsvalg }))(FiltrerAvsender));
