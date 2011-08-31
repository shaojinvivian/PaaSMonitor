Ext.define('PaaSMonitor.controller.Monitees', {
	extend : 'Ext.app.Controller',
	stores : ['Phyms', 'Vims', 'MoniteeTree', 'AppServers', 'AppInstances'],
	models : ['Phym', 'Vim', 'AppServer', 'AppInstance'],
	views : ['monitee.AddPhym', 
			'monitee.ConfigurePhyms',
			'monitee.ListPhyms',
			'monitee.AddAppServer', 
			'monitee.AddVims', 
			'monitee.ListVimsByPhym', 
			'monitee.ViewMonitees', 
			'monitee.ConfigureAppServer', 
			'monitee.ListAppServers', 
			'monitee.AddAppInstance', 
			'monitee.ListAppInstances'],

	init : function() {
		this.control({
			'addPhym button[action=save]' : {
				click : this.savePhym
			},
			'addAppServer button[action=save]' : {
				click : this.saveAppServer
			},
			'addVims button' : {
				click : this.saveVims
			},
			'configureAppServer' : {
				beforeactivate : this.onConfigureAppServerActivated
			},
			'configureAppServer button' : {
				click : this.configureAppServers
			},
			'configurePhyms' : {
				beforeactivate : this.onConfigurePhymsActivated
			},
			'configurePhyms button' : {
				click : this.configurePhyms
			},
			'viewMonitees' : {
				beforeactivate : this.onViewMoniteesActivated
			},
			'addAppInstance button' : {
				click : this.saveAppInstances
			}
		});
	},
	savePhym : function(button) {
		var form = button.up('form'), values = form.getValues(), panel = form.up('panel');
		uppanel = panel.up('panel');
		var phym = Ext.ModelManager.create(values, 'PaaSMonitor.model.Phym');
		var store = this.getVimsStore();		
		phym.save({
			success : function(phym, operation) {
				form.getForm().reset();
				store.getProxy().extraParams = {
					findVims : "ByPhym",
					phymId : phym.internalId
				};
				store.load();
				Ext.ComponentManager.get('add_vims-panel').setTitle('Phym ' + phym.get('name'));
				uppanel.layout.setActiveItem('add_vims-panel');
				store.getProxy().extraParams = {};				
			}
		});
	},
	saveAppServer : function(button) {
		var form = button.up('form'), values = form.getValues(), panel = form.up('panel');
		var uppanel = panel.up('panel');
		var appServer = Ext.ModelManager.create(values, 'PaaSMonitor.model.AppServer');			
		var instanceStore = this.getAppInstancesStore();	
		appServer.set("isMonitee",true);	
		appServer.save({
			success : function(appServer, operation) {
				form.getForm().reset();				
				instanceStore.getProxy().extraParams = {
					findAppInstances : "ByAppServer",
					appServerId : appServer.internalId
				};
				instanceStore.load();
				uppanel.layout.setActiveItem('add_appinstance-panel');
				instanceStore.getProxy().extraParams = {};	
			}
		});
	},
	saveVims : function(button) {
		this.getVimsStore().sync();
		var panel = button.up('panel').up('panel');
		Ext.MessageBox.alert('Success', 'You have successfully added monitees', function() {
			panel.layout.setActiveItem('start-panel');
		});
	},
	configureAppServers : function(button) {
		var serverStore = this.getAppServersStore();
		serverStore.sync();
		var idList = new Array();
		var server;
		serverStore.each(function(record) {
			if(record.get("isMonitee")) {
				idList.push(record.get("id"));
			}
		});
		var instanceStore = this.getAppInstancesStore();
		instanceStore.getProxy().extraParams = {
			findAppInstances : "ByAppServers",
			appServerIdList : idList
		};
		instanceStore.load();

		instanceStore.getProxy().extraParams = {};

		var panel = button.up('panel').up('panel');
		panel.layout.setActiveItem('add_appinstance-panel');
	},
	saveAppInstances : function(button) {
		this.getAppInstancesStore().sync();
		var panel = button.up('panel').up('panel');
		Ext.MessageBox.alert('Success', 'You have successfully added monitees', function() {
			panel.layout.setActiveItem('start-panel');
		});
	},
	onConfigureAppServerActivated : function(panel) {
		var serverStore = this.getAppServersStore();
		serverStore.load({
			callback : function(r, options, success) {
				if(success) {
					if(r.length <= 0) {
						panel.down('panel').hide();
						panel.down('button').hide();
						panel.down('#noAppServerBox').show();

					} else {
						panel.down('panel').show();
						panel.down('button').show();
						panel.down('#noAppServerBox').hide();
					}
				}
			}			
		});
	},
	configurePhyms : function(button){
		
	},
	onConfigurePhymsActivated : function(panel) {
		var phymStore = this.getPhymsStore();
		phymStore.load({
			callback : function(r, options, success) {
				if(success) {
					if(r.length <= 0) {
						panel.down('panel').hide();
						panel.down('button').hide();
						panel.down('#noPhymBox').show();

					} else {
						panel.down('panel').show();
						panel.down('button').show();
						panel.down('#noPhymBox').hide();
					}
				}
			}			
		});
	},
	
	onViewMoniteesActivated : function() {
		this.getMoniteeTreeStore().load();
	}
});
