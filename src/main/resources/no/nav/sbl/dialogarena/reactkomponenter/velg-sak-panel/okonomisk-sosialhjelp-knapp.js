import React from 'react';

class OkonomiskSosialhjelpKnapp extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            ekspandert: true
        };
        this.toggleEkspandering = this.toggleEkspandering.bind(this);
        this.velgOkonomiskSosialhelp = this.velgOkonomiskSosialhelp.bind(this);
    }

    toggleEkspandering(event) {
        event.preventDefault();
        this.setState({ekspandert: !this.state.ekspandert});
    }

    velgOkonomiskSosialhelp(event) {
        event.preventDefault();
        const sak = {
            saksId: null,
            fagsystemSaksId: null,
            temaKode: 'OKSOS',
            temaNavn: 'Ø - Ikke i bruk',
            fagsystemKode: null,
            fagsystemNavn: null,
            sakstype: null,
            finnesIGsak: null,
            finnesIPsak: null
        };
        this.props.velgSak(sak);
    }


    render() {
        const pilRetning = this.state.ekspandert ? 'opp' : 'ned';
        let content = null;
        if (this.state.ekspandert) {
            content = (
                <ul className="list-saker">
                    <li className="text-row-list">
                        <a href="#" role="button" className="content-row-list"
                           onClick={this.velgOkonomiskSosialhelp}>
                            Ø - Ikke i bruk - vil ikke bli journalført
                        </a>
                    </li>
                </ul>
            );
        }
        return (
            <div className="saker-tema">
                <button className="sosialhjelp-knapp" onClick={this.toggleEkspandering} aria-expanded={this.state.ekspandert}>
                    <div className="tema-bar">
                        <h3 className="tema-overskrift">Ø - Ikke i bruk</h3>

                        <div className={'ekspanderingspil ' + pilRetning}></div>
                    </div>
                </button>
                {content}
            </div>
        );
    }
}

OkonomiskSosialhjelpKnapp.propTypes = {
    'velgSak': React.PropTypes.func.isRequired
};

export default OkonomiskSosialhjelpKnapp;
