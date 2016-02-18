import React from 'react/addons';

class TemaListeKomponent extends React.Component {
    _onClick(tema) {
        return () => this.props.velgSak(tema);
    }

    render() {
        const tema = this.props.tema;
        const temakode = this.props.temakode;
        const dato = this.props.dato;
        const valgt = true ? 'valgt' : ''

        return (
            <div className='saksoversikt-liste-element' onClick={this._onClick.bind(this)}>
                <div>{dato}</div>
                <input className="vekk" id={'radio-' + tema} name="tema" type="radio" value={tema}/>
                <label onClick={this._onClick(temakode)} className="saksoversikt-liste-label" htmlFor={'radio-' + tema}>
                    {tema}
                </label>
            </div>
        );
    }
}

TemaListeKomponent.propTypes = {
    tema: React.PropTypes.string.isRequired,
    temakode: React.PropTypes.string.isRequired,
    dato: React.PropTypes.string.isRequired,
    velgSak: React.PropTypes.func.isRequired
};

export default TemaListeKomponent;
