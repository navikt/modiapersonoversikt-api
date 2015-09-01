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

class SakerListe extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const grouped = groupBy(this.props.saker, sak => sak.temaKode);
        const sakerGruppert = mapValues(grouped, (group) => {
            return <SakerForTema tema={group[0].temaKode} saker={group.map(TIL_SAK_ELEMENT)}/>
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

    toggleEkspandering() {
        this.setState({ekspandert: !this.state.ekspandert});
    }

    render() {
        if (this.state.ekspandert) {
            return (
                <div className="saker-tema">
                    <button onClick={this.toggleEkspandering}>
                        <div className="tema-bar">
                            <h3 className="tema-overskrift">{this.props.tema}</h3>

                            <div className="ekspanderingspil opp"></div>
                        </div>

                        <div className="info-bar">
                            <span className="text-cell">SAKSID</span>

                            <span className="text-cell">OPPRETTET</span>
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
                    <button className="tema-bar" onClick={this.toggleEkspandering}>
                        <h3 className="tema-overskrift">{this.props.tema}</h3>

                        <div className="ekspanderingspil ned"></div>
                    </button>
                </div>
            );
        }
    }
}

export default SakerListe;