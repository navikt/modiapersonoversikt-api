import React from 'react';
import Ekspanderbartpanel from 'nav-frontend-ekspanderbartpanel';
import PT from 'prop-types';
import sanitize from 'sanitize-html';
import Utils from '../../utils/utils-module';

function toNameCase(navn) {
    return navn.replace(/\b(?!em)\w+?\b/g, (txt) => txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase());
}

function lagTypeoverskrift(melding) {
    switch (melding.type) {
        case 'sporsmal':
            return 'Spørsmål';
        case 'delvis':
            return 'Delvis Svar';
        default:
            return toNameCase(melding.type);
    }
}

function fraBruker(melding) {
    return melding.erInngaaende ? melding.fnrBruker : melding.navIdent;
}

function lagTittel(melding) {
    const typeoverskrift = lagTypeoverskrift(melding);
    const dato = sanitize(melding.visningsDatoTekst || 'Fant ingen data', { allowedTags: ['em'] });
    const meldingsForfatter = melding.erDokumentMelding || melding.type === 'sporsmal'
        ? ''
        : `Skrevet av: ${toNameCase(melding.skrevetAv.navn)} (${fraBruker(melding)})`;

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
