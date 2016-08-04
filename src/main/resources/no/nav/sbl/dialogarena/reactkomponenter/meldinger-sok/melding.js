import React, { PropTypes as pt } from 'react';
import Utils from './../utils/utils-module';
import sanitize from 'sanitize-html';
import Journalfort from './journalfort';

const toNameCase = (navn) => navn.replace(/\b(?!em)\w+?\b/g, (txt) => txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase());

class Melding extends React.Component {

    render() {
        const { melding } = this.props;

        const clsExt = melding.erInngaaende ? 'inngaaende' : 'utgaaende';
        const cls = `melding clearfix ${clsExt}`;
        const src = `/modiabrukerdialog/img/${(melding.erInngaaende ? 'personikon.svg' : 'nav-logo.svg')}`;
        const altTekst = melding.erInngaaende ? 'Melding fra bruker' : 'Melding fra NAV';
        let meldingsStatusTekst = `${melding.statusTekst}, `;
        if (!melding.erInngaaende) {
            meldingsStatusTekst += `${melding.lestStatus} `;
        }
        meldingsStatusTekst += melding.temagruppeNavn;

        const paragrafer = melding.fritekst.split(/[\r\n]+/)
            .map(Utils.leggTilLenkerTags)
            .map(Utils.tilParagraf);

        const datoTekst = melding.visningsDatoTekst; 

        const dato = sanitize(datoTekst || 'Fant ingen data', { allowedTags: ['em'] });
        const skrevetMelding = melding.erDokumentMelding ? '' : `Skrevet av: ${toNameCase(melding.skrevetAv.navn)} (${melding.fraBruker})`;

        return (
            <div className={cls}>
                <img className={`avsenderBilde ${clsExt}`} src={src} alt={altTekst} />
                <div className="meldingData">
                    <article className="melding-header">
                        <p className="meldingstatus" dangerouslySetInnerHTML={{ __html: meldingsStatusTekst }}></p>
                        <p>{dato}</p>
                        <span dangerouslySetInnerHTML={{ __html: skrevetMelding }}></span>
                    </article>
                    <article className="fritekst">{paragrafer}</article>
                </div>
                <Journalfort melding={melding} />
            </div>
        );
    }
}

Melding.propTypes = {
    melding: React.PropTypes.shape({
        erInngaaende: pt.bool,
        statusTekst: pt.string,
        lestStatus: pt.string,
        temagruppeNavn: pt.string,
        journalfortTemanavn: pt.string,
        journalfortDatoTekst: pt.string,
        journalfortSaksId: pt.string,
        journalfortAvNavIdent: pt.string,
        visningsDatoTekst: pt.string,
        erDokumentMelding: pt.bool,
        skrevetAv: pt.shape({
            navn: pt.string
        }),
        journalfortAv: pt.shape({
            navn: pt.string
        })
    }).isRequired
};

export default Melding;
