(function ($) {
  $.fn.sidebarMenu = function (options) {
    options = $.extend({}, $.fn.sidebarMenu.defaults, options || {});
    var target = $(this);
    target.addClass('treeview-menu');
//    target.addClass('nav-list');
    if (options.data) {
      init(target, options.data);
    }
    else {
      if (!options.url) return;
      $.getJSON(options.url, options.param, function (data) {
        init(target, data);
      });
    }
    var url = window.location.pathname;
    //menu = target.find("[href='" + url + "']");
    //menu.parent().addClass('active');
    //menu.parent().parentsUntil('.nav-list', 'li').addClass('active').addClass('open');
    function init(target, data) {
      $.each(data, function (i, item) {
        var li = $('<li class="treeview"></li>');
        var a = $('<a></a>');
        var icon = $('<i></i>');
        //icon.addClass('glyphicon');
        icon.addClass(item.icon);
        var text = $('<span></span>');
        text.addClass('menu-text').text(item.name);
        a.append(icon);
        if(item.parentId != 1){
    		a.append('<i class="fa fa-circle-o"></i>');
    	}
        a.append(text);
        if (item.childrens && item.childrens.length>0) {
          a.attr('href', '#');
          a.append('<i class="fa fa-angle-left pull-right">');
//          var arrow = $('<b></b>');
//          arrow.addClass('arrow').addClass('icon-angle-down');
//          a.append(arrow);
          li.append(a);
          var menus = $('<ul></ul>');
          menus.addClass('treeview-menu');
          init(menus, item.childrens);
          li.append(menus);
        }
        else {
          var href = 'javascript:addTabs({id:\'' + item.id + '\',title: \'' + item.name + '\',close: true,url: \'' + item.url + '\'});';
          a.attr('href', href);
          //if (item.istab)
          //  a.attr('href', href);
          //else {
          //  a.attr('href', item.url);
          //  a.attr('title', item.text);
          //  a.attr('target', '_blank')
          //}
          li.append(a);
        }
        target.append(li);
      });
    }
  }
 
  $.fn.sidebarMenu.defaults = {
    url: null,
    param: null,
    data: null
  };
})(jQuery);