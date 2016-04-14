$(document).ready(function () {
    var padding = 2 * 16;

    function getOffsetTop($elem) {
        var offset = $elem.offset();
        if (offset) {
            return offset.top;
        }
        return 0;
    }

    $(document).on('focus', '*', function (event) { // Bruk delegate pattern for å unngå masse eventlisteners
        event.stopPropagation(); // Stop, slik at vi ikke får alle parents sine events også

        var $this = $(this);
        var $scroller = $this.scrollParent();

        var offsetTop = getOffsetTop($this) - getOffsetTop($scroller); // Reell offset innenfor scrollcontainer
        var scrollPosition = $scroller.scrollTop();

        //Scroll nedover fungerer av seg selv, vi håndterer derfor bare scroll oppover
        if (offsetTop < padding && scrollPosition !== 0) {
            $scroller.animate({ scrollTop: (scrollPosition + offsetTop - padding) + 'px' }, 'fast');
        }
    });
});