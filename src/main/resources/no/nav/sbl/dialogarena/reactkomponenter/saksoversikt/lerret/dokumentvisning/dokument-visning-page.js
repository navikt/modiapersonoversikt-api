import React, { PropTypes as PT } from 'react';
import { hentDokumentData } from './../../actions';
import { wrapWithProvider } from './../../utils/redux-utils';
import { store } from './../../store';
import { connect } from 'react-redux';
import VedleggFeilmeldingListe from './vedlegg-feilmelding-liste';
import * as Const from './../../konstanter';
import Snurrepipp from './../../../utils/snurrepipp';
import { datoformat, javaLocalDateTimeToJSDate } from './../../utils/dato-utils';
import DokumentVisningListe from './dokument-visning-liste';
import { FormattedMessage, FormattedDate } from 'react-intl';
import KulemenyListe from './kulemeny/kulemeny-liste';

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

class DokumentVisningPage extends React.Component {
    constructor() {
        super();
        this._redirect = this._redirect.bind(this);
    }

    componentWillMount() {
        this.props.hentDokumentData(this.props.fnr, this.props.valgtJournalpost);
    }

    _redirect(e) {
        e.preventDefault();
        this.props.visSide('sakstema');
    }

    render() {
        if (this.props.lerretstatus !== Const.LASTET || this.props.dokumentstatus !== Const.LASTET) {
            return <Snurrepipp />;
        }
        const { journalpostmetadata } = this.props;

        const values = {
            retning: this.props.valgtJournalpost.retning,
            navn: this.props.valgtJournalpost.navn,
            dato: javaLocalDateTimeToJSDate(this.props.valgtJournalpost.dato)
        };

        const kulelisteVM = lagKulelistedata(journalpostmetadata.dokumenter, journalpostmetadata.feilendeDokumenter);

        return (
            <div className="dokument-visning-page">
                <div className="fixed-header blokk-s">
                    <a href="javascript:void(0);" onClick={this._redirect}>Tilbake til sakstema</a>
                    <KulemenyListe dokumentmetadata={kulelisteVM}/>
                </div>

                <div className="grattpanel side-innhold" id="js-kulemeny-scroll">
                    <panel className="panel">
                        <h1 className="decorated typo-innholdstittel">
                            <FormattedMessage id="dokumentvisning.retningsstatus" values={values}/>
                            <FormattedDate value={values.dato} {...datoformat.NUMERISK_2_DIGIT}/>
                        </h1>
                        <section>
                            <DokumentVisningListe dokumenter={journalpostmetadata.dokumenter}/>
                            <VedleggFeilmeldingListe feilmeldinger={journalpostmetadata.feilendeDokumenter}/>
                        </section>
                    </panel>
                </div>
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
    journalpostmetadata: PT.object.isRequired
};

const mapStateToProps = (state) => {
    return {
        journalpostmetadata: state.dokument.data,
        lerretstatus: state.lerret.status,
        dokumentstatus: state.dokument.status,
        valgtJournalpost: state.lerret.valgtJournalpost,
        tekster: state.lerret.data.tekster
    };
};

export default wrapWithProvider(connect(mapStateToProps, {
    hentDokumentData
})(DokumentVisningPage), store);
