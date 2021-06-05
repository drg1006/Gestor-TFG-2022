package ubu.digit.security;

import org.json.JSONArray;
import org.json.JSONObject;

import ubu.digit.ui.entity.MoodleUser;
import ubu.digit.util.*;
import ubu.digit.webService.CoreUserGetUsersByField;

/**
 * 
 * 
 * @author Diana Bringas Ochoa
 */
public class PopulateMoodleUser {

	private WebService webService;

	public PopulateMoodleUser(WebService webService) {
		this.webService = webService;
	}

	public MoodleUser populateMoodleUser(String username, String host) {

		try {
			CoreUserGetUsersByField coreUserGetUsersByField = new CoreUserGetUsersByField();
			coreUserGetUsersByField.setUsername(username);
			JSONArray jsonArray = UtilMethods.getJSONArrayResponse(webService, coreUserGetUsersByField);
			return CreateMoodleUser(jsonArray, host);
		} catch (Exception e) {
			return null;
		}
	}
	
	public MoodleUser CreateMoodleUser(JSONArray jsonArray, String host) {
		JSONObject coreUserGetUsersByField = jsonArray.getJSONObject(0);
		MoodleUser moodleUser = new MoodleUser();
		moodleUser.setId(coreUserGetUsersByField.getInt(Constants.ID));
		moodleUser.setUserName(coreUserGetUsersByField.optString(Constants.USERNAME));
		moodleUser.setFullName(coreUserGetUsersByField.optString(Constants.FULLNAME));
		moodleUser.setEmail(coreUserGetUsersByField.optString(Constants.EMAIL));
		
		return moodleUser;
	}
}
