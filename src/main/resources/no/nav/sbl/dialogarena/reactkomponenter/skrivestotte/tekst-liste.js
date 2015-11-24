import React from 'react/addons';
import Utils from './../utils/utils-module';

class TekstListeKomponent extends React.Component {
    _onClick() {
        const DOMNode = React.findDOMNode(this);
        this.props.store.tekstChanged(this.props.tekst, DOMNode.parentNode);
        DOMNode.querySelector('input').focus();
    }
    render() {
        return (
            <div className="sok-element" onClick={this._onClick.bind(this)}>
                <input id={'tekstElementRadio' + this.props.tekst.key} name="tekstListeRadio" type="radio"
                       readOnly checked={this.props.tekst === this.props.valgtTekst}/>
                <label htmlFor={'tekstElementRadio' + this.props.tekst.key}>
                    <span dangerouslySetInnerHTML={{__html: this.props.tekst.tittel}}></span>
                    <span className="vekk">{' | ' + Utils.getInnhold(this.props.tekst, this.props.locale)}</span>
                </label>
            </div>
        );
    }
}

TekstListeKomponent.propTypes = {
    'store': React.PropTypes.object.isRequired,
    'tekst': React.PropTypes.object.isRequired,
    'valgtTekst': React.PropTypes.object.isRequired,
    'locale': React.PropTypes.string.isRequired
};

export default TekstListeKomponent;
