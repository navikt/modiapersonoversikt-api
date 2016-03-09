import React, { Component } from 'react';
import { debounce, autobind } from './../../../utils/utils-module';
import { Element } from 'react-scroll';

const a4Ratio = 2 / Math.sqrt(2);
const stylingFn = (antallSider, width = 750) => ({
    width: '100%',
    maxWidth: '100%',
    marginBottom: '2rem',
    height: a4Ratio * antallSider * width + 100 //magisk tall
});

class DokumentVisning extends Component {
    constructor(props) {
        super(props);

        this.state = {
            ...stylingFn(props.dokument.antallSider)
        };

        autobind(this);
    }

    _oppdaterPdfVisning() {
        const pdf = this.refs.pdf;
        const width = pdf.offsetWidth;

        const height = stylingFn(this.props.dokument.antallsider, width).height;

        if (this.state.height !== height) {
            this.setState({ height: height });
        }
    }

    _print(e) {
        e.preventDefault();
        try {
            this.refs.pdf.print()
        } catch (e) {
            const printWindow = window.open(this.props.dokument.pdfUrl);

            printWindow.onload = () => {
                printWindow.print();
            };

            return false;
        }

    }

    componentDidMount() {
        this.eventHandler = debounce(this._oppdaterPdfVisning, 150);
        window.addEventListener('resize', this.eventHandler);
        this._oppdaterPdfVisning();
    }

    componentDidUpdate() {
        this._oppdaterPdfVisning();
    }

    componentWillUnmount() {
        window.removeEventListener('resize', this.eventHandler);
    }

    render() {
        const { dokument } = this.props;
        const pdfData = `${dokument.pdfUrl}#view=FitH&scrollbar=0&toolbar=0&statusbar=0&messages=0&navpanes=0`;
        const style = { ...this.state };

        return (
            <Element name={dokument.dokumentreferanse} key={`${dokument.journalpostId}--${dokument.dokumentreferanse}`}>
                <div className="dokumentheader blokk-xxxs">
                    <h2 className="typo-element">{dokument.tittel}</h2>
                    <div className="lokal-linker">
                        <a target="_blank" href={dokument.pdfUrl}>
                            <span>Åpne som PDF</span>
                        </a>
                        <a href="javscript:void(0)" onClick={this._print}>
                            <span>Skriv ut</span>
                        </a>
                    </div>
                </div>
                <object ref="pdf" data={pdfData} type="application/pdf" scrolling="no" style={style} key={style.height}>
                    <param name="view" value="FitV"/>

                    <p>Kunne ikke vise pdf inline</p>
                    <a href={dokument.pdfUrl} target="_blank">
                        Åpne i egen fane
                    </a>
                </object>
            </Element>
        );
    }
}

export default DokumentVisning;
