var React = require('react');

module.exports = React.createClass({
    getDefaultProps: function(){
        return {
            fritekst: ''
        };
    },
    getInitialState: function(){
        return {
            fritekst: this.props.fritekst || ''
        };
    },
    onChangeProxy: function(event){
        this.setState({fritekst: event.target.value});
        this.props.onChange(event.target.value);
    },
    render: function () {
        return (
                <input type="text" placeholder="Søk"
                    aria-label="Søk etter henvendelser" aria-controls={this.props.ariaControls}
                    onChange={this.onChangeProxy} onKeyDown={this.props.onKeyDown}
                />
        );
    }
});