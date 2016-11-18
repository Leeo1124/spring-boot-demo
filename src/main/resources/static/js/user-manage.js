$(function (){
    $(".form_datetime").datetimepicker({
    	minView: "month",//选择日期后，不会再跳转去选择时分秒
        format: "yyyy-MM-dd",
        autoclose: true,//选择日期后自动关闭 
        todayBtn: true,
        pickerPosition: "bottom-left",
        language: 'zh-CN' //汉化
    });
	
	//$('input[name="search_createDate_eq"]').daterangepicker();
	//$("#datemask").inputmask("yyyy-mm-dd", {"placeholder": "yyyy-mm-dd"});
	//$("[data-mask]").inputmask();
	
    var $wrapper = $('#div-table-container');
    var $table = $('#table-user');

    var _table = $table.dataTable($.extend(true,{},CONSTANT.DATA_TABLES.DEFAULT_OPTION, {
    	//是否支持排序
        //ordering: false,
    	//默认排序
    	//order: [[ 2, "desc" ]],
    	ajax : function(data, callback, settings) {//ajax配置为function,手动调用异步查询
            //手动控制遮罩
            $wrapper.spinModal();
            //封装请求参数
            var param = userManage.getQueryCondition(data);
            $.ajax({
                    type: "GET",
                    url: "/admin/sys/user/query2",
                    cache : false,  //禁用缓存
                    data: param,    //传入已封装的参数
                    dataType: "json",
                    success: function(result) {
                        //setTimeout仅为测试延迟效果
                        //setTimeout(function(){
                            //异常判断与处理
                            if (result.errorCode) {
                                $.dialog.alert("查询失败。错误码："+result.errorCode);
                                return;
                            }
 
                            //封装返回数据，这里仅演示了修改属性名
                            var returnData = {};
                            returnData.draw = data.draw;//这里直接自行返回了draw计数器,应该由后台返回
                            returnData.recordsTotal = result.total;
                            returnData.recordsFiltered = result.total;//后台不实现过滤功能，每次查询均视作全部结果
                            returnData.data = result.pageData;
                            //关闭遮罩
                            $wrapper.spinModal(false);
                            //调用DataTables提供的callback方法，代表数据已封装完成并传回DataTables进行渲染
                            //此时的数据需确保正确无误，异常判断应在执行此回调前自行处理完毕
                            callback(returnData);
                        //},200);
                    },
                    error: function(XMLHttpRequest, textStatus, errorThrown) {
                        $.dialog.alert("查询失败：" + errorThrown);
                        $wrapper.spinModal(false);
                    }
                });
        },
        columns: [
            CONSTANT.DATA_TABLES.COLUMN.CHECKBOX,
            {
                className : "ellipsis", //文字过长时用省略号显示，CSS实现
                data: "username",
                render : CONSTANT.DATA_TABLES.RENDER.ELLIPSIS,//会显示省略号的列，需要用title属性实现划过时显示全部文本的效果
            },
            {
                className : "ellipsis",
                data: "email",
                render : CONSTANT.DATA_TABLES.RENDER.ELLIPSIS,
                //固定列宽，但至少留下一个活动列不要固定宽度，让表格自行调整。不要将所有列都指定列宽，否则页面伸缩时所有列都会随之按比例伸缩。
                //切记设置table样式为table-layout:fixed; 否则列宽不会强制为指定宽度，也不会出现省略号。
                //width : "80px"
            },
            {
                className : "ellipsis",
                data: "mobilePhoneNumber",
                render : CONSTANT.DATA_TABLES.RENDER.ELLIPSIS,
                //width : "80px"
            },
            {
                data : "status.info",
                //width : "80px",
                //是否开启排序功能
                //orderable : false,
                render : function(data,type, row, meta) {
                    return '<i class="fa fa-male"></i> '+(data?"在线":"离线");
                }
            },
            {
                data : "createDate",
                //width : "80px"
            },
             {
                className : "td-operation",
                data: null,
                defaultContent:"",
                orderable : false,
                //width : "120px"
            }
        ],
        "createdRow": function ( row, data, index ) {
            //行渲染回调,在这里可以对该行dom元素进行任何操作
            //给当前行加样式
            if (data.role) {
                $(row).addClass("info");
            }
            //给当前行某列加样式
            $('td', row).eq(4).addClass(data.status.info?"text-success":"text-error");
            //不使用render，改用jquery文档操作呈现单元格
            var $btnView = $('<a href="#"><i class="glyphicon glyphicon-zoom-in btn-view"></i></a>');
            var $btnEdit = $('<a style="margin-left:10px;"href="#"><i class="glyphicon glyphicon-pencil btn-edit"></i></a>');
            var $btnDel = $('<a style="margin-left:10px;color:#d9534f" href="#"><i class="glyphicon glyphicon-remove btn-del"></i></a>');
            $('td', row).eq(6).append($btnView).append($btnEdit).append($btnDel);
 
        },
        "drawCallback": function( settings ) {
            //渲染完毕后的回调
            //清空全选状态
            $(":checkbox[name='cb-check-all']",$wrapper).prop('checked', false);
            //默认选中第一行
//            $("tbody tr",$table).eq(0).click();
        }
    })).api();//此处需调用api()方法,否则返回的是JQuery对象而不是DataTables的API对象
 
    //新增
    $("#btn-add").click(function(){
        userManage.addItemInit();
    });
    //批量删除
    $("#btn-del").click(function(){
        var arrItemId = [];
        $("tbody :checkbox:checked",$table).each(function(i) {
            var item = _table.row($(this).closest('tr')).data();
            arrItemId.push(item);
        });
        userManage.deleteItem(arrItemId);
    });
    //查询
    $("#btn-search").click(function(){
        _table.draw();
    });
 
//    //行点击事件
//    $("tbody",$table).on("click","tr",function(event) {
//        $(this).addClass("active").siblings().removeClass("active");
//        //获取该行对应的数据
//        var item = _table.row($(this).closest('tr')).data();
//        userManage.currentItem = item;
//        userManage.showItemDetail(item);
//    });
    
    $table.on("change",":checkbox",function() {
        if ($(this).is("[name='cb-check-all']")) {
            //全选
            $(":checkbox",$table).prop("checked",$(this).prop("checked"));
        }else{
            //一般复选
            var checkbox = $("tbody :checkbox",$table);
            $(":checkbox[name='cb-check-all']",$table).prop('checked', checkbox.length == checkbox.filter(':checked').length);
        }
    }).on("click",".td-checkbox",function(event) {
        //点击单元格即点击复选框
        !$(event.target).is(":checkbox") && $(":checkbox",this).trigger("click");
    }).on("click",".btn-view",function() {
        //点击查看按钮
        var item = _table.row($(this).closest('tr')).data();
        $(this).closest('tr').addClass("active").siblings().removeClass("active");
        userManage.currentItem = item;
        userManage.showItemDetail(item);
    }).on("click",".btn-edit",function() {
        //点击编辑按钮
        var item = _table.row($(this).closest('tr')).data();
        $(this).closest('tr').addClass("active").siblings().removeClass("active");
        userManage.currentItem = item;
        userManage.editItemInit(item);
    }).on("click",".btn-del",function() {
        //点击删除按钮
        var item = _table.row($(this).closest('tr')).data();
        $(this).closest('tr').addClass("active").siblings().removeClass("active");
        userManage.deleteItem([item]);
    });
 
});
 
