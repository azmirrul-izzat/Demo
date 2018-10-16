// ----------------------------------------------------------------------
// bookmark
// ----------------------------------------------------------------------


(function () {
    "use strict";
    //get from local storage
    var data = JSON.parse(localStorage.getItem('bookmarkStore'));
    var originalData = [];
    var originalDataReversed = [];
    var currOnStageMainResultData = [];
    var storeKey = "";
    var gettedSharedKey = "";
    var urlParams = new URLSearchParams(window.location.search);


    $(document).ready(function () {
        if(urlParams.has('key')){
            gettedSharedKey = urlParams.get('key');
            console.log(gettedSharedKey);
            getBookmarkJson();
        } else {
            if(data == null || data.length == 0){
                $('[data-target="#bookmark-remove-all-modal"]').addClass('hidden');
                $('.relevant-content').addClass('hidden');
                $('.social-media-group').addClass('hidden');
                $('.search-filter-sort').addClass('hidden');
                $('.no-result').removeClass('hidden');
            } else {
                $('.no-result').addClass('hidden');
                $('.relevant-content').removeClass('hidden');
                $('[data-target="#bookmark-remove-all-modal"]').removeClass('hidden');
                $('.relevant-content').removeClass('hidden');
                $('.social-media-group').removeClass('hidden');
                $('.search-filter-sort').removeClass('hidden');
            }

            //after ajax get all data in array, run pagination
            getAll(data).done(function(results) {
                originalData = results;
                var clone = results.slice();
                originalDataReversed = clone.reverse();
                currOnStageMainResultData = originalData;
                if(currOnStageMainResultData.length > 0){
                    // sorting by default: a-z
                    currOnStageMainResultData.sort(function(a, b){
                        if(a.pagePropertiesList[0].pageTitle.toLowerCase() < b.pagePropertiesList[0].pageTitle.toLowerCase()) return -1;
                        if(a.pagePropertiesList[0].pageTitle.toLowerCase() > b.pagePropertiesList[0].pageTitle.toLowerCase()) return 1;
                        return 0;
                    });
                    paginationResult(currOnStageMainResultData);
                    getShareKey();
                }

                if(currOnStageMainResultData.length < 10){
                    $('#pagination-container').addClass('hidden');
                }

            });

            $(document).on('click', '.cta-trigger-remove', function(e){
                e.preventDefault();
                var url = $(this).closest('.each').find('.thumbs a').attr('href').replace('.html', '.properties_v3.json');
                $('#bookmark-remove-modal').attr('data-url', url);
                $('#bookmark-remove-modal').modal('show');
            });

            $(document).on('click', '.cta-remove-bookmark', function(e){
                e.preventDefault();
                var url = $(this).closest('.modal').attr('data-url');
                var targetUrl = $(this).closest('.modal').attr('data-url').replace('.properties_v3.json', '.html');
                removeBookmark(url);
                $('#bookmark-remove-modal').modal('hide');
                $('a[href="'+ targetUrl + '"]').closest('.each').remove();
                getShareKey();
            });

            $(document).on('click', '.cta-remove-all-bookmark', function(e){
                e.preventDefault();
                removeAllBookmark();
                $('#bookmark-remove-all-modal').modal('hide');
                $('.bookmark-list').html('');
                $('#pagination-container').addClass('hidden');
            });

        }

        $(document).on('click', '.bookmark-wrap .search-filter-sort .dropdown-menu a', function(e){
            switch ($(this).attr('data-val')) {
                case "a_z":
                    currOnStageMainResultData.sort(function(a, b){
                        if(a.pagePropertiesList[0].pageTitle.toLowerCase() < b.pagePropertiesList[0].pageTitle.toLowerCase()) return -1;
                        if(a.pagePropertiesList[0].pageTitle.toLowerCase() > b.pagePropertiesList[0].pageTitle.toLowerCase()) return 1;
                        return 0;
                    });
                    break;
                case "z_a":
                    currOnStageMainResultData.sort(function(a, b){
                        if(a.pagePropertiesList[0].pageTitle.toLowerCase() > b.pagePropertiesList[0].pageTitle.toLowerCase()) return -1;
                        if(a.pagePropertiesList[0].pageTitle.toLowerCase() < b.pagePropertiesList[0].pageTitle.toLowerCase()) return 1;
                        return 0;
                    });
                    break;
                case "latest":
                    currOnStageMainResultData = originalData;
                    break;
                case "oldest":
                    currOnStageMainResultData = originalDataReversed;
                    break;
                case "categorize":
                    currOnStageMainResultData.sort(function(a, b){
                        if(a.pagePropertiesList[0].pageCategoryName.toLowerCase() < b.pagePropertiesList[0].pageCategoryName.toLowerCase()) return -1;
                        if(a.pagePropertiesList[0].pageCategoryName.toLowerCase() > b.pagePropertiesList[0].pageCategoryName.toLowerCase()) return 1;
                        return 0;
                    });
                    break;
            }
            paginationResult(currOnStageMainResultData);
            if(currOnStageMainResultData.length < 10){
                $('#pagination-container').addClass('hidden');
            }

        });

        $(document).on('change', '.bookmark-wrap .dp-form-mobile select', function(e){
            switch ($(this).val()) {
                case "a_z":
                    currOnStageMainResultData.sort(function(a, b){
                        if(a.pagePropertiesList[0].pageTitle.toLowerCase() < b.pagePropertiesList[0].pageTitle.toLowerCase()) return -1;
                        if(a.pagePropertiesList[0].pageTitle.toLowerCase() > b.pagePropertiesList[0].pageTitle.toLowerCase()) return 1;
                        return 0;
                    });
                    break;
                case "z_a":
                    currOnStageMainResultData.sort(function(a, b){
                        if(a.pagePropertiesList[0].pageTitle.toLowerCase() > b.pagePropertiesList[0].pageTitle.toLowerCase()) return -1;
                        if(a.pagePropertiesList[0].pageTitle.toLowerCase() < b.pagePropertiesList[0].pageTitle.toLowerCase()) return 1;
                        return 0;
                    });
                    break;
                case "latest":
                    currOnStageMainResultData = originalData;
                    break;
                case "oldest":
                    currOnStageMainResultData = originalDataReversed;
                    break;
                case "categorize":
                    currOnStageMainResultData.sort(function(a, b){
                        if(a.pagePropertiesList[0].pageCategoryName.toLowerCase() < b.pagePropertiesList[0].pageCategoryName.toLowerCase()) return -1;
                        if(a.pagePropertiesList[0].pageCategoryName.toLowerCase() > b.pagePropertiesList[0].pageCategoryName.toLowerCase()) return 1;
                        return 0;
                    });
                    break;
            }
            paginationResult(currOnStageMainResultData);
            if(currOnStageMainResultData.length < 10){
                $('#pagination-container').addClass('hidden');
            }
        });
    });

    function paginationResult(dataForPagination) {
        $('#pagination-container').pagination({
            dataSource: dataForPagination,
            callback: function (data, pagination) {
                var html = $('#bookmark_template').html();
                var template = Handlebars.compile(html);
                console.log(data);
                $('.bookmark-list').html(template(data));
            },
            beforePageOnClick: function () {
                scrollTop();
            },
            beforeNextOnClick: function () {
                scrollTop();
            },
            beforePageOnClick: function () {
                scrollTop();
            }
        });
    }

    function scrollTop(target){
        if(target){
            $(target).stop().animate({scrollTop:0}, 500, 'swing');
        } else{
            $("html, body").stop().animate({scrollTop:0}, 500, 'swing');
        }
    }

    function getAll(requests) {
        var count = requests.length;
        var results = [];
        var deferreds = [];
        var all_done = $.Deferred();

        for(var i=0; i < requests.length; i++) {
            var deferred = $.ajax({
                url: requests[i],
                type: "GET",
                cache: false,
            })
                .done(function(data) {
                    results.push(data);
                })
                .always(function() {
                    count--;

                    if(count === 0) {
                        all_done.resolve(results);
                    }
                });
            deferreds.push(deferred);
        }
        return all_done.promise();
    }

    function removeBookmark(url) {
        var store = JSON.parse(localStorage.getItem('bookmarkStore')) || [];
        var index = store.indexOf(url);
        if (index > -1) {
            store.splice(index, 1);
        }
        data = store;
        localStorage.setItem('bookmarkStore', JSON.stringify(store));
    }

    function removeAllBookmark() {
        localStorage.removeItem('bookmarkStore');
        data = [];
    }

    function getShareKey(){
        $.ajax({
            url: "/bin/usgb/v3/shareService",
            type: "GET",
            data: 'rType=write&paths={"paths":' + JSON.stringify(data) + '}',
            cache: false,
            success: function(response) {
                storeKey = response.key;
                console.log(window.location.origin + '/bin/usgb/shareService?rType=get&key=' + storeKey);
                $(".share-icons").jsSocials({
                    url: window.location.href + "?key=" + storeKey,
                    showLabel: false,
                    showCount: false,
                    text: "My USG Boral Bookmark",
                    shares: [
                        {
                            share: "email",
                            logo: "fa fa-envelope",
                            label: "Envoyer",
                            showCount: true,
                            to: "",
                            shareIn: "self"
                        }
                        , "facebook", "twitter", "linkedin"]
                });
            }
        });
    }

    function getBookmarkJson(){
        $.ajax({
            url: "/bin/usgb/v3/shareService",
            type: "GET",
            data: 'rType=get&key=' + gettedSharedKey,
            cache: false,
            success: function(response) {
                console.log(response.path);
                data = response.path;
                if(data == null || data.length == 0){
                    $('[data-target="#bookmark-remove-all-modal"]').addClass('hidden');
                    $('.relevant-content').addClass('hidden');
                    $('.no-result').removeClass('hidden');
                } else {
                    $('.no-result').addClass('hidden');
                    $('.relevant-content').removeClass('hidden');
                }

                getAll(data).done(function(results) {
                    currOnStageMainResultData = results;
                    if(currOnStageMainResultData.length > 0){
                        paginationResult(currOnStageMainResultData);
                        getShareKey();

                    }

                    if(currOnStageMainResultData.length < 10){
                        $('#pagination-container').addClass('hidden');
                    }

                    $('.cta-trigger-remove, [data-target="#bookmark-remove-all-modal"], .social-media-group').addClass('hidden');

                });
            }
        });
    }



})();


