// ----------------------------------------------------------------------
// Sticky icons
// ----------------------------------------------------------------------
(function () {
    "use strict";

    $(document).ready(function () {

        $('.sticky-icons-button a, .sticky-icons-expander .close-pop-up').on("click", function (e) {
            e.preventDefault();

            var $stickyIconsExpander = $('.sticky-icons-expander');

            if ($stickyIconsExpander.hasClass('hidden')) {
                $stickyIconsExpander.removeClass('hidden');
                setTimeout(function () { $stickyIconsExpander.addClass('active') }, 50);
            } else {
                $stickyIconsExpander.removeClass('active')
                setTimeout(function () { $stickyIconsExpander.addClass('hidden'); }, 600);
            }

            defaultScrollUpdate(".sticky-icons-expander");

        });

    })

})();