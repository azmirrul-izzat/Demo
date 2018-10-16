// ----------------------------------------------------------------------
// contact us
// ----------------------------------------------------------------------

(function () {
    "use strict";

    $(document).ready(function () {
        var getParam = new URLSearchParams(window.location.search);
        var paramValue = getParam.get("enquiryType") !== null? getParam.get("enquiryType").replace(" ","_").toLowerCase(): "";
        $('[value="'+ paramValue +'"]').attr("selected","selected");

        if($('.floating-type-form').length > 0){
            for(var i=0; i<$('.drop-down-select').length; i++){
                if($($('.drop-down-select')[i]).val() !== ""){
                    $($('.drop-down-select')[i]).closest('.float-label').find('label').addClass('open');
                }
            }

            $(document).on('change', '.floating-type-form .drop-down-select', function (e) {
                var labelDropDown = $(this).closest('.float-label').find('label');
                if ($(this).val() == "") {
                    $(labelDropDown).removeClass('open');
                } else {
                    $(labelDropDown).addClass('open');
                }
            });

            $('.chooseFile').bind('change', function () {
                var filename = $(".chooseFile").val();
                if (/^\s*$/.test(filename)) {
                    $(".file-select-name").text("");
                } else {
                    $(".file-select-name").text(filename.replace("C:\\fakepath\\", ""));
                }
            });
            
            $(document).on('change', '.chooseFile', function (e) {
                var labelChooseFile = $(this).closest('.float-label').find('label');
                if ($(this).val() == "") {
                    $(labelChooseFile).removeClass('open');
                } else {
                    $(labelChooseFile).addClass('open');
                }
            });

            $(document).on('click', '.btn-no-thanks', function(){
                location.reload();
            });
    
            
            if($("#contactUsForm1").length > 0){
                var form = $("#contactUsForm1");
                if($.ssoManager.isLogin){
                    $(form).find('[name="name"]').val($.ssoManager.sso.userInfo().responseJSON.profile.last_name + " " + $.ssoManager.sso.userInfo().responseJSON.profile.first_name);
                    $(form).find('[name="email"]').val($.ssoManager.sso.userInfo().responseJSON.email.address);
                }

                $("#contactUsForm1").validate({
                    ignore: ".ignore",
                    rules: {
                        enquiry_type : "required",
                        name: "required",
                        chooseFile: "required",
                        email: {
                            required: true,
                        },
                        message: "required",
                        hiddenRecaptcha: {
                            required: function () {
                                if (grecaptcha.getResponse() == '') {
                                    return true;
                                } else {
                                    return false;
                                }
                            }
                        },
                        agree: "required",
                    },
                    messages: {
                        enquiry_type: "Please select one",
                        name: "Please enter your name",
                        email: "Please enter a valid email address",
                        message: "Please enter a valid message",
                        hiddenRecaptcha: "Please complete the Captcha",
                        agree: "Please accept our policy"
                    },
                    errorElement: "span",
                    errorPlacement: function (error, element) {
                        // Add the `help-block` class to the error element
                        error.addClass("help-block");
    
                        if (element.prop("type") === "checkbox") {
                            error.insertAfter(element.closest('.checkbox'));
                        } else if (element.prop("tagName").toLowerCase() === "select") {
                            error.insertAfter(element);
                        } else {
                            error.insertAfter(element.parent().find('label'));
                        }
                    },
                    highlight: function (element, errorClass, validClass) {
                        $(element).parents(".respond-msg").addClass("has-error").removeClass("has-success");
                    },
                    unhighlight: function (element, errorClass, validClass) {
                        $(element).parents(".respond-msg").addClass("has-success").removeClass("has-error");
                    },
    
                    submitHandler: function(form) {
                        console.log("successs submit");
                
                        $.ajax({ //ajax form submit
                            url: '/bin/usgb/v3/contactusbasic',
                            type: 'POST',
                            data: JSON.stringify($("form").serializeObject()),
                            dataType: 'json',
                            contentType: false,
                            cache: false,
                            processData: false
                        }).done(function (data) { //fetch server "json" messages when done
                            console.log("successs submit3");
                            $('#dbid').val(data.id);
                            $('#contactUsForm1').hide();
                            $('#contactUsForm2').removeClass('hidden');
                            $('.thank-you-contacting-msg').removeClass('hidden');
                            scrollToTarget();
                        });
                       
                    }// end submit handler
                }); //contactUsForm1
            }
            
            if($("#contactUsForm2").length > 0){
                $("#contactUsForm2").validate({
                    ignore: ".ignore",
                    rules: {
                        contact: {
                            required: true,
                            digits: true
                        },
                        occupation: "required",
                        i_work_with: "required",
                        i_am_interested: "required"
                    },
                    messages: {
                        contact: "Please fill in your contact number",
                        occupation: "Please select one",
                        i_work_with: "Please select one",
                        i_am_interested: "Please select one"
                    },
                    errorElement: "span",
                    errorPlacement: function (error, element) {
                        // Add the `help-block` class to the error element
                        error.addClass("help-block");

                        if (element.prop("type") === "checkbox") {
                            error.insertAfter(element.closest('.checkbox'));
                        } else if (element.prop("tagName").toLowerCase() === "select") {
                            error.insertAfter(element);
                        } else {
                            error.insertAfter(element.parent().find('label'));
                        }
                    },
                    highlight: function (element, errorClass, validClass) {
                        $(element).parents(".respond-msg").addClass("has-error").removeClass("has-success");
                    },
                    unhighlight: function (element, errorClass, validClass) {
                        $(element).parents(".respond-msg").addClass("has-success").removeClass("has-error");
                    },

                    submitHandler: function(form) {
                        console.log("successs submit", $("form").serializeObject());

                        $.ajax({ //ajax form submit
                            url: '/bin/usgb/v3/contactusadvanced',
                            type: 'POST',
                            data: JSON.stringify($("form").serializeObject()),
                            dataType: 'json',
                            contentType: false,
                            cache: false,
                            processData: false
                        }).done(function (data) { //fetch server "json" messages when done
                            console.log("successs submit3");
                
                            $('#thankyou-modal').modal('show');
                            setTimeout(function(){
                                location.reload()
                            }, 8000);
                            scrollToTarget();
                        });
                    }// end submit handler
                }); 
            }
        }
    });

    function scrollToTarget(target){
        $('html, body').animate({
            scrollTop: $('.page-content').offset().top - 100
        }, 500);
    }
    
})();
    
function recaptchaCallback() {
    $('#hiddenRecaptcha').valid();
};