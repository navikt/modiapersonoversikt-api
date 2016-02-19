import React from 'react/addons';
import DokumentInfoElm from './dokument-info-elm';

class DokumentListeForAarstall extends React.Component {
    render() {
        const dokumenter = this.props.dokumenter;
        const dokumentinfo = dokumenter.map(dokument => <DokumentInfoElm dokumentinfo={dokument}/>);
        const gjeldendeAar = new Date().getFullYear().toString();
        const aarstallSkille = this.props.aarstall === gjeldendeAar ? '' :
            <div className="aarstall">{this.props.aarstall}</div>;

        return (
            <li className="blokk-xxxs">
                {aarstallSkille}
                {dokumentinfo}
            </li>);
    }
}

DokumentListeForAarstall.propTypes = {
    dokumenter: React.PropTypes.array.isRequired,
    aarstall: React.PropTypes.string.isRequired
};

export default DokumentListeForAarstall;
