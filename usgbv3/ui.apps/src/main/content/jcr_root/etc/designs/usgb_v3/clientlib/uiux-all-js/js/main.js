// ----------------------------------------------------------------------
//Default reusable functions
// ----------------------------------------------------------------------

/*viewport width====================*/
function viewport() {
    var e = window,
        a = 'inner';
    if (!('innerWidth' in window)) {
        a = 'client';
        e = document.documentElement || document.body;
    }
    return {
        width: e[a + 'Width'],
        height: e[a + 'Height']
    };
}
/*end viewport width====================*/

/*defaultScrollUpdate====================*/
function defaultScrollUpdate(element) {

    var mobileExpandStateMonitor = setInterval(function () {
        //if hidden show scrollbar
        if ($(element).hasClass('hidden')) {

            $('html, body').removeClass('disable-scroll');

        } else {
            //else hide scrollbar
            $('html, body').addClass('disable-scroll');
        }
    }, 200);

    //stop after running for 1 sec, prevent running in background
    setTimeout(function () {
        clearInterval(mobileExpandStateMonitor);
    }, 1000);

};
/*end defaultScrollUpdate====================*/

/*load script before body====================*/
function hookHeadScript(url, async, defer, callback) {

    if (document.getElementsByTagName('html')[0].innerHTML.indexOf(url) === -1) {
        var script = document.createElement("script")
        script.type = "text/javascript";
        script.onload = callback;
        script.src = url;
        if (async === true) {
            script.setAttribute("async", "");
        }
        if (defer === true) {
            script.setAttribute("defer", "");
        }
        document.getElementsByTagName("head")[0].appendChild(script);
    }
}
/*end load script before body====================*/

// ----------------------------------------------------------------------
//END Default reusable functions
// ----------------------------------------------------------------------


// ----------------------------------------------------------------------
// Load inline youtube video(s) in background
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {


        //load youtube api
        hookHeadScript("//www.youtube.com/iframe_api", true, false, function () {

            //console.log('youtbe iframe api loaded');

            //add youtube video

            $(".YouTubeVideoPlayer").each(function (index) {
                var youtubeID = $(this).attr('id');
                var youtubeVideoID = $(this).attr('data-youtubeid');

                //console.log(youtubeVideoID);

                //load all youtube videos in background when page load
                onYouTubeIframeAPIReady(youtubeID, youtubeVideoID);

            });


            function onYouTubeIframeAPIReady(name, id) {

                //The callback is fired once the api script has been loaded but not necessarily executed.
                if ((typeof YT !== "undefined") && YT && YT.Player) {

                    //console.log('not ready');
                    var player;

                    player = new YT.Player(name, {
                        videoId: id,
                        width: 560,
                        height: 316,
                        playerVars: {
                            autoplay: 0,
                            controls: 1,
                            showinfo: 1,
                            modestbranding: 1,
                            loop: 0,
                            fs: 1,
                            cc_load_policy: 0,
                            autohide: 0
                        },
                        events: {
                            onReady: function (e) {
                                //e.target.mute();//chrome need to set to mute to autoplay
                                //e.target.playVideo();

                                //bind play buttons to each player
                                var playButton = $("#" + name).parent().prev('button');
                                playButton.on('click', function () {
                                    player.playVideo();
                                });

                            }
                        }
                    });

                } else {
                    //console.log('ready');
                    setTimeout(function () {
                        onYouTubeIframeAPIReady(name, id);
                    }, 100);
                }

            }


        });


    });

})();

// ----------------------------------------------------------------------
// Component: Hero Banner
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {


        var mobileWidth = window.matchMedia("(max-width: 768px)").matches;
        var target = $('.hero-banner-wrapper');
        for (var i = 0, len = target.length; i < len; i++) {
            var src = $(target[i]).data('img-mobile');
            if (mobileWidth && src.replace(/\s/g, "") != "") {
                $(target[i]).attr('style', src);
            }
        }

    });

})();

// ----------------------------------------------------------------------
// Social media buttons
// ----------------------------------------------------------------------
(function () {
    "use strict";

    $('.social-media-pop-up').click(function (e) {
        e.preventDefault();
        window.open($(this).attr('href'), 'shareWindow', 'height=450, width=550, toolbar=0, location=0, menubar=0, directories=0, scrollbars=0');
        return false;
    });

})();

// ----------------------------------------------------------------------
// Component: CTA Video Popup
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {
        if ($('.cta-video-popup').length > 0) {

            $('.cta-video-popup').on('click', function () {
                switch ($(this).data('video-type')) {
                    case "youtube":
                        togglePopupYoutubeVideo('show', this);
                        $(this).parent().find('.modal').attr('data-video', 'youtube');
                        break;

                    case "html5":
                        togglePopupVideo();
                        $(this).parent().find('.modal').attr('data-video', 'html5');
                        break;
                }
            });


            $('.modal-video-popup .close').on('click', function (e) {
                switch ($(this).closest('.modal').data('video')) {
                    case "youtube":
                        togglePopupYoutubeVideo('hide', this);
                        break;

                    case "html5":
                        togglePopupVideo('hide');
                        break;
                }
            });
        }
    });

    function togglePopupVideo(state) {

        // if state == 'hide', hide. Else: show video
        if (state == "hide") {
            $('video.modal-video-elem')[0].pause();
        } else {
            $('video.modal-video-elem')[0].play();
        }
    }

    function togglePopupYoutubeVideo(state, element) {

        console.log("testvideo")
        //autoplay video not supported unless set to mute by default
        //if state == 'hide', hide. Else: show video

        if (state == "hide") {

            var youtubevideo = $(element).next().find($('.modal-video-elem'));

            //pause/stop videos using iframe only, for more customization use youtube api instead
            $('iframe.modal-video-elem').each(function () {
                this.contentWindow.postMessage('{"event":"command","func":"pauseVideo","args":""}', '*')
            });

        }

        // if state == 'hide', hide. Else: show video
        // var func;
        // var div = document.getElementById("modal-video");
        // var iframe = div.getElementsByTagName("iframe")[0].contentWindow;
        // div.style.display = state == 'hide' ? 'none' : '';
        // func = state == 'hide' ? 'pauseVideo' : 'playVideo';
        // iframe.postMessage('{"event":"command","func":"' + func + '","args":""}', '*');
    }

})();

// ----------------------------------------------------------------------
// Inline videos
// ----------------------------------------------------------------------
(function () {
    "use strict";

    $(document).ready(function () {

        //   if($('.inline-video').length)playVideoPlayer()

        // function playVideoPlayer(){

        $('.inline-video').on("click", function () {


            var $this = $(this);
            var thumbs = $this.children('.cta-video-inline');
            var video = $this.find('video');

            if ($this.data('video') === "html5") {

                //retain image for video responsiveness
                thumbs.css({
                    "z-index": -1
                });
                video.css({
                    "z-index": 1
                });
                video[0].play();

            } else if ($this.data('video') === "youtube") {

                var videoWrapper = $this.find('.video');
                var video = $this.find('.YouTubeVideoPlayer');
                //YT.Player name parameter must be an ID not class
                var videoIdName = video.attr('id');
                var videoId = video.attr('data-youtubeId');


                /* load youtube api with video based on click
 
                 //load youtube api
                 hookHeadScript("//www.youtube.com/iframe_api", true, false, function ()
                 {
 
                     console.log('youtbe iframe api loaded');
 
 
 
                     //add youtube video
                     onYouTubeIframeAPIReady('ranID', 'aqz-KE-bpKQ');
 
                     function onYouTubeIframeAPIReady(name, id) {
 
                         //The callback is fired once the api script has been loaded but not necessarily executed.
                         if( (typeof YT !== "undefined") && YT && YT.Player )
                         {
 
                             //console.log('not ready');
                             var player;
 
                             player = new YT.Player(name,
                                 {
                                     videoId: id,
                                     width: 560,
                                     height: 316,
                                     playerVars:
                                     {
                                         autoplay: 0,
                                         controls: 1,
                                         showinfo: 1,
                                         modestbranding: 1,
                                         loop: 0,
                                         fs: 1,
                                         cc_load_policy: 0,
                                         autohide: 0
                                     },
                                     events:
                                     {
                                         onReady: function (e) {
                                             //e.target.mute();//chrome need to set to mute to autoplay
                                             e.target.playVideo();
                                         }
                                     }
                                 });
 
                         }else
                         {
                             //console.log('ready');
                             setTimeout(function(){ onYouTubeIframeAPIReady(name, id); }, 100);
                         }
 
                     }
 
 
                 });
 
                 */


                //retain image for video responsiveness
                thumbs.css({
                    "z-index": -1
                });
                video.css({
                    "z-index": 1
                });
            }
        });


        //}





        // <script async src="https://www.youtube.com/iframe_api"></script>
        // <script>
        //  function onYouTubeIframeAPIReady() {
        //   var player;
        //   player = new YT.Player('muteYouTubeVideoPlayer', {
        //     videoId: '5DEdR5lqnDE', // YouTube Video ID
        //     width: 560,               // Player width (in px)
        //     height: 316,              // Player height (in px)
        //     playerVars: {
        //       //playlist: '5DEdR5lqnDE', //must be included for autoplay to work
        //       //list:'5DEdR5lqnDE', //id of listType
        //       //listType:'search', // search, user upload and playlist base on the list's id
        //       autoplay: 0,        // Auto-play the video on load
        //       controls: 1,        // Show pause/play buttons in player
        //       showinfo: 1,        // Hide the video title
        //       modestbranding: 1,  // Hide the Youtube Logo
        //       loop: 0,            // Run the video in a loop
        //       fs: 1,              // fullscreen
        //       cc_load_policy: 0, // Hide closed captions
        //       autohide: 0         // Hide video controls when playing
        //     },
        //     events: {
        //       onReady: function(e) {
        //         //e.target.mute();//chrome need to set to mute to autoplay
        //         //e.target.playVideo();





        //           // bind events
        //   var playButton = document.getElementById("vv");
        //   playButton.addEventListener("click", function() {
        //     player.playVideo();
        //   });





        //       }
        //     }
        //   });
        //  } 

        //  // Written by @labnol 
        // </script>



    })

})();

