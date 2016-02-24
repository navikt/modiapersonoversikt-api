import React from 'react';
import DokumentinfoVedlegg from './dokument-info-vedlegg';

class DokumentInfoElm extends React.Component {
    render() {
        const dokumentinfo = this.props.dokumentinfo;
        const temaHvisAlleTemaer = this.props.visTema === 'true' ? <p>{dokumentinfo.temakodeVisning}</p> : <noscript/>;
        return (
            <li className="dokumentlisteelement">
                <p>Fra {dokumentinfo.avsender}</p>
                <a>{dokumentinfo.hoveddokument.tittel}</a>
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
