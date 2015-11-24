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
        });
    }

    toggleEkspandering(event) {
        event.preventDefault();
        this.setState({ekspandert: !this.state.ekspandert});
    }

    render() {
        if (this.state.ekspandert) {
            const datoHeader = this.props.erPesysSak ? 'løpende f.o.m.' : 'opprettet';
            return (
                <div className="saker-tema">
                    <button onClick={this.toggleEkspandering} aria-expanded="true">
                        <div className="tema-bar">
                            <h3 className="tema-overskrift">{this.props.tema}</h3>
                            <div className="ekspanderingspil opp"></div>
                        </div>
                    </button>
                    <ul className="list-saker">
                        <li>
                            <div className="info-bar">
                                <span className="text-cell text-transform-uppercase">saksid</span>
                                <span className="vekk"> | </span>
                                <span
                                    className="text-cell text-align-right text-transform-uppercase">{datoHeader}</span>
                                <span className="vekk"> | </span>
                                <span className="text-cell text-transform-uppercase">fagsystem</span>
                            </div>
                        </li>
                        {this.props.saker}
                    </ul>
                </div>
            );
        }

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

SakerForTema.propTypes = {
    erPesysSak: React.PropTypes.bool.isRequired,
    erEkspandert: React.PropTypes.bool.isRequired,
    tema: React.PropTypes.string.isRequired,
    saker: React.PropTypes.array.isRequired
};

export default SakerForTema;
