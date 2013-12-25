package com.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.base.UserBase;
import com.entity.ArcNode;
import com.entity.Paths;
import com.entity.User;
import com.entity.VertexNode;
import com.util.Consts;
import com.util.ReadFileUtil;

/**
 * SocialGraph.java
 * 
 * @description some operations about users' social graph
 *              including creating graph and
 *              get shortest path between all users
 * 
 */
public class SocialGraph implements Serializable {

	private static final long serialVersionUID = 1L;

	// number of vertex
	public int vexNum;
	// number of arc
	public int arcNum;
	// all directed adjacency vertex nodes
	public VertexNode vextices[];

	/**
	 * Get the edge weight from one vertex node to another vertex node
	 * 
	 * @param sg
	 *            user social graph
	 * @param vexId1
	 *            one vertex id
	 * @param vexId2
	 *            another vertex id
	 * @return weight the edge weight if there is directed edge, 
	 *         else a max number if not
	 */
	public int GetWeight(SocialGraph sg, int vexId1, int vexId2) {
		int weight = Consts.MAX_NUM;
		
		// the first adjacency node
		ArcNode p = sg.vextices[vexId1].firstarc;

		while (p != null) {
			if (p.adjvex == vexId2) {
				// default weight is 1 if there is directed edge
				weight = 1; 
				break;
			}
			p = p.nextarc;
		}

		return weight;
	}

	/**
	 * Create directed graph using adjacency list
	 * 
	 * @param sFileName
	 *            file name
	 * @return a directed graph
	 */
	public SocialGraph CreateDG(String sFileName, List<User> userList) {
		// return social graph
		SocialGraph sg = new SocialGraph();

		// get social information between users
		List<String> infoList = ReadFileUtil.ReadFileByLine(sFileName);

		// key is one user id, value is a list of all his directly connected user id
		Map<Integer, List<Integer>> socialMap = new HashMap<Integer, List<Integer>>();
		List<Integer> socialList;

		ArcNode p;
		int arcNum = 0;
		int i = 0, j = 0;
		int size = infoList.size();
		int uData;
		StringTokenizer st;

		// process social list and save in socialMap
		for (i = 0; i < size; i++) {
			// remove blank lines
			if (!infoList.get(i).equals("")) {
				socialList = new ArrayList<Integer>();
				// split data by "\t"
				st = new StringTokenizer(infoList.get(i), "\t");

				if (st.countTokens() == 2) {
					// one user id
					uData = Integer.parseInt(st.nextToken());

					// a list of all his directly connected user id
					socialList.add(Integer.parseInt(st.nextToken(",").trim()));
					while (st.hasMoreElements()) {
						socialList.add(Integer.parseInt(st.nextToken()));
					}
					
					socialMap.put(uData, socialList);
				}
				
				// free memory space
				socialList = null;
			}
		}

		sg.vexNum = userList.size();
		sg.vextices = new VertexNode[sg.vexNum];

		int uSize = userList.size();

		// initialize vextices
		for (i = 0; i < uSize; i++) {
			sg.vextices[i] = new VertexNode();
			sg.vextices[i].vexId = userList.get(i).userId;
			sg.vextices[i].vexData = userList.get(i).userData;
			sg.vextices[i].firstarc = new ArcNode();
			sg.vextices[i].firstarc = null;
		}

		// create adjacency list
		List<Integer> toList = new ArrayList<Integer>();
		int toSize;
		
		for (int from : socialMap.keySet()) {
			toList = socialMap.get(from);
			toSize = toList.size();
			j = UserBase.GetIdByUserData(userList, from);
			if(j == -1) {
				System.out.println(from + " has no id.");
				return null;
			}

			// linked list operations
			for (i = 0; i < toSize; i++) {
				p = new ArcNode();
				p.adjvex = UserBase.GetIdByUserData(userList, toList.get(i));
				if(p.adjvex == -1) {
					System.out.println("from = " + from + " and " + toList.get(i) + " has no id.");
					return null;
				}
				p.nextarc = sg.vextices[j].firstarc;
				sg.vextices[j].firstarc = p;
				arcNum++;
			}
		}

		sg.arcNum = arcNum;

		// free memory space
		infoList = null;
		socialList = null;
		socialMap = null;
		userList = null;
		toList = null;
		p = null;

		return sg;
	}

