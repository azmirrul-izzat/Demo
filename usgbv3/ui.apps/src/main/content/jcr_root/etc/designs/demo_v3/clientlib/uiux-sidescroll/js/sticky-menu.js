
// ----------------------------------------------------------------------
// Product Sub Menu Nav [KY]
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {

        if ($('div[data-product-menu="start"]').length > 0) {

            $(window).scroll(function () {
                var posSub = $(".data-scroll-wrapper .product-menu-start").offset().top - 125;
                var posSubEnd = $(".data-scroll-wrapper .product-menu-end").offset().top - 200;
                var posEqual = $(".data-scroll-wrapper .product-menu-start").offset().bottom + 50;

                if ($(this).scrollTop() > posSub) {
                    $('.product-menu-sticky').fadeIn(600).slideDown(700);
                    $('.product-menu-desktop').delay(100).css({ 'visibility': 'hidden' });
                } else {
                    $('.product-menu-sticky').slideUp(10).fadeOut(10);
                    $('.product-menu-desktop').css({ 'visibility': 'visible' });
                }

                if ($(this).scrollTop() > posSubEnd) {
                    $('.product-menu-sticky').addClass("hidden");
                } else {
                    $('.product-menu-sticky').removeClass("hidden");
                }

                if ($(this).scrollTop() == posEqual) {
                    $('.product-menu-sticky').fadeOut(10);
                    $('.product-menu-desktop').css({ 'visibility': 'visible' });
                }

            });
        }


    });
})();


// ----------------------------------------------------------------------
// Product-Sub Menu scroll [KY]
// ----------------------------------------------------------------------


        function detectSection() {

            // Click To Sections
            var getValue = "product-information";
            //mobile
            $('#mobile-sub-dropdown').change(function () {
                getValue = $(this).val();
                scrollToSection();
            });

            function scrollToSection() {
                var scrollToDiv = "div[data-scroll=" + getValue + "]"
                var offset = 120;
                $('html, body').animate({
                    scrollTop: $(scrollToDiv).offset().top - offset,
                }, 900, callback);

                function callback() {
                    //hide header when section is click to avoid overlaps 
                    $('.header .wrapper').removeClass('desktop-mobile-slide-down').addClass('desktop-mobile-slide-up');
                }
            }


            $(document).on("scroll", onScroll);
            //smoothscroll
            $('.desktop-list-inline li').on('click', function (e) {
                getValue = $(this).attr("data-product-list");
                scrollToSection();
                $(window).on("scroll", onScroll);
            });

            function onScroll(event) {
                var scrollPos = $(document).scrollTop();
                $('.desktop-list-inline li').each(function () {
                    var currLink = $(this);
                    var getText = $(this).text();

                    var refElement = $(this).attr("data-product-list");
                    var divName = '[data-scroll=' + refElement + ']';
                    var getTopPos = $(divName).position().top - 120;
                    var getHeightPos = $("[data-scroll=" + refElement + "]").height();

                    if (getTopPos <= scrollPos && getTopPos + getHeightPos > scrollPos) {
                        $(".mobile-list-inline li").text(getText);
                        currLink.addClass("arrow-up");
                    }
                    else {
                        currLink.removeClass("arrow-up");
                    }
                });
            }

        }


