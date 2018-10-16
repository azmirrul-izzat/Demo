// ----------------------------------------------------------------------
// search
// ----------------------------------------------------------------------

(function () {
    "use strict";

    var resultData = [];
    var currCategory = "content";
    var currOnStageMainResultData = [];
    var urlParams = new URLSearchParams(window.location.search);
    var searchedText = "";
    var sort = "a_z";
    var dt = {};

    $(document).ready(function(){

        AutoComplete({
            EmptyMessage: "No item found",
            QueryArg: "text",
            _Render: function(response) {
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

                for(var i=0; i<$('[data-autocomplete-value]').length; i++){
                    if( $($('[data-autocomplete-value]')[i]).find('strong').length == 0){
                        $($('[data-autocomplete-value]')[i]).hide();
                    }
                }
            }
        }, "#search-form input");

        if($('.search-form').length > 0){
            if($.ssoManager.isLogin){
                if (typeof $.ssoManager.sso.userInfo() !== 'undefined' &&
                    typeof $.ssoManager.sso.userInfo().responseJSON !== 'undefined' &&
                    ($.ssoManager.sso.userInfo().responseJSON).hasOwnProperty('id')) {
                    var userInfo = $.ssoManager.sso.userInfo().responseJSON;

                    //var dt = { user_info: {user_id: userInfo.id} };
                    dt = {
                        user_info: {
                            user_id: userInfo.id,
                            display_name: userInfo.profile.display_name,
                            country: $.ssoManager.countryCode,
                            first_name: userInfo.profile.first_name,
                            last_name: userInfo.profile.last_name,
                            email: userInfo.email.address
                        }
                    };
                }
            }

            if(urlParams.has('text')){
                var text = urlParams.get('text');
                $('#search-form input').val(text);
                searchedText = text;
                getResult();
            }

            $(document).on('click', '.sub-search-section a, .sub-search-result-link', function(e){
                e.preventDefault();
                var val = "";
                var parent = $(this).closest('.sub-search-section');
                $(parent).find('li').removeClass('active');
                $(this).closest('li').addClass('active');
                val = $(this).attr('data-category');
                currCategory = val;
                getResult();
                scrollToTarget('.search-category-controller-container');
            });

            $(document).on('click', '#search-form button[type="submit"]', function(e){
                e.preventDefault();
                console.log(currCategory, $('#search-form input').val());
                searchedText = $('#search-form input').val();
                if(searchedText !== ""){
                    getResult();
                }
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
                if(filteredData.length > 0){
                    paginationResult (currOnStageMainResultData);
                }
                scrollToTarget('.search-category-controller-container');
                if($('.filter-icon').is('visible')){
                    $('.close-filter-mobile-content').click();
                }
            });

            $(document).on('click', '.search-result-container .search-filter-sort .dropdown-menu a', function(e){
                sort = $(this).attr('data-val');
                paginationResult (currOnStageMainResultData);
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
                scrollToTarget('.search-category-controller-container');
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

            // add bookmark
            $(document).on('click', '.cta-bookmark', function(e){
                e.preventDefault();
                if(!$(this).hasClass('disabled')){
                    $('#add-bookmark-modal').modal('show');
                    var url = $(this).attr('href').replace(".html", ".properties_v3.json");
                    addNewBookmark(url);
                } else {
                    $('#signin-modal').modal('show');
                }
            });

            // add submittal
            $(document).on('click', '.cta-add-submittal', function(e){
                e.preventDefault();
                if(!$(this).hasClass('disabled')){
                    var url = $(this).closest('.each').find('.thumbs a').attr('href');
                    var data = {
                        document_list: [
                            {
                                document_id: "",
                                document_name: $(this).closest('.each').find('.right .headline').html(),
                                document_path: $(this).closest('.each').find('.right a').attr('href'),
                                document_url: window.location.origin + $(this).closest('.each').find('.right a').attr('href')
                            }
                        ],
                        user_info: dt.user_info
                    }
                    $.ajax({
                        url: url + ".json",
                        type: "GET",
                        cache: false,
                        success: function(response) {
                            $('#add-submittal-modal').modal('show');
                            var uuid = response["jcr:uuid"];
                            data.document_list[0].document_id = uuid;
                            $.ajax({
                                url: "/bin/sso/mySubmittal/add",
                                data: JSON.stringify(data),
                                type: "POST",
                                dataType: 'json',
                                contentType: "application/json",
                                cache: false,
                                success: function(response) {
                                    console.log('add submittal status', response);
                                }
                            });
                        }
                    });
                } else {
                    $('#signin-modal').modal('show');
                }
            });

            // header search keyword
            $(document).on('click', '.search-pop-up button[type="submit"]', function(e){
                e.preventDefault();
                var text = $(this).closest('.search-pop-up').find('.search-box input').val();
                window.location.href = "http://usgbuataut.cloudapp.net:4504/content/v3/demo/samplepage/search-content.html?text=" + text;
            });

            // login
            $(document).on('click', '.cta-login', function(e){
                e.preventDefault();
                $('.pre-login-nav a').click();
            });
        }
    });

    

    function paginationResult(dataForPagination) {
        sorting();
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
                scrollToTarget('.search-category-controller-container');
            },
            beforeNextOnClick: function(){
                scrollToTarget('.search-category-controller-container');
            },
            beforePageOnClick: function(){
                scrollToTarget('.search-category-controller-container');
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

    function scrollToTarget(target){
        $('html, body').animate({
            scrollTop: $(target).offset().top - 50
        }, 500);
    }

    function getResult(){
        $.ajax({
            //url: "/etc/designs/demo_v3/clientlib/uiux-less-ct/js/json/search-content-result.json",
            //url: "/etc/designs/demo_v3/clientlib/uiux-less-ct/js/json/search-doc-finder.json",
            url: "/bin/usgb/v3/search",
            data: "text="+ searchedText + "&category=" + currCategory,
            type: "GET",
            cache: false,
            success: function(response) {
                resultData = response;
                currOnStageMainResultData = resultData[currCategory];

                $('.result-caption').removeClass('hidden');
                $('.result-caption .num').html(resultData.num_result);
                $('.result-caption .current-category').html($('[data-category="'+currCategory+'"]').html());

                if(response.result){
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
                    //if(resultData.searches_related.length > 0){
                        var relatedHtml = $('#templSearchRelated').html();
                        var temptRelatedHtml = Handlebars.compile(relatedHtml);
                        $('.related-search').html(temptRelatedHtml(resultData));
                    //}

                    switch (currCategory) {
                        case "content":
                            // if search in content, doc finder is the sub search result
                            if(response.doc_finder.length > 0){
                                var subResultDocFinderHtml = $('#templSubSearchResult_docFinder').html();
                                var temptSubResultDocFinderHtml = Handlebars.compile(subResultDocFinderHtml);
                                $($('.sub-search-result-wrapper')[0]).html(temptSubResultDocFinderHtml(resultData));
                            }
                            break; 
                        case "doc_finder":
                            // if search in doc finder, content is the sub search result
                            if(response.content.length > 0){
                                var subResultContentHtml = $('#templSubSearchResult_content').html();
                                var temptSubResultContentHtml = Handlebars.compile(subResultContentHtml);
                                $($('.sub-search-result-wrapper')[0]).html(temptSubResultContentHtml(resultData));
                            }
                            break;
                        case "cad":
                            text = "No required yet!";
                            break; 
                    }

                    // check login
                    if($.ssoManager.isLogin){
                        $('.cta-bookmark, .cta-add-submittal').removeClass('disabled');
                    }

                }else{
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


    //add bookmark
    function addNewBookmark(url) {
        // retrieve it (Or create a blank array if there isn't any info saved yet),
        var urls = JSON.parse(localStorage.getItem('bookmarkStore')) || [];
        // add to it,
        urls.push(url);
        // then put it back.
        urls = uniqueArray(urls);
        localStorage.setItem('bookmarkStore', JSON.stringify(urls));
    }

    // unique array   check no same name
    function uniqueArray(arrArg) {
        return arrArg.filter(function (elem, pos, arr) {
            return arr.indexOf(elem) == pos;
        });
    };

    function sorting(){
        switch (sort) {
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
    }

    
})();
