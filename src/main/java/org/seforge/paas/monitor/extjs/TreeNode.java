package org.seforge.paas.monitor.extjs;
import java.util.ArrayList;
import java.util.List;

public class TreeNode {
	private String id;
	private String text;
	private Boolean leaf;
	private Boolean expanded;	
	private List<TreeNode> children = new ArrayList<TreeNode>();
	
	public TreeNode(){
		
	}
	
	public TreeNode(String text){
		this.text = text;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Boolean getLeaf() {
		return leaf;
	}
	public void setLeaf(Boolean leaf) {
		this.leaf = leaf;
	}
	public Boolean getExpanded() {
		return expanded;
	}
	public void setExpanded(Boolean expanded) {
		this.expanded = expanded;
	}
	public List<TreeNode> getChildren() {
		return children;
	}
	public void setChildren(List<TreeNode> children) {
		this.children = children;
	}
	
	public void addChild(TreeNode node){
		this.children.add(node);
	}
	
}
