package me.umov.androidandlua;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

/**
 * The main activity. Shows the fields on screen, calls the plugin engine (using native methods) and answers for requests
 * from the native code to update field values.
 */
public class MainActivity extends Activity {
	
	private static List<EditText> fields;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initFields();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		installPlugin("myscript");
	}
	
	private void initFields() {
		fields = Arrays.asList((EditText) findViewById(R.id.value_one), (EditText) findViewById(R.id.value_two), (EditText) findViewById(R.id.value_three));
		for (final EditText editText : fields) {
			editText.addTextChangedListener(new TextWatcher() {
				
				boolean recursivityControl = false;
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					if (!recursivityControl) {
						recursivityControl = true;
						Log.i("androidandlua", "EditText '" + editText.getTag() + "' changed, executing script...");
						String context = createContextTable(editText);
						Log.i("androidandlua", "Context created: " + context);
						callPluginFunction("onfieldvaluechangedbyuser", context);
						recursivityControl = false;
					}
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
				}
				
				@Override
				public void afterTextChanged(Editable s) {
				}
			});
		}
	}
	
	/**
	 * MANUAL creation of context table! Just made this way because JNI is complicated, but obviously it can be done better than this.
	 * 
	 * It also allows INJECTION!!1!
	 */
	private String createContextTable(EditText currentEditText) {
		StringBuilder builder = new StringBuilder();
		builder.append("return { ");
		builder.append("field = ").append(createFieldValueLuaTable(currentEditText)).append(", ");
		builder.append("fields = { ");
		for (EditText editText : fields) {
			builder.append("[\"").append(editText.getTag()).append("\"] = ");
			builder.append(createFieldValueLuaTable(editText)).append(", ");
		}
		builder.append(" } }");
		return builder.toString();
	}
	
	private String createFieldValueLuaTable(EditText field) {
		if (field.getText().toString().trim().length() == 0) { // nil value
			return "{ name = \"" + field.getTag() + "\" }";
		} else {
			return "{ name = \"" + field.getTag() + "\", value = \"" + field.getText().toString() + "\" }";
		}
	}
	
	/**
	 * Called from the native engine (C code).
	 * @param value the value to set
	 * @param fieldId the tag of the edit text
	 */
	public static void setValueToField(String value, String fieldId) {
		Log.i("androidandlua", "Setting value '" + value + "' to field '" + fieldId + "'");
		for (EditText editText : fields) {
			if (editText.getTag().equals(fieldId)) {
				Log.i("androidandlua", "Field found, setting value.");
				editText.setText(value);
			}
		}
	}
	
	private native void installPlugin(String scriptName);
	
	private native void callPluginFunction(String functionName, String context);

}
