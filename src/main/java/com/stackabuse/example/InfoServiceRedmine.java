package com.stackabuse.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

public class InfoServiceRedmine {
	public static final String REDMINE_HOST = "http://10.0.63.81";
	public static final int REDMINE_CF_TFS_ID = 293;
	public static final int REDMINE_CF_ACTIVITY = 294;
	public static final int REDMINE_PROJECT_ID = 525;
	public static final String SEARCH_USER_BY_LOGIN_URL_PATH = "/users.json?name=";
	public static final String SEARCH_ISSUES_BY_CUSTOM_FIELD_URL_PATH = "/issues.json?cf_";
	static final String REDMINE_API_KEY = "beb50ea768f5d16c96030a9dbbf3cb5c4a5ccdcd";

	public static Integer getUserIdByLogin(String login) throws Exception {
		try {
			URL url = new URL(REDMINE_HOST + SEARCH_USER_BY_LOGIN_URL_PATH + login);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			conn.setRequestProperty("X-Redmine-API-Key", REDMINE_API_KEY);
			int responseCode = conn.getResponseCode();
			System.out.println("GET Response Code :: " + responseCode);
			if (responseCode == HttpURLConnection.HTTP_OK) { // success
				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				// print result
//			System.out.println(response.toString());
				JSONObject obj = new JSONObject(response.toString());
				int users_count = obj.getInt("total_count");
				if (users_count > 0) {
					JSONArray users = obj.getJSONArray("users");
					JSONObject user_obj = users.getJSONObject(0);
					return user_obj.getInt("id");
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			System.out.println(e.toString());
			return null;
		}
	}

	// http://10.0.63.81/projects/525/issues.json?cf_293=403
	public static Integer getIssueIdByFtsTastId(Integer tfs_task_id) throws Exception {
		try {
			URL url = new URL(REDMINE_HOST + "/" + REDMINE_PROJECT_ID + SEARCH_ISSUES_BY_CUSTOM_FIELD_URL_PATH
					+ REDMINE_CF_TFS_ID + "=" + tfs_task_id);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			conn.setRequestProperty("X-Redmine-API-Key", REDMINE_API_KEY);
			int responseCode = conn.getResponseCode();
			System.out.println("GET Response Code :: " + responseCode);
			if (responseCode == HttpURLConnection.HTTP_OK) { // success
				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				// print result
//			System.out.println(response.toString());
				JSONObject obj = new JSONObject(response.toString());
				int users_count = obj.getInt("total_count");
				if (users_count > 0) {
					JSONArray issues = obj.getJSONArray("issues");
					JSONObject issue_obj = issues.getJSONObject(0);
					return issue_obj.getInt("id");
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			System.out.println(e.toString());
			return null;
		}

	}
}