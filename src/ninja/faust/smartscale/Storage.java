package ninja.faust.smartscale;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Storage {

	protected static File DIR = null;
	
	public static void setDIR(String dir) {
		DIR = new File(dir);
		if (!DIR.exists()) {
			DIR.mkdir();
		}
		else if (!DIR.isDirectory()) {
			throw new RuntimeException(dir + " is not a directory.");
		}
	}
	
	/**
	 * Generates a new Flag ID by adding 10 to the last Flag ID
	 * ---> Vulnerability 1: Should be fixed by replacing this method
	 * 		with a more random algorithm.
	 */
	private static String generateId() {
		File[] files = DIR.listFiles();
		if (files != null) {
			Arrays.sort(files);
			for (int i = files.length - 1; i >= 0; --i) {
				try {
					return Long.toHexString(Long.parseLong(files[i].getName(), 16) + 0xA);
				} catch (Exception e) {}
			}
		}
		return Long.toHexString(0x5f1eabc89d209a2L);
	}

	public static void cleanup() {
		long currTime = System.currentTimeMillis();

		// Get last cleanup timestamp
		long lastCleanup = 0;
		try {
			File f = new File(DIR, "last_cleanup");
			Scanner s = new Scanner(f);
			lastCleanup = s.nextLong();
			s.close();
			if (lastCleanup > currTime || lastCleanup < 0) {
				lastCleanup = 0;
			}
		} catch (Exception e) {
		}

		// Cleanup only every 3 minutes
		if (lastCleanup == 0 || lastCleanup + 3 * 60 * 1000 < currTime) {
			File[] files = DIR.listFiles();
			if (files != null) {
				for (int i = files.length - 1; i >= 0; --i) {
					try {
						// file should last for 8 ticks: 1tick = 3min
						if (!files[i].getName().equals("last_cleanup") && currTime - files[i].lastModified() > (8 * 3 * 60 * 1000)) {
							files[i].delete();
						}
					} catch (Exception e) {
					}
				}

				// Save last cleanup timestamp
				try {
					File f = new File(DIR, "last_cleanup");
					FileOutputStream fs = new FileOutputStream(f);
					fs.write(Long.toString(currTime).getBytes());
					fs.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * Stores a data object to disk.
	 */
	public static String store(Data data) {
		String id = generateId();
		File f = new File(DIR, id);
		JSONObject result = new JSONObjectSorted();
		try {
			FileOutputStream fs = new FileOutputStream(f);
			JSONObject json = data.toJSON();
			json.put("id", id);
			fs.write((json.toString() + "\n").getBytes());
			fs.close();
			result.put("status", "ok");
			result.put("flag_id", id);
			System.out.println(result);
			return id;
		} catch (IOException | JSONException e) {
			try {
				result.put("status", "error");
				System.out.println(result);
			} catch (JSONException e1) {}
			return null;
		}
	}
	
	/**
	 * Prints the data with flag id 'id' to stdout.
	 */
	public static Data retrieve(String id) {
		File f = new File(DIR, id);
		JSONObject result = new JSONObjectSorted();
		try {
			Scanner s = new Scanner(f);
			s.useDelimiter("\\Z");
			String content = s.next();
			s.close();
			JSONObject json = new JSONObject(content);
			String currentid = json.getString("id");
			if (currentid == null || !currentid.equals(id)) {
				throw new JSONException("");
			}
			Data data = new Data(json);
			result.put("status", "ok");
			result.put("data", data.toJSON());
			return data;
		} catch (FileNotFoundException | JSONException e) {
			try {
				result.put("status", "error");
			} catch (JSONException e1) {}
			return null;
		} finally {
			System.out.println(result);
		}
	}
	
	/**
	 * Prints a list with all flag IDs to stdout.
	 */
	public static List<String> getAllFlagIDs() {
		List<String> l = new ArrayList<>();
		File[] files = DIR.listFiles();
		for (File f : files) {
			try {
				if (Long.parseLong(f.getName(), 16) > 0) {
					l.add(f.getName());
				}
			} catch (Exception e) {}
		}
		JSONObject result = new JSONObjectSorted();
		try {
			JSONArray ids = new JSONArray();
			for (String id : l) {
				ids.put(id);
			}
			result.put("status", "ok");
			result.put("flag_ids", ids);
			System.out.println(result);
		} catch (JSONException e) {}
		return l;
	}
}
