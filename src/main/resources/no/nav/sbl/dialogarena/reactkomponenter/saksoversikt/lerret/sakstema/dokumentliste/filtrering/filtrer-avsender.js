import React, { PropTypes as pt } from 'react';
import { velgFiltreringAvsender } from './../../../../actions';
import { connect } from 'react-redux';
import { FormattedMessage, injectIntl } from 'react-intl';
import { NAV, BRUKER, ANDRE } from './filtrering-avsender-valg';

const _onChange = (alleredeValgt, dispatch) => (event) => {
    const nyttFilterValg = {
        NAV: alleredeValgt[NAV],
        BRUKER: alleredeValgt[BRUKER],
        ANDRE: alleredeValgt[ANDRE]
    };

    nyttFilterValg[event.target.value] = !nyttFilterValg[event.target.value];
    return dispatch(velgFiltreringAvsender(nyttFilterValg));
};

const FiltrerAvsender = ({ alleredeValgt, dispatch }) => {
    const filtervalg = [NAV, BRUKER, ANDRE];
    const filtreringsCheckbox = filtervalg.map((valg) => (
            <div className="filtreringsvalg">
                <input name={valg} type="checkbox" value={valg} id={valg} checked={alleredeValgt[valg]}
                       onChange={_onChange(alleredeValgt, dispatch)}/>
                <label htmlFor={valg}>{valg}</label>
            </div>
        )
    );

    return (
        <div className="filtrering-container">
            <p>
                <FormattedMessage id={'dokumentliste.filtrering.forklaring'}/>
            </p>
            {filtreringsCheckbox}
        </div>
    );
};

FiltrerAvsender.propTypes = {
    alleredeValgt: pt.object.isRequired
};

export default injectIntl(connect(({ filtreringsvalg }) => ({ filtreringsvalg }))(FiltrerAvsender));
