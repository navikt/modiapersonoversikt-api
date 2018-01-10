import React from 'react';
import Modal from "../modal/modal-module";
import MeldingerSok from "../meldinger-sok/meldinger-sok-module";

const modalConfig = {
    title: {
        text: 'Slaa-sammen-traader-modal',
        show: false,
        tag: 'h1.vekk'
    },
    description: {
        text: '',
        show: false,
        tag: 'div.vekk'
    },
    closeButton: {
        text: 'Lukk slaa-sammen-traader-modal',
        show: true,
        tag: 'span.vekk'
    }
};

class SlaaSammenTraader extends MeldingerSok {

    callBack() {
        const boxes = document.querySelectorAll('.slaa-sammen-traader-visning .skjemaelement__input.checkboks');
        const checkedBoxes = Array.from(boxes)
            .filter((checkbox) => checkbox.checked)
            .map((checkbox) => checkbox.id);
        return checkedBoxes;
    }

    onSubmit(event) {
        if (this.callBack().length < 2) {
            this.store.update({
                submitError: <p className="feedbacklabel">{this.props.mode.submitErrorMessage}</p>
            });
            event.preventDefault();
            return false;
        }
        this.store.update({
            submitError: '',
            mode: { ...this.store.state.mode,
                submitButtonValue: 'Laster..'
            }
        });
        return this.callBack();
    }

    render() {
    const { sokVisning, tomVisning } = this.lagVisninger();
        return (
            <Modal
                ref="modal"
                title={modalConfig.title}
                description={modalConfig.description}
                closeButton={modalConfig.closeButton}
            >
                <form
                    className="slaa-sammen-traader-visning"
                    onSubmit={(event) => this.onSubmit(event)} /*this.store.submit.bind(this.store, this.skjul.bind(this))*/
                >
                    {sokVisning}
                    {tomVisning}
                </form>
            </Modal>
        );
    }
}

SlaaSammenTraader.defaultProps = {
    mode: {
        name: 'SlaSammenTraader',
        visCheckbox: true,
        submitButtonValue: 'Slå sammen valgte tråder',
        submitErrorMessage: 'Du må velge minst to tråder som skal slåes sammen.'
    }
};

export default SlaaSammenTraader;