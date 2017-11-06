import React, { Component } from 'react';
import PT from 'prop-types';

import PleiepengerRettenPanel from './components/pleiepengeretten/pleiepengeretten-panel';
import ArbeidssituasjonPanel from './components/arbeidsituasjon/arbeidssituasjon-panel';
import UtbetalingerPanel from './components/utbetalinger/utbetalinger-panel';

// eslint-disable-next-line react/prefer-stateless-function
class PleiepengerPanel extends Component {

    render() {
        const props = this.props;

        return (
            <div className="pleiepenger-panel">
                <PleiepengerRettenPanel
                    tekst={props.tekst.pleiepengerRetten}
                    pleiepengegrad={props.pleiepengegrad}
                    pleiepengedager={props.pleiepengedager}
                    kompensasjonsgrad={props.kompensasjonsgrad}
                    andreOmsorgsperson={props.andreOmsorgsperson}
                    forbrukteDagerTOMIDag={props.forbrukteDagerTOMIDag}
                    forbrukteDagerEtterDennePerioden={props.forbrukteDagerEtterDennePerioden}
                    barnet={props.barnet}
                    fomDato={props.FOMDato}
                    tomDato={props.TOMDato}
                />
                <ArbeidssituasjonPanel tekst={props.tekst.arbeidsforhold} arbeidsforhold={props.arbeidsforhold} />
                <UtbetalingerPanel tekst={props.tekst.utbetalinger} perioder={props.perioder} />
            </div>
        );
    }
}

PleiepengerPanel.propTypes = {
    tekst: PT.object.isRequired,
    pleiepengedager: PT.number.isRequired,
    forbrukteDagerTOMIDag: PT.number.isRequired,
    forbrukteDagerEtterDennePerioden: PT.number.isRequired,
    kompensasjonsgrad: PT.number,
    pleiepengegrad: PT.number,
    barnet: PT.string.isRequired,
    andreOmsorgsperson: PT.string,
    arbeidsforhold: PT.array.isRequired,
    perioder: PT.arrayOf(PT.object).isRequired
};

export default PleiepengerPanel;
