/** @jsx React.DOM */
var React = require('react');

var EnkeltElement = React.createClass({
    componentDidMount: function(){
        console.warn('Du bruker er default listevisningen av søk for søkelayouten.');
    },
    render: function () {
        return <div className="tekstElement" onClick={this.props.onClick}>
            <h4>{this.props.element.tittel}</h4>
        </div>;
    }
});

module.exports = EnkeltElement;