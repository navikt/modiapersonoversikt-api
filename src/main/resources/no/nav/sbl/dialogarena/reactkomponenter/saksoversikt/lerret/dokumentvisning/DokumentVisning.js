import React, { Component } from 'react';
import { autobind } from './../../../utils/utils-module';

const a4Ratio = 2 / Math.sqrt(2);
const iterimStyling = (antallSider, width = 750) => ({
    width: '100%',
    maxWidth: '100%',
    marginBottom: '2rem',
    height: a4Ratio * antallSider * width //magisk tall
});

class DokumentVisning extends Component {
    constructor(props) {
        super(props);

        this.state = {
            ...iterimStyling(props.dokument.antallSider)
        };

        autobind(this);
    }

    _oppdaterPdfVisning() {
        const pdf = this.refs.pdf;
        const width = pdf.offsetWidth;

        var height = a4Ratio * this.props.dokument.antallsider * width;

        if (this.state.height !== height) {
            this.setState({ height: height });
        }
    }

    componentDidMount() {
        this._oppdaterPdfVisning();
    }

    componentDidUpdate() {
        this._oppdaterPdfVisning();
    }

    render() {
        const { dokument } = this.props;
        const pdfData = `${dokument.pdfUrl}#view=FitH&amp;scrollbar=0&amp;toolbar=0&amp;statusbar=0&amp;messages=0&amp;navpanes=0`;

        return (
            <section key={`${dokument.journalpostId}--${dokument.dokumentreferanse}`}>
                <h2>{dokument.tittel}</h2>
                <object ref="pdf" data={pdfData} type="application/pdf" scrolling="no"
                        style={iterimStyling(dokument.antallsider)}>
                    <param name="view" value="FitV"/>

                    <p>Kunne ikke vise pdf inline</p>
                    <a href={dokument.pdfUrl} target="_blank">
                        Åpne i egen fane
                    </a>
                </object>
            </section>
        );
    }
}

export default DokumentVisning;