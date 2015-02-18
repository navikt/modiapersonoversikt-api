var React = ModiaJS.React;

var ModalPortal = React.createClass({
        getInitialState: function () {
            console.log('initial state');
            return {
                isOpen: false,
                focusAfterClose: undefined
            };
        },
        componentWillMount: function () {
            if (this.props.isOpen) {
                this.open();
            }
        },
        componentWillReceiveProps: function (newProps) {
            if (newProps.hasOwnProperty('isOpen') && newProps.isOpen === true && !this.props.isOpen) {
                this.open();
                $(document.body).addClass('modal-open');
            } else if (!newProps.isOpen && this.props.isOpen) {
                this.close();
                $(document.body).removeClass('modal-open');

                if (this.state.focusAfterClose && this.state.focusAfterClose.length > 0) {
                    this.state.focusAfterClose.blur().focus();
                }
            }
        },
        open: function () {
            this.setState({isOpen: true, focusAfterClose: $(':focus')});
            setTimeout(function () {
                $(this.refs.content.getDOMNode()).find(':focusable:first').blur().focus();
            }.bind(this), 0);
        },
        close: function () {
            this.setState({isOpen: false});
        },
        keyHandler: function (event) {
            var keyMap = {
                27: function escHandler() { //ESC
                    this.close();
                    event.preventDefault();
                },
                9: function tabHandler() { //TAB
                    console.log('tab handler');
                    if (this.handleTab(event.shiftKey)) {
                        event.preventDefault();
                    }
                }
            };

            (keyMap[event.keyCode] || function () {
            }).bind(this)();

            //No leaks
            event.stopPropagation();
        }
        ,
        handleTab: function (isShiftkey) {
            var $content = $(this.refs.content.getDOMNode());
            var focusable = $content.find(':focusable');
            var lastValidIndex = isShiftkey ? 0 : focusable.length - 1;

            var currentFocusElement = $content.find(':focus')

            if (focusable.eq(lastValidIndex).is(currentFocusElement)) {
                var newFocusIndex = isShiftkey ? focusable.length - 1 : 0;
                focusable.eq(newFocusIndex).focus();
                return true;
            }
            return false;
        }
        ,
        render: function () {
            if (!this.state.isOpen) {
                return null;
            }

            var children = this.props.children;
            if (!children.hasOwnProperty('length')) {
                children = [children];
            }

            children = [].slice.call(children, 0);
            children.map(function (child) {
                child.props.closeModal = this.close;
                return child;
            });

            return (
                <div tabIndex="-1" onKeyDown={this.keyHandler}>
                    <div className="backdrop" onClick={this.close}></div>
                    <div className="centering">
                        <div className="content" ref="content" role="dialog">
                        {children}
                        </div>
                    </div>
                </div>
            );
        }
    })
    ;

module.exports = ModalPortal;
