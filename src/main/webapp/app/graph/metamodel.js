/*本文件中包含构建元模型时各个类的定义*/

function Attribute(name) {
	this.name = name;
}

Attribute.prototype.type = 'String';
Attribute.prototype.category = '';
Attribute.prototype.mapped = false;
Attribute.prototype.mapping = 'none';
Attribute.prototype.value = '';
Attribute.prototype.clone = function() {
	return mxUtils.clone(this);
};

/*Definition of Phym*/
function Phym(object) {	
	// var obj = new Object();
            // obj.id = object.id;
            // obj.name = object.name;
            // return obj;
	
	this.name = object.name;
	this.ip = object.ip;	
}

Phym.prototype.name = null;
Phym.prototype.ip = null;

Phym.prototype.clone = function() {
	return mxUtils.clone(this);
}


/*Definition of Vim*/
function Vim(object) {
	this.name = object.name;
	this.ip = object.ip;
}

Vim.prototype.clone = function() {
	return mxUtils.clone(this);
}

/*Definition of Service*/
function Service(name)	{
	this.name = name;
	//貌似通过prototype定义属性和直接在构造函数里添加作用是一样的？
	this.ip = null;
	this.port = null;
}

Service.prototype.clone = function() {
	return mxUtils.clone(this);
}

/*Definition of AppServer*/
function AppServer(appserver) {
	var obj = new Object();
	obj.name = appserver.name;
	obj.ip = appserver.ip;
	obj.httpPort = appserver.httpPort;
	obj.jmxPort = appserver.jmxPort;
	obj.appInstances = appserver.appInstances;
	obj.status = appserver.status;
	return obj;
}

AppServer.prototype.clone = function() {
	return mxUtils.clone(this);
}

/*Definition of App*/
function App(name) {
	this.name = name;
}

App.prototype.type = null;

App.prototype.clone = function() {
	return mxUtils.clone(this);
}

/*Definition of AppInstance*/
function AppInstance(object) {
	this.name = object.name;
}

AppInstance.prototype.contextName = null;

AppInstance.prototype.clone = function() {
	return mxUtils.clone(this);
}
function MBeanAttribute(name) {
	this.name = name;
}

MBeanAttribute.prototype.objectName = null;

MBeanAttribute.prototype.clone = function() {
	return mxUtils.clone(this);
}

function Monitee(name) {
	this.name = name;
}

Monitee.prototype.clone = function() {
	return mxUtils.clone(this);
}


/*Definition of PaaSUser*/
function PaasUser(name)	{
	this.name = name;	
}

PaasUser.prototype.clone = function() {
	return mxUtils.clone(this);
}