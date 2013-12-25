package com.base;

import java.util.List;
import com.entity.User;

public class UserBase {

	/**
	 * get user's id by user data
	 * 
	 * @param userData
	 *            user data
	 * @return user id if exist or -1 if not exist
	 */
	public static int GetIdByUserData(List<User> userList, int userData) {
		// initialize id is -1
		int id = -1;
		int size = userList.size();

		// traverse by line
		for (int i = 0; i < size; i++) {
			if (userList.get(i).userData == userData) {
				id = userList.get(i).userId;
				break;
			}
		}

		return id;
	}

}
