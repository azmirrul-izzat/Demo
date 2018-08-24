// ----------------------------------------------------------------------
// Carousel Home Banner
// ----------------------------------------------------------------------
(function(){
    "use strict";
    $(document).ready(function(){
       $(".carousel-home-banner").slick({
                "dots": true,
                "infinite": true,
                "speed": 300,
                "slidesToShow": 1,
                "slidesToScroll": 1,
                "responsive": [
                    {
                        "breakpoint": 992,
                        "settings": {
                            "slidesToShow": 1
                        }
                    }
                ]
            });



        var movementStrength = 25;
        var height = movementStrength / $(window).height();
        var width = movementStrength / $(window).width();

        $(".carousel-home-banner-wrapper").mousemove(function(e){
            var getWidth=$(window).width();
            var newX
            var newY 
            if(getWidth < 768){
                newX = 0;
                newY = 0;
            }
             if(getWidth > 768){
                newX = 50;
                newY = 50;
            }

            if(getWidth > 1200){
                newX = 80;
                newY = 100;
            }
            if(getWidth > 1500){
                newX = 100;
                newY = 180;
            }


            var pageX = e.pageX - ($(window).width() / 2);
            var pageY = e.pageY - ($(window).height() / 2);
            var newvalueX = width * pageX * -1 - 25 - newX;
            var newvalueY = height * pageY * -1 - 50 - newY;
            $('.slick-current').css("background-position", newvalueX+"px     "+newvalueY+"px");
        });



    });



	function detectVideo() {
		theVideo.play();
	}

	if ($('.hero-banner-wrapper video').length){
    	detectVideo();
    }

	// On before slide change
	$(".carousel-home-banner").on('beforeChange', function (event, slick, currentSlide, nextSlide) {

		if ($('.hero-banner-wrapper video').length){
        	detectVideo();
        }
	});

    $('.carousel-home-banner').on('init', function(ev, el){ 
            $('.carousel-home-banner > video').each(function () {
                this.play();
            });
        }); 
})();