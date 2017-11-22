import React from 'react';
import Meldingspanel from './meldingspanel';
import Kategoripanel from './kategoripanel';
import PT from 'prop-types';
import { erDelvisSvar } from '../../utils/melding-utils';
import { meldingITraadVisning } from '../props';
import { slaaSammenDelviseSvar, erBesvart, erIkkeBesvart } from '../../utils/traad-utils';

function lagMeldingspanel(melding, key, apen) {
    return (
        <Meldingspanel key={key} apen={apen} melding={melding}>
            {melding.fritekst}
        </Meldingspanel>
    );
}

function lagNedtrekkspanel(traad, tittel, skalVisesApen) {
    const apneMeldingsPanel = skalVisesApen || traad.length < 2;
    const meldingspaneler = traad.map((melding, index) => (
        lagMeldingspanel(melding, `meldingspanel-${index}`, apneMeldingsPanel)
    ));
    return (
        <Kategoripanel tittel={tittel} apen={skalVisesApen}>
            {meldingspaneler}
        </Kategoripanel>
    );
}

function lagTraadPanel(traad) {
    const sammenslaattTraad = slaaSammenDelviseSvar(traad);
    const sporsmal = sammenslaattTraad[0];
    const skalIkkeVisesApen = false;
    const skalVisesApen = true;
    const tittel = 'Vis tidligere meldinger';
    const key = 'meldingspanel';
    return erIkkeBesvart(traad)
        ? <div className="frittstaaende-meldingspanel">{lagMeldingspanel(sporsmal, key, skalVisesApen)}</div>
        : lagNedtrekkspanel(sammenslaattTraad, tittel, skalIkkeVisesApen);
}

function lagDelviseSvarPanel(traad) {
    const delviseSvar = traad.filter(erDelvisSvar);
    if (delviseSvar.length === 0 || erBesvart(traad)) return '';
    const tittel = 'Tidligere delsvar';
    const skalVisesApen = true;
    const key = 'delvissvarpanel';
    return delviseSvar.length === 1
        ? <div className="frittstaaende-meldingspanel">{lagMeldingspanel(delviseSvar[0], key, skalVisesApen)}</div>
        : lagNedtrekkspanel(delviseSvar, tittel, skalVisesApen);
}

function TraadVisning(props) {
    const traad = props.traad;
    const traadPanel = lagTraadPanel(traad);
    const delvisSvarPanel = lagDelviseSvarPanel(traad);
    return (
        <div className="reactTraadVisning">
            {traadPanel}
            {delvisSvarPanel}
        </div>
    );
}

TraadVisning.propTypes = {
    traad: PT.arrayOf(meldingITraadVisning).isRequired
};

export default TraadVisning;
