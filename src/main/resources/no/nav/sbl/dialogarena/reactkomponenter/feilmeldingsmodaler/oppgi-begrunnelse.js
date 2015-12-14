import React from 'react';
import Modal from './../modal/modal-module';
import { autobind } from './../utils/utils-module';
import sendToWicket from './../react-wicket-mixin/wicket-sender';

class OppgiBegrunnelse extends React.Component {
    constructor(props) {
        super(props);
        autobind(this);
        this.state = {showFeilmelding: false};
        this.sendToWicket = sendToWicket.bind(this, this.props.wicketurl, this.props.wicketcomponent);
    }

    avbrytCallback(text) {
        text.value = '';
        this.setState({showFeilmelding: false});
        this.sendToWicket(this.props.discardCallback);
    }

    fortsettCallback() {
        const begrunnelse = React.findDOMNode(this.refs.begrunnelse).value;
        React.findDOMNode(this.refs.begrunnelse).value = '';

        if (begrunnelse.length > 0) {
            this.sendToWicket(this.props.confirmCallback, begrunnelse);
        } else {
            this.setState({showFeilmelding: true});
        }
    }

    vis() {
        this.refs.modal.open();
    }

    skjul() {
        this.refs.modal.close();
    }

    render() {
        const {isOpen} = this.props;
        const modalProps = {isOpen};
        const feilmelding = (<span className="feedbacklabel" aria-hidden="false"
                                   aria-live="assertive"
                                   aria-atomic="true" role="alert">Tekstfeltet kan ikke være tomt.</span>);

        return (
            <Modal {...modalProps} width={600} height={381}
                                   onClosing={() => {this.setState({showFeilmelding: false}); return true;}}
                                   ref="modal">
                <section className="bekreft-dialog" style={OppgiBegrunnelse.styling.section}>
                    <h1 className="robust-ikon-hjelp-strek"
                        style={OppgiBegrunnelse.styling.text}>
                        {this.props.title}</h1>
                    <form id="begrunnelseForm" onSubmit={(e) => e.preventDefault()}
                          style={OppgiBegrunnelse.styling.form}>
                        <div style={OppgiBegrunnelse.styling.inputDiv}>
                        <textarea id="begrunnelse"
                                  className={'begrunnelsestekst ' + (this.state.showFeilmelding ? 'invalid' : '')}
                                  required="" name="begrunnelse"
                                  aria-label="Begrunnelse" style={OppgiBegrunnelse.styling.textarea}
                                  ref="begrunnelse" rows="4"></textarea>
                            {this.state.showFeilmelding ? feilmelding : null}
                        </div>
                        <div>
                            <button className="knapp-hoved" style={OppgiBegrunnelse.styling.buttonFortsett}
                                    onClick={this.fortsettCallback.bind(this)}>{this.props.lagretekst}
                            </button>
                            <button className="knapp-lenke"
                                    style={Object.assign(OppgiBegrunnelse.styling.buttonAvbryt, OppgiBegrunnelse.styling.button)}
                                    onClick={this.avbrytCallback.bind(this)}>{this.props.avbryttekst}
                            </button>
                        </div>
                    </form>
                </section>
            </Modal>
        );
    }
}

OppgiBegrunnelse.styling = {

    section: {
        padding: 30 + 'px'
    },

    form: {
        maxWidth: 470 + 'px',
        margin: '0 auto'
    },
    text: {
        textTransform: 'none !important'
    },

    inputDiv: {
        height: 6 + 'rem',
        marginBottom: 3 + 'rem',
        marginTop: 0
    },

    textarea: {
        width: 100 + '%'
    },

    buttonFortsett: {
        display: 'block',
        margin: '0 auto',
        textTransform: 'none',
        marginBottom: 3 + 'rem'
    },
    buttonAvbryt: {
        display: 'block',
        margin: '0 auto',
        textTransform: 'none',
        padding: 0
    }
};

OppgiBegrunnelse.defaultProps = {
    isOpen: false,
    title: {
        text: 'Oppgi begrunnelse for å se denne brukeren',
        show: false,
        tag: 'h1.vekk'
    },
    description: {
        text: '',
        show: false,
        tag: 'div.vekk'
    },
    closeButton: {
        text: '',
        show: false,
        tag: 'span.vekk'
    }
};

OppgiBegrunnelse.propTypes = {
    title: React.PropTypes.string.isRequired,
    wicketurl: React.PropTypes.string.isRequired,
    wicketcomponent: React.PropTypes.string.isRequired,
    lagretekst: React.PropTypes.string.isRequired,
    avbryttekst: React.PropTypes.string.isRequired,
    discardCallback: React.PropTypes.string.isRequired,
    confirmCallback: React.PropTypes.string.isRequired,
    isOpen: React.PropTypes.bool
};


export default OppgiBegrunnelse;

