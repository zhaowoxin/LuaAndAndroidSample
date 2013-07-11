package me.umov.androidandlua;

import java.util.List;

import android.util.Log;
import android.widget.EditText;

/**
 * Static functios that can be called by Lua scripts.
 */
public class LuaFunctions {
	
	private static List<EditText> fields;
	
	public static void setValueToField(String value, String fieldId) {
		Log.i("androidandlua", "Setting value '" + value + "' to field '" + fieldId + "'");
		for (EditText editText : fields) {
			if (editText.getTag().equals(fieldId)) {
				Log.i("androidandlua", "Field found, setting value.");
				editText.setText(value);
			}
		}
	}
	
	public static String getValueFromField(String fieldId) {
		Log.i("androidandlua", "Getting value from field '" + fieldId + "'");
		for (EditText editText : fields) {
			if (editText.getTag().equals(fieldId)) {
				String value = editText.getText().toString();
				Log.i("androidandlua", "Returning value '" + value + "'");
				return value;
			}
		}
		Log.i("androidandlua", "Field not found.");
		return "";
	}

	public static List<EditText> getFields() {
		return fields;
	}

	public static void setFields(List<EditText> fields) {
		LuaFunctions.fields = fields;
	}
	
}
