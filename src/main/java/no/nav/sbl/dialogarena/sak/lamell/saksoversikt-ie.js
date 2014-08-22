function resizeElement() {
    setHeight();
    $(window).resize(setHeight)
}

function setHeight() {
    var $saker = $('.saksoversikt .saker');
    $saker.height($saker.parent().height());
}