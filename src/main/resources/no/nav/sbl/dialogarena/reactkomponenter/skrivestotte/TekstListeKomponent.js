/** @jsx React.DOM */
var React = require('react');

var TekstListeKomponent = React.createClass({
    render: function(){
        return (
            <div className="sok-element" onClick={this.props.store.tekstChanged.bind(this.props.store, this.props.tekst)}>
                <input id={"tekstElementRadio" + this.props.tekst.key} name="tekstListeRadio" type="radio"
                    readOnly checked={erValgtTekst(this.props.tekst, this.props.valgtTekst)} />
                <label htmlFor={"tekstElementRadio" + this.props.tekst.key}>
                    <h4 dangerouslySetInnerHTML={{__html: this.props.tekst.tittel}}></h4>
                </label>
            </div>
        );
    }
});

function erValgtTekst(tekst, valgtTekst){
    return tekst === valgtTekst;
}

module.exports = TekstListeKomponent;