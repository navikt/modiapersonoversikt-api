window.ModiaJS = {};
window.ModiaJS.Components = {};
(function(){
    var Ap = Array.prototype;
    var slice = Ap.slice;
    var Fp = Function.prototype;

    Fp.bind = function(context) {
        var func = this;
        var args = slice.call(arguments, 1);

        function bound() {
            var invokedAsContructor = func.prototype && (this instanceof func);
            return func.apply(
                !invokedAsContructor && context || this, args.concat(slice.call(arguments))
            );
        }

        bound.prototype = func.prototype;

        return bound;
    };
})();
module.exports = {};
