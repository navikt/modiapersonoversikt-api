import React, { PropTypes as PT } from 'react';
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

    componentDidMount() {
        const saksoversiktLamellhode = document.querySelector('.saksoversikt .lamellhode a');
        if (saksoversiktLamellhode) {
            saksoversiktLamellhode.focus();
        }
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
                    <GenerellFeilMeldingDokumentvisning/>
                </div>);
        }
        if (this.props.lerretstatus !== Const.LASTET || this.props.dokumentstatus !== Const.LASTET) {
            return <Snurrepipp farge="hvit"/>;
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
                    <a href="#" onClick={this._redirect} className="close-document" aria-label={ intl.formatMessage({ id: 'dokumentvisning.aria.lukk' })}></a>
                    <KulemenyListe dokumentmetadata={kulelisteVM} indexValgtDokument={indexValgtDokument}/>
                </div>

                <section aria-labelledby="journalposttittel" className="scrollpanel gratt side-innhold" id="js-kulemeny-scroll">
                    <panel className="panel">
                        <h1 className="decorated typo-innholdstittel" id="journalposttittel">
                            <FormattedMessage id="dokumentvisning.retningsstatus" values={values}/>
                            <FormattedDate value={values.dato} {...datoformat.NUMERISK_2_DIGIT}/>
                        </h1>
                        <section aria-label="Dokumentliste">
                            <DokumentVisningListe dokumenter={journalpostmetadata.dokumenter}/>
                            <VedleggFeilmeldingListe feilmeldinger={journalpostmetadata.feilendeDokumenter}/>
                        </section>
                    </panel>
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
