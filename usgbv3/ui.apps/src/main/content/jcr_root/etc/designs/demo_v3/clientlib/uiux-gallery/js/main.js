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
                const filterKeys = Object.keys(filters);
                // filters all elements passing the criteria
                return array.filter((item) => {
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
                console.log('pagename - ', pagename);
                var ext = pathname.split(".").pop();
                console.log("ext-",ext);
                $.ajax({
                    //url: "/etc/designs/demo_v3/clientlib/uiux-all-js/js/json/gallery-filter.json",
                    url: "/bin/usgb/v3/galleryDetails?pageurl="+pagename,
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

            function paginationResult(dataForPagination) {
                $('#pagination-container').pagination({
                    dataSource: dataForPagination,
                    pageSize: 9,
                    callback: function (data, pagination) {
                        var resultFilterHtml = $("#result").html();
                        var temptResultFilterHtml = Handlebars.compile(resultFilterHtml);
                        $('.results-gallery').html(temptResultFilterHtml(data));
                        // console.log('p-', pagination)
                        //console.log('d-', data)
                    },
                    beforePageOnClick: function () {
                        scrollTop_one();
                    },
                    beforeNextOnClick: function () {
                        scrollTop_one();
                    },
                    beforePageOnClick: function () {
                        scrollTop_one();
                    }
                });
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