// ----------------------------------------------------------------------
// CTA Hover Slideup Tile
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {
        var target = $('.cta-hover-slideup-tile-label');
        for (var i = 0, len = target.length; i < len; i++) {
            console.log($(target[i]));
            var content = $(target[i]).find('div');
            var divh = $(target[i]).height();
            while (content.outerHeight() > divh) {
                content.text(function (index, text) {
                    return text.replace(/\W*\s(\S)*$/, '...');
                });
            }
        }
    });
})();

// ----------------------------------------------------------------------
// CTA Hover Slideup Tile
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {
        var target = $('.carousel-featured-item');
        var mobileWidth = window.matchMedia("(max-width: 992px)").matches;
        var desktopWidth = window.matchMedia("(min-width: 992px)").matches;
        for (var i = 0, len = target.length; i < len; i++) {
            if (mobileWidth && $(target[i]).find('.featured-item-card-group').length > 1) {
                $(target[i]).slick();
            } else if (mobileWidth && $(target[i]).find('.featured-item-card-group').length <= 1) {
                $(target[i]).css('display', 'flex').css('justify-content', 'center');
            } else if (desktopWidth && $(target[i]).find('.featured-item-card-group').length > 4) {
                $(target[i]).slick();
            } else if (desktopWidth && $(target[i]).find('.featured-item-card-group').length <= 4) {
                $(target[i]).css('display', 'flex').css('justify-content', 'center');
            }
            $(target[i]).find('.featured-item-card-group').matchHeight();
        }
    });
})();


// ----------------------------------------------------------------------
// Floating Component
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {
        var target = $('.floating-component-group');
        var mobileWidth = window.matchMedia("(max-width: 992px)").matches;
        var desktopWidth = window.matchMedia("(min-width: 992px)").matches;

        $(window).scroll(function () {
            var winPos = $(window).scrollTop();
            var pageHalfPos = $(window).height() / 2;
            if (winPos > pageHalfPos && sessionStorage.closeFloatingComponent == undefined) {
                $(target).fadeIn('slow');
            } else {
                $(target).fadeOut('slow');
            }
        });

        $('.floating-component-group .close, .floating-component-group .btn').on('click', function (e) {
            //e.preventDefault();
            $(this).closest(target).hide();
            sessionStorage.setItem("closeFloatingComponent", 'true');
        });

    });
})();




// ----------------------------------------------------------------------
// Carousel Home Banner
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {

        $(".carousel-home-banner").slick();


        function detectVideo() {
            //theVideo.play();
        }
        if ($('.hero-banner-wrapper video').length) detectVideo();



        // On swipe event
        // $(".carousel-home-banner").on('swipe', function(event, slick, direction){
        //    console.log(direction);
        //    // left
        // });

        // On edge hit
        // $(".carousel-home-banner").on('edge', function(event, slick, direction){
        //   console.log('edge was hit')
        // });

        // On before slide change
        $(".carousel-home-banner").on('beforeChange', function (event, slick, currentSlide, nextSlide) {

            if ($('.hero-banner-wrapper video').length) detectVideo();

            // var currSlide = currentSlide;
            // var $currItem = $(".carousel-home-banner [data-slick-index="+currSlide+"]");
            // //var $currActive = $(".carousel-home-banner .slick-active");
            // var animation = $currItem.find('h2').attr("data-slick-animation");

            // //$currActive.find('[data-slick-animation]')
            // console.log(animation);
            // console.log($currItem);
            // console.log($currActive);
            // console.log(currentSlide);
            // console.log(nextSlide);
        });

    });
})();


// ----------------------------------------------------------------------
// Carousel Feature Card
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {
        if ($(".carousel-feature-card").length > 0) {
            $(".carousel-feature-card").slick();
        }
    });
})();


// ----------------------------------------------------------------------
// Header Scroll Function
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {

        var lastScrollY = 0;
        var $floater = $('.header .wrapper');

        $(window).scroll(scrollStatus);

        function scrollStatus() {

            var currScroll = $(this).scrollTop();

            //scroll 
            //scroll down && scroll down more than 100px && activate when 768px and higher
            //if ( (currScroll > lastScrollY) && (currScroll > 100) && (viewport().width >= 768) )
            if ((currScroll > lastScrollY) && (currScroll >= 100)) {
                //scroll down 
                //console.log('down');
                $floater.removeClass('notransition');
                $floater.removeClass('scroll-up').removeClass('desktop-mobile-slide-down');
                $floater.addClass('scroll-down').addClass('desktop-mobile-slide-up');

            } else if ((currScroll < lastScrollY)) {
                //scroll up
                //console.log('up');
                $floater.removeClass('notransition');
                if (currScroll < 100) {
                    $floater.addClass('notransition');
                }

                $floater.removeClass('scroll-down').removeClass('desktop-mobile-slide-up');
                $floater.addClass('scroll-up').addClass('desktop-mobile-slide-down');


            }

            lastScrollY = currScroll;
            //end scroll 

        }


        // html, body {
        //     overflow: hidden;
        // }





    });
})();

// ----------------------------------------------------------------------
// Mega Menu Hover
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {

        function megadropdownfc() {

            //public
            var $all = $(".mega-menu-wrapper .nav-dropdown-wrapper");
            var i = 0;




            $(".mega-menu-wrapper .nav-dropdown-wrapper").on("mouseenter", function () {

                var $this = $(this);


                $this.addClass('open').find($('.mega-dropdown-menu')).css({
                    "z-index": calc()
                });

                //provide a decent scrollbar when mega menu out of screen
                //setTimeout(delay, 1000);



                /*
                if( $all.hasClass('open') ){
                    setTimeout(delay, 1000);
                    console.log('exist');
                }else{
                    $this.addClass('open').find($('.mega-dropdown-menu')).css({"z-index": calc() });
                    console.log('none');
                }
                */
                function calc() {
                    i++;
                    return i;
                }

                function delay() {
                    //$this.addClass('open').find($('.mega-dropdown-menu')).css({"z-index": calc() });
                    $('.mega-dropdown-menu').css({
                        "overflow": 'auto'
                    });
                }


            }).on("mouseleave", function () {

                var $this = $(this);


                $all.removeClass('open');

                //clear all inline style
                // setTimeout(delayClear, 1000);

                //$('.mega-dropdown-menu').css({"overflow": 'hidden'}); 

                function delayClear() {
                    $('.mega-dropdown-menu').attr('style', '');
                }

            });

        }


        if ($(".mega-menu-wrapper .nav-dropdown-wrapper").length) megadropdownfc();


    });
})();

// ----------------------------------------------------------------------
// Floater with share slide
// ----------------------------------------------------------------------
(function () {
    "use strict";

    $(document).ready(function () {

        function shareExpander() {


            var $floatTitleShare = $('.float-title-share');
            var $shareIcon = $('.share-icon');
            var $closeShare = $('.close-share');
            var $slideContent = $('.slide-content');

            var $floatTitleSharePlaceholder = $('.float-title-share-placeholder');
            //var $floatTitleSharePlaceholderYpos = $floatTitleSharePlaceholder.position().top;

            $floatTitleShare.removeClass('hidden');

            //console.log($floatTitleSharePlaceholderYpos);

            $shareIcon.on('click', function () {
                $slideContent.addClass('active');
            });

            $closeShare.on('click', function () {
                $slideContent.removeClass('active');
            });


            var trigger = new Waypoint({
                element: $floatTitleSharePlaceholder,
                handler: function (direction) {
                    //console.log(direction);

                    //if down (scroll beyond placeholder)
                    if (direction === "down") {
                        $floatTitleShare.addClass('floater-slide-in');
                    }

                    //if down (scroll above placeholder)
                    if (direction === "up") {
                        $floatTitleShare.removeClass('floater-slide-in');
                    }


                }
            });


        }

        //only show when floater (in template) and placeholder (in content) exist.
        if ($('.float-title-share').length && $('.float-title-share-placeholder').length) shareExpander();

    });

})();

// ----------------------------------------------------------------------
// Filter content (mobile filter menu with slide in & out)
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {


        var $fmc = $('.filter-mobile-content');
        var $child = $fmc.children();

        $(".filter-icon").on("click", function () {


            $fmc.removeClass('hidden');


            $fmc.addClass('animateIn');
            $child.addClass('animateIn');


            var mobileExpandStateMonitor = setInterval(function () {
                //if hidden show scrollbar
                if ($fmc.hasClass('hidden')) {

                    $('html, body').removeClass('disable-scroll');
                    //$('html, body').css({ "overflow":"visible" });


                } else {
                    //else hide scrollbar
                    $('html, body').addClass('disable-scroll');
                    //$('html, body').css({ "overflow":"hidden" });

                }
            }, 50);

            //stop after running for 1 sec, prevent running in background
            setTimeout(function () {
                clearInterval(mobileExpandStateMonitor);
            }, 2000);


        });

        $(".close-filter-mobile-content").on("click", function () {


            $fmc.removeClass('animateIn');
            $child.removeClass('animateIn');

            $fmc.addClass('animateOut');
            $child.addClass('animateOut');

            //reset to normal
            setTimeout(function () {
                $fmc.addClass('hidden');
                $fmc.removeClass('animateIn').removeClass('animateOut');
                $child.removeClass('animateIn').removeClass('animateOut');
            }, 800);


            var mobileExpandStateMonitor = setInterval(function () {
                //if hidden show scrollbar
                if ($fmc.hasClass('hidden')) {

                    $('html, body').removeClass('disable-scroll');
                    //$('html, body').css({ "overflow":"visible" });


                } else {
                    //else hide scrollbar
                    $('html, body').addClass('disable-scroll');
                    //$('html, body').css({ "overflow":"hidden" });

                }
            }, 50);

            //stop after running for 1 sec, prevent running in background
            setTimeout(function () {
                clearInterval(mobileExpandStateMonitor);
            }, 2000);

        });


        $('[type="checkbox"]').each(iterate); //run once when page ready

        //everytime when click on any of the checkbox
        $(document).on("click", ".container-checkbox.mobile", function () {
            //iterate through 'all' instead of one just to be save
            $('.container-checkbox.mobile input[type="checkbox"]').each(iterate);
        });



        function iterate(index, value) {
            //console.log(index);
            //console.log(this);
            //console.log(value);
            var $ele = $(this);
            var $parent = $ele.parent();

            //limit to mobile checkbox only
            if ($parent.hasClass('mobile')) {

                //if check
                if ($ele.is(':checked')) {
                    $parent.addClass('checked');
                }

                //if not check
                if (!$ele.is(':checked')) {
                    $parent.removeClass('checked');
                }

            }



        }




    });
})();



