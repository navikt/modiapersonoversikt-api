import React from 'react';
import PT from 'prop-types';

class Journalfort extends React.Component {
    render() {
        const { melding } = this.props;
        const {
            journalfortAv,
            journalfortAvNavIdent,
            journalfortDatoTekst,
            journalfortTemanavn,
            journalfortSaksId
        } = melding;
        const erJournalfort = journalfortTemanavn;

        if (!erJournalfort) {
            return <noscript />;
        }

        const journalFortInformasjon = `Journalført av: ${journalfortAv.navn} (${journalfortAvNavIdent}) |
            ${journalfortDatoTekst} | ${journalfortTemanavn}`;
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
    melding: PT.shape({
        journalfortTemanavn: PT.string,
        journalfortDatoTekst: PT.string,
        journalfortSaksId: PT.string,
        journalfortAvNavIdent: PT.string,
        journalfortAv: PT.shape({
            navn: PT.string
        })
    }).isRequired
};

export default Journalfort;
