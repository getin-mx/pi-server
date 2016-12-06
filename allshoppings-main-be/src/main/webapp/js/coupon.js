var changed = false;

$(document).ready(function() {

	var $body = $('body'),
	$content = $body.find('#content'),
	$validateCouponForm = $content.find('#validateCouponForm'),
	$couponCode = $content.find('#couponCode'),
	$couponDetailsSection = $validateCouponForm.find('#couponDetailsSection'),
	$innerCouponCode = $couponDetailsSection.find('#couponDetailsInnerCouponCode'),
	$comments = $couponDetailsSection.find('#couponDetailsComments'),
	$redeemStatus = $couponDetailsSection.find('#couponDetailsRedeemStatus');

	var $btnRedeem = $couponDetailsSection.find('#couponDetailsRedeem');
	var $btnCancel = $couponDetailsSection.find('#couponDetailsCancel');

	var REDEEM_STATUS_DELIVERED = 0;
	var REDEEM_STATUS_ACCEPTED = 1;
	var REDEEM_STATUS_REJECTED = 2;
	var REDEEM_STATUS_REDEEMED = 3;
	var REDEEM_STATUS_EXPIRED = 4;

	
	$("input").uniform();
	$("input").bind("change", function () {
		changed = true;
	});

	$validateCouponForm.wl_Form({
		ajax:true,
		status:false,
		confirmSend: false,
		onBeforeSubmit: function(data){
			$code = $validateCouponForm.find('#couponCode');
			$code.val($code.val().toUpperCase().trim());
			$validateCouponForm.wl_Form('set','sent',false);
			
			hideCouponDetails();
		},
		onSuccess: function (data, textStatus, jqXHR) {
			var obj = jQuery.parseJSON(data);
			if( obj.response == 'fail') {
				if( obj.code == 404 ) obj.message = $.i18n._('error.coupon.not.found');
				$.msg($.i18n._(obj.message),{header:$.i18n._('js.wl.alert.header')});
			} else {
				showCouponDetails(obj);
			}
		},
		onError: function (data, textStatus, jqXHR) {
			window.location = '/error-pages/error_500.html';
		}
	});
	
	function hideCouponDetails() {
		var $couponDetailsSection = $('#couponDetailsSection');
		$couponDetailsSection.css('display', 'none');
		return false;
	}
	
	function showCouponDetails(data) {
		var $avatarId = $couponDetailsSection.find('#couponDetailsAvatarId'),
		$avatarRef = $couponDetailsSection.find('#couponDetailsAvatarRef'),
		$name = $couponDetailsSection.find('#couponDetailsName'),
		$description = $couponDetailsSection.find('#couponDetailsDescription'),
		$user = $couponDetailsSection.find('#couponDetailsUser'),
		$validity = $couponDetailsSection.find('#couponDetailsValidity');
		
		if( data.avatarId != undefined ) {
			$avatarId.attr('src', '/img/' + data.avatarId);
			$avatarRef.attr('href', '/img/' + data.avatarId);
		} else {
			$avatarId.attr('src', '/img/default.png');
			$avatarRef.attr('href', '/img/default.png');
		}

		$innerCouponCode.val(data.couponCode);
		$name.text(data.name);
		$description.text(data.description);
		$user.text(data.userName + ' (' + data.email + ')');
		$validity.text(data.limitDateTime);
		$redeemStatus.text($.i18n._('app.coupon.redeemStatus.' + data.redeemStatus).toUpperCase());
		$btnCancel.text($.i18n._('app.button.cancel'));
		if( data.redeemStatus > REDEEM_STATUS_ACCEPTED ) {
			$redeemStatus.css('color', 'red');
			$comments.text($.i18n._('app.validateCoupon.comments.noRedeemable'));
			$btnRedeem.css('display', 'none');
			$btnCancel.css('display', 'block');
		} else {
			$redeemStatus.css('color', 'green');
			$comments.text($.i18n._('app.validateCoupon.comments.redeemable'));
			$btnRedeem.css('display', 'block');
			$btnCancel.css('display', 'block');
		}
		
		$couponDetailsSection.css('display', 'block');
		
		return false;
	}

	$('a.fancybox').fancybox();

	$btnRedeem.click(function(e) {
		e.preventDefault();

		var fd = new FormData();
		fd.append('couponCode', $innerCouponCode.val());

		$.ajax({
			type: "POST",
			url: 'doRedeemCoupon',
			data: 'couponCode=' + $innerCouponCode.val(),
			success: function(data) {
				$.msg($.i18n._('app.validateCoupon.redeem.success'),{header:$.i18n._('js.wl.alert.header')});
				$btnRedeem.css('display', 'none');
				$btnCancel.text($.i18n._('app.button.back'));
				$comments.text('');
				$redeemStatus.css('color', 'red');
				$redeemStatus.text($.i18n._('app.coupon.redeemStatus.' + REDEEM_STATUS_REDEEMED).toUpperCase());
			},
			contentType: 'application/x-www-form-urlencoded',
		});
	});
	
	$btnCancel.click(function(e) {
		e.preventDefault();
		hideCouponDetails();
		$couponCode.val('');
	});
	
});