// ----------------------------------------------------------------------
// Hamburger menu
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {

        $(".header .hamburger").on("click", function () {
            var $hamburgerContent = $('.hamburger-content');


            if ($hamburgerContent.hasClass('hidden')) {
                $hamburgerContent.removeClass('hidden');
                $hamburgerContent.css("left", "800px");
                $hamburgerContent.animate({
                    left: "0px"
                }, 800);
            } else {

                $hamburgerContent.animate({
                    left: "800px"

                }, 1000, function () {
                    $hamburgerContent.addClass('hidden');
                });

            }

            defaultScrollUpdate(".hamburger-content");

        });

    });

})();


// ----------------------------------------------------------------------
// Mobile navigation link to function (level 1, 2 & 3)
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {

        //when click on [data-link-to] urls
        $("[data-link-to]").on("click", function () {

            var $this = $(this);

            var $allSections = $('.hamburger-content>.section');

            var $thisAttrVal = $this.attr('data-link-to');
            var $thisAttrValReplace = $this.attr('data-link-to').replace(/\s/g, '');

            var $allContents = $("[data-link-to-content]");
            //var allContentsAttrValue = $allContents.attr('data-link-to-content');

            function getAllAttrValue() {

                var arr = [];

                $allContents.each(function (index) {
                    //console.log( index + ": " + $( this ).text() );
                    var $eachContent = $(this);
                    var eachContentAttrValue = $eachContent.attr('data-link-to-content').replace(/\s/g, '');
                    //console.log(eachContentAttrValue);

                    arr.push(eachContentAttrValue);
                });

                return arr;
            }


            if ($.inArray($thisAttrValReplace, getAllAttrValue()) !== -1) {
                console.log(getAllAttrValue());
                console.log($thisAttrValReplace);

                //hide/reset all levels
                $allSections.addClass('hidden');

                //show content
                $('.hamburger-content>.section[data-link-to-content="' + $thisAttrVal + '"]').removeClass('hidden');
            }

            //console.log(compareAttrValue.eachContentAttrValue);

            //console.log("This attr: " + $thisAttrVal);
            //console.log("All attr: " + $allAttrVal);

            //if($thisAttrVal === $allContentsAttrValue){
            //console.log('match');
            //$allSections.addClass('hidden');
            //$("[data-link-to]").removeClass('hidden');
            //console.log($this);
            //}

            //console.log($thisAttrVal);
        });


        //when click on [data-link-back-to] urls
        $("[data-link-back-to]").on("click", function () {

            console.log('link back to');

            var $this = $(this);

            var $allSections = $('.hamburger-content>.section');


            //if has parent associated, revert to level 2, level 3 ......
            if ($this.attr('data-parent')) {

                var $thisParentAttrVal = $this.attr('data-parent');

                //hide/reset all levels
                $allSections.addClass('hidden');

                //show content
                $('.hamburger-content>.section[data-link-to-content="' + $thisParentAttrVal + '"]').removeClass('hidden');

                //scroll to top
                $('.hamburger-content > *').scrollTop(0);

            } else
            //else all revert back to level 1
            {

                //hide/reset all levels
                $allSections.addClass('hidden');

                //show content
                $('.hamburger-content>.section.level-1').removeClass('hidden');

                //scroll to top
                $('.hamburger-content > *').scrollTop(0);

            }


        });


        //when click on [data-link-back-to] urls
        // $("[data-link-back-to-level-2]").on( "click",  function() { 
        //     console.log('click');
        //     var $this = $(this);

        //     var $allSections = $('.hamburger-content>.section');
        //     var $allSectionsLevel2 = $('.hamburger-content>.section.level-2');
        //     var $allSectionsLevel2AttrVal = $allSectionsLevel2.attr('data-link-back-to-level-2');


        //     if ($('.level-2').index($allSectionsLevel2AttrVal) >= 0){
        //         console.log('yes');
        //     }

        //     //var $thisAttrVal = $this.attr('data-link-back-to');

        //      //hide/reset all levels
        //      $allSections.addClass('hidden');

        //      //show content
        //     $('.hamburger-content>.section.level-2').removeClass('hidden');

        //     //scroll to top
        //     $('.hamburger-content > *').scrollTop(0);


        // });





    });
})();


// ----------------------------------------------------------------------
// Header dropdown
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {
        $(".header-side-nav .nav-dropdown-wrapper").hover(
            function () {
                $('.dropdown-show', this).not('.in dropdown-show').stop(true, true).slideDown("400");
                $(this).toggleClass('open');
            },
            function () {
                $('.dropdown-show', this).not('.in dropdown-show').stop(true, true).slideUp("400");
                $(this).toggleClass('open');
            }
        );
    });
})();

// ----------------------------------------------------------------------
// Hover (for megamenu dropdown)
// ----------------------------------------------------------------------
// (function(){
//     "use strict";
//     $(document).ready(function(){

//             $('.product-mega').hover(
//                 function(){ 
//                     //var getCurrent = $(this).attr("id");
//                     //console.log(getCurrent);
//                     $('#product-btn a').addClass('arrow-up');
//                 }, 
//                 function(){ 
//                     $('#product-btn a').removeClass('arrow-up')
//                  }
//             )

//             $('.solution-mega').hover(
//                 function(){ $('#solution-btn a').addClass('arrow-up');}, 
//                 function(){ $('#solution-btn a').removeClass('arrow-up') }
//             )
//     });
// })();


// ----------------------------------------------------------------------
// Scroll reveal
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {
        //  This data attribute will do the magic, data-scroll-reveal="wait 0.2s, then enter over 500ms after 0.3s"
        if (!(/msie [6|7|8|9]/i.test(navigator.userAgent)) && $('[data-scroll-reveal]').length > 0) {
            (function () {
                window.scrollReveal = new scrollReveal({
                    reset: false
                });
            })();
        };
    });
})();

// ----------------------------------------------------------------------
// Search pop up
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {
        $('body').on("click", '[data-search-pop-up]', function () {

            var $searchPopUp = $('.search-pop-up');

            if ($searchPopUp.hasClass('hidden')) {

                $searchPopUp.removeClass('hidden');
                setTimeout(function () {
                    $searchPopUp.addClass('active');
                    AutoComplete({
                        EmptyMessage: "No item found",
                        QueryArg: "text",
                        _Render: function (response) {
                            var ul;
                            if (typeof response == "string") {
                                ul = this._RenderRaw(response);
                            } else {
                                ul = this._RenderResponseItems(response);
                            }
                            if (this.DOMResults.hasChildNodes()) {
                                this.DOMResults.removeChild(this.DOMResults.childNodes[0]);
                            }

                            this.DOMResults.appendChild(ul);

                            for (var i = 0; i < $('[data-autocomplete-value]').length; i++) {
                                if ($($('[data-autocomplete-value]')[i]).find('strong').length == 0) {
                                    $($('[data-autocomplete-value]')[i]).hide();
                                }
                            }
                        }
                    }, "#header-search-input");
                }, 50);

                defaultScrollUpdate(".search-pop-up");

            }

        });

        $('.search-pop-up .close-pop-up>*').on("click", function () {

            var $searchPopUp = $('.search-pop-up');

            $searchPopUp.removeClass('active')
            setTimeout(function () {
                $searchPopUp.addClass('hidden');
            }, 600);

            defaultScrollUpdate(".search-pop-up");

        });

    });
})();


// ----------------------------------------------------------------------
// countries pop up
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {
        $('body').on("click", '[data-countries-pop-up]', function (e) {

            e.preventDefault();

            var $searchPopUp = $('.countries-pop-up');

            if ($searchPopUp.hasClass('hidden')) {

                $searchPopUp.removeClass('hidden');
                setTimeout(function () {
                    $searchPopUp.addClass('active')
                }, 50);

            }


            defaultScrollUpdate(".countries-pop-up");

        });

        $('.countries-pop-up .close-pop-up>*').on("click", function () {

            var $searchPopUp = $('.countries-pop-up');

            $searchPopUp.removeClass('active')
            setTimeout(function () {
                $searchPopUp.addClass('hidden');
            }, 600);

            defaultScrollUpdate(".countries-pop-up");

        });

    });
})();

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
                setTimeout(function () {
                    $stickyIconsExpander.addClass('active')
                }, 50);
            } else {
                $stickyIconsExpander.removeClass('active')
                setTimeout(function () {
                    $stickyIconsExpander.addClass('hidden');
                }, 600);
            }

            defaultScrollUpdate(".sticky-icons-expander");

        });

    })

})();


// ----------------------------------------------------------------------
// Custom dropdown submission
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {

        $(document).on("click", '.dp-form .dp-list a', function () {

            var dataVal = $(this).data("val");
            var dataHtml = $(this).html();
            var $dpText = $(this).parent().parent().prev().find($('.dp-text'));
            var $hiddenField = $dpText.next();

            $dpText.html(dataHtml);
            $hiddenField.val(dataVal);

            //console.log(dataVal);
            // console.log($(this).parent().parent().prev().find($('.dp-text')));

        });
    });
})();


// ----------------------------------------------------------------------
// Default movile dropdown submission (syn with desktop)
// ----------------------------------------------------------------------
(function () {
    "use strict";

    $(document).ready(function () {

        $(document).on("change", '.dp-form-mobile select', function () {
            $(this).each(function () {
                if ($(this).val()) {
                    $(this).parent().prev().find($("input[type=hidden]")).val($(this).html());
                }
            });
        });

        // $('.dp-form-mobile select').change(unniversalSelectSync);

        // function unniversalSelectSync() {
        //     $(this).each(function () {
        //         if ($(this).val()) {
        //             $(this).parent().prev().find($("input[type=hidden]")).val($(this).html());
        //         }
        //     });

        // } 



        // $('.dp-form-mobile').on( "click", '.dp-list a', function() {


        //     var dataVal = $(this).data("val");
        //     var $dpText = $(this).parent().parent().prev().find($('.dp-text'));
        //     var $hiddenField = $dpText.next(); 

        //     $dpText.html(dataVal);
        //     $hiddenField.val(dataVal);


        // });



    });
})();




