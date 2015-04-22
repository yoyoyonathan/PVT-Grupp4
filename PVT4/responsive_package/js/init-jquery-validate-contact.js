/**************************
			 Validate
	**************************/
	// http://bassistance.de/jquery-plugins/jquery-plugin-validation/
	// http://docs.jquery.com/Plugins/Validation/
	// http://docs.jquery.com/Plugins/Validation/validate#toptions
	
	$('#contact-form').validate({
		rules: {
			name: {
				minlength: 2,
				required: true
			},
			email: {
				required: true,
				email: true
			},
			message: {
				minlength: 2,
				required: true
			}
		},
		highlight: function(label) {
			$(label).closest('.control-group').addClass('error');
		},
		success: function(label) {
			label
			.text('OK!').addClass('valid')
			.closest('.control-group').addClass('success');
		}
	});	  