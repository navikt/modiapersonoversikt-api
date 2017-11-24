import React, { Component } from 'react';
import PT from 'prop-types';
import classnames from 'classnames';
import Collapse from 'react-collapse';
import 'nav-frontend-ekspanderbartpanel-style'; // eslint-disable-line import/extensions

const cls = (className, props) => classnames('ekspanderbartPanel', className, {
    'ekspanderbartPanel--lukket': !props.apen,
    'ekspanderbartPanel--apen': props.apen
});

class EkspanderbartpanelBasePure extends Component {
    constructor(props) {
        super(props);

        this.isCloseAnimation = false;

        this.onRestProxy = this.onRestProxy.bind(this);
        this.tabHandler = this.tabHandler.bind(this);
    };

    componentWillReceiveProps(nextProps) {
        if (this.props.apen && !nextProps.apen) {
            this.isCloseAnimation = true;
        }
    };

    onRestProxy() {
        return () => {
            this.isCloseAnimation = false;
            if (this.props.collapseProps.onRest) this.props.collapseProps.onRest();
        };
    }

    tabHandler(event) {
        const { keyCode } = event;
        const isTab = keyCode === 9;

        if (isTab && this.isCloseAnimation) {
            event.preventDefault();
        }
    };

    render() {
        const { className, children, apen, heading, ariaTittel, onClick, collapseProps, ...renderProps } = this.props;
        const myCollapseProps = {
            ...collapseProps,
            isOpened: apen,
            onRest: this.onRestProxy
        };

        return (
            <div className={cls(className, this.props)} {...renderProps}>
                <div
                    role="button"
                    tabIndex="0"
                    className="ekspanderbartPanel__hode"
                    onKeyDown={this.tabHandler}
                    onClick={onClick}
                    aria-expanded={apen}
                >
                    <div className="ekspanderbartPanel__flex-wrapper">
                        {heading}
                        <span className="ekspanderbartPanel__indikator" />
                    </div>
                </div>
                <Collapse {...myCollapseProps} >
                    <article aria-label={ariaTittel} className="ekspanderbartPanel__innhold">{children}</article>
                </Collapse>
            </div>
        );
    }
}

EkspanderbartpanelBasePure.propTypes = {
    heading: PT.oneOfType([
        PT.arrayOf(PT.node),
        PT.node
    ]).isRequired,
    className: PT.string,
    onClick: PT.func.isRequired,
    ariaTittel: PT.string.isRequired,
    apen: PT.bool.isRequired,
    children: PT.oneOfType([
        PT.arrayOf(PT.node),
        PT.node
    ]).isRequired,
    collapseProps: PT.shape(
        {
            springConfig: PT.objectOf(PT.number),
            forceInitialAnimation: PT.bool,
            hasNestedCollapse: PT.bool,
            fixedHeight: PT.number,
            theme: PT.objectOf(PT.string),
            style: PT.object,
            onRender: PT.func,
            onRest: PT.func,
            onMeasure: PT.func
        }
    )
};
EkspanderbartpanelBasePure.defaultProps = {
    className: undefined,
    collapseProps: {}
};

export default EkspanderbartpanelBasePure;