// ----------------------------------------------------------------------
// Sticky tool
// ----------------------------------------------------------------------

$(window).on('load', function () {

    function makeSticky() {

        var stickySidebar = new StickySidebar('.make-sticky', {
            topSpacing: 105,
            bottomSpacing: 0,
            containerSelector: '.make-sticky_limit',
            innerWrapperSelector: '.make-sticky_inner',
            resizeSensor: true,
            stickyClass: 'is-affixed',
            minWidth: 0
        });

        //console.log('onload');

    }


    if ($('.make-sticky').length) {

        setTimeout(function () {
            makeSticky();
        }, 100); //for stupid cacat IE browser

    }
});


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
                    $('.product-menu-desktop').delay(100).css({
                        'visibility': 'hidden'
                    });
                } else {
                    $('.product-menu-sticky').slideUp(10).fadeOut(10);
                    $('.product-menu-desktop').css({
                        'visibility': 'visible'
                    });
                }

                if ($(this).scrollTop() > posSubEnd) {
                    $('.product-menu-sticky').addClass("hidden");
                } else {
                    $('.product-menu-sticky').removeClass("hidden");
                }

                if ($(this).scrollTop() == posEqual) {
                    $('.product-menu-sticky').fadeOut(10);
                    $('.product-menu-desktop').css({
                        'visibility': 'visible'
                    });
                }

            });
        }


    });
})();


// ----------------------------------------------------------------------
// Product-Sub Menu scroll [KY]
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {

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
                    } else {
                        currLink.removeClass("arrow-up");
                    }
                });
            }

        }
        if ($('[data-product-menu=start]').length) detectSection();



    });
})();




// ----------------------------------------------------------------------
// Shaftliner Pop up content [not in use currently]
// ----------------------------------------------------------------------
// (function(){
//     "use strict";
//     $(document).ready(function(){
//       //change image and content dynamic

//       var img_change 
//         var img_change2 
//         var title_shaft
//         var desc_shaft  

//         function  changeContent(){
//             $('div[data-content="content-img1"] img').attr("src","images/"+img_change+ "");
//             $('div[data-content="content-img2"] img').attr("src","images/"+img_change2+ "");
//             $('div[data-content="content-text"] h2').text(title_shaft);
//             $('div[data-content="content-text"] p').text(desc_shaft);
//         }

//         //hide div first
//         $("div[data-shaft-content]").hide();

//         //close btn
//         $( "a[data-content='close-btn']" ).on( "click", function() {
//             $("div[data-shaft-content]").slideUp();
//         });


//         // click to show content
//         $( "div[data-shaft]" ).on( "click", function() {

//             var getShaft = $( this ).attr( "data-shaft" );
//             $("div[data-shaft-content]").slideDown();


//             if(getShaft == "content-1")
//             {
//                 img_change = "bg-banner-tile.jpg";
//                 img_change2 = "bg-banner-tile.jpg";
//                 title_shaft = "Shaftliner1 lorem ipsum";
//                 desc_shaft  = "Lorem ipsum dolor sit amet, consectetuer. Lorem ipsum dolor sit amet, consectetuer. Lorem ipsum dolor sit amet, consectetuer dolor sit amet, consectetuer one";       
//             }
//             if(getShaft == "content-2")
//             {
//                 img_change = "bg-banner-tile-2.jpg";
//                 img_change2 = "bg-banner-tile-2.jpg";
//                 title_shaft = "Shaftliner2 lorem ipsum";
//                 desc_shaft  = "Lorem ipsum dolor sit amet, consectetuer. Lorem ipsum dolor sit amet, consectetuer. Lorem ipsum dolor sit amet, consectetuer dolor sit amet, consectetuer two";
//             }
//             if(getShaft == "content-3")
//             {
//                 img_change = "bg-banner-tile-3.jpg";
//                 img_change2 = "bg-banner-tile-3.jpg";
//                 title_shaft = "Shaftliner3 lorem ipsum";
//                 desc_shaft  = "Lorem ipsum dolor sit amet, consectetuer. Lorem ipsum dolor sit amet, consectetuer. Lorem ipsum dolor sit amet, consectetuer dolor sit amet, consectetuer three";
//             }
//             if(getShaft == "content-4")
//             {
//                 img_change = "bg-banner-tile.jpg";
//                 img_change2 = "bg-banner-tile-2.jpg";
//                 title_shaft = "Shaftliner4 lorem ipsum";
//                 desc_shaft  = "Lorem ipsum dolor sit amet, consectetuer. Lorem ipsum dolor sit amet, consectetuer. Lorem ipsum dolor sit amet, consectetuer dolor sit amet, consectetuer 4";
//             }
//             if(getShaft == "content-5")
//             {
//                 img_change = "bg-banner-tile-2.jpg";
//                 img_change2 = "bg-banner-tile-3.jpg";
//                 title_shaft = "Shaftliner5 lorem ipsum";
//                 desc_shaft  = "Lorem ipsum dolor sit amet, consectetuer. Lorem ipsum dolor sit amet, consectetuer. Lorem ipsum dolor sit amet, consectetuer dolor sit amet, consectetuer 5";
//             }

//             changeContent();
//         });


//     });
// })();


// ----------------------------------------------------------------------
// Equal Height for contact us and thank you page
// ----------------------------------------------------------------------


// (function(){
//     "use strict";
//     $(document).ready(function(){
//         if($(window).width() > 767){
//             var highestBox = 0;
//            $('.equal-height').each(function(){  
//                if($(this).height() > highestBox){  
//                    highestBox = $(this).height();  
//                }
//            });    
//            $('.equal-height').height(highestBox);
//        }else{
//            var highestBox = 0;
//            $('.equal-height').each(function(){  
//                if($(this).height() > highestBox){  
//                    highestBox = $(this).height();  
//                }
//            });    
//        }
//     });
// })();




// ----------------------------------------------------------------------
// Match Height
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {
        $('[data-img-height="div-height"]').matchHeight();
        $('[data-img-height="img-height"]').matchHeight();
        //$('.gallery-listing-search-content .box-type-2 ').matchHeight();
        $('.listing-search-content .each .custom-block').matchHeight();
    });
})();


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
            // videoType();

            $(".mix-media-gallery-wrapper .slick-arrow").click(function () {
                //   videoType();
                console.log("test1")
                $(".modal-video-elem").get(0).pause();
                if ($(".mix-media-gallery-wrapper .slick-slide .video #ranID").hasClass('YouTubeVideoPlayer')) {
                    $('.YouTubeVideoPlayer')[0].contentWindow.postMessage('{"event":"command","func":"' + 'stopVideo' + '","args":""}', '*');
                }
            });

            function videoType() {
                if ($(".mix-media-gallery-wrapper .slick-current .video").prev().hasClass('modal-video-elem')) {
                    $(".modal-video-elem").get(0).pause();
                }
                if ($(".mix-media-gallery-wrapper .slick-current .video").prev().hasClass('YouTubeVideoPlayer')) {
                    $('.YouTubeVideoPlayer')[0].contentWindow.postMessage('{"event":"command","func":"' + 'stopVideo' + '","args":""}', '*');
                }
            }
        }
        ///  MIX MEDIA  GALLERY///

    });
})();


// ----------------------------------------------------------------------
// Compare Product Page 
// ----------------------------------------------------------------------
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {
        function productCompare() {

            var url_string = window.location.href;
            var url = new URL(url_string);
            var c = url.searchParams.get("category");
            var cc = url.searchParams.get("countrycode");

            var data;
            var getJSONarr = [];
            var currOnStageMainResultData = [];
            var currCategoryKey = c + "_" + cc;


            $(".default-col").hide();

            getLocalCompare();

            function getLocalCompare() {
                if (localStorage.getItem(currCategoryKey) === null) {
                    //console.log("localempty")
                    $(".default-col").show();
                } else {
                    $(".default-col").hide();


                    //get from local storage
                    data = JSON.parse(localStorage.getItem(currCategoryKey));
                    //console.log("data1-", data);

                    getJSONarr = data.map(function (getJSONarr) {
                        return getJSONarr.url.split(".").reverse()[1] + ".properties_v3.json";
                    });


                    if (data.length < 1) {
                        //console.log("nomore");
                        localStorage.removeItem(currCategoryKey);
                        $(".compare-product-col").remove();
                        $(".default-col").show();

                    }
                    loadLocalCompare();
                }
            }


            function loadLocalCompare() {
                //data = JSON.parse(localStorage.getItem('ceiling')) || [];
                // getJSONarr = data.map(function (getJSONarr) {
                // return getJSONarr.url.split(".").reverse()[1] + ".json";
                //  });

                //after ajax get all data in array, run pagination
                getAll(getJSONarr).done(function (results) {

                    currOnStageMainResultData = results;

                    if (currOnStageMainResultData.length > 0) {
                        //load the compare
                        var compareContent = $('#compare-product-col').html();
                        var temptcompareContent = Handlebars.compile(compareContent);
                        $('.compare-product-col').html(temptcompareContent(results));

                    }

                    switch (currOnStageMainResultData.length) {
                        case 1:
                            $(".compare-one").hide();
                            break;
                        case 2:
                            $(".compare-one , .compare-two").hide();
                            break;
                        case 3:
                            $(".compare-one , .compare-two, .compare-three").hide();
                            break;
                    }


                    $('[data-div-height="physicalProperties"]').matchHeight();
                    $('[data-div-height="featureAndBenefits"]').matchHeight();
                    $('[data-div-height="applications"]').matchHeight();
                    $('[data-div-height="resourceList"]').matchHeight();
                    $('[data-div-height="sustainability"]').matchHeight();
                    $('[data-div-height="wrapper"]').matchHeight();

                });


                // iterate thru all  json list
                function getAll(requests) {
                    var count = requests.length;
                    var results = [];
                    var deferreds = [];
                    var all_done = $.Deferred();

                    for (var i = 0; i < requests.length; i++) {
                        var deferred = $.ajax({
                                url: requests[i],
                                type: "GET",
                                cache: false,
                            })
                            .done(function (data) {
                                results.push(data);

                            })
                            .always(function () {
                                count--;

                                if (count === 0) {
                                    all_done.resolve(results);
                                }
                            });
                        deferreds.push(deferred);
                    }
                    return all_done.promise();
                }

            }


            // close btn on popup to remove content
            $("body").on("click", ".close-btn-box", function () {
                event.preventDefault();

                var btnURL = $(this).closest("button").attr("href");

                // var getIndex1 = data.findIndex(x => x.url == btnURL);
                var getIndex1 = data.findIndex(function (x) {
                    return x.url == btnURL;
                });
                data.splice(getIndex1, 1);
                //console.log("btn-", btnURL);

                localStorage.setItem(currCategoryKey, JSON.stringify(data));
                getLocalCompare();

            });

        }
        if ($('.product-compare-content').length) productCompare();


    });
})();


