import React, { Component } from 'react';
import Meldingspanel from './meldingspanel';
import Kategoripanel from './kategoripanel';
import PT from 'prop-types';

class MeldingsDetaljer extends Component {

    lagMeldingspanel(melding, key, apen) {
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

    lagNedtrekkspanel(traad, tittel, apen) {
        apen = apen || traad.length < 2;
        return (
            <Kategoripanel
                tittel={tittel}
                apen={apen}
            >
                {
                    traad.map((melding, index) => (
                        this.lagMeldingspanel(melding, index, apen)
                    ))
                }
            </Kategoripanel>
        );
    }

    render() {
        const traad = this.props.traad.map((melding) => ({
            type: melding.meldingstype.split('_')[0].toLowerCase(),
            ...melding
        }));
        const delviseSvar = traad.filter((melding) => melding.type === 'delvis');
        const apneTidligereMeldingsDetaljerPanel = delviseSvar.length === 0 && traad.length < 2;
        return (
            <div className="reactMeldingsDetaljer">
                {traad.length === 1 ?
                    this.lagMeldingspanel(traad[0], 0, true) :
                    this.lagNedtrekkspanel(traad, 'Vis tidligere meldingsdetaljer', apneTidligereMeldingsDetaljerPanel)}
                {delviseSvar.length !== 0 ? this.lagNedtrekkspanel(delviseSvar, 'Delvise Svar', true) : ''}
            </div>
        );
    }
}

MeldingsDetaljer.propTypes = {
    traad: PT.array.isRequired
};

export default MeldingsDetaljer;
