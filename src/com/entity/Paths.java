package com.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Paths.java
 * 
 * @description save all shortest paths which have the same path length
 * 
 */
public class Paths {

	// total of paths
	public int pathCount = 0;

	// all shortest paths
	// key is path index and value is a list of all user id on the path
	public Map<Integer, List<Integer>> pathMap = new HashMap<Integer, List<Integer>>();

}
