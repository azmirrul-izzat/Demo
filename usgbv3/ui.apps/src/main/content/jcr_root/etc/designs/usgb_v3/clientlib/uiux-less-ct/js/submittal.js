// ----------------------------------------------------------------------
// my submittal
// ----------------------------------------------------------------------
(function () {
    "use strict";
    var dataOnStage = {};

    $(document).ready(function(){
        if($('#submittal-form').length > 0) {
            // check sso login
            if(!$.ssoManager.isLogin){
                window.location.href = "http://myaccount-stg.usgboral.com/dialog/#/?client=USG%20Boral%20(UAT)&lang=en&state=T%2FpRSJMCUpdFsLG5v2uA2w&bu=AU";
            }
            
            $.ajax({
                url: "/etc/designs/usgb_v3/clientlib/uiux-less-ct/js/json/submittal-listing.json",
                data: "",
                type: "GET",
                cache: false,
                success: function (response) {
                    dataOnStage = response.documentCollatorList;
                    if(dataOnStage.length > 0){
                        var html = $("#templateSubmittalItem").html();
                        var template = Handlebars.compile(html);
                        $('.submittal-content').html(template(dataOnStage));
                    } else {
                        $('.submittal-content').html(`
                            <div class="col-xs-12 p-vertical-m bottom-line-grey">
                                <div class="col-xs-12 col-sm-6 color-grey text-left">
                                    <p>No document added.</p>
                                </div>
                            </div>
                        `);
                    }
                },
                beforeSend: function () {
                    //$('.loader').fadeIn("fast");
                },
                complete: function () {
                    //$('.loader').fadeOut("slow");
                }
            });

            $(document).on('change', '.document-item, [data-check="all"]', function(){
                if($('.document-item:checked').length > 0){
                    $('#draft-btn, #delete-btn, #download-btn').removeClass('disabled');
                    if($('.document-item:checked').length == $('.document-item').length){
                        $('[data-check="all"]').prop('checked', true);    
                    }
                } else {
                    $('#draft-btn, #delete-btn, #download-btn').addClass('disabled');
                    $('[data-check="all"]').prop('checked', false);
                }
            });

            $('#draft-btn').on('click', function(){
                clearError();

                var txt = 'Someone';
                if (typeof $.ssoManager.sso.userInfo() !== 'undefined' &&
                    typeof $.ssoManager.sso.userInfo().responseJSON !== 'undefined' &&
                    ($.ssoManager.sso.userInfo().responseJSON).hasOwnProperty('id')) {
                    var userInfo = $.ssoManager.sso.userInfo().responseJSON;
                    txt = (typeof userInfo.profile.display_name !== 'undefined') ? userInfo.profile.display_name : userInfo.profile.first_name + ' ' + userInfo.profile.last_name;
                }

                txt += ' has shared document(s) for you to read;\n\n';
                $.each(getSelected(), function (index, doc) {
                    txt += (index + 1) + '. ' + doc.document_name + '\n';
                });

                $('#document-email-content').html(txt);
                $('#emailModal').modal('show');
            });

            $('#email-btn').on('click', function(){
                if (validateSend()) {
                    $('#emailModal').modal('hide');
                    var data = {
                        email_detail: {
                            sent_to: $('#recipientEmail').val()
                        },
                        document_list: getSelected()
                    }
                } else {
                    $('#emailModal').modal('show');
                }
            });

            $('#delete-btn').on('click', function(){
                getSelected();
            });

            $('#download-btn').on('click', function(){
                getSelected();
            });

            function getSelected() {
                var docs = []
                $(".document-item:checked").each(function () {
                    if ($(this).prop("checked") == true) {
                        docs.push({
                            document_id: $(this).attr("doc-id"),
                            document_name: $(this).attr("doc-name"),
                            document_url: location.origin + $(this).attr("doc-path"),
                            document_path: $(this).attr("doc-path")
                        });
                    }
                });
                return docs;
            }

            function clearError(){
                var parent = $("#recipientEmail").closest(".form-group");
                parent.removeClass("has-error");
                parent.removeClass("has-feedback");
                parent.find("span").remove();
            }

            function validateSend(){
                clearError();
                var flag = true;

                var re = new RegExp(/[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,4}/);
                var validEmail =  $('#recipientEmail').val().trim().match(re);

                if($('#recipientEmail').val().trim().length < 1 || !validEmail) {
                    var parent = $("#recipientEmail").closest(".form-group");
                    parent.addClass("has-error");
                    parent.addClass("has-feedback");
                    parent = $("#recipientEmail").parent();
                    parent.append("<span class='glyphicon glyphicon-remove form-control-feedback'></span>");
                    flag = false;
                }

                if(!flag) 
                    return false;

                else
                    return true;
            }
            
        }
    });

})();