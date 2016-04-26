import React, { Component } from 'react';
import { debounce, autobind } from './../../../utils/utils-module';
import { Element } from 'react-scroll';
import { injectIntl, intlShape } from 'react-intl';
import GenerellFeilMeldingDokumentvisning from './feilmelding/generell-feilmelding-dokumentvisning';

const a4Ratio = 2 / Math.sqrt(2);
const stylingFn = (antallSider = 1, width = 750) => ({
    width: '100%',
    maxWidth: '100%',
    marginBottom: '2rem',
    height: a4Ratio * antallSider * width + 100 // magisk tall
});

class DokumentVisning extends Component {
    constructor(props) {
        super(props);

        this.state = {
            ...stylingFn(props.dokument.antallSider)
        };

        autobind(this);
    }

    componentDidMount() {
        this.eventHandler = debounce(this._oppdaterPdfVisning, 150);
        window.addEventListener('resize', this.eventHandler);
        this._oppdaterPdfVisning();

        // Firefox fix
        $(this.refs.pdf).ready(() => {
            this.cancelIntervalID = setInterval(() => this._oppdaterPdfVisning(), 250);
        });
    }

    componentDidUpdate() {
        this._oppdaterPdfVisning();
    }

    componentWillUnmount() {
        window.removeEventListener('resize', this.eventHandler);
        clearInterval(this.cancelIntervalID);
    }

    _oppdaterPdfVisning() {
        const pdf = this.refs.pdf;
        if (!pdf) {
            return;
        }
        const width = pdf.offsetWidth;

        const height = stylingFn(this.props.dokument.antallSider, width).height;

        if (this.state.height !== height) {
            this.setState({ height });
        }
    }

    _print(e) {
        e.preventDefault();
        try {
            this.refs.pdf.print();
        } catch (ex) {
            const printWindow = window.open(this.props.dokument.pdfUrl);

            printWindow.onload = () => {
                printWindow.print();
            };
            return false;
        }
    }

    render() {
        const { dokument, intl: { formatMessage } } = this.props;
        const pdfData = `${dokument.pdfUrl}#view=FitH&scrollbar=0&toolbar=0&statusbar=0&messages=0&navpanes=0`;
        const style = { ...this.state };
        const aapneSomPDFLink = (
            <a target="_blank" href={dokument.pdfUrl}>
                { formatMessage({ id: 'dokumentvisning.pdf.aapne.pdf' }) }
            </a>
        );

        return (
            <Element name={dokument.dokumentreferanse} key={`${dokument.journalpostId}--${dokument.dokumentreferanse}`}>
                <article>
                    <div className="dokumentheader blokk-xxxs">
                        <h2 className="typo-element">{dokument.tittel}</h2>
                        <div className="lokal-linker">
                            {aapneSomPDFLink}
                            <a href="javscript:void(0)" onClick={this._print}>
                                { formatMessage({ id: 'dokumentvisning.pdf.skriv.ut' }) }
                            </a>
                        </div>
                    </div>
                    <object ref="pdf" data={pdfData} type="application/pdf" scrolling="no" style={style}
                      key={`${dokument.journalpostId}--${dokument.dokumentreferanse}`}
                    >
                        <param name="view" value="FitV"/>
                        <GenerellFeilMeldingDokumentvisning aapneSomPDFLink={aapneSomPDFLink}/>
                    </object>
                </article>
            </Element>
        );
    }
}

DokumentVisning.propTypes = {
    dokument: React.PropTypes.shape({
        antallSider: React.PropTypes.number,
        dokumentreferanse: React.PropTypes.string,
        journalpostId: React.PropTypes.string,
        pdfUrl: React.PropTypes.string
    }).isRequired,
    intl: intlShape
};

export default injectIntl(DokumentVisning);