// ----------------------------------------------------------------------
// Checkbox  (product details)
// ----------------------------------------------------------------------
// (function () {
//     "use strict";
//     $(document).ready(function () {

//         var currCategoryKey;
//         var countryCode;
//         var getLink;
//         var checkStatus;
//         var storeData = [];
//         var detailsData;

//         var tempURL = "/etc/designs/usgb_v3/clientlib/uiux-all-js/js/json/product-compare-one.html";

//         //get browser url
//         var pathname = window.location.pathname;
//         var pagename = pathname.split(".").reverse()[1] + ".json";
//         console.log('pagename - ', pagename)
//         //pass url to load json
//         var detailsJSON

//         function productCompareDetailPage() {

//             detailsJSON = tempURL.split(".").reverse()[1] + ".json";
//             getJSONDetails();

//             $(document).on("change", '[data-input-value]', function () {

//                 event.preventDefault();
//                 getLink = $(this).data("input-value");

//                 if ($(this).is(':checked')) {
//                     console.log("check")
//                     checkStatus = $(this).prop("checked");
//                     var groupElem = {
//                         "url": detailsData.pagePropertiesList[0].url,
//                         "img": detailsData.pagePropertiesList[0].image68x56,
//                         "title": detailsData.pagePropertiesList[0].pageTitle,
//                         "checked": checkStatus,
//                         "countryCode": countryCode
//                     }
//                     storeData.push(groupElem);
//                     addToDetailsToLocal();

//                 } else {
//                     console.log("uncheck")
//                     removeCompareDetails();
//                 }
//             });

//               // close btn on popup to remove content
//               $("body").on("click", ".close-btn-box", function () {
//                 event.preventDefault();
//                 var currInput = $(this).closest('.each').attr("data-content-title");
//                 $("[data-input-value ='" + currInput + "']").attr('checked', false);
//                 removeCompareDetails(currInput);
//             });



//         }
//         if ($('#compare-pop-detail-page').length) productCompareDetailPage();

//         function addToDetailsToLocal() {
//             localStorage.setItem(currCategoryKey, JSON.stringify(storeData));
//             getDetailsLocalCompare();
//         }

//         function removeCompareDetails(data) {
//             // var getIndex1 = storeData.findIndex(x => x.url == data);     
//             var getIndex1
//             storeData.some(function (x, i) {
//                 if (x.url == getLink) {
//                     getIndex1 = i;
//                     return true;
//                 }
//             });

//             //console.log("in1", getIndex1)
//             storeData.splice(getIndex1, 1);
//             addToDetailsToLocal();
//             getDetailsLocalCompare();

//         }


//         function getDetailsLocalCompare() {

//             if (localStorage.getItem(currCategoryKey) === null) {
//                 console.log("detailLocalempty")
//             } else {
//                 //get from local storage
//                 storeData = JSON.parse(localStorage.getItem(currCategoryKey));
//                 console.log("currinLocal-", storeData);
//                 loadCompare();

//                 if (storeData.length < 1) {
//                     //console.log("nomore");
//                     localStorage.removeItem(currCategoryKey);
//                 }
//             }
//         }

//         function countPopContent(){
//             var popContent = $("[data-content-title]").length;
//             console.log(popContent)
//            // var currInput1 = $(this).closest('.each').attr("data-content-title");
//             if(detailsData.pagePropertiesList[0].url = $("[data-input-value]").data('input-value')){
//              //   $("[data-input-value]").prop('checked',true);
//             }else{
//             //    $("[data-input-value]").prop('checked',false);
//             }
//         }

//         function loadCompare() {
//             //load the popup
//             var comparePopupHTML = $('#compare-popup-local').html();
//             var temptcomparePopupHTML = Handlebars.compile(comparePopupHTML);
//             $('.compare-popup-local').html(temptcomparePopupHTML(storeData));
//             $(".compare-popup").show();

//             countPopContent();
//         }



//         function getJSONDetails() {
//             console.log('de', detailsJSON)
//             $.ajax({
//                 url: detailsJSON,
//                 type: "GET",
//                 cache: false,
//                 success: function (response) {
//                     detailsData = response;
//                     // console.log(detailsData)
//                     countryCode = detailsData.country_key;
//                     currCategoryKey = detailsData.category_key + "_" + countryCode;
//                     console.log(currCategoryKey);

//                     // var groupElem = {
//                     //     "url": detailsData.pagePropertiesList[0].url,
//                     //     "img": detailsData.pagePropertiesList[0].image68x56,
//                     //     "title": detailsData.pagePropertiesList[0].pageTitle,
//                     //     "checked": checkStatus,
//                     //     "countryCode": countryCode
//                     // }
//                     // storeData.push(groupElem);
//                     // console.log("dStore", storeData)
//                     //localStorage.setItem(currCategoryKey, JSON.stringify(storeData));
//                     getDetailsLocalCompare();

//                 },
//                 beforeSend: function () {},
//                 complete: function () {}
//             });

//         }

//     });
// })();

