import React from 'react';
import PT from 'prop-types';
import Utils from './../utils/utils-module';
import {generateId} from "../utils/utils-module";

class TekstListeKomponent extends React.Component {
    constructor(props) {
        super(props);
        this.inputId = generateId('tekstElementRadio' + this.props.tekst.key);

        Utils.autobind(this);
    }

    _bindInputRef(input) {
        this.input = input;
    }

    _onClick() {
        this.props.store.tekstChanged(this.props.tekst);
        this.input.focus();
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
                    id={this.inputId}
                    name="tekstListeRadio"
                    type="radio"
                    ref={this._bindInputRef}
                    readOnly
                    checked={this.props.tekst === this.props.valgtTekst}
                    onClick={this._onClick}
                />
                <label htmlFor={this.inputId}>
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
