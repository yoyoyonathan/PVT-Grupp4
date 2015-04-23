/* Portfolio Isotope */
	$(window).load(function() {
		var c = $("#portfolio-items");
		function b(d) {
			c.isotope({
				filter: d
			});
			$("#portfolio li.active").removeClass("active");
			$("#portfolio-filter").find("[data-filter='" + d + "']").parent().addClass("active");
			if (d != "*") {
				window.location.hash = d.replace(".", "")
			}
			if (d == "*") {
				window.location.hash = ""
			}
		}
		if (c.length) {
			$(".project").each(function () {
				$this = $(this);
				var d = $this.data("tags");
				if (d) {
					var f = d.split(",");
					for (var e = f.length - 1; e >= 0; e--) {
						$this.addClass(f[e])
					}
				}
			});
			
			c.isotope({
			itemSelector: ".project",
			layoutMode: "fitRows"
			});

			$("#portfolio-filter li a").click(function () {
				var d = $(this).attr("data-filter");
				b(d);
				return false
			});
			if (window.location.hash != "") {
				b("." + window.location.hash.replace("#", ""))
			}
		}
	});