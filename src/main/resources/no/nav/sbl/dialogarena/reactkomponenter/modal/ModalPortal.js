var React = ModiaJS.React;

var ModalPortal = React.createClass({
    focusAfterClose: undefined,
    componentDidMount: function () {
        if (this.props.isOpen === true) {
            this.focusFirst();
        }
    },
    componentDidUpdate: function () {
        if (this.props.isOpen) {
            $(document.body).addClass('modal-open');
            this.focusFirst();
        } else {
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
        var focusable = $content.find(':not(div):focusable').not('div');
        console.log('focusable', focusable);
        var lastValidIndex = isShiftkey ? 0 : focusable.length - 1;

        var currentFocusElement = $content.find(':focus')

        if (focusable.eq(lastValidIndex).is(currentFocusElement)) {
            var newFocusIndex = isShiftkey ? focusable.length - 1 : 0;
            console.log('newFocus', focusable.eq(newFocusIndex));
            focusable.eq(newFocusIndex).focus();
            return true;
        }
        return false;
    },
    focusFirst: function () {
        this.focusAfterClose = document.activeElement;
        var tabbables = $(this.refs.content.getDOMNode()).find(':focusable').not('div');
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
        if (!this.props.isOpen) {
            return null;
        }

        var children = this.props.children;
        if (!children.hasOwnProperty('length')) {
            children = [children];
        }

        children.map(function (child) {
            child.props.modal = this.props.modal;
            return child;
        }.bind(this));

        var title = createAriaOptional('title', this.props.title);
        var description = createAriaOptional('description', this.props.description);

        return (
            <div tabIndex="-1" onKeyDown={this.keyHandler} role="dialog" aria-labelledby={title.id} aria-describedby={description.id}>
                <div className="backdrop" onClick={this.props.modal.close}></div>
                    {title.hidden}
                    {description.hidden}
                <div className="centering">
                        {title.visible}
                        {description.visible}
                    <div className="content" ref="content" role="dialog">
                        {children}
                    </div>
                </div>
            </div>
        );
    }
});

function createAriaOptional(name, data) {
    var id = createId('react-modalx-'+name + '-');

    var tagComponent = data.tag.split(".");
    var tagType = tagComponent[0];
    var className = "";

    if (tagComponent.length > 2) {
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
