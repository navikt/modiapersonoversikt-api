(function () {
    $(document).ready(function () {
        var $lamell = $('.main-content');

        $lamell.on('click', '.besvarBoble > a', function () {
            var $link = $(this);
            $link.parent('.besvarBoble').replaceWith('<img class="snurrepipp" src="/modiabrukerdialog/img/ajaxloader/hvit_roed/loader_hvit_rod_32.gif"/>');
        });
    });
})();