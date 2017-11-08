<!-- Bootstrap core JavaScript
================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<script src="/Javascript/jquery.js"></script>
<script>window.jQuery || document.write('<script src="/Javascript/jquery.js"><\/script>')</script>
<script src="/Javascript/bootstrap.js"></script>
<script type="text/javascript">
    (function ($) {
        $.getUrlParam = function (name) {
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
            var r = window.location.search.substr(1).match(reg);
            if (r != null) return unescape(r[2]); return null;
        }
    })(jQuery);
    $(function () {
        var curPath=window.location.pathname;
        if(menus!=undefined) {
            var menus = $("ul a[href='" + curPath + "']");
            var parentLi = menus.parent()[0];
            parentLi.setAttribute("class", "active");
        }
    })
</script>