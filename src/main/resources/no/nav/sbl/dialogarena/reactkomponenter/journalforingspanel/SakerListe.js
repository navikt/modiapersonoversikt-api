import React from 'react';
import { chain, mapValues, contains, partition, flatten } from 'lodash';

//var React = require('react');
//var chain = require('lodash');
//var mapValues = require('lodash');
//var contains = require('lodash');
//var partition = require('lodash');
//var flatten = require('lodash');

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
                        <a className="content-row-list" onClick={() => velgSak(sak)}>
                            <div>
                                <span className="text-cell">{sak.saksIdVisning}</span>
                                <span className="vekk">'|'</span>
                                <span className="text-cell">{sak.opprettetDatoFormatert}</span>
                                <span className="vekk">'|'</span>
                                <span className="text-cell">{sak.fagsystemNavn}</span>
                            </div>
                        </a>
                    </li>
                );
            });
            var temagruppe = this.props.temagruppe;
            var temaKode = group[0].temaKode;
            const erEkspandert = !temagruppe || contains(this.props.temagruppeTemaMapping[temagruppe], temaKode);
            return <SakerForTema tema={group[0].temaNavn} saker={saker} erEkspandert={erEkspandert}/>
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


class SakerForTema extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            ekspandert: props.erEkspandert
        };
        this.toggleEkspandering = this.toggleEkspandering.bind(this);
    }

    componentWillReceiveProps(nextProps) {
        this.setState({
            ekspandert: nextProps.erEkspandert
        })
    }

    toggleEkspandering(event) {
        event.preventDefault();
        this.setState({ekspandert: !this.state.ekspandert});
    }

    render() {
        if (this.state.ekspandert) {
            return (
                <div className="saker-tema">
                    <button onClick={this.toggleEkspandering} aria-expanded="true">
                        <div className="tema-bar">
                            <h3 className="tema-overskrift">{this.props.tema}</h3>

                            <div className="ekspanderingspil opp"></div>
                        </div>

                        <div className="info-bar">
                            <span className="text-cell">SAKSID</span>
                            <span className="vekk">'|'</span>
                            <span className="text-cell">OPPRETTET</span>
                            <span className="vekk">'|'</span>
                            <span className="text-cell">FAGSYSTEM</span>
                        </div>
                    </button>
                    <ul className="list-saker">
                        {this.props.saker}
                    </ul>
                </div>
            );
        } else {
            return (
                <div className="saker-tema">
                    <button className="tema-bar" onClick={this.toggleEkspandering} aria-expanded="false">
                        <h3 className="tema-overskrift">{this.props.tema}</h3>

                        <div className="ekspanderingspil ned"></div>
                    </button>
                </div>
            );
        }
    }
}

export default SakerListe;

//module.exports='SakerListe';