/* eslint "react/jsx-no-bind": 1 */
import React from 'react';
import ReactDOM from 'react-dom';
import PT from 'prop-types';
import Utils from './../utils/utils-module';

class TekstListeKomponent extends React.Component {
    _onClick() {
        const DOMNode = ReactDOM.findDOMNode(this);
        this.props.store.tekstChanged(this.props.tekst, DOMNode.parentNode);
        DOMNode.querySelector('input').focus();
    }

    shouldComponentUpdate(nextProps) {
        const dagensState = this.props.tekst === this.props.valgtTekst;
        const nesteState = this.props.tekst === nextProps.valgtTekst;

        return dagensState !== nesteState;
    }

    render() {
        return (
            <div className="sok-element">
                <input
                    id={'tekstElementRadio' + this.props.tekst.key}
                    name="tekstListeRadio"
                    type="radio"
                    readOnly
                    checked={this.props.tekst === this.props.valgtTekst}
                    onClick={this._onClick.bind(this)}
                />
                <label htmlFor={'tekstElementRadio' + this.props.tekst.key}>
                    <span dangerouslySetInnerHTML={{ __html: this.props.tekst.tittel }}></span>
                    <span className="vekk">{' | ' + Utils.getInnhold(this.props.tekst, this.props.locale)}</span>
                </label>
            </div>
        );
    }
}

TekstListeKomponent.propTypes = {
    store: PT.object.isRequired,
    tekst: PT.object.isRequired,
    valgtTekst: PT.object.isRequired,
    locale: PT.string.isRequired
};

export default TekstListeKomponent;
