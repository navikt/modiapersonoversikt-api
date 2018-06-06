import React from 'react';
import PT from 'prop-types';
import AlertStripeSuksessSolid from '../../alertstriper/alertstripe-module';
import Utils from '../../utils/utils-module';
import sanitize from 'sanitize-html';
import Journalfort from './journalfort';
import { finnMeldingsForfattere } from '../../utils/melding-utils';

class Melding extends React.Component {

    render() {
        const { melding } = this.props;

        const clsExt = melding.erInngaaende ? 'inngaaende' : 'utgaaende';
        const cls = `melding clearfix ${clsExt}`;
        const src = `/modiabrukerdialog/img/${(melding.erInngaaende ? 'meldinger/personikon.svg' : 'nav-logo.svg')}`;
        const altTekst = melding.erInngaaende ? 'Melding fra bruker' : 'Melding fra NAV';
        let meldingsStatusTekst = `${melding.statusTekst} – `;
        if (!melding.erInngaaende) {
            meldingsStatusTekst += `${melding.lestStatus} `;
        }
        meldingsStatusTekst += melding.temagruppeNavn;

        const paragrafer = melding.fritekst.split(/[\r\n]+/)
            .map(Utils.leggTilLenkerTags)
            .map(Utils.tilParagraf);

        const datoTekst = melding.visningsDatoTekst;
        const dato = sanitize(datoTekst || 'Fant ingen data', { allowedTags: ['em'] });
        const meldingsForfatter = finnMeldingsForfattere(melding);

        class HentEventuelleMarkeringer extends React.Component {
            render()
            {
                if (melding.erFerdigstiltUtenSvar) {
                    return (
                        <AlertStripeSuksessSolid header={'Henvendelsen er avsluttet uten å svare bruker'}>
                            {melding.ferdigstiltUtenSvarAv.fornavn} {melding.ferdigstiltUtenSvarAv.etternavn} ({melding.ferdigstiltUtenSvarAv.ident}) {melding.ferdigstiltUtenSvarDatoTekst}
                        </AlertStripeSuksessSolid>);
                } else if (melding.markertSomFeilsendtAvNavIdent) {
                    return (
                        <AlertStripeSuksessSolid header={'Feilsendt post'}>
                            {melding.markertSomFeilsendtAv.fornavn} {melding.markertSomFeilsendtAv.etternavn} ({melding.markertSomFeilsendtAv.ident}) {melding.markertSomFeilsendtDatoTekst}
                        </AlertStripeSuksessSolid>);
                } else if (melding.kontorsperretAvNavIdent != null && melding.kontorsperretAvNavIdent != '') {
                    return (
                        <AlertStripeSuksessSolid header={'Kontorsperret til enhet ' + melding.kontorsperretEnhet}>
                            {melding.kontorsperretAv.fornavn} {melding.kontorsperretAv.etternavn} ({melding.kontorsperretAv.ident}) {melding.kontorsperretDatoTekst}
                        </AlertStripeSuksessSolid>);
                }
                return (<div></div>);
            }

        }

        return (
            <div>
                <HentEventuelleMarkeringer melding={melding} />
                <div className={cls}>
                    <img className={`avsenderBilde ${clsExt}`} src={src} alt={altTekst} />
                    <div className="meldingData">
                        <article className="melding-header">
                            <p className="meldingstatus" dangerouslySetInnerHTML={{ __html: meldingsStatusTekst }}></p>
                            <p>{dato}</p>
                            <span dangerouslySetInnerHTML={{ __html: meldingsForfatter }}></span>
                        </article>
                        <article className="fritekst">{paragrafer}</article>
                    </div>
                    <Journalfort melding={melding} />
                </div>
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
        }),
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

export default Melding;