	/**
	 * Get shortest distance from one vertex node to all others using Dijkstra
	 * algorithm
	 * 
	 * @param sg
	 *            user social graph
	 * @param vexId
	 *            one vertex id
	 * @param prePath
	 *            save previous vertex id on the target shortest path
	 * @return true if the vertex has adjacency nodes, false if not
	 */
	public boolean ShortestPath_Djst(SocialGraph sg, int vexId,
			String prePath[]) {
		// return value, default is true
		boolean ret = true;

		// save all shortest distance from vexId to others
		int distance[] = new int[sg.vexNum];

		// element value is 1 if visited, 0 if not visited
		int visited[] = new int[sg.vexNum];

		// save minimal vertex id on the shortest path
		Map<Integer, Integer> distMap = new HashMap<Integer, Integer>();

		// temporary map, save next minimal vertex id
		Map<Integer, Integer> tmpMap = new HashMap<Integer, Integer>();

		int size = sg.vexNum;
		int i = 0, m;

		// initialize all arrays
		for (i = 0; i < size; i++) {
			visited[i] = 0;
			distance[i] = Consts.MAX_NUM;
			prePath[i] = "";
		}

		ArcNode p = sg.vextices[vexId].firstarc;
		// have no shortest path, return false
		if (p == null) {
			return false;
		}

		// if there is directed path between vextId and others,
		// distance value is assigned to 1
		while (p != null) {
			// all edge weight is 1
			distance[p.adjvex] = 1;
			distMap.put(p.adjvex, 1);
			prePath[p.adjvex] = String.valueOf(vexId);
			p = p.nextarc;
		}

		visited[vexId] = 1;
		
		// find all the shortest paths
		for (m = 0; m < size - 1; m++) {
			for (int key : distMap.keySet()) {
				// if path length > 5, then return
				if (distMap.get(key) >= 5)
					return false;
				visited[key] = 1;
			}

			// update distance array
			for (int key : distMap.keySet()) {
				p = sg.vextices[key].firstarc;
								
				while(p != null) {
					if(visited[p.adjvex] == 0) {
						// all edge weight is 1
						if (!distMap.containsKey(p.adjvex)) {
							distance[p.adjvex] = distance[key] + 1;
							tmpMap.put(p.adjvex, distance[p.adjvex]);
						}

						prePath[p.adjvex] += key + "|";
						
					}
					p = p.nextarc;
				}
			}

			// clear distMap
			distMap.clear();

			// assign content of tmpMap to distMap and clear tmpMap
			for (int key : tmpMap.keySet()) {
				// remove the last "|" 
				if (prePath[key].endsWith("|")) {
					prePath[key] = prePath[key].substring(0,
							prePath[key].length() - 1);
				}
				distMap.put(key, tmpMap.get(key));
			}

			tmpMap.clear();

		}
		
		return ret;
	}

	/**
	 * Get all possible shortest paths
	 * 
	 * @param prePath
	 *            previous vertex id on the target shortest path
	 * @param v0
	 *            one vertex id
	 * @param v1
	 *            another vertex id
	 * @param paths
	 *            save all user id on one shortest path
	 * @param i
	 *            recursive variable
	 * @param p
	 *            save all possible shortest paths
	 */
	public void ShowPath(String prePath[], int v0, int v1, int paths[], int i,
			Paths p) {
		// recursive solution
		while (v1 != v0) {
			paths[i] = v1;
			
			if (prePath[v1].contains("|")) {
				String arrayStr[] = prePath[v1].split("\\|");
				
				for (int j = 0; j < arrayStr.length; j++) {
					v1 = Integer.parseInt(arrayStr[j]);
					ShowPath(prePath, v0, v1, paths, i + 1, p);
				}
				return;
			} else if (prePath[v1].equals("")) {
				return;
			} else {
				v1 = Integer.parseInt(prePath[v1]);
			}
			i++;
			paths[i] = v1;
		}

		// the list of all user id on one shortest path
		List<Integer> pathList = new ArrayList<Integer>();

		for (int j = i; j >= 0; j--) {
			// print shortest path
			// System.out.print(paths[j] + " ");
			pathList.add(paths[j]);
		}
		// System.out.println();

		p.pathMap.put(p.pathCount, pathList);
		p.pathCount++;

		return;
	}
}
