import React from 'react/addons';

class TemaListeKomponent extends React.Component {
    _onClick() {
        const DOMNode = React.findDOMNode(this);
        DOMNode.querySelector('input').focus();
    }

    render() {
        const tema = this.props.tema;
        return (
            <div className="saksoversikt-liste-element" onClick={this._onClick.bind(this)}>
                <input className="vekk" id={'radio-' + tema} name="tema" type="radio" value={tema}/>
                <label className="saksoversikt-liste-label" htmlFor={'radio-' + tema}>
                    {tema}
                </label>
            </div>
        );
    }
}

TemaListeKomponent.propTypes = {
    'tema': React.PropTypes.string.isRequired
};

export default TemaListeKomponent;
