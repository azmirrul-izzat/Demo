


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
               //console.log(scrollPos)
               //if (scrollPos <= 500 || scrollPos < 800) {
                    stickySidebar.updateSticky();
              // }

                //if ( scrollPos > 1300) {
                  // stickySidebar.destroy();
                //}else{
                 //   stickySidebar.updateSticky();
               // }

            }



            //////COMPARE POPUP start//////
            $(".compare-popup").hide();
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

                    var dataUncheck = $('[data-content-title="'+getLink+'"]');
                        dataUncheck.closest(".each").animate({
                            left:"100px",
                            opacity:0	
                    }, 900);

                    removeCompareLocalStorage(inputVal);
                    
                }

                

            });


            //hide show popup
                $("body").on("click", ".compare-popup .title", function () {
                    minimisePopup();
                    clearTimeout(countDown);
                    if($(".compare-popup .title").hasClass("selected")){
                        //console.log("click true")
                    }else{
                        //console.log("click false")
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
                
                if(numberOfChecked < 1 ){
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
                    left:"100px",
                    opacity:0
                  }, 600 );

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


                    $(".compare-popup .popup-content .each").css("opacity","0");
                    setTimeout(function () {
                         $(".compare-popup .popup-content .each").css({"top":"-5px","opacity":"0"}).animate({
                             top: "0px",
                             opacity: 1	
                         }, 700);
                     }, 200);
                    
                    checkNumberOfCheckbox();    
                    
                }
            }


            //minimise Popup if idle   
            $( ".compare-popup .inner-content" )
              .mouseenter(function() {
                //console.log("enterrring");
                clearTimeout(countDown);
                //checkPopupState();
              })
              .mouseleave(function() {
                //console.log("mouseleave");
                clearTimeout(countDown);
                countDownPop();
              });
    
    
            function countDownPop(){
                //console.log("counting")
                countDown = setTimeout(function () {
                    minimisePopup();
                }, 5000);
            }
    
            function checkPopupState(){
                clearTimeout(countDown);
                if($(".compare-popup .inner-content").is(":visible")){
                    //console.log("true")
                    countDownPop();
                }else{
                    //console.log("false")
                    $(".compare-popup .inner-content").slideToggle(800);
                    $(".compare-popup .title").toggleClass("selected");   
                    countDownPop();
                }
            }
    
            function minimisePopup(){
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

                        $(".compare-popup").hide();
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
                        currOnStageMainResultData.sort(function(a, b){
                            if(Number(a.created_date) > Number(b.created_date)) return -1;
                            if(Number(a.created_date) < Number(b.created_date)) return 1;
                            return 0;
                        });
                        break; 
                    case "oldest":
                        currOnStageMainResultData.sort(function(a, b){
                            if(Number(a.created_date) < Number(b.created_date)) return -1;
                            if(Number(a.created_date) > Number(b.created_date)) return 1;
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
                        currOnStageMainResultData.sort(function(a, b){
                            if(Number(a.created_date) > Number(b.created_date)) return -1;
                            if(Number(a.created_date) < Number(b.created_date)) return 1;
                            return 0;
                        });
                        break; 
                    case "oldest":
                        currOnStageMainResultData.sort(function(a, b){
                            if(Number(a.created_date) < Number(b.created_date)) return -1;
                            if(Number(a.created_date) > Number(b.created_date)) return 1;
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
                    $('.product-listing-result').prepend("<div class='m-bottom-xxl p-side-m shoutout-txt'><div  class='bg-light-grey width-full custom-block flex-column justify-center align-stretch'><h6 class='title ht6 uppercase text-center p-s'>SHOUT SHOUT SHOUT</h6></div></div>");
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

                    $('[data-checkbutton=' + dataSearchVal + ']').append("<div data-selection=" + checkname + " class='bg-light-2-grey m-xs bold btn btn-xs'>" + checkboxName + "<span class='btn-close fs-1 p-left-s'>&times;</span></div>");
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

                    $('.mobile-checkbox-button-wrapper > [data-m-checkbutton=' + mdataSearchVal + ']').append("<div data-selection=" + checkname + " class='bg-light-2-grey m-xs bold btn btn-xs'>" + checkboxName + "<span class='btn-close fs-1 p-left-s'>&times;</span></div>");
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
                $("[data-checkname=" + checkname + "]").prop('checked', false);
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


