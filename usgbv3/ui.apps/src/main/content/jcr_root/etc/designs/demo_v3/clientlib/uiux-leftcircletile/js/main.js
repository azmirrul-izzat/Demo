// ----------------------------------------------------------------------
// Left Circle Tile
// ----------------------------------------------------------------------
(function () {

    "use strict";
    $(document).ready(function () {


        $(".left-circle-tile").slick({
            "dots": true,
            "infinite": false,
            "speed": 300,
            "slidesToShow": 3,
            "slidesToScroll": 1,
            "responsive": [{
                    breakpoint: 950,
                    settings: {
                        slidesToShow: 2,
                        slidesToScroll: 1
                    }
                },
                {
                    breakpoint: 768,
                    settings: {
                        slidesToShow: 1,
                        slidesToScroll: 1
                    }
                }
            ]
        });



        if ($(window).width() < 960) {
        }
        else {
    
                var tileNum =  $(".left-circle-tile .slick-slide").length;
                if(tileNum < 4){
                    $(".left-circle-tile .slick-dots").hide(100);
                    $(".left-circle-tile .slick-arrow").hide(100);
                }else{
                    $(".left-circle-tile .slick-dots").show(100);
                    $(".left-circle-tile .slick-arrow").show(100);
                }
        }

    });



})();