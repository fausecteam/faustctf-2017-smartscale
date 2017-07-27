package ninja.faust.smartscale;

import java.lang.reflect.InvocationTargetException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Data {
	public Data(int weight, double size, double fat_quotient, String comment) {
		this.weight = weight;
		this.size = size;
		this.fat_quotient = fat_quotient;
		this.comment = comment;
	}
	public Data(String str) throws JSONException {
		this(new JSONObject(str));
	}
	public Data(JSONObject json) throws JSONException {
		this(json, true);
	}
	public Data(JSONObject json, boolean checkHash) throws JSONException {
		weight = json.getDouble("weight");
		size = json.getDouble("size");
		fat_quotient = json.getDouble("fat_quotient");
		bmi = json.optDouble("bmi", -1d);
		comment = json.getString("comment");
		if (checkHash) {
			String hash = json.getString("hash");
			// Check if hash is correct
			if (hash == null || !hash.equals(AwesomeHash.digest(toJSONBase().toString()))) {
				throw new JSONException("");
			}
		}
		// Second vulnerability: "calculateBMI" is called as a task,
		// but "magic" can also be called...
		JSONArray tasks = json.optJSONArray("tasks");
		if (tasks == null) {
			tasks = new JSONArray("[\"calculateBMI\"]");
		}
		for (int i = 0; i < tasks.length(); ++i) {
			String task = tasks.getString(i);
			try {
				Data.class.getMethod(task).invoke(this);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException
					| SecurityException e) {
				throw new RuntimeException();
			}
		}
	}
	
	/**
	 * Weight in Kg
	 */
	double weight = 0.0;
	/**
	 * Size in meters
	 */
	double size = 0.0;
	/**
	 * Another attribute: value doesn't matter
	 */
	double fat_quotient = 0.0;
	/**
	 * Body-Mass-Index (If calculated)
	 */
	double bmi = -1.0;
	/**
	 * Should contain the flag
	 */
	String comment;
	
	/**
	 * Creates a basic JSON object with all the relevant data (before hashing).
	 */
	private JSONObject toJSONBase() {
		try {
			JSONObject json = new JSONObjectSorted();
			// Add data to json object
			json.put("weight", weight);
			json.put("size", size);
			json.put("fat_quotient", fat_quotient);
			json.put("bmi", bmi);
			json.put("comment", comment);
			return json;
		} catch (JSONException e) {
			return null;
		}
	}
	/**
	 * Creates a full JSON object (including the hash).
	 */
	public JSONObject toJSON() {
		JSONObject json = toJSONBase();
		try {
			json.put("hash", AwesomeHash.digest(json.toString()));
		} catch (JSONException e) {
			return null;
		}
		return json;
	}
	
	@Override
	public String toString() {
		try {
			return toJSON().toString();
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	public void calculateBMI() {
		bmi = weight / (size * size);
	}
	
	public void magic() {
		Storage.getAllFlagIDs();
		throw new RuntimeException(); // Don't actually store the data
	}
}
