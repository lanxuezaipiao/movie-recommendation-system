package com.entity;

import java.io.Serializable;

/**
 * VertexNode.java(implements Serializable)
 * 
 * @description basic information of vertex node
 *
 */
public class VertexNode implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// vertex id
	public int vexId;
	
	// vertex data
	public int vexData;
	
	// first node pointed to table
	public ArcNode firstarc;  
	
}
