import React from 'react/addons';

class TemaListeKomponent extends React.Component {
    _onClick() {
        const DOMNode = React.findDOMNode(this);
        DOMNode.querySelector('input').focus();
    }

    render() {
        const tema = this.props.tema;
        return (
            <div className="tema-liste-komponent" onClick={this._onClick.bind(this)}>
                <input id={"radio-"+tema} name="tema" type="radio" value={tema}/>
                <label htmlFor={"radio-"+tema}>
                    {tema}
                </label>
            </div>
        );
    }
}

export default TemaListeKomponent;