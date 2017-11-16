import React from 'react';
import { EkspanderbartpanelBase } from 'nav-frontend-ekspanderbartpanel';
import PT from 'prop-types';
import sanitize from 'sanitize-html';
import Utils from '../../utils/utils-module';
import { fraBruker, getMeldingsTypeTekst, MeldingsTyper } from '../../utils/melding-utils';
import { meldingITraadVisning } from '../props';

function finnMeldingsForfattere(melding) {
    if (melding.skrevetAvFlere !== undefined) {
        return `Skrevet av: ${melding.skrevetAvFlere}`;
    }
    return melding.erDokumentMelding || melding.meldingstype === MeldingsTyper.SPORSMAL_SKRIFTLIG ?
        '' :
        `Skrevet av: ${melding.skrevetAv.navn} (${fraBruker(melding)})`;
}

function lagTittel(melding) {
    const typeoverskrift = getMeldingsTypeTekst(melding);
    const dato = sanitize(melding.visningsDatoTekst || 'Fant ingen data', { allowedTags: ['em'] });
    const meldingsForfatter = finnMeldingsForfattere(melding);

    return (
        <div className="meldingsHeader">
            <div className="metadata">
                <span className="meldingsDato">
                    {dato}
                </span><br />
                <span className="meldingsForfatter">
                    {meldingsForfatter}
                </span>
            </div>
            <div className="tittelwrapper">
                <h3 className="typeoverskrift fortsettdialogpaneloverskrift">
                    {typeoverskrift}
                </h3>
                <h4 className="temagruppeoverskrift">
                    {melding.temagruppeNavn}
                </h4>
            </div>
        </div>
    );
}

function Meldingspanel(props) {
    const melding = props.melding;
    const paragrafer = props.children.split(/[\r\n]+/)
        .map(Utils.leggTilLenkerTags)
        .map((avsnitt, index) => Utils.tilParagraf(avsnitt, index));
    const tittel = lagTittel(melding);
    return (
        <EkspanderbartpanelBase
            className="meldingspanel"
            ariaTittel={`Ekspander ${getMeldingsTypeTekst(melding)}`}
            heading={tittel}
            apen={props.apen}
        >
            <div className="ekspanderbartPanel__innhold__melding">
                {paragrafer}
            </div>
        </EkspanderbartpanelBase>
    );
}

Meldingspanel.propTypes = {
    melding: meldingITraadVisning.isRequired,
    apen: PT.bool,
    children: PT.node.isRequired
};
Meldingspanel.defaultProps = {
    apen: false
};

export default Meldingspanel;
