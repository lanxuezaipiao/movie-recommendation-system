package com.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import com.base.MovieBase;
import com.base.UserBase;
import com.entity.Movie;
import com.entity.Paths;
import com.entity.User;
import com.util.Consts;
import com.util.ReadFileUtil;

/**
 * UserTraining.java
 * 
 * @description some operations about training set 
 *              including storing all rating data and 
 *              calculate similarity between users.
 * 
 */
public class UserTraining {

	/**
	 * Get user list and movie list from user_history.txt
	 * 
	 * @param userList
	 *            save all users
	 * @param movieList
	 *            save all movies
	 */
	public void GetUMList(List<User> userList, List<Movie> movieList) {
		// read file and get list
		List<String> infoList = ReadFileUtil
				.ReadFileByLine("res/user_history.txt");
		
		// temporary user set
		Set<Integer> uSet = new HashSet<Integer>();
		// temporary movie set
		Set<Integer> mSet = new HashSet<Integer>();

		// the number of file lines
		int size = infoList.size();

		// temporary object
		User user;
		Movie movie;

		StringTokenizer st;
		int i, uId = 0, mId = 0;

		for (i = 0; i < size; i++) {
			// remove blank lines
			if (!infoList.get(i).equals("")) {
				// split data by "\t"
				st = new StringTokenizer(infoList.get(i), "\t");
				uSet.add(Integer.parseInt(st.nextToken()));
				mSet.add(Integer.parseInt(st.nextToken()));
			}
		}
		
		// merge users in user_social.txt and users in user_history.txt
		infoList = ReadFileUtil
				.ReadFileByLine("res/user_social.txt");
		size = infoList.size();

		for (i = 0; i < size; i++) {
			// remove blank lines
			if (!infoList.get(i).equals("")) {
				// split data by "\t"
				st = new StringTokenizer(infoList.get(i), "\t");

				if (st.countTokens() == 2) {
					uSet.add(Integer.parseInt(st.nextToken()));

					// a list of all his directly connected user id
					uSet.add(Integer.parseInt(st.nextToken(",").trim()));
					while (st.hasMoreElements()) {
						uSet.add(Integer.parseInt(st.nextToken()));
					}
				}
			}
		}
	
		// copy uSet info to userList
		Iterator<Integer> it = uSet.iterator();
		while (it.hasNext()) {
			user = new User();
			user.userId = uId++;
			user.userData = it.next();
			// save in list
			userList.add(user);
		}

		// copy mSet info to movieList
		it = mSet.iterator();
		while (it.hasNext()) {
			movie = new Movie();
			movie.movieId = mId++;
			movie.movieData = it.next();
			// save in list
			movieList.add(movie);
		}
		
		uSet = null;
		mSet = null;
	}
	
	/**
	 * Get the training set and save in rating matrix
	 * 
	 * @param sFileName
	 *            file name
	 * @param userList
	 *            save all users
	 * @param movieList
	 *            save all movies
	 * @return rating matrix (element value is byte type, if rating value is x,
	 *         then element value is 2 * x)
	 * 
	 */
	public byte[][] GetTrainingSet(String sFileName, List<User> userList, List<Movie> movieList) {

		// read file by line and save in a list
		List<String> infoList = ReadFileUtil.ReadFileByLine(sFileName);

		// rows is the number of users and columns is the number of movies
		int rows = userList.size();
		int columns = movieList.size();

		// return rating matrix
		byte rating[][] = new byte[rows][columns];
		
		// the number of rating data
		int size = infoList.size();

		StringTokenizer st;
		int rowIndex, colIndex;

		// process each rating data and save in rating matrix
		for (int i = 0; i < size; i++) {
			// remove blank lines
			if (!infoList.get(i).equals("")) {
				// split data by "\t"
				st = new StringTokenizer(infoList.get(i), "\t");
				// get user index
				rowIndex = UserBase.GetIdByUserData(userList,
						Integer.parseInt(st.nextToken()));
				// get movie index
				colIndex = MovieBase.GetIdByMovieData(movieList,
						Integer.parseInt(st.nextToken()));

				// transform into the byte type and save in rating matrix
				rating[rowIndex][colIndex] = (byte) (Float.parseFloat(st
						.nextToken()));
			}
		}

		// free memory space
		infoList = null;
		userList = null;
		movieList = null;

		return rating;
	}

	/**
	 * Get all users' average ratings about rated movies
	 * 
	 * @param rating
	 *            rating matrix
	 * @return all users' average ratings
	 */
	public float[] GetAvgRatings(byte[][] rating) {
		// the number of users
		int uSize = rating.length;
		// the number of movies
		int mSize = rating[0].length;

		// return avgs array
		float[] avgs = new float[uSize];

		// the total ratings about rated movies
		float totalRating = 0;
		// the number of rated movies
		int ratedCount = 0;

		int i, j = 0;

		// traverse rating matrix and calculate average rating
		for (i = 0; i < uSize; i++) {
			ratedCount = 0;
			totalRating = 0;

			for (j = 0; j < mSize; j++) {
				if (rating[i][j] != 0) {
					totalRating += rating[i][j];
					ratedCount++;
				}
			}

			avgs[i] = totalRating / ratedCount;
		}

		return avgs;
	}

