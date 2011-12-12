Ext.require(['Ext.Ajax', 'Ext.tip.QuickTipManager', 'Ext.form.*', 'Ext.window.MessageBox', 'Ext.window.Window', 'Ext.chart.*']);

Ext.onReady(function() {

	Ext.tip.QuickTipManager.init();

	var ip = getRequestParam('ip');
	var jmxPort = getRequestParam('jmxPort');
	var contextName = getRequestParam('contextName');

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
	var formPanel = Ext.create('Ext.form.Panel', {
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
			fieldLabel : 'Status'
		}, {
			xtype : 'displayfield',
			name : 'cpuPercent',
			fieldLabel : 'CPU Percentage'
		}, {
			xtype : 'displayfield',
			name : 'usedMemory',
			fieldLabel : 'Used Memory'
		}, {
			xtype : 'displayfield',
			name : 'availableMemory',
			fieldLabel : 'Available Memory'
		}, {
			xtype : 'displayfield',
			name : 'runningDuration',
			fieldLabel : 'Running Duration'
		}]
	});

	var showCpuButton = Ext.create('Ext.Button', {
		text : 'Show CPU Statistics',
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
	
	var startButton =  Ext.create('Ext.Button', {
		text: 'Start',
	});
	
	var stopButton =  Ext.create('Ext.Button', {
		text: 'Stop',
	});
	

	var buttonsPanel = Ext.create('Ext.panel.Panel', {
		layout : 'anchor',
		padding : 10,
		height : 200,
		items : [showCpuButton, startButton, stopButton]
	});

	var monitorPanel = Ext.create('Ext.panel.Panel', {
		renderTo : Ext.getBody(),
		layout : {
			type : 'vbox', // Arrange child items vertically
			align : 'stretch'
		},
		title : 'Current status of ' + contextName + ' @ ' + ip,
		width : 500,
		height : 400,
		items : [formPanel, buttonsPanel]
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
		}
	});

});
