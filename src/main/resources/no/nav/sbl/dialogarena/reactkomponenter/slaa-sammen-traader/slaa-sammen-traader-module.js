import React, { Component } from 'react';
import MeldingerSok from '../meldinger-sok/meldinger-sok-module';
import PT from 'prop-types';
import WicketSender from '../react-wicket-mixin/wicket-sender';

class SlaaSammenTraader extends Component {
    constructor(props) {
        super();
        this.state = {
            ...props,
            submitError: false,
            checkedBoxes: [],
            vis: this.vis
        };
        this.sendToWicket = WicketSender.bind(this, props.wicketurl, props.wicketcomponent);
        this.checkBoxActionHandler = this.checkBoxActionHandler.bind(this);
    }

    onSubmit(event, state, lukkModalVindu) {
        const checkedBoxes = this.state.checkedBoxes;
        const mindreEnnToTraaderErValgt = checkedBoxes.length < 2;
        if (mindreEnnToTraaderErValgt) {
            this.setState({
                submitError: true
            });
            event.preventDefault();
            return false;
        }
        this.sendToWicket('slaaSammen', checkedBoxes);
        event.preventDefault();
    }

    checkBoxActionHandler(traad) {
        const traadId = traad.traadId;
        const newCheckedBoxes = this.state.checkedBoxes;
        if (this.state.checkedBoxes.includes(traadId)) {
            const index = newCheckedBoxes.indexOf(traadId);
            newCheckedBoxes.splice(index, 1);
        }
        else {
            newCheckedBoxes.push(traadId);
        }
        this.setState({
            submitError: false,
            checkedBoxes: newCheckedBoxes
        });
    }

    visModal() {}

    vis(props) {
        this.setState({
            traadIder: props.traadIder,
            checkedBoxes: []
        });
        this.visModal();
    }

    skjul() {
        this.skjulModal();
    }

    render() {
        const antallValgteOppgaver = this.state.checkedBoxes.length;
        const buttonText = antallValgteOppgaver < 2
            ? 'Du må velge minst to dialoger'
            : `Besvar ${antallValgteOppgaver} valgte dialoger`;
        const hjelpetekst = (
            <div>
                <h4>Vindu for å velge dialoger du ønsker å besvare samtidig.</h4>
                <ul>
                    <li>Trykk på radioknapp for å utvide en dialog.</li>
                    <li>Merk dialogene du ønsker å besvare ved å huke av i avkryssningsboks.</li>
                    <li>Når du har merket minst to dialoger trykker du på knappen "Besvar x valgte dialoger".</li>
                    <li>Dialogene blir slått sammen og kan besvares på vanlig måte.</li>
                </ul>
            </div>
        );
        return (
            <MeldingerSok
                traadIder={this.state.traadIder}
                fnr={this.props.fnr}
                traadMarkupIds={this.props.traadMarkupIds}
                className="slaa-sammen-traader-visning"
                visSok={false}
                checkboxProps={{
                    visCheckbox: true,
                    checkBoxAction: this.checkBoxActionHandler,
                    checkedBoxes: this.state.checkedBoxes
                }}
                submitButtonProps={{
                    buttonText,
                    errorMessage: 'Du må velge minst to oppgaver.',
                    error: this.state.submitError
                }}
                onSubmit={(event, state, onSuccess) => this.onSubmit(event, state, onSuccess)}
                setVisModalVindu={(func) => this.visModal = func}
                setSkjulModalVindu={(func) => this.skjulModal = func}
                modulNavn="BesvarFlereOppgaverModul"
                hjelpetekst={hjelpetekst}
            />
        );
    }
}

SlaaSammenTraader.propTypes = {
    fnr: PT.string.isRequired,
    traadIder: PT.arrayOf(PT.string).isRequired,
    traadMarkupIds: PT.object.isRequired,
    wicketcomponent: PT.string.isRequired,
    wicketurl: PT.string.isRequired
};

export default SlaaSammenTraader;
