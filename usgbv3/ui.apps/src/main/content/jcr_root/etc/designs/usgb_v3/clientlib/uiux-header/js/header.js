jQuery(document).ready(function ($) {
    var mainHeader = $('.cd-auto-hide-header'),
        secondaryNavigation = $('.cd-secondary-nav'),
        //this applies only if secondary nav is below intro section
        belowNavHeroContent = $('.sub-nav-hero'),
        headerHeight = mainHeader.height();

    //set scrolling variables
    var scrolling = false,
        previousTop = 0,
        currentTop = 0,
        scrollDelta = 10,
        scrollOffset = 150;

    mainHeader.on('click', '.nav-trigger', function (event) {
        // open primary navigation on mobile
        event.preventDefault();
        mainHeader.toggleClass('nav-open');
    });

    $(window).on('scroll', function () {
        if (!scrolling) {
            scrolling = true;
            (!window.requestAnimationFrame) ?
            setTimeout(autoHideHeader, 250): requestAnimationFrame(autoHideHeader);
        }
    });

    $(window).on('resize', function () {
        headerHeight = mainHeader.height();
    });

    function autoHideHeader() {
        var currentTop = $(window).scrollTop();

        (belowNavHeroContent.length > 0) ?
        checkStickyNavigation(currentTop) // secondary navigation below intro
            : checkSimpleNavigation(currentTop);

        previousTop = currentTop;
        scrolling = false;
    }

    function checkSimpleNavigation(currentTop) {
        //there's no secondary nav or secondary nav is below primary nav
        if (previousTop - currentTop > scrollDelta) {
            //if scrolling up...
            mainHeader.removeClass('is-hidden');
        } else if (currentTop - previousTop > scrollDelta && currentTop > scrollOffset) {
            //if scrolling down...
            mainHeader.addClass('is-hidden');
        }
    }

    function checkStickyNavigation(currentTop) {
        //secondary nav below intro section - sticky secondary nav
        var secondaryNavOffsetTop = belowNavHeroContent.offset().top - secondaryNavigation.height() - mainHeader.height();

        if (previousTop >= currentTop) {
            //if scrolling up... 
            if (currentTop < secondaryNavOffsetTop) {
                //secondary nav is not fixed
                mainHeader.removeClass('is-hidden');
                secondaryNavigation.removeClass('fixed slide-up');
                belowNavHeroContent.removeClass('secondary-nav-fixed');
            } else if (previousTop - currentTop > scrollDelta) {
                //secondary nav is fixed
                mainHeader.removeClass('is-hidden');
                secondaryNavigation.removeClass('slide-up').addClass('fixed');
                belowNavHeroContent.addClass('secondary-nav-fixed');
            }

        } else {
            //if scrolling down...	
            if (currentTop > secondaryNavOffsetTop + scrollOffset) {
                //hide primary nav
                mainHeader.addClass('is-hidden');
                secondaryNavigation.addClass('fixed slide-up');
                belowNavHeroContent.addClass('secondary-nav-fixed');
            } else if (currentTop > secondaryNavOffsetTop) {
                //once the secondary nav is fixed, do not hide primary nav if you haven't scrolled more than scrollOffset 
                mainHeader.removeClass('is-hidden');
                secondaryNavigation.addClass('fixed').removeClass('slide-up');
                belowNavHeroContent.addClass('secondary-nav-fixed');
            }

        }
    }
});

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
            var i = 5;




            $(".mega-menu-wrapper .nav-dropdown-wrapper").on("mouseenter", function () {

                var $this = $(this);


                $this.addClass('open').find($('.mega-dropdown-menu')).css({
                    "z-index": calc()
                });

                $this.find('a').first().addClass('arrow-up-green');
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
                $this.find('a').first().removeClass('arrow-up-green');
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
                        Limit: 11,
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
                                    $($('[data-autocomplete-value]')[i]).addClass('remove');
                                }
                            }

                            $('.remove[data-autocomplete-value]').remove();
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

        // header search keyword
        //$(document).on('click', '.search-pop-up button[type="submit"]', function(e){
        //    e.preventDefault();
        //    var text = $(this).closest('.search-pop-up').find('.search-box input').val();
        //    window.location.href = "/content/usgboral/en_au/search.html?text=" + text;
        //});

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
// Order Sample Notification
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {

        if ($.ssoManager.isLogin) {
            console.log("has login");
            $("a.cart > .pop-notification").addClass("width-none");
            setTimeout(function () {

                if (localStorage.getItem('sampleOrdersJson') === null) {
                    console.log("orderSampleEmpty")
                } else {
                    var array = JSON.parse(localStorage.getItem('sampleOrdersJson'));
                    console.log("sampleORder", array.length);
                    var countSample = array.length;
                    $("a.cart > .pop-notification").removeClass("width-none");
                    $("a.cart > .pop-notification").addClass("counter");
                    $("a.cart > .pop-notification.counter").text(countSample);
                    $(".header>.wrapper").addClass("desktop-mobile-slide-down");
                }

            }, 1000);

        }

    });
})();





// ----------------------------------------------------------------------
// REusable code
// ------------------------------------------------------------------

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