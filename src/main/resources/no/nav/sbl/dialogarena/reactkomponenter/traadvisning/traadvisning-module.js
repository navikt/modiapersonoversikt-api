import React from 'react';
import Meldingspanel from './meldingspanel';
import Kategoripanel from './kategoripanel';
import PT from 'prop-types';

function TraadVisning(props) {
    function lagMeldingspanel(melding, key, apen) {
        return (
            <Meldingspanel key={key} apen={apen} melding={melding}>
                {melding.fritekst}
            </Meldingspanel>
        );
    }

    function lagNedtrekkspanel(traad, tittel, apen) {
        const apneMeldingsPanel = apen || traad.length < 2;
        const meldingspaneler = traad.map((melding, index) => (
            lagMeldingspanel(melding, index, apneMeldingsPanel)
        ));
        return (
            <Kategoripanel tittel={tittel} apen={apen}>
                {meldingspaneler}
            </Kategoripanel>
        );
    }

    const traad = props.traad.map((melding) => ({
        type: melding.meldingstype.split('_')[0].toLowerCase(),
        ...melding
    }));
    const delviseSvar = traad.filter((melding) => melding.type === 'delvis');

    const apneTraadVisning = delviseSvar.length === 0 && traad.length < 2;

    const traadPanel = traad.length === 1 ?
        lagMeldingspanel(traad[0], 0, true) :
        lagNedtrekkspanel(traad, 'Vis tidligere meldingsdetaljer', apneTraadVisning);
    const delvisSvarPanel = delviseSvar.length !== 0 ?
        lagNedtrekkspanel(delviseSvar, 'Delvise Svar', true) :
        '';

    return (
        <div className="reactTraadVisning">
            {traadPanel}
            {delvisSvarPanel}
        </div>
    );
}

TraadVisning.propTypes = {
    traad: PT.array.isRequired
};

export default TraadVisning;