	/**
	 * Calculate Similarity between a user and all his similar users
	 * 
	 * @param rating
	 *            rating matrix
	 * @param avgs
	 *            all users' average rating array
	 * @param uId
	 *            one user id
	 * @param uList
	 *            list of all his similar user id
	 * @return similarity between a user and all his similar users
	 */
	public float[] CalcAllSimilarity(byte[][] rating, float[] avgs, int uId,
			List<Integer> uList) {
		// the number of all his similar users
		int uSize = uList.size();
		// the number of movies
		int mSize = rating[0].length;

		// return similarity array
		float sims[] = new float[uSize];

		// some formula variables
		float avegRating1;
		float avegRating2;
		float rating1;
		float rating2;
		float total1;
		float total2;
		float total3;
		int i, j;

		// calculate similarity by formula
		for (i = 0; i < uSize; i++) {
			total1 = 0;
			total2 = 0;
			total3 = 0;

			// average rating
			avegRating1 = avgs[uId];
			avegRating2 = avgs[uList.get(i)];

			for (j = 0; j < mSize; j++) {
				rating1 = rating[uId][j];
				rating2 = rating[uList.get(i)][j];

				if (rating1 != 0 && rating2 != 0) {
					total1 += (rating1 - avegRating1) * (rating2 - avegRating2);
					total2 += (rating1 - avegRating1) * (rating1 - avegRating1);
					total3 += (rating2 - avegRating2) * (rating2 - avegRating2);
				}
			}

			// sims[i] = 0 if total1 = 0 or total2 = 0 or total3 = 0
			if (total1 != 0 && total2 != 0 && total3 != 0) {
				sims[i] = (float) (total1 / Math.sqrt(total2 * total3));
			}
		}

		return sims;
	}

	/**
	 * Get the best shortest path between two users
	 * 
	 * @param rating
	 *            rating matrix
	 * @param avgs
	 *            all users' average rating array
	 * @param p
	 *            save all possible shortest paths
	 * @return index of the best shortest path in p
	 */
	public int GetBestPath(byte[][] rating, float[] avgs, Paths p) {
		// return the best path index
		int minPathIndex = 0;

		float minS = Consts.MAX_DEVIATION, minF = Consts.MAX_DEVIATION;
		float s = 0, f = 0;
		// average similarity
		float avg = 0;

		// the number of all users in a shortest path
		int n = p.pathMap.get(0).size();

		// similarity array between all users on a shortest path
		// sims[0] is similarity between u0 and u1, sims[1] is similarity
		// between u1 and u2 and so on.
		float sims[] = new float[n - 1];

		int i = 0, j = 0, k = 0;

		// the number of all possible shortest paths
		int size = p.pathCount;

		// the list of all user id on a shortest path
		List<Integer> pathList = new ArrayList<Integer>();

		// some formula variables that calculate similarity
		float avegRating1;
		float avegRating2;
		float rating1;
		float rating2;
		float total1;
		float total2;
		float total3;

		// the number of movies
		int mSize = rating[0].length;

		// Calculate deviation, the minimal deviation is the best
		for (i = 0; i < size; i++) {
			pathList = p.pathMap.get(i);

			// calculate similarity between all users on a shortest path
			for (j = 0; j < n - 1; j++) {
				total1 = 0;
				total2 = 0;
				total3 = 0;

				// average rating
				avegRating1 = avgs[pathList.get(j)];
				avegRating2 = avgs[pathList.get(j + 1)];

				for (k = 0; k < mSize; k++) {
					rating1 = rating[pathList.get(j)][k];
					rating2 = rating[pathList.get(j + 1)][k];

					if (rating1 != 0 && rating2 != 0) {
						total1 += (rating1 - avegRating1)
								* (rating2 - avegRating2);
						total2 += (rating1 - avegRating1)
								* (rating1 - avegRating1);
						total3 += (rating2 - avegRating2)
								* (rating2 - avegRating2);
					}
				}

				// sims[i] = 0 if total1 = 0 or total2 = 0 or total3 = 0
				if (total1 != 0 && total2 != 0 && total3 != 0) {
					sims[j] = (float) (total1 / Math.sqrt(total2 * total3));
					avg += sims[j];
				}
			}

			// calculate average similarity
			avg = avg / (n - 1);

			for (j = 0; j < n - 1; j++) {
				s += (sims[j] - avg) * (sims[j] - avg);
				f += sims[j] - avg;
			}

			s = s / (n - 1);

			if (s < minS) {
				minS = s;
				minPathIndex = i;
			}

			if (f < minF) {
				minF = f;
			}

			if (s == minS && f < minF) {
				minPathIndex = i;
			}
		}

		return minPathIndex;
	}

	/**
	 * Calculate advanced user similarity between two users
	 * 
	 * @param rating
	 *            rating matrix
	 * @param avgs
	 *            all users' average rating array
	 * @param p
	 *            save all possible shortest paths
	 * @param sim
	 *            traditional user similarity calculated by CalcAllSimilarity()
	 * @return advanced user similarity
	 */
	public float AdvSimilarity(byte[][] rating, float[] avgs, Paths p, float sim) {
		// return value
		float advSim = 0;

		// the best shortest path index
		int pathId = GetBestPath(rating, avgs, p);

		// the list of all user id on a shortest path
		List<Integer> pathList = p.pathMap.get(pathId);
		// the number of all users in the shortest path
		int len = pathList.size();

		// calculate the social relation weight
		float weight = Consts.decIndex * (6 - len);

		// calculate advanced user similarity
		advSim = sim + (1 - sim) * weight;

		return advSim;
	}

}
