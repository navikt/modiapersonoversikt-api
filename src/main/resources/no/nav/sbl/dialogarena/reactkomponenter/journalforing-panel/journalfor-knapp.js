import React from 'react';
import PT from 'prop-types';
import Snurrepipp from '../utils/snurrepipp';
import Ajax from './../utils/ajax';

class JournalforKnapp extends React.Component {
    constructor(props) {
        super(props);
        this.journalfor = this.journalfor.bind(this);
        this.state = {
            sender: false
        };
    }

    journalfor(event) {
        event.preventDefault();
        this.setState({
            sender: true
        });


        const url = '/modiabrukerdialog/rest/journalforing/' + this.props.fnr + '/' + this.props.traadId;
        const data = JSON.stringify(this.props.sak);

        const journalforPromise = Ajax.post(url, data);

        journalforPromise.done(() => {
            this.props.traadJournalfort();
        });
        journalforPromise.fail(() => {
            this.setState({ sender: false });
            this.props.feiletCallback();
        });
    }

    render() {
        if (this.state.sender) {
            return <Snurrepipp />;
        }

        return (
            <div>
                <button className="journalfor-knapp knapp-hoved-stor" onClick={this.journalfor}>Journalfør</button>
            </div>
        );
    }
}

JournalforKnapp.propTypes = {
    sak: PT.object.isRequired,
    fnr: PT.string.isRequired,
    traadId: PT.string.isRequired,
    traadJournalfort: PT.func.isRequired,
    feiletCallback: PT.func.isRequired
};

export default JournalforKnapp;
