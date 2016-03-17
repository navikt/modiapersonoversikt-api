import React from 'react';
import { hentDokumentData } from './../../actions';
import { wrapWithProvider } from './../../utils/redux-utils';
import { store } from './../../store';
import { connect } from 'react-redux';
import VedleggFeilmeldingListe from './VedleggFeilmeldingListe';
import * as Const from './../../konstanter';
import Snurrepipp from './../../../utils/snurrepipp';
import { datoformat, javaLocalDateTimeToJSDate } from './../../utils/dato-utils';
import DokumentVisningListe from './DokumentVisningListe'
import { FormattedMessage, FormattedDate } from 'react-intl';
import KulemenyListe from './kulemeny/KulemenyListe';

function lagKulelistedata(dokumenter, feiledeDokumenter) {
    const feil = feiledeDokumenter.map((dokument, index) => {
        dokument.dokumentreferanse = dokument.feilmeldingEnonicKey + index;
        return dokument;
    });

    return [].concat(dokumenter).concat(feil)
        .map((dokument) => ({
            dokumentreferanse: dokument.dokumentreferanse,
            tittel: dokument.tittel
        }));
}

class DokumentVisningPage extends React.Component {
    _redirect(e) {
        e.preventDefault();
        this.props.visSide('sakstema');
    }

    componentWillMount() {
        this.props.hentDokumentData(this.props.fnr, this.props.valgtJournalpost);
    }

    render() {
        if (this.props.lerretstatus !== Const.LASTET || this.props.dokumentstatus !== Const.LASTET) {
            return <Snurrepipp />;
        }
        const { journalpostmetadata, valgtTema } = this.props;

        const values = {
            retning: this.props.valgtJournalpost.retning,
            navn: this.props.valgtJournalpost.navn,
            dato: javaLocalDateTimeToJSDate(this.props.valgtJournalpost.dato)
        };

        const kulelisteVM = lagKulelistedata(journalpostmetadata.dokumenter, journalpostmetadata.feilendeDokumenter);

        return (
            <div className="dokument-visning-page">
                <div className="fixed-header blokk-s">
                    <a href="javascript:void(0);" onClick={this._redirect.bind(this)}>Tilbake til sakstema</a>
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
        )
    };
}

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
