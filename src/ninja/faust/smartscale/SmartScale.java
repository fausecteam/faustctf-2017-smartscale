package ninja.faust.smartscale;

import java.util.Scanner;

import org.json.JSONObject;

public class SmartScale {
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Expected storage path as first argument");
			return;
		}
		Storage.setDIR(args[0]);
		try {
			Scanner s = new Scanner(System.in);
			JSONObject json = new JSONObjectSorted(s.nextLine());
			s.close();
			String action = json.getString("action");
			if (action.equals("store")) {
				Storage.store(new Data(json.getJSONObject("data"), false));
				Storage.cleanup();
			} else if (action.equals("retrieve")) {
				Storage.retrieve(json.getString("flag_id"));
			}
		} catch (Exception e) {}
	}
}
