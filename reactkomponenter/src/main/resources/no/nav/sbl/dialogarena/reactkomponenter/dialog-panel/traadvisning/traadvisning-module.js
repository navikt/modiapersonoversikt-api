import React from 'react';
import Meldingspanel from './meldingspanel';
import Kategoripanel from './kategoripanel';
import PT from 'prop-types';
import { erDelvisSvar } from '../../utils/melding-utils';
import { meldingITraadVisning } from '../props';
import { erBesvart, erIkkeBesvart, filtrerBortDelviseSvar } from '../../utils/traad-utils';

function lagMeldingspanel(melding, apen) {
    return (
        <Meldingspanel key={melding.id} apen={apen} melding={melding}>
            {melding.fritekst}
        </Meldingspanel>
    );
}

function lagNedtrekkspanel(traad, tittel, skalVisesApen) {
    const apneMeldingsPanel = skalVisesApen || traad.length < 2;
    const meldingspaneler = traad.map((melding) => (
        lagMeldingspanel(melding, apneMeldingsPanel)
    ));
    return (
        <Kategoripanel tittel={tittel} apen={skalVisesApen}>
            {meldingspaneler}
        </Kategoripanel>
    );
}

function lagEnkelstaaendePanel(traad) {
    const skalVisesApen = true;
    const sporsmal = traad[0];
    return (
        <div className="frittstaaende-meldingspanel">
            {lagMeldingspanel(sporsmal, skalVisesApen)}
        </div>
    );
}

function lagTraadPanel(traad) {
    const filtrertTraad = filtrerBortDelviseSvar(traad);

    const ubesvartSporsmaal = erIkkeBesvart(traad) && traad.length === 1;
    if (ubesvartSporsmaal) {
        return lagEnkelstaaendePanel(filtrertTraad);
    }
    const sammenslattOgUbesvart = erIkkeBesvart(traad) && traad.length >= 1;
    if (sammenslattOgUbesvart) {
        const tittel = 'Spørsmål fra bruker';
        const skalVisesApen = true;
        return lagNedtrekkspanel(filtrertTraad, tittel, skalVisesApen);
    }
    const tittel = 'Vis tidligere meldinger';
    const skalVisesApen = false;
    return lagNedtrekkspanel(filtrertTraad, tittel, skalVisesApen);
}

function lagDelsvarPanel(traad) {
    const delviseSvar = traad.filter(erDelvisSvar);
    if (delviseSvar.length === 0 || erBesvart(traad)) {
        return null;
    }

    const tittel = 'Tidligere delsvar';
    const skalVisesApen = true;
    return delviseSvar.length === 1
        ? <div className="frittstaaende-meldingspanel">{lagMeldingspanel(delviseSvar[0], skalVisesApen)}</div>
        : lagNedtrekkspanel(delviseSvar, tittel, skalVisesApen);
}

function TraadVisning(props) {
    const traad = props.traad.slice(0).reverse();
    const traadPanel = lagTraadPanel(traad);
    const delsvarPanel = lagDelsvarPanel(traad);
    return (
        <div className="reactTraadVisning">
            {traadPanel}
            {delsvarPanel}
        </div>
    );
}

TraadVisning.propTypes = {
    traad: PT.arrayOf(meldingITraadVisning).isRequired
};

export default TraadVisning;
