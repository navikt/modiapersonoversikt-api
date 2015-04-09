var React = require('react');

var ModalPortal = React.createClass({
    focusAfterClose: undefined,

    getDefaultProps: function () {
        return {
            skipFocus: ['div'],
            isOpen: false
        };
    },
    getInitialState: function () {
        return {
            title: createAriaOptional('title', this.props.title),
            description: createAriaOptional('description', this.props.description),
            closeButton: createAriaOptional('closeButton', this.props.closeButton)
        }
    },
    componentDidMount: function () {
        if (this.props.isOpen === true) {
            this.focusFirst();
        }
    },
    componentDidUpdate: function () {
        if (this.props.isOpen) {
            $(document.body).addClass('modal-open');
            $(document.body).children().not(this.getDOMNode().parentNode).attr('aria-hidden', true);

            if (!$.contains(this.refs.content.getDOMNode(), document.activeElement)) {
                this.focusFirst();
            }
        } else {
            $(document.body).children().not(this.getDOMNode().parentNode).removeAttr('aria-hidden');
            this.restoreFocus();
            $(document.body).removeClass('modal-open');
        }
    },
    keyHandler: function (event) {
        var keyMap = {
            27: function escHandler() { //ESC
                this.props.modal.close();
                event.preventDefault();
            },
            9: function tabHandler() { //TAB
                if (this.handleTab(event.shiftKey)) {
                    event.preventDefault();
                }
            }
        };

        (keyMap[event.keyCode] || function () {
        }).bind(this)();

        //No leaks
        event.stopPropagation();
    },
    handleTab: function (isShiftkey) {
        var $content = $(this.refs.content.getDOMNode());
        var focusable = $content.find(':tabbable');
        var lastValidIndex = isShiftkey ? 0 : focusable.length - 1;


        var currentFocusElement = $content.find(':focus');

        if (focusable.eq(lastValidIndex).is(currentFocusElement)) {
            var newFocusIndex = isShiftkey ? focusable.length - 1 : 0;
            focusable.eq(newFocusIndex).focus();
            return true;
        }
        return false;
    },
    focusFirst: function () {
        this.focusAfterClose = document.activeElement;
        var tabbables = $(this.refs.content.getDOMNode()).find(':tabbable');
        this.props.skipFocus.forEach(function (skipFocusTag) {
            tabbables = tabbables.not(skipFocusTag);
        });

        if (tabbables.length > 0) {
            tabbables.eq(0).focus();
        }
    },
    restoreFocus: function () {
        if (this.focusAfterClose) {
            this.focusAfterClose.focus();
            this.focusAfterClose = undefined;
        }
    },
    render: function () {
        var children = this.props.children;
        if (!children.hasOwnProperty('length')) {
            children = [children];
        }

        children.map(function (child) {
            return React.addons.cloneWithProps(child, {
                modal: this.props.modal
            });
        }.bind(this));

        var title = this.state.title;
        var description = this.state.description;
        var closeButton = null;
        if (this.props.closeButton.show) {
            closeButton = <button className="closeButton" onClick={this.props.modal.close}>
                {this.state.closeButton.visible}
            </button>;
        }

        var cls = this.props.isOpen ? '' : 'hidden';
        return (
            <div tabIndex="-1" className={cls} aria-hidden={!this.props.isOpen} onKeyDown={this.keyHandler} role="dialog" aria-labelledby={title.id} aria-describedby={description.id}>
                <div className="backdrop" onClick={this.props.modal.close}></div>
                    {title.hidden}
                    {description.hidden}
                <div className="centering">
                    <div className="content" ref="content">
                        {title.visible}
                        {description.visible}
                        {children}
                        {closeButton}
                    </div>
                </div>
            </div>
        );
    }
});

function createAriaOptional(name, data) {
    var id = createId('react-modalx-' + name + '-');
    var tagComponent = data.tag.split(".");
    var tagType = tagComponent[0];
    var className = "";

    if (tagComponent.length > 1) {
        className = tagComponent[1];
    }
    var element = React.createElement(tagType, {id: id, className: className}, data.text);
    return {
        id: id,
        hidden: data.show ? null : element,
        visible: data.show ? element : null
    };
}
function createId(prefix) {
    return prefix + new Date().getTime() + "-" + Math.random();
}

module.exports = ModalPortal;
