import React from 'react';
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

        journalforPromise.done(()=> {
            this.props.traadJournalfort();
        });
        journalforPromise.fail(()=> {
            this.setState({sender: false});
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
    sak: React.PropTypes.object.isRequired,
    fnr: React.PropTypes.string.isRequired,
    traadId: React.PropTypes.string.isRequired,
    traadJournalfort: React.PropTypes.func.isRequired,
    feiletCallback: React.PropTypes.func.isRequired
};

export default JournalforKnapp;
