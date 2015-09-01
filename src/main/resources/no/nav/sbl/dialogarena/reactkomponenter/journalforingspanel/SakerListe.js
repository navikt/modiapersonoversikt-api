import React from 'react';
import { groupBy } from 'lodash';
import { mapValues } from 'lodash';

const TIL_SAK_ELEMENT = (sak) => {
    return (
        <li className="text-row-list">
            <a href="#">
                <div>
                    <span className="text-cell">{sak.saksIdVisning}</span>
                    <span className="text-cell">{sak.opprettetDatoFormatert}</span>
                    <span className="text-cell">{sak.fagsystemKode}</span>
                </div>
            </a>
        </li>
    );
};

const TIL_SAK_GROUPED = (group) => {
    console.log('group', group);
    const sakerElementer = group.map(TIL_SAK_ELEMENT);
    return (
        <div className="saker-tema">
            <h3 className="tema-overskrift">{group[0].temaKode}</h3>

            <div className="info-bar">
                <span className="text-cell">SAKSID</span>
                <span className="text-cell">OPPRETTET</span>
                <span className="text-cell">FAGSYSTEM</span>
            </div>
            <ul className="list-saker">
                {sakerElementer}
            </ul>
        </div>
    );
};


class SakerListe extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const grouped = groupBy(this.props.saker, sak => sak.temaKode);
        console.log('groups', grouped);
        const sakerGruppert = mapValues(grouped, TIL_SAK_GROUPED);

        return (
            <div className="alla-saker">
                {sakerGruppert}
            </div>
        );
    }
}

export default SakerListe;