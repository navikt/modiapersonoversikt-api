import React from 'react';
import DokumentinfoVedlegg from './dokument-info-vedlegg';
import DokumentAvsender from './dokument/dokument-avsender';

class DokumentInfoElm extends React.Component {
    render() {
        const { dokumentinfo, visTema, brukerNavn } = this.props;
        const temaHvisAlleTemaer = visTema === 'true' ? <p>{dokumentinfo.temakodeVisning}</p> : <noscript/>;
        return (
            <li className="dokumentliste-element dokument-kan-vises">
                <DokumentAvsender retning={dokumentinfo.retning}
                                  avsender={dokumentinfo.avsender}
                                  mottaker={dokumentinfo.mottaker}
                                  brukerNavn={brukerNavn} navn={dokumentinfo.navn}/>

                <a className="hoveddokument-tittel">{dokumentinfo.hoveddokument.tittel}</a>
                {temaHvisAlleTemaer}
                <div className="typo-info">
                    <DokumentinfoVedlegg vedlegg={dokumentinfo.vedlegg}/>
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
