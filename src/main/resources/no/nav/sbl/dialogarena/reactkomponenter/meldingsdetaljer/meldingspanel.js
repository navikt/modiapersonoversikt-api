import React, { Component } from 'react';
import Ekspanderbartpanel from 'nav-frontend-ekspanderbartpanel';
import PT from 'prop-types';
import sanitize from 'sanitize-html';
import Utils from '../utils/utils-module';

const toNameCase = (navn) => navn.replace(/\b(?!em)\w+?\b/g,
    (txt) => txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase());

class Meldingspanel extends Component {

    tittel() {
        const melding = this.props.melding;

        const typeoverskrift = (() => {
            switch (melding.type) {
                case 'sporsmal':
                    return 'Spørsmål';
                case 'delvis':
                    return 'Delvis Svar';
                default:
                    return toNameCase(melding.type);
            }
        })();

        const dato = sanitize(melding.visningsDatoTekst || 'Fant ingen data', { allowedTags: ['em'] });
        const meldingsForfatter = melding.erDokumentMelding || melding.type === 'sporsmal'
            ? ''
            : `Skrevet av: ${toNameCase(melding.skrevetAv.navn)} (${melding.fraBruker})`;

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

    render() {
        const paragrafer = this.props.children.split(/[\r\n]+/)
            .map(Utils.leggTilLenkerTags)
            .map((avsnitt, index) => Utils.tilParagraf(avsnitt, index));
        return (
            <Ekspanderbartpanel className="meldingspanel" tittel={this.tittel()} apen={this.props.apen}>
                <div className="ekspanderbartPanel__innhold__melding">
                    {paragrafer}
                </div>
            </Ekspanderbartpanel>
        );
    }
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
