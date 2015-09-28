import React from 'react';
import Snurrepipp from '../utils/snurrepipp';

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

        $.ajax({
            type: 'POST',
            url: '/modiabrukerdialog/rest/journalforing/' + this.props.fnr + '/' + this.props.traadId,
            contentType: 'application/json',
            data: JSON.stringify(this.props.sak)
        })
            .done(function (response, status, xhr) {
                this.props.traadJournalfort();
            }.bind(this))
            .fail(function () {
                this.setState({sender: false});
                this.props.feiletCallback();
            }.bind(this));
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