// ----------------------------------------------------------------------
// Checkbox Count /filtering (product listing)
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {


        function productListingFeatures() {


            // readjust sticky side bar
            var stickySidebar = new StickySidebar('.make-sticky', {
                topSpacing: 105,
                bottomSpacing: 0,
                containerSelector: '.make-sticky_limit',
                innerWrapperSelector: '.make-sticky_inner',
                resizeSensor: true,
                stickyClass: 'is-affixed',
                minWidth: 0
            });


            $(document).on("scroll", alignSticky);

            function alignSticky(event) {
                var scrollPos = $(document).scrollTop();
                // console.log(scrollPos)
                if (scrollPos <= 500 || scrollPos < 800) {
                    stickySidebar.updateSticky();
                }
                if (scrollPos > 900) {
                    stickySidebar.destroy();
                }

            }



            //////COMPARE POPUP start//////
            $(".compare-popup").hide()
            $(".popup-content .content-wrapper").hide();
            $(".compare-popup .btn-compare").hide();
            $(".compare-popup .instruction-text").hide();
            $(".compare-popup .btn-menu-down").hide();

            var getCard;
            var inputVal;
            var numberOfChecked;
            var categoryName; //key name in localStorage
            var countryCode;
            var storeData = [];
            var getIMG;
            var getTitle;
            var getLink;
            var checkStatus;
            var groupElem;
            var findElem;
            var currInput;
            var storePopup; //localstorage arr
            var countDown;


            //populate comparePopup
            function getElement() {

                //match getLink from checkbox to find array info
                var findElem = findElement(currProductData, "link", getLink);

                function findElement(currProductData, propName, propValue) {

                    for (var i = 0; i < currProductData.length; i++)
                        if (currProductData[i][propName] == propValue)
                            return currProductData[i];
                }
                //pass json details
                inputVal = findElem.link;
                getIMG = findElem.img;
                getTitle = findElem.title;
            }


            // detect change for cardcheckbox
            $(document).on("change", '.listing-search-content .container-checkbox input[type="checkbox"]', function () {

                event.preventDefault();
                clearTimeout(countDown);
                getCard = $(this);
                getLink = $(this).closest(".each").attr("href");
                getElement();
                checkStatus = $(this).closest(".each").find(".container-checkbox input").prop("checked");

                //pass to  arr to compare popup to append
                groupElem = {
                    "url": inputVal,
                    "img": getIMG,
                    "title": getTitle,
                    "checked": checkStatus,
                    "countryCode": countryCode
                }

                if ($(this).is(':checked')) {


                    storeData.push(groupElem);
                    storeCompareLocalStorage();
                    //retrieveCompare();
                    if ($(".compare-popup .title").hasClass("selected")) {
                        $(".compare-popup .title").removeClass("selected");
                    }

                } else {

                    var dataUncheck = $('[data-content-title="' + getLink + '"]');
                    dataUncheck.closest(".each").animate({
                        left: "100px",
                        opacity: 0
                    }, 900);
                    removeCompareLocalStorage(inputVal);
                }
            });


            //hide show popup
            $("body").on("click", ".compare-popup .title", function () {
                minimisePopup();
                clearTimeout(countDown);
                if ($(".compare-popup .title").hasClass("selected")) {
                    console.log("click true")
                } else {
                    console.log("click false")
                    countDownPop();
                }
            });

            function checkNumberOfCheckbox() {
                //numberOfChecked = $('.listing-search-content div input:checkbox:checked').length;
                numberOfChecked = storeData.length;

                if (numberOfChecked >= 3) {
                    //disbled all checks
                    $('.listing-search-content .container-checkbox input[type="checkbox"]').prop('disabled', true);
                    //enable only checked items to be uncheck
                    $('.listing-search-content .container-checkbox input:checkbox:checked').prop('disabled', false);
                } else {
                    $('.listing-search-content .container-checkbox input[type="checkbox"]').prop('disabled', false);
                }

                if (numberOfChecked < 1) {
                    localStorage.removeItem(categoryName);
                }


                if (numberOfChecked > 1) {
                    $(".compare-popup .instruction-text").fadeOut(10);
                    $(".compare-popup .popup-content .each").fadeIn(1000);
                    $(".compare-popup .btn-compare").fadeIn(800);

                } else {
                    $(".compare-popup .btn-compare").fadeOut(10);
                    $(".compare-popup .instruction-text").fadeIn(800);
                }

                if (numberOfChecked > 0) {
                    $(".compare-popup").fadeIn(800);
                    $(".compare-popup .btn-menu-down").fadeIn(800);
                    $(".popup-content").fadeIn(800);
                } else {
                    $(".compare-popup .instruction-text").fadeOut(10);
                    $(".compare-popup").fadeOut(800);
                    $(".popup-content").fadeOut(10);
                    $(".compare-popup .btn-menu-down").fadeOut(10);
                    $(".compare-popup .btn-compare").fadeOut(10);
                }

                clearTimeout(countDown);
                checkPopupState();
                //countDownPop();
            }


            // close btn on popup to remove content
            $("body").on("click", ".close-btn-box", function () {
                event.preventDefault();
                currInput = $(this).closest('.each').attr("data-content-title");
                $("[data-input-value ='" + currInput + "']").prop('checked', false);
                $(this).closest('.each').animate({
                    left: "100px",
                    opacity: 0
                }, 600);
                removeCompareLocalStorage(currInput);
            });



            function storeCompareLocalStorage() {
                //console.log("stored", storeData)
                // Put the object into storage
                localStorage.setItem(categoryName, JSON.stringify(storeData));
                //console.log("storeINput", inputVal)
                retrieveCompare();
            }


            function removeCompareLocalStorage(data) {
                // var getIndex1 = storeData.findIndex(x => x.url == data);     
                var getIndex1
                storeData.some(function (x, i) {
                    if (x.url == data) {
                        getIndex1 = i;
                        return true;
                    }
                });

                setTimeout(function () {
                    storeData.splice(getIndex1, 1);
                    storeCompareLocalStorage();
                    $("[data-input-value ='" + currInput + "']").attr('checked', false);
                }, 500);

            }



            function retrieveCompare() {

                if (localStorage.getItem(categoryName) === null) {
                    console.log("localempty")
                } else {

                    // retrieve it (Or create a blank array if there isn't any info saved yet),
                    storeData = JSON.parse(localStorage.getItem(categoryName)) || [];

                    // Retrieve the object from storage
                    // storePopup = JSON.parse(localStorage.getItem(categoryName))
                    //storeData = JSON.parse(localStorage.getItem(categoryName))
                    console.log('storePopupAA-', storeData.length)





                    //load the popup
                    var comparePopupHTML = $('#compare-popup-local').html();
                    var temptcomparePopupHTML = Handlebars.compile(comparePopupHTML);
                    $('.compare-popup-local').html(temptcomparePopupHTML(storeData));




                    //target checked true and checkback the card
                    // var localNum = storeData.map(localNum => localNum.url);
                    var localNum = storeData.map(function (localNum) {
                        return localNum.url;
                    });
                    //console.log(localNum)
                    $.each(localNum, function (i, val) {
                        var selectedCard = $('[data-input-value="' + val + '"]');
                        $(selectedCard).prop("checked", true);
                        $('.listing-search-content .container-checkbox input:checkbox:checked').prop('disabled', false);
                    });


                    $(".compare-popup .popup-content .each").css("opacity", "0");
                    setTimeout(function () {
                        $(".compare-popup .popup-content .each").css({
                            "top": "-5px",
                            "opacity": "0"
                        }).animate({
                            top: "0px",
                            opacity: 1
                        }, 700);
                    }, 200);
                    checkNumberOfCheckbox();
                }
            }
            //minimise Popup if idle   
            $(".compare-popup .inner-content")
                .mouseenter(function () {
                    console.log("enterrring");
                    clearTimeout(countDown);
                    //checkPopupState();
                })
                .mouseleave(function () {
                    console.log("mouseleave");
                    clearTimeout(countDown);
                    countDownPop();
                });


            function countDownPop() {
                console.log("counting")
                countDown = setTimeout(function () {
                    minimisePopup();
                }, 5000);
            }

            function checkPopupState() {
                clearTimeout(countDown);
                if ($(".compare-popup .inner-content").is(":visible")) {
                    console.log("true")
                    countDownPop();
                } else {
                    console.log("false")
                    $(".compare-popup .inner-content").slideToggle(800);
                    $(".compare-popup .title").toggleClass("selected");
                    countDownPop();
                }
            }

            function minimisePopup() {
                $(".compare-popup .inner-content").slideToggle(900);
                $(".compare-popup .title").toggleClass("selected");
            }


            //////COMPARE POPUP END//////





            ////// REFINE SEARCH  FEATURE start //////

            ////filtering product listing////
            var productData
            var currOnStageMainResultData = [];
            var currProductData //target the results
            var shoutout



            //productListingResult();
            //load all from json first
            function productListingResult() {

                $.ajax({
                    //url: "/etc/designs/usgb_v3/clientlib/uiux-all-js/js/json/gallery-filter-one.json",
                    url: "/etc/designs/usgb_v3/clientlib/uiux-all-js/js/json/product-listing-filter.json",
                    type: "GET",
                    cache: false,
                    success: function (response) {
                        // store in global
                        productData = response
                        currProductData = productData.product_result

                        //for sorting
                        currOnStageMainResultData = productData.product_result

                        if (response.product_listing_filter.length > 0) {
                            $("#product-listing-form").removeClass("hidden");
                            //desktop
                            var productFilterHtml = $('#product-listing-filter').html();
                            var temptProductFilterHtml = Handlebars.compile(productFilterHtml);
                            $('.product-listing-filter').html(temptProductFilterHtml(productData.product_listing_filter[0]));

                            //mobile
                            $('#filter-icon-btn').addClass();
                            $('#filter-icon-btn span').removeClass('hidden');
                            var MobileProductFilterHtml = $('#m-product-listing-filter').html();
                            var MobileTemptProductFilterHtml = Handlebars.compile(MobileProductFilterHtml);
                            $('.m-product-listing-filter').html(MobileTemptProductFilterHtml(productData.product_listing_filter[0]));

                        } else {
                            $('#filter-icon-btn').removeClass();
                            $('#filter-icon-btn span').addClass('hidden');

                        }

                        //results
                        // var productFilterResult = $('#product-listing-result').html();
                        // var TemptProductFilterResult = Handlebars.compile(productFilterResult);
                        // $('.product-listing-result').html(TemptProductFilterResult(productData.product_result));

                        //checkshoutout
                        shoutout = productData.shoutout;
                        //console.log(shoutout)

                        //getCategoryName - to store array
                        categoryName = productData.category_key + "_" + productData.country_key;
                        countryCode = productData.country_key;


                        hideAllWrappers();
                        renderProductListingResult();
                        retrieveCompare();

                    },
                    beforeSend: function () {},
                    complete: function () {}
                });
            }

            function productListingResultString(data) {
                //console.log("productListingResultString in");
                productData = data
                currProductData = productData.product_result

                //for sorting
                currOnStageMainResultData = productData.product_result

                if (productData.product_listing_filter.length > 0) {
                    $("#product-listing-form").removeClass("hidden");
                    //desktop
                    var productFilterHtml = $('#product-listing-filter').html();
                    var temptProductFilterHtml = Handlebars.compile(productFilterHtml);
                    $('.product-listing-filter').html(temptProductFilterHtml(productData.product_listing_filter[0]));

                    //mobile
                    $('#filter-icon-btn').addClass();
                    $('#filter-icon-btn span').removeClass('hidden');
                    var MobileProductFilterHtml = $('#m-product-listing-filter').html();
                    var MobileTemptProductFilterHtml = Handlebars.compile(MobileProductFilterHtml);
                    $('.m-product-listing-filter').html(MobileTemptProductFilterHtml(productData.product_listing_filter[0]));

                } else {
                    $('#filter-icon-btn').removeClass();
                    $('#filter-icon-btn span').addClass('hidden');

                }

                //results
                // var productFilterResult = $('#product-listing-result').html();
                // var TemptProductFilterResult = Handlebars.compile(productFilterResult);
                // $('.product-listing-result').html(TemptProductFilterResult(productData.product_result));

                //checkshoutout
                shoutout = productData.shoutout;
                //console.log(shoutout)

                //getCategoryName - to store array
                categoryName = productData.category_key + "_" + productData.country_key;
                countryCode = productData.country_key;


                hideAllWrappers();
                renderProductListingResult();
                retrieveCompare();

            }




            function renderProductListingResult() {
                // console.log(currOnStageMainResultData)
                paginationResult(currOnStageMainResultData);
            }

            var btnSubmit
            //desktop
            $(document).on('click', '#product-listing-form button[type="submit"]', function (e) {
                e.preventDefault();
                btnSubmit = this;
                submitResults();
            });
            //mobile
            $(document).on('click', '#m-product-listing-form button[type="submit"]', function (e) {
                e.preventDefault();
                btnSubmit = this;
                submitResults();
            });

            function submitResults() {
                var form = $(btnSubmit).closest('form');
                var selectedFilterData = ($(form).serializeObject());
                var filteredData = multiFilter(productData.product_result, selectedFilterData);
                //console.log('filter', selectedFilterData);
                // console.log('filteredDataPL', filteredData);
                currOnStageMainResultData = filteredData;
                // console.log(currOnStageMainResultData)

                paginationResult(currOnStageMainResultData);

            }

            //sort by dropdown
            $(document).on('click', '.listing-search-content .search-filter-sort .dropdown-menu a', function (e) {
                switch ($(this).attr('data-val')) {
                    case "a_z":
                        currOnStageMainResultData.sort(function (a, b) {
                            if (a.title.toLowerCase() < b.title.toLowerCase()) return -1;
                            if (a.title.toLowerCase() > b.title.toLowerCase()) return 1;
                            return 0;
                        });
                        break;
                    case "z_a":
                        currOnStageMainResultData.sort(function (a, b) {
                            if (a.title.toLowerCase() > b.title.toLowerCase()) return -1;
                            if (a.title.toLowerCase() < b.title.toLowerCase()) return 1;
                            return 0;
                        });
                        break;
                    case "latest":
                        currOnStageMainResultData.sort(function (a, b) {
                            if (Number(a.created_date) > Number(b.created_date)) return -1;
                            if (Number(a.created_date) < Number(b.created_date)) return 1;
                            return 0;
                        });
                        break;
                    case "oldest":
                        currOnStageMainResultData.sort(function (a, b) {
                            if (Number(a.created_date) < Number(b.created_date)) return -1;
                            if (Number(a.created_date) > Number(b.created_date)) return 1;
                            return 0;
                        });
                        break;
                }
                paginationResult(currOnStageMainResultData);
                //  scrollTop();
            });

            $(document).on('change', '.listing-search-content .dp-form-mobile select', function (e) {
                switch ($(this).val()) {
                    case "a_z":
                        currOnStageMainResultData.sort(function (a, b) {
                            if (a.title.toLowerCase() < b.title.toLowerCase()) return -1;
                            if (a.title.toLowerCase() > b.title.toLowerCase()) return 1;
                            return 0;
                        });
                        break;
                    case "z_a":
                        currOnStageMainResultData.sort(function (a, b) {
                            if (a.title.toLowerCase() > b.title.toLowerCase()) return -1;
                            if (a.title.toLowerCase() < b.title.toLowerCase()) return 1;
                            return 0;
                        });
                        break;
                    case "latest":
                        currOnStageMainResultData.sort(function (a, b) {
                            if (Number(a.created_date) > Number(b.created_date)) return -1;
                            if (Number(a.created_date) < Number(b.created_date)) return 1;
                            return 0;
                        });
                        break;
                    case "oldest":
                        currOnStageMainResultData.sort(function (a, b) {
                            if (Number(a.created_date) < Number(b.created_date)) return -1;
                            if (Number(a.created_date) > Number(b.created_date)) return 1;
                            return 0;
                        });
                        break;
                }
                paginationResult(currOnStageMainResultData);
                // scrollTop();
            });
            //sort by end

            function multiFilter(array, filters) {
                var filterKeys = Object.keys(filters);
                // filters all elements passing the criteria
                return array.filter(function (item) {
                    // dynamically validate all filter criteria
                    return filterKeys.every(function (key) {
                        if (Array.isArray(item[key]) && item[key].length > 0) {
                            // if data is tagging array, one matches in the array, return the object true
                            for (var i = 0; i < item[key].length; i++) {
                                if (!!~filters[key].indexOf(item[key][i])) {
                                    return filters[key];
                                };
                            }
                        } else if (filters[key] == "" || filters[key] == "all") {
                            return true;
                        } else {
                            return !!~filters[key].indexOf(item[key]);
                        }
                    });
                });
            }



            function _defineProperty(obj, key, value) {
                if (key in obj) {
                    Object.defineProperty(obj, key, {
                        value: value,
                        enumerable: true,
                        configurable: true,
                        writable: true
                    });
                } else {
                    obj[key] = value;
                }
                return obj;
            }

            function paginationResult(dataForPagination) {
                stickySidebar.updateSticky();

                $('#pagination-container').pagination(_defineProperty({
                    dataSource: dataForPagination,
                    pageSize: 9,
                    callback: function callback(data, pagination) {
                        //results
                        var productFilterResult = $('#product-listing-result').html();
                        var TemptProductFilterResult = Handlebars.compile(productFilterResult);
                        $('.product-listing-result').html(TemptProductFilterResult(data));
                        checkForShoutout();
                    },
                    beforePageOnClick: function beforePageOnClick() {
                        scrollTop_one();
                    },
                    beforeNextOnClick: function beforeNextOnClick() {
                        scrollTop_one();
                    }
                }, 'beforePageOnClick', function beforePageOnClick() {
                    scrollTop_one();
                }));
            }


            function checkForShoutout() {
                if (shoutout == true) {
                    $('.product-listing-result').prepend("<div class=' m-bottom-xxl p-side-m shoutout-txt'><div  class='bg-light-grey width-full custom-block flex-column justify-center align-stretch'><h6 class='title ht6 uppercase text-center p-s'>SHOUT SHOUT SHOUT</h6></div></div>");
                    $('.shoutout-txt > .custom-block').html($('.shoutout-hidden').html());
                }
            }

            function scrollTop_one(target) {
                if (target) {
                    $(target).stop().animate({
                        scrollTop: 0
                    }, 500, 'swing');
                } else {
                    $("html, body").stop().animate({
                        scrollTop: 0
                    }, 500, 'swing');
                }
            }


            ////////FILTER END//////

            var dataSearchVal
            var mdataSearchVal
            var checkname
            var countCheckboxSelect
            var checkboxName
            var currentData
            var countButton
            var MobileCurrentData
            var MobileCountButton

            function hideAllWrappers() {
                // hide html divs desktop and mobile
                $(".checkbox-wrapper").hide();
                $('.checkbox-button-wrapper').hide();
                $('[data-checkbutton]').hide();
                //mobile
                $(".m-checkbox-wrapper").hide();
                $('.mobile-checkbox-button-wrapper').hide();
                $('[data-m-checkbutton]').hide();
            }


            // REFINE SEARCH DESKTOP//
            // get value of ul dropdown click function wraps all functions
            $(document).on("click", '.refine-search .refine-list a', function () {
                stickySidebar.updateSticky();

                $('.checkbox-button-wrapper').show();
                // get value of dropdown ul
                dataSearchVal = $(this).data("search-val");

                // target different dropdown
                if (dataSearchVal = dataSearchVal) {
                    $(".checkbox-wrapper").hide();
                    $("." + dataSearchVal + "-wrapper").show();
                } else {
                    $(".checkbox-wrapper").hide();
                }
                // function get data and check data changes in checkbox to button 
                $('.' + dataSearchVal + '-wrapper div input[type="checkbox"]').change(function () {
                    stickySidebar.updateSticky();

                    // update wrapper to currentDATA
                    dataSearchVal = $('.' + dataSearchVal + '-wrapper').data("checkbox-wrapper");
                    //get checkbox name to pass to text
                    checkboxName = $(this).attr("data-label");
                    // checkname of the checkbox to pass to button
                    checkname = $(this).data("checkname");
                    //count how many checkbox is checked
                    countCheckboxSelect = $('.' + dataSearchVal + '-wrapper div input:checkbox:checked').length;
                    appendCheckboxToButton();
                });
            });
            //desktop dropown ul end

            // append checkbox to button  [desktop]
            function appendCheckboxToButton() {
                // pass checked to button
                if ($('.' + dataSearchVal + '-wrapper  input[data-checkname=' + checkname + ']').is(':checked')) {

                    $('[data-checkbutton=' + dataSearchVal + ']').append("<div data-selection=" + checkname + " class='bg-grey m-xs bold btn btn-xs'>" + checkboxName + "<span class='btn-close fs-1 p-left-s'>&times;</span></div>");
                    // remove duplicates

                    if ($('[data-selection=' + checkname + ']').length > 1) {
                        $('[data-selection=' + checkname + ']').last().remove();
                    }
                } else {
                    if ($('[data-checkbutton=' + dataSearchVal + ']').has('[data-selection =' + checkname + ']')) {
                        $('[data-selection=' + checkname + ']').remove();
                        $('.' + dataSearchVal + '-wrapper input[data-checkname=' + checkname + ']').attr('checked', false)
                    }
                }
                // hide/show title
                if (countCheckboxSelect < 1) {
                    $('[data-checkbutton=' + dataSearchVal + ']').hide();
                } else {
                    $('[data-checkbutton=' + dataSearchVal + ']').show();
                }
            }
            // REFINE SEARCH  DESKTOP FEATURE end//



            //REFINE SEARCH MOBILE//
            $(document).on("change", '#refine-search-mobile', function () {
                //$(document).change( '#refine-search-mobile',function () {
                $('.mobile-checkbox-button-wrapper').show();
                //get mobile option val
                mdataSearchVal = $(this).val();
                // target different dropdown
                if (mdataSearchVal = mdataSearchVal) {
                    $(".m-checkbox-wrapper").hide();
                    $(".mobile-" + mdataSearchVal + "-wrapper").show();
                } else {
                    $(".m-checkbox-wrapper").hide();
                }

                // mobile function get data and check data changes in checkbox to button 
                $('.mobile-' + mdataSearchVal + '-wrapper label input[type="checkbox"]').change(function () {
                    stickySidebar.updateSticky();

                    // update wrapper to currentDATA
                    mdataSearchVal = $('.mobile-' + mdataSearchVal + '-wrapper').data("checkbox-wrapper");

                    checkboxName = $(this).attr("data-label");
                    // checkname of the checkbox to pass to button
                    checkname = $(this).data("checkname");
                    //count how many checkbox is checked
                    countCheckboxSelect = $('.mobile-' + mdataSearchVal + '-wrapper label input:checkbox:checked').length;
                    $(".mobile-checkbox-button-wrapper >[data-checkbutton=" + mdataSearchVal + "]").show();

                    MobileAppendCheckboxToButton();
                });
            });

            // mobile append checkbox to button 
            function MobileAppendCheckboxToButton() {

                if ($('.mobile-' + mdataSearchVal + '-wrapper label input[data-checkname=' + checkname + ']').is(':checked')) {

                    $('.mobile-checkbox-button-wrapper > [data-m-checkbutton=' + mdataSearchVal + ']').append("<div data-selection=" + checkname + " class='bg-grey m-xs bold btn btn-xs'>" + checkboxName + "<span class='btn-close fs-1 p-left-s'>&times;</span></div>");
                    //remove duplicates
                    if ($('[data-selection=' + checkname + ']').length > 1) {
                        $('[data-selection=' + checkname + ']').last().remove();
                    }
                } else {
                    if ($('.mobile-checkbox-button-wrapper > [data-checkbutton=' + mdataSearchVal + ']').has('[data-selection =' + checkname + ']')) {
                        $('[data-selection=' + checkname + ']').remove();
                        $('.mobile-' + mdataSearchVal + '-wrapper >[data-checkname=' + checkname + ']').parent().removeClass('checked');
                    }
                }

                // hide/show title
                if (countCheckboxSelect < 1) {
                    $('[data-m-checkbutton=' + mdataSearchVal + ']').hide();
                } else {
                    $('[data-m-checkbutton=' + mdataSearchVal + ']').show();
                }
            }
            /////REFINE SEARCH MOBILE END//////


            // function share between desktop and mobile//

            //target selected buttons to remove and uncheck checkbox
            $('body').on("click", "[data-selection]", function () {
                stickySidebar.updateSticky();

                //get current data-selection button  
                checkname = $(this).data("selection")
                //uncheck the checkbox (desktop)
                $("[data-checkname=" + checkname + "]").attr('checked', false);
                //get current wrapper data when button click desktop
                currentData = $('[data-checkbutton]>[data-selection=' + checkname + ']').parent().data('checkbutton')
                //count how many buttons remaning in selected wrapper desktop
                countButton = $('[data-checkbutton=' + currentData + ']>[data-selection]').length


                /////MOBILE///////
                //remove class for Mobile
                $('label >[data-checkname=' + checkname + ']').parent().removeClass('checked');
                //get current wrapper data when button click mobile
                MobileCurrentData = $('[data-m-checkbutton] > [data-selection=' + checkname + '] ').parent().data('m-checkbutton')
                //count how many buttons remaning in selected wrapper desktop
                MobileCountButton = $('[data-m-checkbutton=' + MobileCurrentData + ']>[data-selection]').length


                countSelection();

                //remove target button desktop and mobile
                //after checking for name
                $('[data-selection=' + checkname + ']').remove();

            });

            function countSelection() {
                if (countButton <= 1) {
                    $('[data-checkbutton=' + currentData + ']').hide();
                } else {
                    $('[data-checkbutton=' + currentData + ']').show();
                }
                if (MobileCountButton <= 1) {
                    $('.mobile-checkbox-button-wrapper > [data-m-checkbutton=' + MobileCurrentData + ']').hide("p");
                } else {
                    $('.mobile-checkbox-button-wrapper >[data-m-checkbutton=' + MobileCurrentData + ']').show();
                }
            }
            // function share between desktop and mobile end//




        }
        if ($('.page-product-listing section').length) productListingFeatures();



    });
})();






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

    });

})();




