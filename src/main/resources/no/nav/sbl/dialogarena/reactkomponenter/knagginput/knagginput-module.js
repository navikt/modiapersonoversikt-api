import React from 'react/addons';

function ariaLabel(props) {
    const knagger = props.knagger;
    const fritekst = props.fritekst;

    if (knagger.length === 0 && fritekst.length === 0) {
        return props['aria-label'];
    }

    const label = [];
    if (knagger.length > 0) {
        label.push('nagger: ' + knagger.join(' '));
    }
    if (fritekst.length > 0) {
        label.push('ritekst: ' + fritekst);
    }
    return label.join(' ');
}

function finnKnaggerOgFritekst(fritekst, eksistendeKnagger) {
    const processedfritekst = fritekst.replace(/\B#(\S+)\s/g, (fullmatch, capturegroup) => {
        eksistendeKnagger.push(capturegroup);
        return '';
    });

    return {
        knagger: eksistendeKnagger,
        fritekst: processedfritekst
    };
}

function IEHack() {
    // Dette er en IE hack.... Hvis man en gang i fremtiden ikke bruker IE 9, så kanskje man kan fjerne denne.
    const $knaggcontainer = $(this.refs.knaggcontainer.getDOMNode());
    if (!$knaggcontainer.is(':visible')) {
        return;
    }
    const $input = $knaggcontainer.find('input[type="text"]');
    const $knagger = $knaggcontainer.find('span.knagg');

    let maxWidth = $knaggcontainer.width();
    $knagger.each((index, knagg) => {
        maxWidth -= $(knagg).outerWidth() + 4.2;// knagg bredde + margin;
    });
    maxWidth -= 46;// Forstørrelseglass ikon

    $input.outerWidth(maxWidth);
    // End IE hack
}

const KnaggInput = React.createClass({
    propTypes: {
        'auto-focus': React.PropTypes.boolean,
        'aria-controls': React.PropTypes.string,
        store: React.PropTypes.object.isRequired,
        tablisteId: React.PropTypes.string.isRequired,
        knagger: React.PropTypes.array,
        placeholder: React.PropTypes.string,
        fritekst: React.PropTypes.string
    },
    getDefaultProps: function getDefaultProps() {
        return {
            knagger: [],
            fritekst: '',
            tablisteId: null
        };
    },
    getInitialState: function getInitialState() {
        return {
            selectionStart: -1,
            selectionEnd: -1,
            focus: false
        };
    },
    componentDidMount: function componentDidMount() {
        if (this.props['auto-focus']) {
            this.refs.search.getDOMNode().focus();
        }
    },
    componentDidUpdate: function componentDidUpdate() {
        IEHack.call(this);
    },
    onKeyDownProxy: function onKeyDownProxy(event) {
        this.setState({
            selectionStart: this.refs.search.getDOMNode().selectionStart,
            selectionEnd: this.refs.search.getDOMNode().selectionEnd
        });
        this.props.store.onKeyDown($('#' + this.props.tablisteId), event);
    },
    onChangeProxy: function onChangeProxy(event) {
        const data = finnKnaggerOgFritekst(event.target.value, this.props.knagger);
        this.props.store.onChange(data);
    },
    handleKeyUp: function handleKeyUp(event) {
        const selectionStart = this.state.selectionStart;
        const selectionEnd = this.state.selectionEnd;

        if (event.keyCode === 8 /* backspace */ && selectionStart === 0 && selectionStart === selectionEnd) {
            if (this.props.knagger.length === 0) {
                return;
            }
            const nyeKnagger = this.props.knagger.slice(0);
            this.props.store.slettKnagg(nyeKnagger.pop());
        }
    },
    fjernKnagg: function fjernKnagg(knagg, event) {
        event.preventDefault();

        this.props.store.slettKnagg(knagg);
        this.refs.search.getDOMNode().focus();
    },
    focusHighlighting: function focusHighlighting(event) {
        if (event.type === 'focus') {
            this.setState({focus: true});
        } else {
            this.setState({focus: false});
        }
    },
    render: function render() {
        let knagger = this.props.knagger.map((knagg) => {
            return (
                <span className="knagg">
                    {knagg}
                    <button aria-label={'Fjern knagg: ' + knagg} onClick={this.fjernKnagg.bind(this, knagg)}></button>
                </span>
            );
        });

        knagger = React.addons.createFragment({
            knagger: knagger
        });

        return (
            <div ref="knaggcontainer" className="knagg-input">
                <div className={'knagger' + (this.state.focus ? ' focus' : '')}>
                    {knagger}
                    <input type="text" ref="search" className="search" placeholder={this.props.placeholder}
                           value={this.props.fritekst} title={this.props.placeholder}
                           onChange={this.onChangeProxy} onKeyDown={this.onKeyDownProxy} onKeyUp={this.handleKeyUp}
                           onFocus={this.focusHighlighting} onBlur={this.focusHighlighting}
                           aria-label={ariaLabel(this.props)} aria-controls={this.props['aria-controls']}/>
                    <img src="../img/sok.svg" alt="Forstørrelseglass-ikon" aria-hidden="true"/>
                </div>
            </div>
        );
    }
});

module.exports = KnaggInput;
