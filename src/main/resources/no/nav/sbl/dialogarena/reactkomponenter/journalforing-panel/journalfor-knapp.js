import React from 'react';
import Snurrepipp from '../utils/snurrepipp';
import Ajax from './../utils/ajax';
import Q from 'q';

class JournalforKnapp extends React.Component {
    constructor(props) {
        super(props);
        this.journalfor = this.journalfor.bind(this);
        this.state = {
            sender: false
        }
    }

    journalfor(event) {
        event.preventDefault();
        this.setState({
            sender: true
        });


        const url = '/modiabrukerdialog/rest/journalforing/' + this.props.fnr + '/' + this.props.traadId;
        const data = JSON.stringify(this.props.sak);

        const journalforPromise = Ajax.post(url,data);

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
        } else {
            return (
                <div>
                    <button className="journalfor-knapp knapp-hoved-stor" onClick={this.journalfor}>Journalfør</button>
                </div>
            );
        }
    }
}

export default JournalforKnapp;