import React from 'react';
import {groupBy} from 'lodash';
import DokumentInfoElm from './dokument/dokument-info-elm';

const nyesteForst = (a, b) => a.dato.dayOfYear < b.dato.dayOfYear ? 1 : -1;
const nyesteAarForst = (a, b) => a < b ? 1 : -1;

class DokumentListe extends React.Component {

    render() {
        const dokumentMetadata = this.props.dokumentMetadata;
        const dokumenterGruppertPaaAar = groupBy(dokumentMetadata, dokument => dokument.dato.year);

        const gjeldendeAar = new Date().getFullYear().toString();

        const dokumentListeForAarstall = Object.keys(dokumenterGruppertPaaAar)
            .sort(nyesteAarForst)
            .map(aarstall => ({aarstall, dokumenter: dokumenterGruppertPaaAar[aarstall].sort(nyesteForst)}))
            .reduce((acc, {aarstall, dokumenter}) => {
                if (aarstall !== gjeldendeAar) {
                    acc.push(<li className="aarstall">{aarstall}</li>);
                }
                return acc.concat(
                    dokumenter.map((dokument) => <DokumentInfoElm brukerNavn={this.props.brukerNavn} visTema={this.props.visTema}
                                                                  dokumentinfo={dokument}/>)
                );
            }, []);

        return (<ul className="ustilet dokumentliste">{dokumentListeForAarstall}</ul>);
    }
}

DokumentListe.propTypes = {
    dokumentMetadata: React.PropTypes.array.isRequired
};

export default DokumentListe;
