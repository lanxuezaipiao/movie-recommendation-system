package com.service;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.base.MovieBase;
import com.base.UserBase;
import com.entity.Movie;
import com.entity.Paths;
import com.entity.User;
import com.util.ReadFileUtil;
import com.util.WriteFileUtil;

/**
 * Predict.java
 * 
 * @description predict ratings about unrated movies stored in predict.txt and
 *              append the result to predict.txt
 * 
 */
public class Predict {

	/**
	 * Predict ratings about unrated movies and append to predict.txt
	 * 
	 * @param rating
	 *            rating matrix
	 * @param sg
	 *            user social graph
	 * @param sFileName
	 *            file name
	 * @param userList
	 *            save all users
	 * @param movieList
	 *            save all movies
	 * @return true(predict OK) or false(predict FAIL)
	 */
	public boolean PredictUnratedMovies(byte[][] rating, SocialGraph sg,
			String sFileName, List<User> userList, List<Movie> movieList) {
		UserTraining ut = new UserTraining();

		List<String> infoList = ReadFileUtil.ReadFileByLine(sFileName);
		// DeleteFileUtil.DeleteFile(sFileName);

		// the number of items that need to predict
		int pSize = infoList.size();

		// the number of all users
		int uSize = rating.length;

		// save previous vertex id on the target shortest path
		String prePath[] = new String[sg.vexNum];;
		// save all user id on one shortest path
		int paths[];
		// save all possible shortest paths
		Paths p;

		float preRating = 0;
		float total1 = 0;
		float total2 = 0;
		int userData, userId, movieData, movieId;

		// advanced user similarity
		float advSim = 0;
		// similarity array between a user and all his similar users
		float sims[];
		int i, j;
		StringTokenizer st;

		// get average rating of all users
		float avgs[] = ut.GetAvgRatings(rating);

		List<Integer> uList;
		int sSize;
		int uId, uData = -1;
		long startTime, endTime;

		// predict one by one
		for (i = 0; i < pSize; i++) {
			total1 = 0;
			total2 = 0;

			startTime = System.currentTimeMillis();

			// split by "\t"
			st = new StringTokenizer(infoList.get(i), "\t");

			// get user and movie information that need to predict
			userData = Integer.parseInt(st.nextToken());
			movieData = Integer.parseInt(st.nextToken());
			userId = UserBase.GetIdByUserData(userList, userData);
			movieId = MovieBase.GetIdByMovieData(movieList, movieData);
			
			// for the same user, avoid repeating calculating shortest path
			if(userData != uData) {
				// get shortest path from userId to others
				sg.ShortestPath_Djst(sg, userId, prePath);
				
				uData = userData;
			}

			paths = new int[6];

			// calculate all his similar users and save in a list
			uList = new ArrayList<Integer>();

			for (j = 0; j < uSize; j++) {
				if (j != userId && rating[j][movieId] != 0) {
					uList.add(j);
				}
			}

			sSize = uList.size();
			sims = new float[sSize];
			sims = ut.CalcAllSimilarity(rating, avgs, userId, uList);

			// calculate predicted rating
			for (j = 0; j < sSize; j++) {
				advSim = sims[j];
				uId = uList.get(j);

				if (advSim != 0) {
					p = new Paths();
					sg.ShowPath(prePath, userId, uId, paths, 0, p);

					// have some paths
					if (p.pathCount > 0) {
						advSim = ut.AdvSimilarity(rating, avgs, p,
								advSim);
					}

					total1 += advSim
							* (rating[uId][movieId] - avgs[uId]);
					total2 += Math.abs(advSim);

					// free memory space
					p = null;
				}
			}

			if (total1 != 0 && total2 != 0)
				preRating = avgs[userId] + total1 / total2;
			else {
				preRating = avgs[userId];
			}

			// write predicted rating to predict.txt
			WriteFileUtil.WriteFile("predict.txt", userData + "\t"
					+ movieData + "\t" + (Math.round(preRating) + 0.0)
					+ "\r\n");

			endTime = System.currentTimeMillis();

			System.out.println("predict " + (i + 1) + ": "
					+ (endTime - startTime) + "ms");
		}

		return true;
	}

}
