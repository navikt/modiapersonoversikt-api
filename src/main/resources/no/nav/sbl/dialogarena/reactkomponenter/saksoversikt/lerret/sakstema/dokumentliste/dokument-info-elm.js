import React from 'react';
import DokumentinfoVedlegg from './dokument-info-vedlegg';
import DokumentAvsender from './dokument/dokument-avsender';
import { FormattedDate } from 'react-intl';
import { javaLocalDateTimeToJSDate } from './../../../utils/dato-utils';

class DokumentInfoElm extends React.Component {

    _redirect(e) {
        e.preventDefault();
        this.props.velgJournalpost(this.props.dokumentinfo);
        this.props.visSide('dokumentvisning');
    }


    render() {
        const dokumentinfo = this.props.dokumentinfo;
        const temaHvisAlleTemaer = this.props.visTema === 'true' ? <p>{dokumentinfo.temakodeVisning}</p> : <noscript/>;
        return (
            <li className="dokumentlisteelement">
                <p className="datodokumentliste"><FormattedDate value={javaLocalDateTimeToJSDate(dokumentinfo.dato)}
                                                                day="2-digit" month="2-digit"
                                                                year="numeric"/></p>
                <DokumentAvsender className="avsendertext" retning={dokumentinfo.retning}
                                  avsender={dokumentinfo.avsender}
                                  mottaker={dokumentinfo.mottaker}
                                  brukerNavn={this.props.brukerNavn} navn={dokumentinfo.navn}/>

                <div className="hoveddokumenttextwrapper"><a href="javascript:void(0);"
                                                             onClick={this._redirect.bind(this)}
                                                             className="hoveddokumenttext">{dokumentinfo.hoveddokument.tittel}</a>
                </div>
                {temaHvisAlleTemaer}
                <div className="typo-info">
                    <DokumentinfoVedlegg visSide={this.props.visSide} vedlegg={dokumentinfo.vedlegg}/>
                </div>
            </li>);
    }
}

DokumentInfoElm.propTypes = {
    dokumentinfo: React.PropTypes.shape({
        avsender: React.PropTypes.string,
        hoveddokument: React.PropTypes.object.isRequired,
        vedlegg: React.PropTypes.array
    }).isRequired
};

export default DokumentInfoElm;
