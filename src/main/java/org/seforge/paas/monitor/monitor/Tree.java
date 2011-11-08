package org.seforge.paas.monitor.monitor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.management.ObjectName;

import org.seforge.paas.monitor.extjs.TreeNode;


public class Tree {
	private Map<String, TreeNode> nodes = new HashMap<String, TreeNode>();
	private TreeNode root = new TreeNode();

	private static final List<String> orderedKeyPropertyList = new ArrayList<String>();
	static {

		String keyPropertyList = System
				.getProperty("com.sun.tools.jconsole.mbeans.keyPropertyList");
		if (keyPropertyList == null) {
			orderedKeyPropertyList.add("type");
			orderedKeyPropertyList.add("j2eeType");
		} else {
			StringTokenizer st = new StringTokenizer(keyPropertyList, ",");
			while (st.hasMoreTokens()) {
				orderedKeyPropertyList.add(st.nextToken());
			}
		}
	}

	public TreeNode getRoot(){
		return this.root;
	}
	
	public void addMBeanToView(final ObjectName mbean) {

		// Add the new nodes to the MBean tree from leaf to root

		Dn dn = buildDn(mbean);
		if (dn.size() == 0)
			return;
		Token token = dn.getToken(0);
		TreeNode node = null;
		boolean nodeCreated = true;

		//
		// Add the node or replace its user object if already added
		//

		String hashKey = dn.getHashKey(token);
		if (nodes.containsKey(hashKey)) {
			// already in the tree, means it has been created previously
			// when adding another node
			node = nodes.get(hashKey);
			// sets the user object
			/*
			 * final Object data = createNodeValue(xmbean, token); final String
			 * label = data.toString(); final XNodeInfo userObject = new
			 * XNodeInfo( Type.MBEAN, data, label, mbean .toString());
			 */
			changeNodeValue(node, token);
			nodeCreated = false;
		} else {
			// create a new node
			node = createDnNode(mbean, token);
			if (node != null) {
				nodes.put(hashKey, node);
				nodeCreated = true;
			} else {
				return;
			}
		}

		//
		// Add (virtual) nodes without user object if necessary
		//

		for (int i = 1; i < dn.size(); i++) {
			TreeNode currentNode = null;
			token = dn.getToken(i);
			hashKey = dn.getHashKey(token);
			if (nodes.containsKey(hashKey)) {
				// node already present
				if (nodeCreated) {
					// previous node created, link to do
					currentNode = nodes.get(hashKey);
					addChildNode(currentNode, node);
					return;
				} else {
					// both nodes already present
					return;
				}
			} else {
				// creates the node that can be a virtual one
				if (token.getKeyDn().equals("domain")) {
					// better match on keyDn that on Dn
					currentNode = createDomainNode(dn, token);
					if (currentNode != null) {
						addChildNode(root, currentNode);
					}
				} else {
					currentNode = createSubDnNode(dn, token);
					if (currentNode == null) {
						// skip
						continue;
					}
				}
				nodes.put(hashKey, currentNode);
				addChildNode(currentNode, node);
				nodeCreated = true;
			}
			node = currentNode;
		}
	}

	public void changeNodeValue(TreeNode node, Token token) {
		node.setText(token.getKey());
	}

	public TreeNode createDnNode(ObjectName name, Token token) {
		TreeNode node = new TreeNode();
		node.setText(token.getValue());
		node.setLeaf(true);
		node.setId(name.getCanonicalName());
		return node;
	}

	public void addChildNode(TreeNode currentNode, TreeNode node) {
		currentNode.addChild(node);
	}

	public TreeNode createDomainNode(Dn dn, Token token) {
		TreeNode node = new TreeNode();
		node.setExpanded(false);
		node.setLeaf(false);
		node.setText(dn.getDomain());
		return node;
	}

	public TreeNode createSubDnNode(Dn dn, Token token) {
		TreeNode node = new TreeNode();
		node.setExpanded(false);
		node.setLeaf(false);
		node.setText(token.getValue());
		return node;
	}

	private Map<String, String> extractKeyValuePairs(String properties,
			ObjectName mbean) {
		String props = properties;
		Map<String, String> map = new LinkedHashMap<String, String>();
		int eq = props.indexOf("=");
		while (eq != -1) {
			String key = props.substring(0, eq);
			String value = mbean.getKeyProperty(key);
			map.put(key, value);
			props = props.substring(key.length() + 1 + value.length());
			if (props.startsWith(",")) {
				props = props.substring(1);
			}
			eq = props.indexOf("=");
		}
		return map;
	}

	private String getKeyPropertyListString(ObjectName mbean) {
		String props = mbean.getKeyPropertyListString();
		Map<String, String> map = extractKeyValuePairs(props, mbean);
		StringBuilder sb = new StringBuilder();
		// Add the key/value pairs to the buffer following the
		// key order defined by the "orderedKeyPropertyList"
		for (String key : orderedKeyPropertyList) {
			if (map.containsKey(key)) {
				sb.append(key + "=" + map.get(key) + ",");
				map.remove(key);
			}
		}
		// Add the remaining key/value pairs to the buffer
		for (Map.Entry<String, String> entry : map.entrySet()) {
			sb.append(entry.getKey() + "=" + entry.getValue() + ",");
		}
		String orderedKeyPropertyListString = sb.toString();
		orderedKeyPropertyListString = orderedKeyPropertyListString.substring(
				0, orderedKeyPropertyListString.length() - 1);
		return orderedKeyPropertyListString;
	}

