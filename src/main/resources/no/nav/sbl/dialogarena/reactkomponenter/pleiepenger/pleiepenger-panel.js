import React, { Component } from 'react';

import PleiepengerRettenPanel from './components/pleiepengeretten';
import ArbeidsituasjonPanel from './components/arbeidssituasjon';
import UtbetalingerPanel from './components/utbetalinger/utbetalinger-panel';

// eslint-disable-next-line react/prefer-stateless-function
class PleiepengerPanel extends Component {

    render() {
        const props = this.props;

        return (
            <div className="pleiepenger-panel">
                <PleiepengerRettenPanel
                    tekst={props.tekst.pleiepengerRetten}
                    graderingsgrad={props.graderingsgrad}
                    pleiepengedager={props.pleiepengedager}
                    forbrukteDagerTOMIDag={props.forbrukteDagerTOMIDag}
                    forbrukteDagerEtterDennePerioden={props.forbrukteDagerEtterDennePerioden}
                    barnet={props.barnet}
                    fomDato={props.FOMDato}
                    tomDato={props.TOMDato}
                />
                <ArbeidsituasjonPanel tekst={props.tekst.arbeidsforhold} arbeidsforhold={props.arbeidsforhold} />
                <UtbetalingerPanel tekst={props.tekst.utbetalinger} perioder={props.perioder} />
            </div>
        );
    }
}

PleiepengerPanel.propTypes = {
    tekst: React.PropTypes.object.isRequired,
    pleiepengedager: React.PropTypes.number.isRequired,
    forbrukteDagerTOMIDag: React.PropTypes.number.isRequired,
    forbrukteDagerEtterDennePerioden: React.PropTypes.number.isRequired,
    kompensasjonsgrad: React.PropTypes.number,
    graderingsgrad: React.PropTypes.number.isRequired,
    barnet: React.PropTypes.string.isRequired,
    andreOmsorgsperson: React.PropTypes.string,
    arbeidsforhold: React.PropTypes.array.isRequired,
    perioder: React.PropTypes.arrayOf(React.PropTypes.object).isRequired
};

export default PleiepengerPanel;
