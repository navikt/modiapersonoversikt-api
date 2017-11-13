import React from 'react';
import PT from 'prop-types';
import Utils from './../utils/utils-module';
import sanitize from 'sanitize-html';
import Journalfort from './journalfort';

const toNameCase = (navn) => navn.replace(/\b(?!em)\w+?\b/g,
    (txt) => txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase());

class Melding extends React.Component {

    render() {
        const { melding } = this.props;

        const clsExt = melding.erInngaaende ? 'inngaaende' : 'utgaaende';
        const cls = `melding clearfix ${clsExt}`;
        const src = `/modiabrukerdialog/img/${(melding.erInngaaende ? 'meldinger/personikon.svg' : 'nav-logo.svg')}`;
        const altTekst = melding.erInngaaende ? 'Melding fra bruker' : 'Melding fra NAV';
        let meldingsStatusTekst = `${melding.statusTekst} - `;
        if (!melding.erInngaaende) {
            meldingsStatusTekst += `${melding.lestStatus} `;
        }
        meldingsStatusTekst += melding.temagruppeNavn;

        const paragrafer = melding.fritekst.split(/[\r\n]+/)
            .map(Utils.leggTilLenkerTags)
            .map(Utils.tilParagraf);

        const datoTekst = melding.visningsDatoTekst;

        const dato = sanitize(datoTekst || 'Fant ingen data', { allowedTags: ['em'] });
        const skrevetMelding = melding.erDokumentMelding
            ? ''
            : `Skrevet av: ${toNameCase(melding.skrevetAv.navn)} (${melding.fraBruker})`;

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
    melding: PT.shape({
        erInngaaende: PT.bool,
        statusTekst: PT.string,
        lestStatus: PT.string,
        temagruppeNavn: PT.string,
        journalfortTemanavn: PT.string,
        journalfortDatoTekst: PT.string,
        journalfortSaksId: PT.string,
        journalfortAvNavIdent: PT.string,
        visningsDatoTekst: PT.string,
        erDokumentMelding: PT.bool,
        skrevetAv: PT.shape({
            navn: PT.string
        }),
        journalfortAv: PT.shape({
            navn: PT.string
        })
    }).isRequired
};

export default Melding;
