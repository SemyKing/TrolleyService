package com.grigorij.trolleyservice.utils;

import android.content.Context;
import android.content.res.AssetManager;
import com.grigorij.trolleyservice.data.StaticValues;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Util {
	public static String getProperty(String key, Context context) throws IOException {
		Properties properties = new Properties();
		AssetManager assetManager = context.getAssets();
		InputStream inputStream = assetManager.open(StaticValues.PROPERTIES_FILE_NAME);
		properties.load(inputStream);
		return properties.getProperty(key);
	}
}
