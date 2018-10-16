// ----------------------------------------------------------------------
// Component Tab tiles  (filter) [ky]
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {

        var getID = "all";
        var dataCategory

        loadAllDiv();
        function loadAllDiv() {
            $('[data-category]').fadeOut(500);
            $('[data-category]').slice(0, 12).fadeIn(500);
            $('[data-category]').delay(800).matchHeight({ byRow: true });

            if ($(".all:hidden").length != 0) {
                $("#loadMore").show();
                $('.content-wrapper').addClass('crop-height');
                $(".btn-scroll-top").fadeIn(500);
            }
            if ($('[data-category]').length < 4) {
                $(".btn-scroll-top").fadeOut(500);
            }
        }

        function loadMoreBtn() {
            $("#loadMore").fadeIn(500);
            $(".fade-bg").fadeIn(500);
            $('.content-wrapper').css('height', '830px');
        }
        function completeLoadMore() {
            $("#loadMore").fadeOut(500);
            $(".fade-bg").fadeOut(500);
            $('.content-wrapper').css('height', '100%');
        }

        //when load more btn is click
        function loadMoreContent() {
             $("#loadMore").fadeIn(500);
            $(".fade-bg").fadeIn(500);
            var getDivHeight = $(".content-wrapper").height();
            var cardHeight = $('[data-category]').height();
            var newDivHeight = getDivHeight + cardHeight;
            //$('.content-wrapper').css('height', newDivHeight);
            $('.content-wrapper').css({
                'transition': 'height 1.2s',
                'height': newDivHeight
            });
            $('[data-category=' + getID + ']').matchHeight({ byRow: true });

        }


        // get data content to filter
        $('.tab-tiles-wrapper ul li').click(function () {
            getID = $(this).attr('data-btn-category');
            dataCategory = $('[data-category=' + getID + ']')
            checkFilterData();
            //btn add active class
            $(this).parent().find('li.active').removeClass('active');
            $(this).addClass('active');

        });

        function checkFilterData() {
            if (getID == "all") {
                if ($('[data-category]').length > 12) {
                    loadAllDiv();
                    loadMoreBtn();

                } else {
                    loadAllDiv();
                    completeLoadMore();

                }

            } else {

                //hide all divs and load according to category
                $('.content-wrapper [data-category]').fadeOut(500);
                //  $('.content-wrapper [data-category]').not(dataCategory).fadeOut(500);
                if (dataCategory.length > 12) {
                    loadMoreBtn();
                    dataCategory.slice(0, 12).fadeIn(900).matchHeight({ byRow: true });
                } else {
                    completeLoadMore();
                    dataCategory.slice(0, 12).fadeIn(900).matchHeight({ byRow: true });
                }


                //hide show scroll top btn
                if (dataCategory.length < 4) {
                    $(".btn-scroll-top").fadeOut();
                } else {
                    $(".btn-scroll-top").fadeIn();
                }

            }
        }


        $("#loadMore").on('click', function (e) {
            e.preventDefault();
            //detect category to load hidden div by 4
            var dataCategory2 = '[data-category=' + getID + ']'

            if (getID == "all") {
                $('[data-category]' + ":hidden").slice(0, 4).fadeIn(700).slideDown(900);
                if ($('[data-category]' + ":hidden").length == 0) {
                    completeLoadMore();
                }
                if ($('[data-category]' + ":hidden").length != 0) {
                    loadMoreContent();
                }
            } else {
                $(dataCategory2 + ":hidden").slice(0, 4).fadeIn(600).slideDown(900);
                if ($(dataCategory2 + ":hidden").length == 0) {
                    completeLoadMore();
                }
                if ($(dataCategory2 + ":hidden").length != 0) {
                    loadMoreContent();
                }
            }

        });


        $(".btn-scroll-top").on('click', function (e) {
            e.preventDefault();
            $('html, body').animate({
                scrollTop: $(".tab-tiles-title").offset().top - 110,
            }, 600);
        });




    });
})();