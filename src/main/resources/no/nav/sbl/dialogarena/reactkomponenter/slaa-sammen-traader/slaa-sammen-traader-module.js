import React, { Component } from 'react';
import MeldingerSok from '../meldinger-sok/meldinger-sok-module';
import PT from 'prop-types';
import WicketSender from '../react-wicket-mixin/wicket-sender';

function findCheckedBoxes() {
    const boxes = document.querySelectorAll('.slaa-sammen-traader-visning .skjemaelement__input.checkboks');
    return Array.from(boxes)
        .filter((checkbox) => checkbox.checked)
        .map((checkbox) => checkbox.id);
}

class SlaaSammenTraader extends Component {
    constructor(props) {
        super();
        this.state = {
            ...props,
            submitError: false,
            vis: this.vis
        };
        this.sendToWicket = WicketSender.bind(this, props.wicketurl, props.wicketcomponent);
    }

    onSubmit(event, state, lukkModalVindu) {
        const checkedBoxes = findCheckedBoxes();
        const mindreEnnToTraaderErValgt = checkedBoxes.length < 2;
        if (mindreEnnToTraaderErValgt) {
            this.setState({
                submitError: true
            });
            event.preventDefault();
            return false;
        }
        this.sendToWicket('slaaSammen', checkedBoxes);
        lukkModalVindu();
    }

    vis(props) {
        this.setState({ ...this.state, traadIder: props.traadIder });
        this.visModal();
    }

    render() {
        return (
            <MeldingerSok
                traadIder={this.state.traadIder}
                fnr={this.props.fnr}
                traadMarkupIds={this.props.traadMarkupIds}
                className="slaa-sammen-traader-visning"
                visSok={false}
                visCheckbox
                submitButtonProps={{
                    buttonText: 'Besvar flere oppgaver',
                    errorMessage: 'Du må velge minst to oppgaver.',
                    error: this.state.submitError
                }}
                onSubmit={(event, state, onSuccess) => this.onSubmit(event, state, onSuccess)}
                setVisModalVindu={(func) => this.visModal = func}
                modulNavn="SlaaSammenTraader"
            />
        );
    }
}

SlaaSammenTraader.propTypes = {
    fnr: PT.string.isRequired,
    traadIder: PT.arrayOf(PT.string).isRequired,
    wicketcomponent: PT.string.isRequired,
    wicketurl: PT.string.isRequired
};

export default SlaaSammenTraader;
