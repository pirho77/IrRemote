package com.rngtng.irdude;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;

public class MainActivity extends Activity {
	Object irdaService;
	Method irWrite;
	SparseArray<String> irData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		irData = new SparseArray<String>();
		irData.put(
				R.id.buttonOn,
				hex2dec("0000 0067 0000 0006 0341 010E 005C 010E 005C 0168 005C 00BA 005C 005C 010E 028D"));
		irData.put(
				R.id.buttonOff,
				hex2dec("0000 0067 0000 0007 0341 010E 005D 010E 005D 005D 0168 005D 005D 010E 005D 00BA 005D 01D9"));
		irData.put(
				R.id.buttonUp,
				hex2dec("0000 0067 0000 0006 0341 010E 005C 010E 005C 01D9 005C 010E 005C 010E 005C 01D9"));
		irData.put(
				R.id.buttonDown,
				hex2dec("0000 0067 0000 0006 0341 010E 005D 010E 005D 01D9 005D 0168 005D 005D 005D 0233"));
		irData.put(
				R.id.buttonPreset,
				hex2dec("0000 0067 0000 0007 0341 010E 005D 010E 005D 0168 005D 00BA 005D 005D 00BA 005D 010E 0168"));
		
		irInit();
	}

	public void irInit() {
		irdaService = this.getSystemService("irda");
		Class c = irdaService.getClass();
		Class p[] = { String.class };
		try {
			irWrite = c.getMethod("write_irsend", p);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public void irSend(View view) {
		String data = irData.get(view.getId());
		if (data != null) {
			try {
				irWrite.invoke(irdaService, data);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	protected String hex2dec(String irData) {
		List<String> list = new ArrayList<String>(Arrays.asList(irData
				.split(" ")));
		list.remove(0); // dummy
		int frequency = Integer.parseInt(list.remove(0), 16); // frequency
		list.remove(0); // seq1
		list.remove(0); // seq2

		for (int i = 0; i < list.size(); i++) {
			list.set(i, Integer.toString(Integer.parseInt(list.get(i), 16)));
		}

		frequency = (int) (1000000 / (frequency * 0.241246));
		list.add(0, Integer.toString(frequency));

		irData = "";
		for (String s : list) {
			irData += s + ",";
		}
		return irData;
	}

}
