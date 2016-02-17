import React from 'react';
import Sakstema from './sakstema';
import { take } from 'lodash';

class Sakstemaliste extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const { temaer } = this.props;
        const ANTALL_TEMAER = 6;
        const redusertAntallTemaer = take(temaer, ANTALL_TEMAER);
        const sakstemaer = redusertAntallTemaer.map(tema => <li><Sakstema tema={tema} /></li>);
        return (
            <ul>
                {sakstemaer}
                <li><a href="#">Gå til saksoversikten</a></li>
            </ul>
        );
    }
}

Sakstemaliste.PropTypes = {
    temaer: React.PropTypes.array
};

export default Sakstemaliste;