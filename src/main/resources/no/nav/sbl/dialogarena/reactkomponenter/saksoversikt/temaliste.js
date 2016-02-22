import React from 'react';
import Sakstema from './tema';
import { take } from 'lodash';

const ANTALL_TEMAER = 6;

function Temaliste({ temaer, fnr }) {
    const redusertAntallTemaer = take(temaer, ANTALL_TEMAER);
    const temaliste = redusertAntallTemaer.map(tema => <li><Sakstema tema={tema} fnr={fnr}/></li>);

    return (
        <ul>
            {temaliste}
            <li><a href="#">Se flere saker</a></li>
        </ul>
    );
}

Temaliste.PropTypes = {
    temaer: React.PropTypes.array
};

export default Temaliste;