var userManage = {
    currentItem : null,
    getQueryCondition : function(data) {
        var param = "";
        var orderColumn = "";
        var orderDir = "";
        //组装分页参数
        param += "page_pn=" + data.start;
        param += "&page_size=" + data.length;
        
        //组装排序参数
        if (data.order && data.order.length && data.order[0]) {
            switch (data.order[0].column) {
            case 1:
                orderColumn = "username";
                break;
            case 2:
                orderColumn = "email";
                break;
            case 3:
              orderColumn = "mobilePhoneNumber";
                break;
            case 4:
                orderColumn = "status";
                break;
            default:
                orderColumn = "username";
                break;
            }
            orderDir = data.order[0].dir;
            param += "&sort." + orderColumn + "=" + orderDir;
        }
        
        //组装查询参数
        var fields = $("input[name^='search'], select[name^='search']").serializeArray();
        $.each( fields, function(i, field){
        	var val = $.trim(field.value);
        	if ("" != val) {
        		param += "&" + $.trim(field.name) + "=" + val;
        	}
        });
        alert(param);
 
        return param;
    },
    showItemDetail : function(item) {
//        $("#user-view").show().siblings(".info-block").hide();
        if (!item) {
//            $("#user-view .prop-value").text("");
            return;
        }
//        $("#name-view").text(item.name);
//        $("#position-view").text(item.position);
//        $("#salary-view").text(item.salary);
//        $("#start-date-view").text(item.start_date);
//        $("#office-view").text(item.office);
//        $("#extn-view").text(item.extn);
//        $("#role-view").text(item.role?"管理员":"操作员");
//        $("#status-view").text(item.status?"在线":"离线");
        
        this.dialogPage("查看用户信息","admin/sys/user/"+item.id+"/view");
    },
    addItemInit : function() {
    	this.dialogPage("创建用户","admin/sys/user/add");
    },
    editItemInit : function(item) {
        if (!item) {
            return;
        }
//        $("#form-edit")[0].reset();
//        $("#title-edit").text(item.name);
//        $("#name-edit").val(item.name);
//        $("#position-edit").val(item.position);
//        $("#salary-edit").val(item.salary);
//        $("#start-date-edit").val(item.start_date);
//        $("#office-edit").val(item.office);
//        $("#extn-edit").val(item.extn);
//        $("#role-edit").val(item.role);
//        $("#user-edit").show().siblings(".info-block").hide();
        this.dialogPage("修改用户信息","admin/sys/user/"+item.id+"/edit");
    },
    addItemSubmit : function() {
        $.dialog.tips('保存当前添加用户');
    },
    editItemSubmit : function() {
        $.dialog.tips('保存当前编辑用户');
    },
    deleteItem : function(selectedItems) {
        var message;
        if (selectedItems&&selectedItems.length) {
            if (selectedItems.length == 1) {
                message = "确定要删除 '"+selectedItems[0].id+"' 吗?";
 
            }else{
                message = "确定要删除选中的"+selectedItems.length+"项记录吗?";
            }
            $.dialog.confirmDanger(message, function(){
                $.dialog.tips('执行删除操作');
            });
        }else{
            $.dialog.tips('请先选中要操作的行');
        }
    },
    dialogPage : function(_titile, _url){
		$.dialog({
			title: _titile,
			content: 'url:/'+_url,
			//最大化
			max: false,
			//最小化
		    min: false,
		    //宽
		    width: '500px',
		    //高
		    //height: 300,
		    //最小宽度限制
		    //minWidth: '700px',
		    //最小高度限制
		    //minHeight:500,
		    //静止定位(拖动滚动条位置不变)
		    fixed: true,
		    //锁屏
		    lock: true,
		    //不许拖拽
		    drag: false,
		    resize: false,
		    //自定义按钮
		    button: [
		             {
		                 name: '确定',
		                 callback: function () {
		                	 //刷新窗口调用页面
		                	 this.reload();
		                     return false;
		                 },
		                 focus: true
		             },
		             {
		                 name: '取消'
		             }
		         ]

		});
	}
};