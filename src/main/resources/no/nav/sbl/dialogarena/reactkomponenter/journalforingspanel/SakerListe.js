import React from 'react';
import { groupBy } from 'lodash';
import { mapValues } from 'lodash';

class SakerListe extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const grouped = groupBy(this.props.saker, sak => sak.temaKode);
        const velgSak = this.props.velgSak;
        const sakerGruppert = mapValues(grouped, (group) => {
            const saker = group.map((sak) => {
                return (
                    <li className="text-row-list">
                        <a onClick={() => velgSak(sak)}>
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
            return <SakerForTema tema={group[0].temaNavn} saker={saker}/>
        });

        return (
            <div className="alla-saker">
                {sakerGruppert}
            </div>
        );
    }
}

class SakerForTema extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            ekspandert: true
        };
        this.toggleEkspandering = this.toggleEkspandering.bind(this);
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