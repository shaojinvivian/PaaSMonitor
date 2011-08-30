Ext.application({
	name : 'PaaSMonitor',
	appFolder : 'app',
	controllers : ['Monitees'],
	launch : function() {
		var contentPanel = {
			id : 'content-panel',
			region : 'center', 
			layout : 'card',
			margins : '2 5 5 0',
			activeItem : 0,
			border : false,
			items : [{
				id : 'start-panel',
				title : 'Welcome',
				layout : 'fit',
				bodyStyle : 'padding:25px',
				contentEl : 'start-div'  
			}, {
				id : 'add_phym-panel',
				xtype : 'addPhym'
			}, {
				id : 'add_vims-panel',
				xtype : 'addVims'
			}, {
				id : 'configure_appserver-panel',
				xtype : 'configureAppServer'
			}, {
				id : 'add_appinstance-panel',
				xtype : 'addAppInstance'
			}, {
				id : 'view_monitees-panel',
				xtype : 'viewMonitees'
			}]
		};

		var menuTree = Ext.create('Ext.tree.Panel', {
			id : 'menuTree',
			title : 'Navigation',
			region : 'west',
			split : true,
			height : 300,
			width : 200,
			rootVisible : false,
			autoScroll : true,
			store : Ext.create('Ext.data.TreeStore', {
				proxy : {
					type : 'ajax',
					url : 'data/menu-tree.txt'
				},
				root : {
					expanded : true
				}
			}),
			listeners : {
				'itemclick' : function(view, record) {
					if(record.get('leaf')) {
						Ext.getCmp('content-panel').layout.setActiveItem(record.getId() + '-panel');
					}
				}
			}
		});

		Ext.create('Ext.container.Viewport', {
			layout : 'border',
			renderTo : Ext.getBody(),
			items : [{
				region : 'north',
				contentEl : 'north-header',
				autoHeight : true,
				border : false,
				margins : '0 0 5 0'
				/*
				 }, {
				 region : 'south',
				 title : 'South Panel',
				 collapsible : true,
				 contentEl : 'south-footer',
				 split : true,
				 height : 100,
				 minHeight : 100
				 */
			}, menuTree, contentPanel]
		});
		
		var hideMask = function () {
            Ext.get('loading').remove();
            Ext.fly('loading-mask').animate({
                opacity:0,
                remove:true                
            });
        };

        Ext.defer(hideMask, 200);
	}
});
