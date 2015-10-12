class Store {

    constructor(state) {
        this.listeners = [];
        this.state = state;
        this.container = undefined;

    }

    addListener(listener) {
        this.listeners.push(listener);
    }

    removeListener(listener) {
        var nyeListeners = this.listeners.slice(0);
        var index = nyeListeners.indexOf(listener);
        this.listeners.splice(index, 1);
    }

    getState() {
        return this.state;
    }

    fireUpdate() {
        this.listeners.forEach(function (listener) {
            listener();
        });
    }

    setContainerElement(container) {
        this.container = container;
    }
}

export default Store;

/*var Store = function (state) {
 this.listeners = [];
 this.state = state;
 this.container = undefined;
 };*/
/*
 Store.prototype.addListener = function (listener) {
 this.listeners.push(listener);
 };
 */

/*
 Store.prototype.removeListener = function (listener) {
 var nyeListeners = this.listeners.slice(0);
 var index = nyeListeners.indexOf(listener);
 this.listeners.splice(index, 1);
 };
 */

/*

 Store.prototype.getState = function () {
 return this.state;
 };
 */

/*    var Store = (state)=> {
 this.listeners = [];
 this.state = state;
 this.container = undefined;
 }*/

/*
 Store.prototype.setContainerElement = function (container) {
 this.container = container;
 };
 */

/*
 Store.prototype.fireUpdate = function () {
 this.listeners.forEach(function (listener) {
 listener();
 });
 };
 */
//module.exports = Store;
