import React from 'react';
import Ekspanderbartpanel from 'nav-frontend-ekspanderbartpanel';
import PT from 'prop-types';
import sanitize from 'sanitize-html';
import Utils from '../../utils/utils-module';
import { fraBruker, MeldingsTyper } from '../../utils/melding-utils';

function lagTypeoverskrift(melding) {
    switch (melding.meldingstype) {
        case MeldingsTyper.SPORSMAL_SKRIFTLIG:
            return 'Spørsmål';
        case MeldingsTyper.DELVIS_SVAR:
            return 'Delvis Svar';
        case MeldingsTyper.SVAR_SKRIFTLIG:
            return 'Svar';
        default:
            return melding.meldingstype;
    }
}

function finnMeldingsForfatter(melding) {
    if (typeof melding.skrevetAvFlere !== 'undefined') {
        return `Skrevet av: ${melding.skrevetAvFlere}`;
    }
    return melding.erDokumentMelding || melding.meldingstype === MeldingsTyper.SPORSMAL_SKRIFTLIG ?
        '' :
        `Skrevet av: ${melding.skrevetAv.navn} (${fraBruker(melding)})`;
}

function lagTittel(melding) {
    const typeoverskrift = lagTypeoverskrift(melding);
    const dato = sanitize(melding.visningsDatoTekst || 'Fant ingen data', { allowedTags: ['em'] });
    const meldingsForfatter = finnMeldingsForfatter(melding);

    return (
        <div>
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
        <Ekspanderbartpanel
            className="meldingspanel"
            tittel={tittel}
            apen={props.apen}
        >
            <div className="ekspanderbartPanel__innhold__melding">
                {paragrafer}
            </div>
        </Ekspanderbartpanel>
    );
}

Meldingspanel.propTypes = {
    melding: PT.object.isRequired,
    apen: PT.bool,
    children: PT.node.isRequired
};
Meldingspanel.defaultProps = {
    apen: false
};

export default Meldingspanel;
