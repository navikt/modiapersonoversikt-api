import React, { Component } from 'react';
import MeldingerSok from "../meldinger-sok/meldinger-sok-module";

function findCheckedBoxes() {
    const boxes = document.querySelectorAll('.slaa-sammen-traader-visning .skjemaelement__input.checkboks');
    return Array.from(boxes)
        .filter((checkbox) => checkbox.checked)
        .map((checkbox) => checkbox.id);
}

class SlaaSammenTraader extends Component {

    constructor(props) {
        super(props);
        this.state = {
            submitError: false,
            vis: () => {}
        };
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
        lukkModalVindu();
        console.log('CheckedBoxes', checkedBoxes);
        return checkedBoxes;
    }

    render() {
        return (
            <MeldingerSok
                {...this.props}
                className="slaa-sammen-traader-visning"
                visSok={false}
                visCheckbox={true}
                submitButtonValue="Slå sammen valgte tråder"
                submitErrorMessage="Du må velge minst to tråder som skal slåes sammen."
                submitError={this.state.submitError}
                onSubmit={(event, state, onSuccess) => this.onSubmit(event, state, onSuccess)}
                setVisModalVindu={(func) => this.vis = func }
            />
        );
    }
}

export default SlaaSammenTraader;
