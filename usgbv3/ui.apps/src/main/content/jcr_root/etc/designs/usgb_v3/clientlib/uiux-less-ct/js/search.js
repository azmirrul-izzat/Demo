// ----------------------------------------------------------------------
// search
// ----------------------------------------------------------------------

(function () {
    "use strict";

    var resultData = [];
    var currCategory = "content";
    var currOnStageMainResultData = [];

    $(document).ready(function(){
        if($('.search-form').length > 0){
            $(document).on('click', '.sub-search-section a', function(e){
                e.preventDefault();
                var val = "";
                var parent = $(this).closest('.sub-search-section');
                $(parent).find('li').removeClass('active');
                $(this).closest('li').addClass('active');
                val = $(this).attr('data-category');
                currCategory = val;
                getResult();
            });

            $(document).on('click', '#search-form button[type="submit"]', function(e){
                e.preventDefault();
                // var val = $(this).closest('form').find('input').val();
                console.log(currCategory, $('#search-form input').val());
                getResult();
            });

            $(document).on('click', '#search-filter-mobile-form button[type="submit"], #search-filter-desktop-form button[type="submit"]', function(e){
                e.preventDefault();
                var form = $(this).closest('form');
                var selectedFilterData = ($(form).serializeObject());
                var filteredData = multiFilter(resultData[currCategory], selectedFilterData);
                console.log('filter',selectedFilterData);
                console.log('filteredData', filteredData);
                currOnStageMainResultData = filteredData;
                //console.log(currOnStageMainResultData);
                paginationResult (currOnStageMainResultData);
                
                scrollTop();
            });

            $(document).on('click', '.search-result-container .search-filter-sort .dropdown-menu a', function(e){
                switch ($(this).attr('data-val')) {
                    case "a_z":
                        currOnStageMainResultData.sort(function(a, b){
                            if(a.title.toLowerCase() < b.title.toLowerCase()) return -1;
                            if(a.title.toLowerCase() > b.title.toLowerCase()) return 1;
                            return 0;
                        });
                        break; 
                    case "z_a":
                        currOnStageMainResultData.sort(function(a, b){
                            if(a.title.toLowerCase() > b.title.toLowerCase()) return -1;
                            if(a.title.toLowerCase() < b.title.toLowerCase()) return 1;
                            return 0;
                        });
                        break;
                    case "latest":
                        currOnStageMainResultData.sort(function(a, b){
                            if(a.created_date > b.created_date) return -1;
                            if(a.created_date < b.created_date) return 1;
                            return 0;
                        });
                        break; 
                    case "oldest":
                        currOnStageMainResultData.sort(function(a, b){
                            if(a.created_date < b.created_date) return -1;
                            if(a.created_date > b.created_date) return 1;
                            return 0;
                        });
                        break; 
                }
                paginationResult (currOnStageMainResultData);
                scrollTop();
            });

            $(document).on('change', '.search-result-container .dp-form-mobile select', function(e){
                switch ($(this).val()) {
                    case "a_z":
                        currOnStageMainResultData.sort(function(a, b){
                            if(a.title.toLowerCase() < b.title.toLowerCase()) return -1;
                            if(a.title.toLowerCase() > b.title.toLowerCase()) return 1;
                            return 0;
                        });
                        break; 
                    case "z_a":
                        currOnStageMainResultData.sort(function(a, b){
                            if(a.title.toLowerCase() > b.title.toLowerCase()) return -1;
                            if(a.title.toLowerCase() < b.title.toLowerCase()) return 1;
                            return 0;
                        });
                        break;
                    case "latest":
                        currOnStageMainResultData.sort(function(a, b){
                            if(a.created_date > b.created_date) return -1;
                            if(a.created_date < b.created_date) return 1;
                            return 0;
                        });
                        break; 
                    case "oldest":
                        currOnStageMainResultData.sort(function(a, b){
                            if(a.created_date < b.created_date) return -1;
                            if(a.created_date > b.created_date) return 1;
                            return 0;
                        });
                        break; 
                }
                paginationResult (currOnStageMainResultData);
                scrollTop();
            });

            $(document).on('change', '#type-group input[type="checkbox"]', function(e){
                if(currCategory == "doc_finder"){
                    var value = $(this).val();
                    if($(this).is(":checked")){
                        $('#'+ value +'_category-group').removeClass('hidden');
                    } else {
                        $('#'+ value +'_category-group').addClass('hidden');
                        $('#'+ value +'_category-group input[type="checkbox"]').prop('checked', false);
                    }
                }
            });

            $(document).on('click', '.search-result-container .related-search a', function(e){
                e.preventDefault();
                $('#search-form input').val($(this).html());
                $('#search-form button[type="submit"]').click();
                scrollTop();
            });
            

            
        }
    });

    

    function paginationResult(dataForPagination) {
        $('#pagination-container').pagination({
            dataSource: dataForPagination,
            callback: function(data, pagination) {
                switch (currCategory) {
                    case "content":
                        var mainResulthtml = $('#templSearchResult_content').html();
                        break; 
                    case "doc_finder":
                        var mainResulthtml = $('#templSearchResult_docFinder').html();
                        break;
                    case "cad":
                        var mainResulthtml = $('#templSearchResult_cad').html();
                        break; 
                }
                var temptMainResulthtml = Handlebars.compile(mainResulthtml);
                $('#data-container').html(temptMainResulthtml(data));
            },
            beforePageOnClick: function(){
                scrollTop();
            },
            beforeNextOnClick: function(){
                scrollTop();
            },
            beforePageOnClick: function(){
                scrollTop();
            }
        });
    }


    function multiFilter(array, filters) {
        const filterKeys = Object.keys(filters);
        // filters all elements passing the criteria
        return array.filter((item) => {
            // dynamically validate all filter criteria
            return filterKeys.every(function(key){
                if( Array.isArray(item[key]) && item[key].length > 0 ) {
                    // if data is tagging array, one matches in the array, return the object true
                    for(var i=0; i<item[key].length; i++){
                        if(!!~filters[key].indexOf(item[key][i])){
                            return filters[key];
                        };
                    }
                } else {
                    return !!~filters[key].indexOf(item[key]);
                }
            });
        });
    }

    function scrollTop(target){
        if(target){
            $(target).stop().animate({scrollTop:0}, 500, 'swing');
        } else{
            $("html, body").stop().animate({scrollTop:0}, 500, 'swing');
        }
    }

    function getResult(){
        $.ajax({
            // url: "/etc/designs/usgb_v3/clientlib/uiux-less-ct/js/json/search-content-result.json",
            url: "/etc/designs/usgb_v3/clientlib/uiux-less-ct/js/json/search-doc-finder.json",
            type: "GET",
            cache: false,
            success: function(response) {
                resultData = response;
                currOnStageMainResultData = resultData[currCategory];

                $('.result-caption').removeClass('hidden');
                $('.result-caption .num').html(resultData.num_result);
                $('.result-caption .keyword').html(resultData.searched_text);

                if(response.result){
                    $('.search-category-controller-container').removeClass('hidden');
                    $('.no-search-result-container').addClass('hidden');
                    $('.search-result-container').removeClass('hidden');

                    paginationResult(resultData[currCategory]);

                    //populate filter listing for mobile
                    var filterMobHtml = $('#templSearchFilterMobile').html();
                    var temptFilterMobHtml = Handlebars.compile(filterMobHtml);
                    $('.filter-mobile-content .search-filter .middle').html(temptFilterMobHtml(resultData.filter_listing));

                    //populate filter listing for desktop
                    var filterDeskHtml = $('#templSearchFilterDesktop').html();
                    var temptFilterDeskHtml = Handlebars.compile(filterDeskHtml);
                    $('.search-result-container .search-filter .middle').html(temptFilterDeskHtml(resultData.filter_listing));

                    // Hide filter category by default for document finder
                    if(currCategory == "doc_finder"){
                        $('#products_category-group, #solutions_category-group').addClass('hidden');
                    }

                    //populate search related listing
                    var relatedHtml = $('#templSearchRelated').html();
                    var temptRelatedHtml = Handlebars.compile(relatedHtml);
                    $('.related-search').html(temptRelatedHtml(resultData));


                    switch (currCategory) {
                        case "content":
                            // if search in content, doc finder is the sub search result
                            var subResultDocFinderHtml = $('#templSubSearchResult_docFinder').html();
                            var temptSubResultDocFinderHtml = Handlebars.compile(subResultDocFinderHtml);
                            $($('.sub-search-result-wrapper')[0]).html(temptSubResultDocFinderHtml(resultData));
                            break; 
                        case "doc_finder":
                            // if search in doc finder, content is the sub search result
                            var subResultContentHtml = $('#templSubSearchResult_content').html();
                            var temptSubResultContentHtml = Handlebars.compile(subResultContentHtml);
                            $($('.sub-search-result-wrapper')[0]).html(temptSubResultContentHtml(resultData));
                            break;
                        case "cad":
                            text = "No required yet!";
                            break; 
                    }

                    

                }else{
                    $('.search-category-controller-container').addClass('hidden');
                    $('.search-result-container').addClass('hidden');
                    $('.no-search-result-container').removeClass('hidden');
                }
            },
            beforeSend: function() {
            },
            complete: function() {
            }
        });
    }

    Handlebars.registerHelper('ifEquals', function(arg1, arg2, options) {
        return (arg1 == arg2) ? options.fn(this) : options.inverse(this);
    });

    
})();