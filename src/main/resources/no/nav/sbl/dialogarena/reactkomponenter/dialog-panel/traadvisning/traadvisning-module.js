import React from 'react';
import Meldingspanel from './meldingspanel';
import Kategoripanel from './kategoripanel';
import PT from 'prop-types';
import { eldsteMeldingForst, erDelvisSvar, erSkriftligSvar } from '../../utils/melding-utils';
import { meldingITraadVisning } from '../props';

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
        lagMeldingspanel(melding, index, apneMeldingsPanel)
    ));
    return (
        <Kategoripanel tittel={tittel} apen={skalVisesApen}>
            {meldingspaneler}
        </Kategoripanel>
    );
}

function flettDelviseSvarInnISkriftligSvar(traad, delviseSvar) {
    const skriftligeSvar = traad.filter(erSkriftligSvar);
    if (skriftligeSvar.length > 0) {
        const avsluttendeSvar = skriftligeSvar.sort(eldsteMeldingForst)[0];
        const dobbeltLinjeskift = '\n\u00A0\n';
        avsluttendeSvar.fritekst = delviseSvar.concat(avsluttendeSvar)
            .map(melding => melding.fritekst)
            .join(dobbeltLinjeskift);
        avsluttendeSvar.skrevetAvFlere = delviseSvar.concat(avsluttendeSvar)
            .map(melding => melding.skrevetAv.navn + ' (' + melding.navIdent + ')')
            .join(' og ');
    }
}

function slaaSammen(traad) {
    const delviseSvar = traad.filter(erDelvisSvar);
    if (delviseSvar.length > 0) {
        flettDelviseSvarInnISkriftligSvar(traad, delviseSvar);
    }
    return traad.filter(melding => !erDelvisSvar(melding));
}

function sporsmalErIkkeBesvart(traad) {
    return traad.filter(erSkriftligSvar) < 1;
}

function erUtenSvar(sammenslattTraad) {
    return sammenslattTraad.length === 1;
}

function lagTraadPanel(traad) {
    const sammenslaattTraad = slaaSammen(traad);
    const sporsmal = sammenslaattTraad[0];
    const skalNedtrekkspanelVisesApen = erUtenSvar(sammenslaattTraad);
    const tittel = 'Vis tidligere meldinger';
    const key = 0;
    return erUtenSvar(traad) ?
        lagMeldingspanel(sporsmal, key, skalNedtrekkspanelVisesApen) :
        lagNedtrekkspanel(sammenslaattTraad, tittel, skalNedtrekkspanelVisesApen);
}

function lagDelviseSvarPanel(traad) {
    const delviseSvar = traad.filter(erDelvisSvar);
    const tittel = 'Tidligere delvise svar';
    const skalVisesApen = true;
    return delviseSvar.length > 0 && sporsmalErIkkeBesvart(traad) ?
        lagNedtrekkspanel(delviseSvar, tittel, skalVisesApen) :
        '';
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
