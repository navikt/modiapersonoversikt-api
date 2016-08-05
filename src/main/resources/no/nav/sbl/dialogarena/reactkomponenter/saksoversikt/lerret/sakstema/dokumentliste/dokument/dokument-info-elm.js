import React, { PropTypes as pt } from 'react';
import DokumentinfoVedlegg from './dokument-info-vedlegg';
import DokumentAvsender from './avsender/dokument-avsender';
import { FormattedDate, injectIntl } from 'react-intl';
import { datoformat, javaLocalDateTimeToJSDate } from './../../../../utils/dato-utils';
import dokumentinfoShape from './dokumentinfo-shape';

const kanViseVedlegg = vedleggListe => vedleggListe ? vedleggListe.some(vedlegg => vedlegg.kanVises) : false;
const kanViseDokumenter = (hoveddokument, vedlegg) => hoveddokument.kanVises || kanViseVedlegg(vedlegg);

class DokumentInfoElm extends React.Component {

    constructor() {
        super();
        this._redirect = this._redirect.bind(this);
    }

    _redirect(e) {
        e.preventDefault();
        this.props.velgJournalpost(this.props.dokumentinfo);
        this.props.visSide('dokumentvisning');
    }

    render() {
        const { dokumentinfo, visTema, brukerNavn, velgJournalpost, visSide } = this.props;
        const { retning, avsender, mottaker, navn, hoveddokument, vedlegg,
            temakodeVisning, feilWrapper, kategoriNotat } = dokumentinfo;
        const temaHvisAlleTemaer = visTema ? <p className="tema-dokument">{temakodeVisning}</p> : <noscript/>;
        const dokumentdato = javaLocalDateTimeToJSDate(dokumentinfo.dato);
        const kanViseDokument = (!feilWrapper.inneholderFeil && kanViseDokumenter(hoveddokument, vedlegg)) ?
            'dokument-kan-vises' : 'dokument-kan-ikke-vises';
        const skjultIngenTilgangTekst = kanViseDokument === 'dokument-kan-ikke-vises' ?
            <p className="vekk">Ikke tilgang til dokument</p> : '';

        return (
            <li className={`dokumentliste-element ${kanViseDokument}`} id={`${'a' + dokumentinfo.journalpostId}`}>
                <article aria-label={hoveddokument.tittel}>
                    {skjultIngenTilgangTekst}
                    <div className="datodokumentliste">
                        <FormattedDate value={dokumentdato} {...datoformat.NUMERISK_KORT} />
                        <span> / </span>
                        <DokumentAvsender
                            retning={retning}
                            avsender={avsender}
                            mottaker={mottaker}
                            brukerNavn={brukerNavn}
                            navn={navn}
                            kategoriNotat={kategoriNotat}
                        />
                    </div>
                    <div className="hoveddokument-tittel-wrapper">
                        <a href="javascript:void(0)" className="hoveddokument-tittel" onClick={this._redirect}>
                            {hoveddokument.tittel}
                        </a>
                    </div>
                    <div className="typo-info">
                        <DokumentinfoVedlegg
                            visSide={visSide}
                            velgJournalpost={velgJournalpost}
                            dokumentinfo={dokumentinfo}
                        />
                    </div>
                    {temaHvisAlleTemaer}
                </article>
            </li>
        );
    }
}

DokumentInfoElm.propTypes = {
    dokumentinfo: dokumentinfoShape.isRequired,
    visTema: pt.bool.isRequired,
    brukerNavn: pt.string,
    velgJournalpost: pt.func.isRequired,
    visSide: pt.func.isRequired
};

export default injectIntl(DokumentInfoElm);
