import React from 'react';
import { hentDokumentData, hentLerretData } from './../../actions';
import { wrapWithProvider } from './../../utils/redux-utils';
import { store } from './../../store';
import { connect } from 'react-redux';
import VedleggFeilmeldingListe from './VedleggFeilmeldingListe';
import * as Const from './../../konstanter';
import Snurrepipp from './../../../utils/snurrepipp';

class DokumentVisningPage extends React.Component {
    componentWillMount() {
        this.props.hentDokumentData(this.props.fnr, this.props.valgtJournalpost, this.props.valgtTema.temakode);
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

        return (
            <div className="grattpanel side-innhold">
                <div className="blokk-s">
                    <a href="javascript:void(0);" onClick={this._redirect.bind(this)}>Tilbake til sakstema</a>
                </div>
                <panel className="panel">
                    <h1 className="decorated typo-innholdstittel">Dokumentvisning</h1>
                    <section>
                        <VedleggFeilmeldingListe feilmeldinger={journalpostmetadata.feilendeDokumenter}/>
                    </section>
                </panel>
            </div>
        )
    };
}

const mapStateToProps = (state) => {
    return {
        valgtside: state.lerret.valgtside,
        journalpostmetadata: state.dokument.data[0],
        lerretstatus: state.lerret.status,
        dokumentstatus: state.dokument.status,
        valgtTema: state.lerret.valgtTema,
        valgtJournalpost: state.lerret.valgtJournalpost,
        tekster: state.lerret.data.tekster
    };
};

export default wrapWithProvider(connect(mapStateToProps, {
    hentDokumentData,
    hentLerretData
})(DokumentVisningPage), store);