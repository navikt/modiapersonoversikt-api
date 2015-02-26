/** @jsx React.DOM */
var React = require('react');

var EnkelVisning = React.createClass({
    componentDidMount: function(){
        console.warn('Du bruker er default implementasjon av forhåndsvisning for søkelayouten.');
    },
    render: function(){
        return <h4>{this.props.element.description}</h4>;
    }
});

module.exports = EnkelVisning;