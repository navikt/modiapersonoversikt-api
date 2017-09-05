import React from 'react';

import PleiepengerRettenPanel from './components/pleiepenger-retten';
import ArbeidsituasjonPanel from './components/arbeidssituasjon';
import UtbetalingerPanel from './components/utbetalinger-';

const PleiepengerPanel = props => {
    return (
        <div className="pleiepenger-panel">
            <PleiepengerRettenPanel tekst={props.tekst.pleiepengerRetten} graderingsgrad={props.graderingsgrad} pleiepengedager={props.pleiepengedager} forbrukteDagerTOMIDag={props.forbrukteDagerTOMIDag} forbrukteDagerEtterDennePerioden={props.forbrukteDagerEtterDennePerioden} barnet={props.barnet} fomDato={props.FOMDato} tomDato={props.TOMDato}/>
            <ArbeidsituasjonPanel tekst={props.tekst.arbeidsforhold} arbeidsgiver={props.arbeidsgiver}/>
            <UtbetalingerPanel tekst={props.tekst.utbetalinger} perioder={props.perioder}/>
        </div>
    );
};

PleiepengerPanel.propTypes = {
    tekst: React.PropTypes.object.isRequired,
    pleiepengedager: React.PropTypes.number.isRequired,
    forbrukteDagerTOMIDag: React.PropTypes.number.isRequired,
    forbrukteDagerEtterDennePerioden: React.PropTypes.number.isRequired,
    kompensasjonsgrad: React.PropTypes.number,
    graderingsgrad: React.PropTypes.number.isRequired,
    barnet: React.PropTypes.string.isRequired,
    andreOmsorgsperson: React.PropTypes.string,
    arbeidsforhold: React.PropTypes.object.isRequired,
    perioder: React.PropTypes.arrayOf(React.PropTypes.object).isRequired
};

export default PleiepengerPanel;
