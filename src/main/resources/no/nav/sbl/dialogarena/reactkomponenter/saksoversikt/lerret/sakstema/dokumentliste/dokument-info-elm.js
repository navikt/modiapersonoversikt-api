import React, { PropTypes as pt } from 'react';
import DokumentinfoVedlegg from './dokument-info-vedlegg';
import DokumentAvsender from './dokument/dokument-avsender';
import { FormattedDate } from 'react-intl';
import { javaLocalDateTimeToJSDate } from './../../../utils/dato-utils';

const kanViseVedlegg = vedleggListe => vedleggListe ? vedleggListe.some(vedlegg => vedlegg.kanVises) : false;
const kanViseDokumenter = (hoveddokument, vedlegg) => hoveddokument.kanVises || kanViseVedlegg(vedlegg);

const DokumentInfoElm = ({ dokumentinfo, visTema, brukerNavn }) => {
    const { retning, avsender, mottaker, navn, hoveddokument, vedlegg, temakodeVisning, feilWrapper } = dokumentinfo;
    const temaHvisAlleTemaer = visTema === 'true' ? <p>{temakodeVisning}</p> : <noscript/>;
    const kanViseDokument = (!feilWrapper.inneholderFeil && kanViseDokumenter(hoveddokument, vedlegg)) ? 'dokument-kan-vises' : 'dokument-kan-ikke-vises';

    return (
        <li className={`dokumentliste-element ${kanViseDokument}`}>
            <p className="datodokumentliste"><FormattedDate value={javaLocalDateTimeToJSDate(dokumentinfo.dato)} day="2-digit" month="2-digit" year="numeric"/></p>
            <DokumentAvsender retning={retning} avsender={avsender} navn={navn} mottaker={mottaker} brukerNavn={brukerNavn}/>

            <div className="hoveddokument-tittel-wrapper">
                <a className="hoveddokument-tittel" to="#">{hoveddokument.tittel}</a>
            </div>
            {temaHvisAlleTemaer}
            <div className="typo-info">
                <DokumentinfoVedlegg vedlegg={vedlegg}/>
            </div>
        </li>);
};

DokumentInfoElm.propTypes = {
    dokumentinfo: pt.shape({
        retning: pt.string.isRequired,
        avsender: pt.string.isRequired,
        mottaker: pt.string.isRequired,
        navn: pt.string,
        hoveddokument: pt.object.isRequired,
        vedlegg: pt.array,
        temakodeVisning: pt.string,
        feilWrapper: pt.object.isRequired
    }).isRequired,
    visTema: pt.string,
    brukerNavn: pt.string
};

export default DokumentInfoElm;
