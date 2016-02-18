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
        const temakode = this.props.temakode;
        const dato = this.props.dato + "";
        const valgt = this.props.valgt? 'valgt': '';

        return (
            <div className={'saksoversikt-liste-element' + " " + valgt} onClick={this._onClick(temakode).bind(this)}>
                <div>{dato}</div>
                <input className="vekk" id={'radio-' + tema} name="tema" type="radio" value={tema} checked={this.props.valgt}/>
                <label className="saksoversikt-liste-label" htmlFor={'radio-' + tema}>
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
    onClickSakstema: React.PropTypes.func.isRequired
};

export default TemaListeKomponent;
