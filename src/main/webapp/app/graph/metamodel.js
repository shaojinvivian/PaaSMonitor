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
function Phym(name) {
	this.name = name;
}

Phym.prototype.ip = null;
Phym.prototype.username = null;
Phym.prototype.password = null;

Phym.prototype.clone = function() {
	return mxUtils.clone(this);
}

/*Definition of Vim*/
function Vim(name) {
	this.name = name;
}

Vim.prototype.ip = null;

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
function AppServer(name) {
	this.name = name;
	this.ip = null;
	this.jmxPort = null;
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
function AppInstance(name) {
	this.name = name;
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
function PaaSUser(name)	{
	this.name = name;	
}

PaaSUser.prototype.clone = function() {
	return mxUtils.clone(this);
}