// ----------------------------------------------------------------------
// Component Tab tiles  (filter) [ky]
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {

        //make text to ellipsis when more than 4 lines
        $('[data-category] .content-box .text-container p').each(function (index, element) {
            $clamp(element, {
                clamp: 4,
                useNativeClamp: false
            });
        });


        var getID = "all";
        var dataCategory

        loadAllDiv();

        function loadAllDiv() {
            $('[data-category]').fadeOut(500);
            $('[data-category]').slice(0, 12).fadeIn(500);

            if ($(".all:hidden").length != 0) {
                $("#loadMore").show();
                $('.content-wrapper').addClass('crop-height');
                $(".btn-scroll-top").fadeIn(500);
            }
            if ($('[data-category]').length < 4) {
                $(".btn-scroll-top").fadeOut(500);
            }
            $('[data-category]').matchHeight();
        }

        function loadMoreBtn() {
            $("#loadMore").fadeIn(500);
            $(".fade-bg").fadeIn(500);
            $('.content-wrapper').css('height', '780px');
            $('[data-category]').matchHeight();
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
            $('[data-category=' + getID + ']').matchHeight();
        }


        // get data content to filter
        $('.tab-tiles-wrapper ul li').click(function () {
            getID = $(this).attr('data-btn-category');
            dataCategory = $('[data-category=' + getID + ']')
            checkFilterData();
            //btn add active class
            $(this).parent().find('li.active').removeClass('active');
            $(this).addClass('active');
            $('[data-category]').matchHeight();

        });

        function checkFilterData() {

            console.log(getID)
            $('[data-category=' + getID + ']').matchHeight();
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
                    dataCategory.slice(0, 12).fadeIn(900);
                    $('[data-category=' + getID + ']').matchHeight();

                } else {
                    completeLoadMore();
                    dataCategory.slice(0, 12).fadeIn(900);
                    $('[data-category=' + getID + ']').matchHeight();


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
                $('[data-category]' + ":hidden").slice(0, 4).fadeIn(600).slideDown(900);
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




// ----------------------------------------------------------------------
// Gallery Listing Search  (filter) [ky]
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {

        if ($('.gallery-listing-sidebar').length) {

            galleryResult();

            $(document).on("click", '.gallery-filter-wrapper [data-val]', function () {
                var typeDesc = $(this).data('description');
                $(".type-desc").html("<p class='p-top-no p-xl color-grey'>" + typeDesc + "</p>");
            });
            $(document).on("click", '.refine-mobile select', function () {
                var mtypeDesc = $('option:selected', this).attr('data-description');
                $(".m-type-desc").html("<p class='p-top-no p-xl color-grey'>" + mtypeDesc +
                    "</p>");
            });


            var galleryData = []
            var categoryContent = "result";
            var filteredData
            var currTHIS

            $(document).on('click', '[data-search-val]',
                function (e) {
                    e.preventDefault();
                    currTHIS = this;
                    renderGalleryResultHtml();
                });

            $(document).on('change', '.refine-mobile select',
                function (e) {

                    var currVAL = $('option:selected', this).val();
                    //get value from mobile select and inject to hidden input in desktop
                    $(this).parent().prev().find($("input[type=hidden]")).val(currVAL);

                    //let var"form" know it is still <form> targeted from desktop
                    currTHIS = $(this).parent().prev().find($("input[type=hidden]")).val(currVAL);

                    renderGalleryResultHtml();
                });



            //when dropdown click render result
            function renderGalleryResultHtml() {
                var form = $(currTHIS).closest('form');
                var selectedFilterData = ($(form).serializeObject());
                filteredData = multiFilter(galleryData[categoryContent], selectedFilterData);
                console.log('filter', selectedFilterData);
                console.log('filteredData', filteredData);
                //renderGalleryResultHtml(filteredData);
                paginationResult(filteredData);
            }



            function multiFilter(array, filters) {
                var filterKeys = Object.keys(filters);
                // filters all elements passing the criteria
                return array.filter(function (item) {
                    // dynamically validate all filter criteria
                    return filterKeys.every(function (key) {
                        if (Array.isArray(item[key]) && item[key].length > 0) {
                            // if data is tagging array, one matches in the array, return the object true
                            for (var i = 0; i < item[key].length; i++) {
                                if (!!~filters[key].indexOf(item[key][i])) {
                                    return filters[key];
                                };
                            }
                        } else if (filters[key] == "" || filters[key] == "all") {
                            return true;
                        } else {
                            return !!~filters[key].indexOf(item[key]);
                        }
                    });
                });
            }

            //load all from json first
            function galleryResult() {

                var pathname = window.location.pathname;
                var pagename = pathname.split(".").reverse()[1];
                console.log('pagename - ', pagename)
                var ext = pathname.split(".").pop();
                console.log("ext-", ext)

                $.ajax({
                    //url: "/etc/designs/usgb_v3/clientlib/uiux-all-js/js/json/gallery-filter.json",
                    url: "/etc/designs/usgb_v3/clientlib/uiux-all-js/js/json/test-gallery.json",
                    type: "GET",
                    cache: false,
                    success: function (response) {
                        // store in global
                        galleryData = response

                        var galleryFilterHtml = $('#gallery-filter').html();
                        var temptGalleryFilterHtml = Handlebars.compile(galleryFilterHtml);
                        $('.gallery-filter-wrapper').html(temptGalleryFilterHtml(galleryData));

                        renderGalleryResultHtml(galleryData.result);
                    },
                    beforeSend: function () {},
                    complete: function () {}
                });
            }

            function _defineProperty(obj, key, value) {
                if (key in obj) {
                    Object.defineProperty(obj, key, {
                        value: value,
                        enumerable: true,
                        configurable: true,
                        writable: true
                    });
                } else {
                    obj[key] = value;
                }
                return obj;
            }

            function paginationResult(dataForPagination) {
                $('#pagination-container').pagination(_defineProperty({
                    dataSource: dataForPagination,
                    pageSize: 9,
                    callback: function callback(data, pagination) {
                        var resultFilterHtml = $("#result").html();
                        var temptResultFilterHtml = Handlebars.compile(resultFilterHtml);
                        $('.results-gallery').html(temptResultFilterHtml(data));
                        // console.log('p-', pagination)
                        //console.log('d-', data)
                    },
                    beforePageOnClick: function beforePageOnClick() {
                        scrollTop_one();
                    },
                    beforeNextOnClick: function beforeNextOnClick() {
                        scrollTop_one();
                    }
                }, 'beforePageOnClick', function beforePageOnClick() {
                    scrollTop_one();
                }));
            }

            function scrollTop_one(target) {
                if (target) {
                    $(target).stop().animate({
                        scrollTop: 0
                    }, 500, 'swing');
                } else {
                    $("html, body").stop().animate({
                        scrollTop: 0
                    }, 500, 'swing');
                }
            }

        } // if end


    });
})();



