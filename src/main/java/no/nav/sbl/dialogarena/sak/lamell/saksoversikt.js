function addExpandClickEvent() {
    $('.kollapsbar').each(function() {
        var $kollapsbar, $toggleKollapsbar;

        $kollapsbar = $(this);
        $toggleKollapsbar = $kollapsbar.siblings('.toggle-kollapsbar');
        addExpandOnClickListener($toggleKollapsbar, $kollapsbar);
    });
}

function addExpandOnClickListener(toggleElement, expandableElement) {
    toggleElement.click(function(event) {
        event.preventDefault();
        event.stopPropagation();

        expandableElement.slideToggle(300);
        $(this).find('span').toggle(0);
        return false;
    });
}