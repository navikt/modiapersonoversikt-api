import React from 'react';
import PT from 'prop-types';
import AlertStripeSuksessSolid from '../../alertstriper/alertstripe-module';

class Markeringer extends React.Component {
    render()
    {
        const { melding } = this.props;
        if (melding.erFerdigstiltUtenSvar) {
            return (
                <AlertStripeSuksessSolid header={'Henvendelsen er avsluttet uten å svare bruker'}>
                    <MarkeringsTekst markering={melding.ferdigstiltUtenSvarAv} dato={melding.ferdigstiltUtenSvarDatoTekst}/>
                </AlertStripeSuksessSolid>);
        } else if (melding.markertSomFeilsendtAvNavIdent) {
            return (
                <AlertStripeSuksessSolid header={'Feilsendt post'}>
                    <MarkeringsTekst markering={melding.markertSomFeilsendtAv} dato={melding.markertSomFeilsendtDatoTekst}/>
                </AlertStripeSuksessSolid>);
        } else if (melding.kontorsperretAvNavIdent) {
            return (
                <AlertStripeSuksessSolid header={'Kontorsperret til enhet ' + melding.kontorsperretEnhet}>
                    <MarkeringsTekst markering={melding.kontorsperretAv} dato={melding.kontorsperretDatoTekst}/>
                </AlertStripeSuksessSolid>);
        }
        return (<div></div>);
    }

}

function MarkeringsTekst (props) {
    const { markering, dato }  = props;
    return (<span>{markering.fornavn} {markering.etternavn} ({markering.ident}) {dato}</span>);
}

Markeringer.propTypes = {
    melding: PT.shape({
        erFerdigstiltUtenSvar: PT.bool,
        ferdigstiltUtenSvarAv: PT.shape({
            fornavn: PT.string,
            etternavn: PT.string,
            navn: PT.string,
            ident: PT.string
        }),
        ferdigstiltUtenSvarDatoTekst: PT.string,
        ferdigstiltUtenSvarAvNavIdent: PT.string,
        markertSomFeilsendtAv: PT.shape({
            fornavn: PT.string,
            etternavn: PT.string,
            navn: PT.string,
            ident: PT.string
        }),
        markertSomFeilsendtAvNavIdent: PT.string,
        markertSomFeilsendtDatoTekst: PT.string,
        kontorsperretAv: PT.shape({
            fornavn: PT.string,
            etternavn: PT.string,
            navn: PT.string,
            ident: PT.string
        }),
        kontorsperretAvNavIdent: PT.string,
        kontorsperretDatoTekst: PT.string,
        kontorsperretEnhet: PT.string
    }).isRequired
};

export default Markeringer;