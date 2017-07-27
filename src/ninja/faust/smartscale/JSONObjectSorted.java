package ninja.faust.smartscale;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JSONObjectSorted extends JSONObject {

	public JSONObjectSorted() {
		init(null);
	}

	public JSONObjectSorted(JSONTokener x) throws JSONException {
		super(x);
		init(null);
	}

	public JSONObjectSorted(@SuppressWarnings("rawtypes") Map map) {
		super(map);
		init(map);
	}

	public JSONObjectSorted(Object bean) {
		super(bean);
		init(null);
	}

	public JSONObjectSorted(String source) throws JSONException {
		super(source);
		init(null);
	}

	public JSONObjectSorted(JSONObject jo, String[] names) {
		super(jo, names);
		init(null);
	}

	public JSONObjectSorted(Object object, String[] names) {
		super(object, names);
		init(null);
	}

	public JSONObjectSorted(String baseName, Locale locale)
			throws JSONException {
		super(baseName, locale);
		init(null);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void init(Map _map) {
		try {
			// Hack to preserve order of items
			Field map = JSONObject.class.getDeclaredField("map");
			map.setAccessible(true);
			map.set(this, _map != null ? _map : new LinkedHashMap<>((Map)map.get(this)));
			map.setAccessible(false);
		} catch (Exception e) {}
	}
}
