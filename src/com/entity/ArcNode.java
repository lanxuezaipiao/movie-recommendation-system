package com.entity;

import java.io.Serializable;

/**
 * ArcNode.java(implements Serializable)
 * 
 * @description basic information about table node
 */
public class ArcNode implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// vertex id
	public int adjvex;
	
	// next node
    public ArcNode nextarc;
}