	private Dn buildDn(ObjectName mbean) {

		String domain = mbean.getDomain();
		String globalDn = getKeyPropertyListString(mbean);

		Dn dn = buildDn(domain, globalDn, mbean);

		// update the Dn tokens to add the domain
		dn.updateDn();

		// reverse the Dn (from leaf to root)
		dn.reverseOrder();

		// compute the hashDn
		dn.computeHashDn();

		return dn;
	}

	/**
	 * Builds the Dn for the given MBean.
	 */
	private Dn buildDn(String domain, String globalDn, ObjectName mbean) {
		Dn dn = new Dn(domain, globalDn);
		String keyDn = "no_key";
		// if (isTreeView()) {
		String props = globalDn;
		Map<String, String> map = extractKeyValuePairs(props, mbean);
		for (Map.Entry<String, String> entry : map.entrySet()) {
			dn.addToken(new Token(keyDn, entry.getKey() + "="
					+ entry.getValue()));
		}
		// } else {
		// //flat view
		// dn.addToken(new Token(keyDn, "properties=" + globalDn));
		// }
		return dn;
	}

	public static class Dn {

		private String domain;
		private String dn;
		private String hashDn;
		private ArrayList<Token> tokens = new ArrayList<Token>();

		public Dn(String domain, String dn) {
			this.domain = domain;
			this.dn = dn;
		}

		public void clearTokens() {
			tokens.clear();
		}

		public void addToken(Token token) {
			tokens.add(token);
		}

		public void addToken(int index, Token token) {
			tokens.add(index, token);
		}

		public void setToken(int index, Token token) {
			tokens.set(index, token);
		}

		public void removeToken(int index) {
			tokens.remove(index);
		}

		public Token getToken(int index) {
			return tokens.get(index);
		}

		public void reverseOrder() {
			ArrayList<Token> newOrder = new ArrayList<Token>(tokens.size());
			for (int i = tokens.size() - 1; i >= 0; i--) {
				newOrder.add(tokens.get(i));
			}
			tokens = newOrder;
		}

		public int size() {
			return tokens.size();
		}

		public String getDomain() {
			return domain;
		}

		public String getDn() {
			return dn;
		}

		public String getHashDn() {
			return hashDn;
		}

		public String getHashKey(Token token) {
			final int begin = getHashDn().indexOf(token.getHashToken());
			return getHashDn().substring(begin, getHashDn().length());
		}

		public void computeHashDn() {
			final StringBuilder hashDn = new StringBuilder();
			final int tokensSize = tokens.size();
			for (int i = 0; i < tokensSize; i++) {
				Token token = tokens.get(i);
				String hashToken = token.getHashToken();
				if (hashToken == null) {
					hashToken = token.getToken() + (tokensSize - i);
					token.setHashToken(hashToken);
				}
				hashDn.append(hashToken);
				hashDn.append(",");
			}
			if (tokensSize > 0) {
				this.hashDn = hashDn.substring(0, hashDn.length() - 1);
			} else {
				this.hashDn = "";
			}
		}

		/**
		 * Adds the domain as the first token in the Dn.
		 */
		public void updateDn() {
			addToken(0, new Token("domain", "domain=" + getDomain()));
		}

		public String toString() {
			return tokens.toString();
		}
	}

	public static class Token {

		private String keyDn;
		private String token;
		private String hashToken;
		private String key;
		private String value;

		public Token(String keyDn, String token) {
			this.keyDn = keyDn;
			this.token = token;
			buildKeyValue();
		}

		public Token(String keyDn, String token, String hashToken) {
			this.keyDn = keyDn;
			this.token = token;
			this.hashToken = hashToken;
			buildKeyValue();
		}

		public String getKeyDn() {
			return keyDn;
		}

		public String getToken() {
			return token;
		}

		public void setValue(String value) {
			this.value = value;
			this.token = key + "=" + value;
		}

		public void setKey(String key) {
			this.key = key;
			this.token = key + "=" + value;
		}

		public void setKeyDn(String keyDn) {
			this.keyDn = keyDn;
		}

		public void setHashToken(String hashToken) {
			this.hashToken = hashToken;
		}

		public String getHashToken() {
			return hashToken;
		}

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}

		public String toString() {
			return getToken();
		}

		public boolean equals(Object object) {
			if (object instanceof Token) {
				return token.equals(((Token) object));
			} else {
				return false;
			}
		}

		private void buildKeyValue() {
			int index = token.indexOf("=");
			if (index < 0) {
				key = token;
				value = token;
			} else {
				key = token.substring(0, index);
				value = token.substring(index + 1, token.length());
			}
		}
	}

}
