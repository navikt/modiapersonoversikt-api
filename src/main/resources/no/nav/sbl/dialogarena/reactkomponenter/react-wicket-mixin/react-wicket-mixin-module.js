import WicketSender from './wicket-sender';

module.exports = {
    componentWillMount: function componentWillMount() {
        this.sendToWicket = WicketSender.bind(this, this.props.wicketurl, this.props.wicketcomponent);
    }
};
