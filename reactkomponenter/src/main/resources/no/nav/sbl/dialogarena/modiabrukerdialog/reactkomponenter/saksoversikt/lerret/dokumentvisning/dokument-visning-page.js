import React from 'react';
import PT from 'prop-types';
import { hentDokumentData } from './../../actions';
import { wrapWithProvider } from './../../utils/redux-utils';
import { store } from './../../store';
import { connect } from 'react-redux';
import VedleggFeilmeldingListe from './feilmelding/vedlegg-feilmelding-liste';
import * as Const from './../../konstanter';
import Snurrepipp from './../../../utils/snurrepipp';
import { datoformat, javaLocalDateTimeToJSDate } from './../../utils/dato-utils';
import DokumentVisningListe from './dokument-visning-liste';
import { FormattedMessage, FormattedDate, injectIntl, intlShape } from 'react-intl';
import KulemenyListe from './kulemeny/kulemeny-liste';
import GenerellFeilMeldingDokumentvisning from './feilmelding/generell-feilmelding-dokumentvisning';

function lagKulelistedata(dokumenter, feiledeDokumenter) {
    const feil = feiledeDokumenter.map((dokument, index) => {
        const nytDokument = Object.assign({}, dokument);
        nytDokument.dokumentreferanse = dokument.feilmeldingEnonicKey + index;
        return nytDokument;
    });

    return [].concat(dokumenter).concat(feil)
             .map((dokument) => ({
                 dokumentreferanse: dokument.dokumentreferanse,
                 tittel: dokument.tittel
             }));
}

function getNotatTekst(valgtJournalpost, intl) {
    if (valgtJournalpost.retning !== 'INTERN') {
        return '';
    }
    return valgtJournalpost.kategoriNotat === 'FORVALTNINGSNOTAT' ?
        intl.formatMessage({ id: 'dokumentinfo.forvaltningsnotat' }) :
        intl.formatMessage({ id: 'dokumentinfo.internnotat' });
}

export class DokumentVisningPage extends React.Component {
    constructor() {
        super();
        this._redirect = this._redirect.bind(this);
    }

    componentWillMount() {
        this.props.hentDokumentData(this.props.fnr, this.props.valgtJournalpost);
    }

    componentDidUpdate() {
        setTimeout(() => this.refs.dokumentvisningOverskrift && this.refs.dokumentvisningOverskrift.focus(), 0);
    }

    _redirect(e) {
        e.preventDefault();
        this.props.visSide('sakstema');
    }

    render() {
        if (this.props.dokumentstatus === Const.FEILET) {
            return (
                <div className="dokument-visning-page">
                    <div className="fixed-header blokk-s">
                        <a href="#" onClick={this._redirect} className="close-document"></a>
                    </div>
                    <GenerellFeilMeldingDokumentvisning />
                </div>);
        }
        if (this.props.lerretstatus !== Const.LASTET || this.props.dokumentstatus !== Const.LASTET) {
            return <Snurrepipp farge="hvit" />;
        }

        const { intl, journalpostmetadata } = this.props;

        const values = {
            retning: this.props.valgtJournalpost.retning,
            navn: this.props.valgtJournalpost.navn,
            typeIntern: getNotatTekst(this.props.valgtJournalpost, intl),
            dato: javaLocalDateTimeToJSDate(this.props.valgtJournalpost.dato)
        };

        const indexValgtDokument = this.props.valgtJournalpost.valgtIndex;

        const kulelisteVM = lagKulelistedata(journalpostmetadata.dokumenter, journalpostmetadata.feilendeDokumenter);

        return (
            <div className="dokument-visning-page">
                <div className="fixed-header">
                    <button
                        onClick={this._redirect}
                        className="close-document"
                        type="button"
                        aria-label={intl.formatMessage({ id: 'dokumentvisning.aria.lukk' })}
                        title={intl.formatMessage({ id: 'dokumentvisning.aria.lukk' })}
                    >
                    </button>
                    <KulemenyListe dokumentmetadata={kulelisteVM} indexValgtDokument={indexValgtDokument} />
                </div>

                <section
                    aria-labelledby="journalposttittel"
                    className="scrollpanel side-innhold panel"
                    id="js-kulemeny-scroll"
                >
                    <h1
                        ref="dokumentvisningOverskrift"
                        className="decorated typo-innholdstittel ikke-fokusmarkering"
                        tabIndex="-1"
                        id="journalposttittel"
                    >
                        <FormattedMessage id="dokumentvisning.retningsstatus" values={values} />
                        <FormattedDate value={values.dato} {...datoformat.NUMERISK_2_DIGIT} />
                    </h1>
                    <DokumentVisningListe dokumenter={journalpostmetadata.dokumenter} />
                    <VedleggFeilmeldingListe feilmeldinger={journalpostmetadata.feilendeDokumenter} />
                </section>
            </div>
        );
    }
}

DokumentVisningPage.propTypes = {
    hentDokumentData: PT.func.isRequired,
    fnr: PT.string.isRequired,
    valgtJournalpost: PT.object.isRequired,
    visSide: PT.func.isRequired,
    lerretstatus: PT.string.isRequired,
    dokumentstatus: PT.string.isRequired,
    journalpostmetadata: PT.shape({
        dokumenter: PT.array,
        feilendeDokumenter: PT.array
    }).isRequired,
    intl: intlShape
};

const mapStateToProps = (state) => ({
    journalpostmetadata: state.dokument.data,
    lerretstatus: state.lerret.status,
    dokumentstatus: state.dokument.status,
    valgtJournalpost: state.lerret.valgtJournalpost,
    tekster: state.lerret.data.tekster
});

export default wrapWithProvider(connect(mapStateToProps, {
    hentDokumentData
})(injectIntl(DokumentVisningPage)), store);
