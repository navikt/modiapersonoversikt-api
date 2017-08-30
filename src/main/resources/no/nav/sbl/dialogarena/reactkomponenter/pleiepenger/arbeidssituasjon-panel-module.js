import React from 'react';
import moment from 'moment';

class ArbeidssituasjonPanel extends React.Component {
    render() {
        const { tekst, arbeidsgiver, kontonummer, inntektsperiode, refusjonstype } = this.props;
        const inntektForPerioden = this.props.inntektForPerioden
            .toLocaleString('nb-NO', {style: 'currency', currency: 'NOK', currencyDisplay: 'code'});
        const refusjonTilDato = moment(this.props.refusjonTilDato).format('DD.MM.YYYY');
        return (
            <div className="arbeidssituasjon">
                <h1>{ tekst['title'] }</h1>
                <dl>
                    <dt>{ tekst['arbeidsgiver'] }</dt>
                    <dd>{ arbeidsgiver }</dd>
                    <dt>{ tekst['kontonummer'] }</dt>
                    <dd>{ kontonummer }</dd>
                    <dt>{ tekst['inntektsperiode'] }</dt>
                    <dd>{ inntektsperiode }</dd>
                    <dt>{ tekst['inntektForPerioden'] }</dt>
                    <dd>{ inntektForPerioden }</dd>
                    <dt>{ tekst['refusjonstype'] }</dt>
                    <dd>{ refusjonstype }</dd>
                    <dt>{ tekst['refusjonTilDato'] }</dt>
                    <dd>{ refusjonTilDato }</dd>
                </dl>
            </div>
        );
    }
}

ArbeidssituasjonPanel.propTypes = {
    tekst: React.PropTypes.object.isRequired,
    arbeidsgiver: React.PropTypes.string.isRequired,
    kontonummer: React.PropTypes.string,
    inntektsperiode: React.PropTypes.string,
    inntektForPerioden: React.PropTypes.number,
    refusjonstype: React.PropTypes.string,
    refusjonTilDato: React.PropTypes.string,
};

export default ArbeidssituasjonPanel;
