/* jQuery Web Site Scripts goes here */
jQuery(document).ready(function() {
	
	/* Enable Back To Top Link */	
	$(window).scroll(function(){
	        // global scroll to top button
	        if ($(this).scrollTop() > 300) {
	            $('.scrolltotop').fadeIn();
	        } else {
	            $('.scrolltotop').fadeOut();
	        }        
	});
	$('.scrolltotop').click(function(){
        $("html, body").animate({ scrollTop: 0 }, 700);
        return false;
    });
   


	/* Enable touchTouch for image popup only if present on the page */
	if($('ul.thumbnails li a').length > 0) {
		$('ul.thumbnails li a').touchTouch();
	}



	/* Overlay and Zoom icon */
	$(".zoom").each(function () {
		var d = $(this);
		var b = d.find("img").height();
		var c = $("<span>").addClass("zoom-overlay").html("&nbsp;");
		d.append(c)
	});



	/* Enable Bootstrap Tooltip */
	$('a.tip').tooltip({'placement' : 'bottom'}); 


	/* Enable Bootstrap Tabs */
	$('#tabs a').click(function (e) {
  		e.preventDefault();
  		$(this).tab('show');
	});
});
