import React from 'react/addons';
import DokumentListeForAarstall from './dokumentliste-for-aarstall';
import {groupBy} from 'lodash';

const nyesteForst = (a, b) => a.dato.dayOfYear < b.dato.dayOfYear ? 1 : -1;
const nyesteAarForst = (a, b) => a < b ? 1 : -1;

class DokumentListe extends React.Component {

    render() {
        const dokumentMetadata = this.props.dokumentMetadata;
        const dokumenterGruppertPaaAar = groupBy(dokumentMetadata, dokument => dokument.dato.year);
        const dokumenter = Object.keys(dokumenterGruppertPaaAar).sort(nyesteAarForst).map(aarstall =>
            <DokumentListeForAarstall aarstall={aarstall}
                                      dokumenter={dokumenterGruppertPaaAar[aarstall].sort(nyesteForst)}/>);
        return (<ul className="ustilet">{dokumenter}</ul>);
    }
}

DokumentListe.propTypes = {
    dokumentMetadata: React.PropTypes.array.isRequired
};

export default DokumentListe;
