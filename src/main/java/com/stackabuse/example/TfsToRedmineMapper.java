package com.stackabuse.example;

import java.util.HashMap;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.json.JSONArray;
import org.json.JSONObject;

public class TfsToRedmineMapper implements Processor {

	static final int REDMINE_CF_TFS_ID = 293;
	static final int REDMINE_PROJECT_ID = 525;
	static final String REDMINE_HOST = "http://10.0.63.81";
	
	private final static HashMap<String, Integer> REDMINE_TRACKER_ID = new HashMap<String, Integer>();
	static
	{
		REDMINE_TRACKER_ID.put("Задача", 6);
		REDMINE_TRACKER_ID.put("Ошибка", 1);
		REDMINE_TRACKER_ID.put("Элемент невыполненной работы по продукту", 7);
		
	}
	
	private final static HashMap<String, Integer> REDMINE_STATUD_ID = new HashMap<String, Integer>();
	static
	{
		REDMINE_STATUD_ID.put("Список задач", 1);
		REDMINE_STATUD_ID.put("Выполняется", 2);
		REDMINE_STATUD_ID.put("Решена", 3);
		REDMINE_STATUD_ID.put("Удалено", 6);
	}
	

	@Override
	public void process(Exchange exchange) throws Exception {

		String json = exchange.getIn().getBody(String.class);
		JSONObject obj = new JSONObject(json);

		String message_event_type = obj.getString("eventType");

		if (message_event_type.equals("workitem.created")) {
			String subject = obj.getJSONObject("resource").getJSONObject("fields").getString("System.Title");

			// redmine custom fields
			int tfs_id = obj.getJSONObject("resource").getInt("id");
			JSONObject cf_tfs_id = new JSONObject();

			cf_tfs_id.put("value", tfs_id);
			cf_tfs_id.put("id", REDMINE_CF_TFS_ID);

			JSONArray custom_fields = new JSONArray();
			custom_fields.put(cf_tfs_id);

			// create json for redmine
			JSONObject issue = new JSONObject();

			issue.put("project_id", REDMINE_PROJECT_ID);
			issue.put("subject", subject);
			issue.put("custom_fields", custom_fields);
			issue.put("tracker_id", REDMINE_TRACKER_ID.get(obj.getJSONObject("resource").getJSONObject("fields").getString("System.WorkItemType")));
			issue.put("status_id", REDMINE_STATUD_ID.get(obj.getJSONObject("resource").getJSONObject("fields").getString("System.State")));
			issue.put("priority_id", obj.getJSONObject("resource").getJSONObject("fields").getInt("Microsoft.VSTS.Common.Priority"));

			if (obj.getJSONObject("resource").getJSONObject("fields").has("System.Description")) {
				String description = obj.getJSONObject("resource").getJSONObject("fields")
						.getString("System.Description");
				issue.put("description", description);
			}
			
			if (obj.getJSONObject("resource").getJSONObject("fields").has("System.AssignedTo")) {
				String tfs_assigned_to = obj.getJSONObject("resource").getJSONObject("fields").getString("System.AssignedTo");
				String login = tfs_assigned_to.substring(tfs_assigned_to.indexOf("\\") + 1, tfs_assigned_to.indexOf(">"));
			//	issue.put("assigned_to_id", redmine_user_id);
			}


			String redmine_json = new JSONObject().put("issue", issue).toString();

			exchange.getOut().setBody(redmine_json);

			// set message headers
			String url_create = REDMINE_HOST + "/issues.json";
			exchange.getOut().setHeader("url", url_create);
			exchange.getOut().setHeader(Exchange.HTTP_METHOD, "POST");
		}
		


}
}
