import React from 'react';
import SaksoversiktWidgetStore from './saksoversikt-widget-store';
import Temaliste from './temaliste';

class SaksoversiktWidget extends React.Component {

    constructor(props) {
        super(props);
        this.store = new SaksoversiktWidgetStore(this.props.fnr);
        this.state = this.store.getState();
        this.updateState = this.updateState.bind(this);
    }

    componentDidMount() {
        this.store.addListener(this.updateState);
    }

    componentWillUnmount() {
        this.store.removeListener(this.updateState);
    }

    updateState() {
        this.setState(this.store.getState());
    }

    render() {
        const fnr = this.props.fnr;
        const temaer = this.store.getTemaer();

        return <Temaliste temaer={temaer} fnr={fnr}/>;
    }
}

SaksoversiktWidget.propTypes = {
    fnr: React.PropTypes.string.isRequired
};

export default SaksoversiktWidget;