// ----------------------------------------------------------------------
// Component:  Submittal 
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {

        $(document).on("change", '#submittal-form [data-check="all"]', function () {
            $('input:checkbox').not(this).prop('checked', this.checked);
        });

    });

})();


// ----------------------------------------------------------------------
// Component:  Global Landing 
// ----------------------------------------------------------------------

(function () {
    "use strict";
    $(document).ready(function () {

        function globalLandingCard() {
            function ipLookUp() {
                $.ajax('https://geoip.nekudo.com/api')
                    .then(
                        function success(response) {
                            //console.log('User\'s Location Data is ', response);
                            //console.log('User\'s Country', response.country.code);
                            var countryCode = response.country.code;
                            var countryName = response.country.name;
                            //console.log("cc", countryCode, "cn", countryName);

                            $.ajax({
                                url: "/etc/designs/usgb_v3/clientlib/uiux-all-js/js/json/global-landing-my.json",
                                data: "countrycode=" + countryCode + "&country=" + countryName + "",
                                type: "GET",
                                cache: false,
                                success: function (response) {

                                    var globalLanding = $('#global-landing').html();
                                    var TemptglobalLanding = Handlebars.compile(globalLanding);
                                    $('.global-landing').html(TemptglobalLanding(response));

                                },
                                beforeSend: function () {},
                                complete: function () {}
                            });

                        },

                        function fail(data, status) {
                            console.log('Request failed.  Returned status of', status);
                        }
                    );
            }
            ipLookUp();


        }


        if ($('.global-location-card').length) globalLandingCard();

    });
})();



// ----------------------------------------------------------------------
// Component:  case studies page
// ----------------------------------------------------------------------

(function () {
    "use strict";
    $(document).ready(function () {

        function caseStudiesPlaceholder() {

            var desktopPlaceholder = $(".desktop-sidebar-placeholder").html();

            console.log(desktopPlaceholder)
            $(".mobile-sidebar-placeholder").append(desktopPlaceholder);

            //desktopPlaceholder = mobilePlaceholder;

        }


        if ($('.desktop-sidebar-placeholder').length) caseStudiesPlaceholder();

    });
})();