function addKvitteringsPanelEvents() {
    addExpandClickEvent();
    addPrintClickEvent();
}

function addPrintClickEvent() {
    $('.saksoversikt .skriv-ut').click(function(event) {
        event.preventDefault();
        event.stopPropagation();
        prepareElementForPrint($(this).closest('.behandling-informasjon'), 'saksoversikt');
        window.print();
    });
}

function addExpandClickEvent() {
    $('.saksoversikt .kollapsbar').each(function() {
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

//function settSakHoyde() {
//    settHoyde();
//    $(window).resize(settHoyde);
//    var saker = $('.saker');
//
//    function settHoyde() {
//        saker.css('min-height', 0);
//        var height = $('.main-content').height();
//        $('.lamell .lamellhode')
//            .each(function() {
//                height -= $(this).height()
//            }
//        );
//        $('.saker').css('min-height', height - 1); // -1 for å fjerne scrollbar dersom den ikke er nødvendig
//    }
//}