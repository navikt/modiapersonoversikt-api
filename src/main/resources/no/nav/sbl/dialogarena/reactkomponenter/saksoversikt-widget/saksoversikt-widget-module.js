import React from 'react';
import SaksoversiktWidgetStore from './saksoversikt-widget-store';
import Sakstemaliste from './samstemaliste';

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
        const temaer = this.store.getTemaer();
        return (
            <div>
                <header className="klikkbar-header" role="link">
                    <div className="initial">S</div>
                    <h2 className="widget-header">Saksoversikt</h2>
                </header>
                <Sakstemaliste temaer={temaer}/>
            </div>
        );
    }
}

SaksoversiktWidget.propTypes = {
    fnr: React.PropTypes.string.isRequired
};

export default SaksoversiktWidget;
