import React, {PropTypes as pt} from "react";
import Utils from "./../utils/utils-module";
import sanitize from "sanitize-html";
import format from "string-format";

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

        const erJournalfort = melding.journalfortTemanavn;
        const journalfortMelding = format('Journalført av: {} ({}) | {} | {} | Saksid {}',
            melding.journalfortAv.navn,
            melding.journalfortAvNavIdent,
            melding.journalfortDatoTekst,
            melding.journalfortTemanavn,
            melding.journalfortSaksId);
        const journalfortVisning = !erJournalfort ? null :
            <div className="journalpost-link">
                <div className="journalpost-element ikon">
                    <span className="ikon"></span>
                    <span >{journalfortMelding}</span>
                </div>
            </div>;

        let paragrafer = melding.fritekst.split(/[\r\n]+/)
            .map(Utils.leggTilLenkerTags)
            .map(Utils.tilParagraf);

        const dato = sanitize(melding.opprettetDatoTekst || 'Fant ingen data', { allowedTags: ['em'] });
        const skrevetMelding = melding.erDokumentMelding ? '' : `Skrevet av: ${toNameCase(melding.skrevetAv.navn)} (${melding.fraBruker})`;

        return (
            <div className={cls}>
                <img className={`avsenderBilde ${clsExt}`} src={src} alt={altTekst}/>
                <div className="meldingData">
                    <article className="melding-header">
                        <p className="meldingstatus">{meldingsStatusTekst}</p>
                        <p>{dato}</p>
                        <p>{skrevetMelding}</p>
                    </article>
                    <article className="fritekst">{paragrafer}</article>
                </div>
                {journalfortVisning}
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
        opprettetDatoTekst: pt.string,
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
