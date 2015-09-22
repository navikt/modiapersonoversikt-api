import React from 'react';

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
                            <span className="vekk"> | </span>
                            <span className="text-cell text-align-right">OPPRETTET</span>
                            <span className="vekk"> | </span>
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

export default SakerForTema;