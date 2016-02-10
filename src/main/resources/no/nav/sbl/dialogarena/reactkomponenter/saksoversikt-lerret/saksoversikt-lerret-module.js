import React from 'react';
import SaksoversiktStore from './saksoversikt-store';

class SaksoversiktLerret extends React.Component {
    constructor(props) {
        super(props);
        this.store = new SaksoversiktStore(this.props.fnr);
        this.state = this.store.getState();
    }

    render() {
        console.log('render');
        return (
            <ul>
                <li>TESTING 1</li>
                <li>TESTING 2</li>
            </ul>
        );
    }
}

export default SaksoversiktLerret;
