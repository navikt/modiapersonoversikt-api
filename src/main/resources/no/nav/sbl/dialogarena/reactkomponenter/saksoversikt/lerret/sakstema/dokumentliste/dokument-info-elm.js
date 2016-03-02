import React from 'react';
import DokumentinfoVedlegg from './dokument-info-vedlegg';
import DokumentAvsender from './dokument/dokument-avsender';
import { FormattedDate } from 'react-intl';
import { datoformat, javaLocalDateTimeToJSDate } from './../../../utils/dato-utils';

// TODO stateless function
class DokumentInfoElm extends React.Component {
    render() {
        const { dokumentinfo, visTema, brukerNavn } = this.props;
        const temaHvisAlleTemaer = visTema === 'true' ? <p>{dokumentinfo.temakodeVisning}</p> : <noscript/>;
        const dokumentdato = javaLocalDateTimeToJSDate(dokumentinfo.dato);

        return (
            <li className="dokumentlisteelement">
                <p className="datodokumentliste">
                    <FormattedDate value={dokumentdato} {...datoformat.NUMERISK_KORT} />
                </p>
                <DokumentAvsender className="avsendertext" retning={dokumentinfo.retning}
                                  avsender={dokumentinfo.avsender}
                                  mottaker={dokumentinfo.mottaker}
                                  brukerNavn={brukerNavn} navn={dokumentinfo.navn}/>

                <div className="hoveddokumenttextwrapper">
                    <a href="javascript:void(0)" className="hoveddokumenttext">{dokumentinfo.hoveddokument.tittel}</a>
                </div>
                {temaHvisAlleTemaer}
                <div className="typo-info">
                    <DokumentinfoVedlegg vedlegg={dokumentinfo.vedlegg}/>
                </div>
            </li>
        );
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
