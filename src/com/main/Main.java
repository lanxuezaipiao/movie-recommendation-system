package com.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import com.entity.Movie;
import com.entity.User;
import com.service.Predict;
import com.service.SocialGraph;
import com.service.UserTraining;

/**
 * Main.java
 * 
 * @description get all essential data(training set, social graph)
 *              and predict unrated movies
 *
 */
public class Main {
	
	/**
	 * Predict unrated movies
	 */
	public static void PredictUnrated() {

		// initialize all essential object
		UserTraining ut = new UserTraining();
		Predict predict = new Predict();
		SocialGraph sg = new SocialGraph();
		
		// get all users and all movies
		List<User> userList = new ArrayList<User>();
		List<Movie> movieList = new ArrayList<Movie>();
		ut.GetUMList(userList, movieList);
		
		int uSize = userList.size();
		int mSize = movieList.size();
		
		long startTime, endTime;
		
		startTime = System.currentTimeMillis();
		
		byte rating[][] = new byte[uSize][mSize];
		
		// serialization object input/output stream
		ObjectInputStream is = null;
		ObjectOutputStream os = null;
	
		// read rating matrix object stream if exist
		// or call function to generate and write to file if not exist
		if (new File("res/rating.txt").exists()) {
			System.out.println("rating matrix exist.");

			try {
				is = new ObjectInputStream(new FileInputStream("res/rating.txt"));
				rating = (byte[][]) is.readObject();
				// close input stream
				is.close();
			} catch (Exception e) {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				e.printStackTrace();
			}
		} else {
			System.out
					.println("rating matrix don't exist.\nGenerating...Please wait...");
			rating = ut.GetTrainingSet("res/training_set.txt", userList, movieList);

			try {
				os = new ObjectOutputStream(new FileOutputStream(
						"res/rating.txt"));
				os.writeObject(rating);
				// close output stream
				os.close();
			} catch (Exception e) {
				if (os != null) {
					try {
						os.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				e.printStackTrace();
			}
		}
		
		endTime = System.currentTimeMillis(); 

		System.out.println("read rating matrix time: " + (endTime - startTime) + "ms");  
		
		startTime = System.currentTimeMillis();
		
		// read social graph object stream if exist
	    // or call function to generate and write to file if not exist
		if(new File("res/graph.txt").exists()) {
			System.out.println("graph exist.");
			is = null;
			
			try {
				is = new ObjectInputStream(new FileInputStream(
						"res/graph.txt"));
				sg = (SocialGraph) is.readObject();
				// close input stream
				is.close();
			} catch (Exception e) {
				if(is != null) {
					try {
						is.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				e.printStackTrace();
			}
		}
		else {
			System.out.println("graph don't exist.\nGenerating...Please wait...");
			sg = sg.CreateDG("res/user_social.txt", userList);
			os = null;
			
			try {
				os = new ObjectOutputStream(
						new FileOutputStream("res/graph.txt"));
				os.writeObject(sg);
				// close output stream
				os.close();
			} catch (IOException e) {
				if(os != null) {
					try {
						os.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				e.printStackTrace();
			}
		}
		
		endTime = System.currentTimeMillis(); 

		System.out.println("read graph time: " + (endTime - startTime) + "ms");  
	
		startTime = System.currentTimeMillis();
		
		predict.PredictUnratedMovies(rating, sg, "res/predict.txt", userList, movieList);
		
		endTime = System.currentTimeMillis();

		System.out.println("predict time: " + (endTime - startTime) + "ms");
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PredictUnrated();
	}

}
