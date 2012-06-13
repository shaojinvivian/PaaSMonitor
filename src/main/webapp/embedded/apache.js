//嵌到平台的监测界面

Ext.require(['Ext.Ajax', 'Ext.tip.QuickTipManager', 'Ext.form.*', 'Ext.window.MessageBox', 'Ext.window.Window', 'Ext.chart.*']);

Ext.onReady(function() {

	Ext.tip.QuickTipManager.init();

	var ip = getRequestParam('ip');
	var httpPort = getRequestParam('httpPort');	

	var idField = Ext.create('Ext.form.field.Hidden', {
		name : 'appServerId'
	});

	var formPanel = Ext.create('Ext.form.Panel', {
		title : '监测',
		frame : true,
		bodyPadding : 5,
		fieldDefaults : {
			labelAlign : 'left',
			labelWidth : 150,
			anchor : '100%'
		},
		items : [{
			xtype : 'displayfield',
			name : 'status',
			fieldLabel : '运行状态'
		}, {
			xtype : 'displayfield',
			name : 'totalAccessCount',
			fieldLabel : '总访问数'
		}, {
			xtype : 'displayfield',
			name : 'totalKBytes',
			fieldLabel : '已发送字节数（KB）'		
		}, {
			xtype : 'displayfield',
			name : 'readableUptime',
			fieldLabel : '已运行时间'
		}, {
			xtype : 'displayfield',
			name : 'reqPerSec',
			fieldLabel : '每秒平均请求数'
		}, {
			xtype : 'displayfield',
			name : 'bytesPerSec',
			fieldLabel : '每秒平均响应字节数（Bytes）'
		}, {
			xtype : 'displayfield',
			name : 'bytesPerReq',
			fieldLabel : '请求平均响应字节数（Bytes）'
		}, {
			xtype : 'displayfield',
			name : 'busyWorkerCount',
			fieldLabel : '繁忙线程数'
		}, {
			xtype : 'displayfield',
			name : 'idleWorkerCount',
			fieldLabel : '闲置线程数'		
		}]
	});

	
	formPanel.getForm().load({
		url : '../monitor/apachesnap',
		waitMsg : 'Loading',
		params : {
			ip : ip,
			httpPort : httpPort			
		},
		method : 'get',
		success : function(form, action) {
			var text = action.response.responseText;
			var result = Ext.decode(text);
			var id = result.data.appServer.id;
			idField.setValue(id);
		},
		failure : function(form, action){
			var text = action.response.responseText;
			var result = Ext.decode(text);
			Ext.Msg.alert("Error", result.message);
		}
	});

	var analysisWindow = Ext.create('Ext.window.Window', {
	    title: '日志分析结果',
	    height: 650,
	    width: 970,
	    layout: 'fit',
	    closeAction: 'hide',
	    items: [{
	        xtype : "component",
	        autoEl : {
	            tag : "iframe",
	            src : "http://" + ip + ":8089/polliwog/weblogs/monthly-summary.html"
	        }
    	}]
	});

	var analysisButton = Ext.create('Ext.Button', {
		id : 'mostPop',
		width : 120,
		height : 25,
		text : '显示访问情况统计',
		margin : '0, 20, 0, 0',
		handler : function(button, event) {
			analysisWindow.show();
		}
	});
	
	var analysisPanel = Ext.create('Ext.panel.Panel', {
		layout : 'anchor',
		title : '分析',
		bodyPadding : 5,
		boarder : false,
		height : 70,
		items : [analysisButton]

	});
	
	/*
	Ext.define('MonitorConfig', {
		extend : 'Ext.data.Model',
		fields : [{
			name : 'type',
			type : 'string'
		}, {
			name : 'name',
			type : 'string'
		}, {
			name : 'times',
			type : 'long'
		}]
	});

	
	var monitorConfigStore = new Ext.data.Store({
		storeId : 'monitorConfigStore',
		model : 'MonitorConfig',
		proxy : {
			type : 'ajax',
			url : '../servletmonitor/monitorConfigs?ip=' + ip + '&httpPort=' + httpPort,
			reader : {
				type : 'json',
				root : 'data'
			},			
			listeners : { exception : function(proxy, response) {
					var errorMessage = (Ext.decode(response.responseText)).message
					console.log(errorMessage);
				}
			}
		},
		autoLoad : true
	});



	var monitorConfigGrid = Ext.create('Ext.grid.Panel', {
		title : '已有监测参数',
		store : Ext.data.StoreManager.lookup('monitorConfigStore'),
		autoScroll: true,
		columns : [{
			header : 'Type',
			dataIndex : 'type'
		}, {
			header : 'Name',
			dataIndex : 'name'
		}, {
			header : 'Times',
			dataIndex : 'times'
		}],
		layout : 'fit',
		height : 150,
		viewConfig:
           {             
             emptyText:'暂无自定义监测参数',
             deferEmptyText: false
            
            }
	});

	var customMonitorFormPanel = Ext.create('Ext.form.Panel', {
		title : '添加自定义监测参数',
		frame : true,
		url : 'monitorConfigs',
		method : 'POST',		
		layout : 'fit',
		fieldDefaults : {
			labelAlign : 'left',
			labelWidth : 150,
			anchor : '100%'
		},
		items : [{
			xtype : 'fieldcontainer',
			fieldLabel : '类型',
			defaultType : 'radiofield',
			defaults : {
				flex : 1
			},
			layout : 'hbox',
			allowBlank: false,
			items : [{
				boxLabel : 'Servlet Name',
				name : 'type',
				inputValue : 'servlet',
				id : 'radio1'
			}, {
				boxLabel : 'URL Pattern',
				name : 'type',
				inputValue : 'url',
				id : 'radio2'
			}]
		}, {
			xtype : 'textfield',
			name : 'name',
			allowBlank: false,
			fieldLabel : '类名或模式'
		}, idField],
		buttons : [{
			text : '添加',
			formBind : true, //only enabled once the form is valid
			// monitorValid:true,
			// disabled: true,
			handler : function() {
				var form = this.up('form').getForm();
				if(form.isValid()) {
					form.submit({
						success : function(form, action) {
							monitorConfigStore.load();
						},
						failure : function(form, action) {
							Ext.Msg.alert('Failed', action.result.msg);
						}
					});
				}
			}
		},{
			text : '重置',
			handler : function() {
				this.up('form').getForm().reset();
			}
		}, ],

	});

	var customMonitorPanel = Ext.create('Ext.panel.Panel', {
		layout : 'anchor',
		title : '自定义监测',		
		boarder : false,
		height : 400,
		items : [customMonitorFormPanel, monitorConfigGrid]

	});
	*/
	

	
	//The big panel
	var panel = Ext.create('Ext.panel.Panel', {
		renderTo : Ext.getBody(),
		layout : {
			type : 'vbox', // Arrange child items vertically
			align : 'stretch'
		},
		title : 'Apache @ ' + ip +':' + httpPort,
		width : 420,
		height : 700,
		items : [formPanel, analysisPanel]
	});

	var hideMask = function() {
		Ext.get('loading').remove();
		Ext.fly('loading-mask').animate({
			opacity : 0,
			remove : true
		});
	};

	Ext.defer(hideMask, 200);

});
