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


		var lFollowX = 0,
        lFollowY = 0,
        x = 0,
        y = 0,
        friction = 1 / 20;


        $(".hero-banner-wrapper").on('mousemove', function(e) {

            var lMouseX = Math.max(-100, Math.min(100, $(window).width() / 2 - e.clientX));
            var lMouseY = Math.max(-100, Math.min(100, $(window).height() / 2 - e.clientY));
            lFollowX = (20 * lMouseX) / 100; // 100 : 12 = lMouxeX : lFollow
            lFollowY = (10 * lMouseY) / 100;

			x += (lFollowX - x) * friction;
            y += (lFollowY - y) * friction;

            $('.slick-current').css("background-position", x+"px  " +y+"px");
            //window.requestAnimationFrame(moveBackground);

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