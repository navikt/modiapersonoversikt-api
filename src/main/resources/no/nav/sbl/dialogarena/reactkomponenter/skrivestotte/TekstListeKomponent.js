/** @jsx React.DOM */
var React = require('react');
var Utils = require('./Utils');

var TekstListeKomponent = React.createClass({
    render: function(){
        return (
            <div {...this.props}>
                <input id={"tekstElementRadio" + this.props.element.key} name="tekstListeRadio" type="radio" readOnly checked={this.props.erValgt} />
                <label htmlFor={"tekstElementRadio" + this.props.element.key}>
                    <h4 dangerouslySetInnerHTML={{__html: this.props.element.tittel}}></h4>
                </label>
            </div>
        );
    }
});

module.exports = TekstListeKomponent;