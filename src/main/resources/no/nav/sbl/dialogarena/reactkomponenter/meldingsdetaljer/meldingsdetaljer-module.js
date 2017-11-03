import React, { Component } from 'react';
import Meldingspanel from './meldingspanel';
import Kategoripanel from './kategoripanel';
import PT from 'prop-types';

class MeldingsDetaljer extends Component {

    makeMeldingspanel(melding, key, apen) {
        return (
            <Meldingspanel
                key={key}
                apen={apen}
                melding={melding}
            >
                {melding.fritekst}
            </Meldingspanel>
        );
    }

    makeNedtrekspanel(traad, tittel, apen) {
        apen = apen || traad.length < 2;
        return (
            <Kategoripanel
                tittel={tittel}
                apen={apen}
            >
                {
                    traad.map((melding, index) => (
                        this.makeMeldingspanel(melding, index, apen)
                    ))
                }
            </Kategoripanel>
        );
    }

    render() {
        const traad = this.props.traad;
        traad.forEach((melding) => {
            melding.type = melding.meldingstype.split('_')[0].toLowerCase();
            melding.erInngaaende = ['SPORSMAL_SKRIFTLIG', 'SVAR_SBL_INNGAAENDE'].indexOf(melding.meldingstype) >= 0;
            melding.fraBruker = melding.erInngaaende ? melding.fnrBruker : melding.navIdent;
        });
        const delviseSvar = traad.filter((melding) => melding.type === 'delvis');
        if (delviseSvar.length > 0) {
            const sporsmal = traad[0];
            delviseSvar.unshift(sporsmal);
        }
        const apneTidligereMeldingsDetaljerPanel = delviseSvar > 0 && traad < 2;
        return (
            <div className="reactMeldingsDetaljer">
                {this.makeNedtrekspanel(traad, 'Vis tidligere meldingsdetaljer', apneTidligereMeldingsDetaljerPanel)}
                {delviseSvar.length !== 0 ? this.makeNedtrekspanel(delviseSvar, 'Delvis Svar', true) : ''}
            </div>
        );
    }
}

MeldingsDetaljer.propTypes = {
    traad: PT.array.isRequired
};
MeldingsDetaljer.defaultProps = {
};

export default MeldingsDetaljer;
