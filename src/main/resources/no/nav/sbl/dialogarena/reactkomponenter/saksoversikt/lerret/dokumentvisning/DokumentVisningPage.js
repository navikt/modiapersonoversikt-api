import React from 'react';
import { hentDokumentData, hentLerretData } from './../../actions';
import { wrapWithProvider } from './../../utils/redux-utils';
import { store } from './../../store';
import { connect } from 'react-redux';
import VedleggFeilmelding from './feilmelding/VedleggFeilmelding';
import * as Const from './../../konstanter';
import Snurrepipp from './../../../utils/snurrepipp';

class DokumentVisningPage extends React.Component {
    componentWillMount() {
        this.props.hentDokumentData(this.props.fnr, '123', '123');
    }

    _redirect(e) {
        e.preventDefault();
        this.props.visSide('sakstema');
    }


    render() {
        if (this.props.lerretstatus !== Const.LASTET || this.props.dokumentstatus !== Const.LASTET) {
            return <Snurrepipp />;
        }
        const { dokumentmetadata } = this.props;
        return (
            <div className="grattpanel side-innhold">
                <div className="blokk-s">
                    <a href="javascript:void(0);" onClick={this._redirect.bind(this)}>Tilbake til sakstema</a>
                </div>
                <panel className="panel">
                    <h1 className="decorated typo-innholdstittel">Dokumentvisning</h1>
                    <section>
                        <VedleggFeilmelding dokumentmetadata={dokumentmetadata} />
                    </section>
                </panel>
            </div>
        )
    };
}

const mapStateToProps = (state) => {
    return {
        valgtside: state.lerret.valgtside,
        dokumentmetadata: state.dokument.data[0],
        journalpostmetadata: state.dokument.data[1],
        lerretstatus: state.lerret.status,
        dokumentstatus: state.dokument.status,
        valgtTema: state.lerret.valgtTema,
        tekster: state.lerret.data.tekster
    };
};

export default wrapWithProvider(connect(mapStateToProps, {hentDokumentData, hentLerretData})(DokumentVisningPage), store);