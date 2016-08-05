import React, { PropTypes as pt } from 'react';

class Journalfort extends React.Component {
    render() {
        const { melding } = this.props;
        const { journalfortAv, journalfortAvNavIdent, journalfortDatoTekst, journalfortTemanavn, journalfortSaksId } = melding;
        const erJournalfort = journalfortTemanavn;

        if (!erJournalfort) {
            return <noscript />;
        }

        const journalFortInformasjon = `Journalført av: ${journalfortAv.navn} (${journalfortAvNavIdent}) | ${journalfortDatoTekst} | ${journalfortTemanavn}`;
        const saksIdInformasjon = `Saksid ${journalfortSaksId}`;
        const journalfortMelding = `${journalFortInformasjon} | ${saksIdInformasjon}`;

        return (
            <div className="journalpost-link">
                <div className="journalpost-element ikon">
                    <span className="ikon"></span>
                    <span >{journalfortMelding}</span>
                </div>
            </div>
        );
    }
}

Journalfort.propTypes = {
    melding: React.PropTypes.shape({
        journalfortTemanavn: pt.string,
        journalfortDatoTekst: pt.string,
        journalfortSaksId: pt.string,
        journalfortAvNavIdent: pt.string,
        journalfortAv: pt.shape({
            navn: pt.string
        })
    }).isRequired
};

export default Journalfort;
