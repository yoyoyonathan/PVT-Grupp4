$(function(){
		var doScroll = true;
		var browserRE = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i; 
		if(browserRE.test(navigator.userAgent)){
				doScroll = false;
		};

		
		$(".center-button .btn").click(function(ev){
				if(doScroll == true){
						ev.preventDefault();
						var id = $(this).attr("href");
						$.scrollTo(id, id == "#signUp" ? 650 : 950);
				}
		});


		var animOpa = function(x) {
				$(x).animate({opacity: 1}, {duration: 1300});
		};

		var triggeredCounters = false;


		var studCounter = $("#stud-counter");
		var univCounter = $("#univ-counter");
		var compCounter = $("#comp-counter");

		studCounter.css({opacity: 0});
		univCounter.css({opacity: 0});
		compCounter.css({opacity: 0});
		


		$(window).scroll(function(ev){
				if(triggeredCounters){
						return false;
				}
				var doc = $(document);
				var doch = $(window).height();

				var bottom = doc.scrollTop() + doch;
				var elTop = $("#foretag").position().top;
				
				var offset = 350;

				if(bottom >= (elTop + offset)){
						triggeredCounters = true;
						var startingPoint = {stud: 0, univ: 0, comp: 0};
						var endPoint = {stud: 1500, univ: 4, comp: 47};

						animOpa(studCounter);
						animOpa(univCounter);
						animOpa(compCounter);
						
						$(startingPoint).animate(endPoint,{
								easing: "easeOutQuad",
								duration: 1300,
								step: function(){
										studCounter.html(Math.ceil(this.stud));
										univCounter.html(Math.ceil(this.univ));
										compCounter.html(Math.ceil(this.comp));
								}
						})
				}

		});
});
