var WicketSender = require('./wicketsender.js');

module.exports = {
    componentWillMount: function () {
        this.sendToWicket = WicketSender.bind(this, this.props.wicketurl, this.props.wicketcomponent);
    }
};