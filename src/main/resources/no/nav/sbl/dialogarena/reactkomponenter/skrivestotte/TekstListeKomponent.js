var React = require('react/addons');
var Utils = require('./../utils');

var TekstListeKomponent = React.createClass({
    render: function () {
        return (
            <div className="sok-element" onClick={tekstChanged.bind(this)}>
                <input id={"tekstElementRadio" + this.props.tekst.key} name="tekstListeRadio" type="radio"
                       readOnly checked={erValgtTekst(this.props.tekst, this.props.valgtTekst)}/>
                <label htmlFor={"tekstElementRadio" + this.props.tekst.key}>
                    <span dangerouslySetInnerHTML={{__html: this.props.tekst.tittel}}></span>
                    <span className="vekk">{' | ' + Utils.getInnhold(this.props.tekst, this.props.locale)}</span>
                </label>
            </div>
        );
    }
});

function tekstChanged() {
    this.props.store.tekstChanged(this.props.tekst, this.getDOMNode().parentNode);
    $(this.getDOMNode()).find('input').focus();
}

function erValgtTekst(tekst, valgtTekst) {
    return tekst === valgtTekst;
}

module.exports = TekstListeKomponent;