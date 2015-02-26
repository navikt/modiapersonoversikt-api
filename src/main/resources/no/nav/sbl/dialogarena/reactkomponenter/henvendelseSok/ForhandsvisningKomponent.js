var React = require('react');

module.exports = React.createClass({
    render: function(){
        return (
            <div>
                <h1>{this.props.element.temagruppe}</h1>
                <p>{this.props.element.innhold}</p>
            </div>
        );
    }
});