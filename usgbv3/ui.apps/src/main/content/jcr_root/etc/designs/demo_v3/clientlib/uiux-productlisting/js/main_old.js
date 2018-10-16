// ----------------------------------------------------------------------
// Checkbox Count (product listing)
// ----------------------------------------------------------------------


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


            //////COMPARE POPUP start//////
            var countCheck = 0;
            $(".popup-content").hide()

            // detect change for cardcheckbox
            $('.listing-search-content div input[type="checkbox"]').change(function () {

                var numberOfChecked = $('.listing-search-content div input:checkbox:checked').length;
                $(".popup-content").show();

                //$( "p" ).append( $( "strong" ) );
                console.log($('.listing-search-content div input[type="checkbox"]').attr("name"))

                if (numberOfChecked > 3) {
                    $(this).prop('checked', false);
                    console.log("reached max 3")
                }
                if (numberOfChecked == 0) {
                    $(".popup-content").hide();
                }
            });

            //////COMPARE POPUP END//////






            ////// REFINE SEARCH  FEATURE start //////
            var dataSearchVal
            var checkname
            var countCheckboxSelect
            var checkboxName

            // hide html divs
            $(".checkbox-wrapper").hide();
            $('.checkbox-button-wrapper').hide();
            $('[data-checkbutton]').hide();

            // get value of ul dropdown click function wraps all functions
            $('.refine-search').on("click", '.refine-list a', function () {
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


                // function check data changes in checkbox to button 
                $('.' + dataSearchVal + '-wrapper div input[type="checkbox"]').change(function () {
                    stickySidebar.updateSticky();

                    dataSearchVal = $('.' + dataSearchVal + '-wrapper').data("checkbox-wrapper");
                    checkboxName = $(this).attr("name");
                    // checkname of the checkbox to pass to button
                    checkname = $(this).data("checkname");
                    //count how many checkbox is checked
                    countCheckboxSelect = $('.' + dataSearchVal + '-wrapper div input:checkbox:checked').length;
                    appendCheckboxToButton();

                });

                // append checkbox to button 
                function appendCheckboxToButton() {
                    // pass checked to button
                    console.log("checbox data= " + dataSearchVal)
                    if ($('.' + dataSearchVal + '-wrapper input[data-checkname=' + checkname + ']').is(':checked')) {
                        $('[data-checkbutton=' + dataSearchVal + ']').append("<div data-selection=" + checkname + " class='bg-grey m-xs bold btn btn-xs'>" + checkboxName + "<span class='btn-close fs-1 p-left-s'>&times;</span></div>");
                        // remove duplicates
                        if ($('[data-selection=' + checkname + ']').length > 1) {
                            $('[data-selection=' + checkname + ']').last().remove();
                        }
                    }
                    else {
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


                //target selected buttons to remove and uncheck checkbox
                $('body').on("click", "[data-selection]", function () {
                    stickySidebar.updateSticky();
                    // remove the target button     
                    checkname = $(this).data("selection")
                    $('[data-selection=' + checkname + ']').remove();
                    $("[data-checkname="+ checkname +"]").attr('checked', false);
                   // console.log( $("[data-checkname="+ checkname +"]") );
                   


                  //  $('.' + dataSearchVal + '-wrapper input[data-checkname=' + checkname + ']').removeAttr('checked');
                  //  countCheckboxSelect = $('.' + dataSearchVal + '-wrapper div input:checkbox:checked').length;
                   countSelection();
                  //  console.log("currDATA- "+ $("[data-selection=hard]").parent().data("checkbutton"));
                  //  console.log("aaaaaa")
                });


                function countSelection() {
                    //count again current checkbox
                    countCheckboxSelect = $('.' + dataSearchVal + '-wrapper div input:checkbox:checked').length;
                    console.log(countCheckboxSelect)
                    if (countCheckboxSelect < 1) {
                        $('[data-checkbutton=' + dataSearchVal + ']').hide();
                    } else {
                        $('[data-checkbutton=' + dataSearchVal + ']').show();
                    }
                }

            });
            // REFINE SEARCH  FEATURE end//

        }