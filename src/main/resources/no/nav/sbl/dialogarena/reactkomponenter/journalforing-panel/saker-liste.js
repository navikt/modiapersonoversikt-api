import React from 'react';
import { chain, mapValues, contains, partition, flatten } from 'lodash';
import SakerForTema from './saker-for-tema';

class SakerListe extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const grouped = grupperSaker.apply(this);
        const velgSak = this.props.velgSak;
        const sakerGruppert = mapValues(grouped, (group) => {
            const saker = group.map((sak) => {
                return (
                    <li className="text-row-list">
                        <a href="javascript:void(0)" role="button" className="content-row-list"
                           onClick={() => velgSak(sak)}>
                            <div>
                                <span className="text-cell">{sak.saksIdVisning}</span>
                                <span className="vekk"> | </span>
                                <span className="text-cell text-align-right">{sak.opprettetDatoFormatert}</span>
                                <span className="vekk"> | </span>
                                <span className="text-cell">{sak.fagsystemNavn}</span>
                            </div>
                        </a>
                    </li>
                );
            });
            var temagruppe = this.props.temagruppe;
            var temaKode = group[0].temaKode;
            const erEkspandert = !temagruppe || contains(this.props.temagruppeTemaMapping[temagruppe], temaKode);
            return <SakerForTema tema={group[0].temaNavn} saker={saker} erEkspandert={erEkspandert}
                                 temaKode={temaKode}/>
        });

        return (
            <div className="alla-saker">
                {sakerGruppert}
            </div>
        );
    }
}

function grupperSaker() {
    const saker = this.props.saker;
    const temagruppe = this.props.temagruppe;
    if (temagruppe) {
        const gruppert = skillUtPrioriterteSaker(saker, temagruppe, this.props.temagruppeTemaMapping).map(grupperPaaTemakodeOgSorter);
        return flatten(gruppert);
    } else {
        return grupperPaaTemakodeOgSorter(saker);
    }
}

function skillUtPrioriterteSaker(saker, temagruppe, temagruppeTemaMapping) {
    return partition(saker, sak => contains(temagruppeTemaMapping[temagruppe], sak.temaKode));
}

function grupperPaaTemakodeOgSorter(saker) {
    return chain(saker)
        .groupBy(sak => sak.temaKode)
        .sortBy(group => group[0].temaNavn)
        .value();
}

export default SakerListe;