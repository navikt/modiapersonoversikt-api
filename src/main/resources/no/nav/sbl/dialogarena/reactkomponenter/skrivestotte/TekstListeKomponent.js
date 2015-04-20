/** @jsx React.DOM */
var React = require('react');

var TekstListeKomponent = React.createClass({
    render: function(){
        return (
            <div className="sok-element" onClick={tekstChanged.bind(this)}>
                <input id={"tekstElementRadio" + this.props.tekst.key} name="tekstListeRadio" type="radio"
                    checked={erValgtTekst(this.props.tekst, this.props.valgtTekst)} />
                <label htmlFor={"tekstElementRadio" + this.props.tekst.key}>
                    <span dangerouslySetInnerHTML={{__html: this.props.tekst.tittel}}></span>
                </label>
            </div>
        );
    }
});

function tekstChanged() {
    this.props.store.tekstChanged(this.props.tekst, this.getDOMNode().parentNode);
}

function erValgtTekst(tekst, valgtTekst){
    return tekst === valgtTekst;
}

module.exports = TekstListeKomponent;