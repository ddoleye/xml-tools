package com.argo.xml.tools.stax;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class NodeCount {
	private String name;
	private String value;
	private int count;
	private List<NodeCount> childs;

	public NodeCount(String name) {
		this.name = name;
		this.count = 1;
		this.childs = null;
	}

	public String getName() {
		return this.name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void increaseRef() {
		this.count++;
	}

	public int getCount() {
		return this.count;
	}

	public Iterable<NodeCount> getChildren() {
		if (this.childs == null) {
			// Collections.emptyIterator()는 1.7 버전부터
			return Collections.emptyList();
		} else {
			return this.childs;
		}
	}

	/**
	 * name이 존재할 경우는 카운트를 증가하고 존재하지 않을 경우는 새로 추가한다
	 * 
	 * @param name
	 * @return
	 */
	public NodeCount addChild(String name) {
		NodeCount node = null;
		if (childs == null) {
			childs = new ArrayList<NodeCount>(1);
		} else {
			for (NodeCount child : childs) {
				if (child.name.equals(name)) {
					node = child;
					break;
				}
			}
		}

		if (node == null) {
			node = new NodeCount(name);
			childs.add(node);
		} else {
			node.increaseRef();
		}

		return node;
	}
}