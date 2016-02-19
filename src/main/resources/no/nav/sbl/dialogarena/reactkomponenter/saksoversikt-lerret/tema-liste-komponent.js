import React from 'react/addons';

class TemaListeKomponent extends React.Component {

    _onClick(tema) {
        return () => {
            const DOMNode = React.findDOMNode(this);
            DOMNode.querySelector('input').focus();
            this.props.onClickSakstema(tema);
        };
    }

    render() {
        const tema = this.props.tema;
        const temanavn = tema.temanavn;
        const dato = tema.sistOppdatertDato + '';
        const valgt = this.props.valgt ? 'valgt' : '';

        return (
            <div className={'saksoversikt-liste-element' + ' ' + valgt} onClick={this._onClick(tema).bind(this)}>
                <div>{dato}</div>
                <input className="vekk" id={'radio-' + temanavn} name="temanavn" type="radio" value={temanavn}
                       checked={this.props.valgt}/>
                <label className="saksoversikt-liste-label" htmlFor={'radio-' + temanavn}>
                    {temanavn}
                </label>
            </div>
        );
    }
}

TemaListeKomponent.propTypes = {
    tema: React.PropTypes.shape({
        temanavn: React.PropTypes.string.isRequired,
        temakode: React.PropTypes.string.isRequired,
        sistOppdatertDato: React.PropTypes.date,
        dokumentMetadata: React.PropTypes.array
    }).isRequired,
    onClickSakstema: React.PropTypes.func.isRequired,
    valgt: React.PropTypes.bool.isRequired
};

export default TemaListeKomponent;
