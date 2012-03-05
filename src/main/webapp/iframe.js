//嵌到平台的监测界面

Ext.require(['Ext.Ajax', 'Ext.tip.QuickTipManager', 'Ext.form.*', 'Ext.window.MessageBox', 'Ext.window.Window', 'Ext.chart.*']);

Ext.onReady(function() {

	Ext.tip.QuickTipManager.init();

	var ip = getRequestParam('ip');
	var jmxPort = getRequestParam('jmxPort');
	var contextName = getRequestParam('contextName');

	var idField = Ext.create('Ext.form.field.Hidden', {
		name : 'appInstanceId'
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
			name : 'cpuPercent',
			fieldLabel : 'CPU使用率'
		}, {
			xtype : 'displayfield',
			name : 'usedMemory',
			fieldLabel : '已用内存'
		}, {
			xtype : 'displayfield',
			name : 'availableMemory',
			fieldLabel : '可用内存'
		}, {
			xtype : 'displayfield',
			name : 'runningDuration',
			fieldLabel : '已运行时间'
		}, {
			xtype : 'displayfield',
			name : 'requestCount',
			fieldLabel : '已接收请求总数'
		}, {
			xtype : 'displayfield',
			name : 'avgTime',
			fieldLabel : '平均响应时间'
		}, {
			xtype : 'displayfield',
			name : 'maxTime',
			fieldLabel : '最长响应时间'
		}, {
			xtype : 'displayfield',
			name : 'minTime',
			fieldLabel : '最短响应时间'
		}, {
			xtype : 'displayfield',
			name : 'errorCount',
			fieldLabel : '出错数'
		}, {
			xtype : 'displayfield',
			name : 'bytesReceived',
			fieldLabel : '已接收字节数'
		}, {
			xtype : 'displayfield',
			name : 'bytesSent',
			fieldLabel : '已发送字节数'
		}]
	});

	var startButton = Ext.create('Ext.Button', {
		id : 'startButton',
		text : 'start',
		width : 55,
		height : 25,
		margin : '0, 20, 0, 0',
		disabled : true,
		handler : function(button, event) {
			var op = button.getText();
			var ajax = Ext.Ajax.request({
				url : 'monitor/control',
				params : {
					ip : ip,
					jmxPort : jmxPort,
					contextName : contextName,
					operation : op
				},
				method : 'get',
				success : function(response) {
					Ext.getCmp('stopButton').enable();
					button.disable();

				}
			});
		}
	});

	var stopButton = Ext.create('Ext.Button', {
		id : 'stopButton',
		width : 55,
		height : 25,
		text : 'stop',
		margin : '0, 20, 0, 0',
		handler : function(button, event) {
			var op = button.getText();
			var ajax = Ext.Ajax.request({
				url : 'monitor/control',
				params : {
					ip : ip,
					jmxPort : jmxPort,
					contextName : contextName,
					operation : op
				},
				method : 'get',
				success : function(response) {
					Ext.getCmp('startButton').enable();
					button.disable();
				}
			});
		}
	});

	var reloadButton = Ext.create('Ext.Button', {
		id : 'reload',
		width : 55,
		height : 25,
		text : 'reload',
		margin : '0, 20, 0, 0',
		handler : function(button, event) {
			var op = button.getText();
			var ajax = Ext.Ajax.request({
				url : 'monitor/control',
				params : {
					ip : ip,
					jmxPort : jmxPort,
					contextName : contextName,
					operation : op
				},
				method : 'get',
				success : function(response) {
				}
			});
		}
	});

	formPanel.getForm().load({
		url : 'monitor/snap',
		waitMsg : 'Loading',
		params : {
			ip : ip,
			jmxPort : jmxPort,
			contextName : contextName
		},
		method : 'get',
		success : function(form, action) {
			var text = action.response.responseText;
			var result = Ext.decode(text);
			var id = result.data.appInstance.id;
			idField.setValue(id);

		}
	});

	var controlPanel = Ext.create('Ext.panel.Panel', {
		layout : 'anchor',
		title : '控制',
		bodyPadding : 5,
		boarder : false,
		height : 60,
		items : [startButton, stopButton, reloadButton]
	});

	var mostPopButton = Ext.create('Ext.Button', {
		id : 'mostPop',
		width : 120,
		height : 25,
		text : '显示最受欢迎的URL',
		margin : '0, 20, 0, 0',
		handler : function(button, event) {
		}
	});

	var timeDistributionButton = Ext.create('Ext.Button', {
		id : 'timeDistribution',
		width : 100,
		height : 25,
		text : '显示时间分布图',
		margin : '0, 20, 0, 0',
		handler : function(button, event) {

		}
	});

	var locationDistributionButton = Ext.create('Ext.Button', {
		id : 'locationDistribution',
		width : 100,
		height : 25,
		text : '显示地域分布图',
		margin : '0, 20, 0, 0',
		handler : function(button, event) {

		}
	});

	var analysisPanel = Ext.create('Ext.panel.Panel', {
		layout : 'anchor',
		title : '分析',
		bodyPadding : 5,
		boarder : false,
		height : 70,
		items : [mostPopButton, timeDistributionButton, locationDistributionButton]

	});

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
			url : 'servletmonitor/monitorConfigs?ip=' + ip + '&jmxPort=' + jmxPort + '&contextName=' + contextName,
			reader : {
				type : 'json',
				root : 'data'
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

	//The big panel
	var panel = Ext.create('Ext.panel.Panel', {
		renderTo : Ext.getBody(),
		layout : {
			type : 'vbox', // Arrange child items vertically
			align : 'stretch'
		},
		title : contextName + ' @ ' + ip,
		width : 420,
		height : 700,
		items : [formPanel, controlPanel, 
		// analysisPanel, 
		customMonitorPanel]
	});

	var hideMask = function() {
		Ext.get('loading').remove();
		Ext.fly('loading-mask').animate({
			opacity : 0,
			remove : true
		});
	};

	Ext.defer(hideMask, 200);

	/*
	 var generateData = (function() {
	 var data = [], i = 0, last = false, date = new Date(2011, 1, 1), seconds = +date, min = Math.min, max = Math.max, random = Math.random;
	 return function() {
	 data = data.slice();
	 data.push({
	 date : Ext.Date.add(date, Ext.Date.DAY, i++),
	 visits : min(100, max( last ? last.visits + (random() - 0.5) * 20 : random() * 100, 0)),
	 views : min(100, max( last ? last.views + (random() - 0.5) * 10 : random() * 100, 0)),
	 veins : min(100, max( last ? last.veins + (random() - 0.5) * 20 : random() * 100, 0))
	 });
	 last = data[data.length - 1];
	 return data;
	 };
	 })();

	 var group = false, groupOp = [{
	 dateFormat : 'M d',
	 groupBy : 'year,month,day'
	 }, {
	 dateFormat : 'M',
	 groupBy : 'year,month'
	 }];

	 function regroup() {
	 group = !group;
	 var axis = chart.axes.get(1), selectedGroup = groupOp[+group];
	 axis.dateFormat = selectedGroup.dateFormat;
	 axis.groupBy = selectedGroup.groupBy;

	 chart.redraw();
	 }

	 var store = Ext.create('Ext.data.JsonStore', {
	 fields : ['date', 'visits', 'views', 'veins'],
	 data : generateData()
	 });

	 var chart = Ext.create('Ext.chart.Chart', {
	 xtype : 'chart',
	 style : 'background:#fff',
	 store : store,
	 id : 'chartCmp',
	 axes : [{
	 type : 'Numeric',
	 grid : true,
	 minimum : 0,
	 maximum : 100,
	 position : 'left',
	 fields : ['views', 'visits', 'veins'],
	 title : 'Number of Hits',
	 grid : {
	 odd : {
	 fill : '#dedede',
	 stroke : '#ddd',
	 'stroke-width' : 0.5
	 }
	 }
	 }, {
	 type : 'Time',
	 position : 'bottom',
	 fields : 'date',
	 title : 'Day',
	 dateFormat : 'M d',
	 groupBy : 'year,month,day',
	 aggregateOp : 'sum',

	 constrain : true,
	 fromDate : new Date(2011, 1, 1),
	 toDate : new Date(2011, 1, 7)
	 }],
	 series : [{
	 type : 'line',
	 axis : 'left',
	 xField : 'date',
	 yField : 'visits',
	 label : {
	 display : 'none',
	 field : 'visits',
	 renderer : function(v) {
	 return v >> 0;
	 },
	 'text-anchor' : 'middle'
	 },
	 markerConfig : {
	 radius : 5,
	 size : 5
	 }
	 }, {
	 type : 'line',
	 axis : 'left',
	 xField : 'date',
	 yField : 'views',
	 label : {
	 display : 'none',
	 field : 'visits',
	 renderer : function(v) {
	 return v >> 0;
	 },
	 'text-anchor' : 'middle'
	 },
	 markerConfig : {
	 radius : 5,
	 size : 5
	 }
	 }, {
	 type : 'line',
	 axis : 'left',
	 xField : 'date',
	 yField : 'veins',
	 label : {
	 display : 'none',
	 field : 'visits',
	 renderer : function(v) {
	 return v >> 0;
	 },
	 'text-anchor' : 'middle'
	 },
	 markerConfig : {
	 radius : 5,
	 size : 5
	 }
	 }]
	 });

	 var timeAxis = chart.axes.get(1);

	 var intr = setInterval(function() {
	 var gs = generateData();
	 var toDate = timeAxis.toDate, lastDate = gs[gs.length - 1].date, markerIndex = chart.markerIndex || 0;
	 if(+toDate < +lastDate) {
	 markerIndex = 1;
	 timeAxis.toDate = lastDate;
	 timeAxis.fromDate = Ext.Date.add(Ext.Date.clone(timeAxis.fromDate), Ext.Date.DAY, 1);
	 chart.markerIndex = markerIndex;
	 }
	 store.loadData(gs);
	 }, 100);
	 */
	/*
	 var showCpuButton = Ext.create('Ext.Button', {
	 text : 'Show CPU Statistics',
	 margin : '0, 20, 0, 0',
	 handler : function() {
	 Ext.create('Ext.Window', {
	 width : 800,
	 height : 600,
	 maximizable : true,
	 title : 'Live Updated Chart',
	 layout : 'fit',
	 items : [chart]
	 }).show();
	 }
	 });

	 var showMemoryButton = Ext.create('Ext.Button', {
	 text : 'Show Memory Statistics',
	 handler : function() {
	 Ext.create('Ext.Window', {
	 width : 800,
	 height : 600,
	 maximizable : true,
	 title : 'Live Updated Chart',
	 layout : 'fit',
	 items : [chart]
	 }).show();
	 }
	 });
	 */

	/*
	 var showChartPanel = Ext.create('Ext.panel.Panel', {
	 layout : 'anchor',
	 title : 'View Statistics Chart',
	 bodyPadding : 5,
	 boarder : false,
	 height : 100,
	 items : [showCpuButton, showMemoryButton]
	 });
	 */

});
