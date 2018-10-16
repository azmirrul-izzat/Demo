// ----------------------------------------------------------------------
// Product details gallery slickjs mix media 
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {

        // $(".test-slider-for").slick();
        // $(".test-slider-nav").slick();

        $('[data-fancybox]').fancybox({
            loop: true
        });

        $('.gallery-slider-for').slick({
            // slidesToShow: 1,
            // slidesToScroll: 1,
            // arrows: true,
            // fade: true,
            asNavFor: '.gallery-slider-nav'
        });
        $('.gallery-slider-nav').slick({
            // slidesToShow: 3,
            // slidesToScroll: 1,
            asNavFor: '.gallery-slider-for'
            // dots: false,
            // centerMode: true,
            // focusOnSelect: true
        });


        ///  MIX MEDIA  GALLERY/// 
        if ($(".mix-media-gallery-wrapper").length) {
            console.log("test")
            // videoType();


            $(".mix-media-gallery-wrapper .slick-arrow").click(function () {
                //   videoType();
                console.log("test1")
                $(".modal-video-elem").get(0).pause();
                if ($(".mix-media-gallery-wrapper .slick-slide .video #ranID").hasClass('YouTubeVideoPlayer')) {
                    console.log("bb")
                    $('.YouTubeVideoPlayer')[0].contentWindow.postMessage('{"event":"command","func":"' + 'stopVideo' + '","args":""}', '*');
                }
            });

            function videoType() {

                if ($(".mix-media-gallery-wrapper .slick-current .video").prev().hasClass('modal-video-elem')) {
                    console.log("aaa")
                    $(".modal-video-elem").get(0).pause();
                }
                if ($(".mix-media-gallery-wrapper .slick-current .video").prev().hasClass('YouTubeVideoPlayer')) {
                    console.log("bb")
                    $('.YouTubeVideoPlayer')[0].contentWindow.postMessage('{"event":"command","func":"' + 'stopVideo' + '","args":""}', '*');
                }
            }
        }
        ///  MIX MEDIA  GALLERY///

    });
})();