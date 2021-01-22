package com.qingcloud.iot.core.config;

import java.io.*;
import java.util.Properties;

public class PropertiesCfgUtil {
    private final static String CfgFile = "edgeconf";
    private final static Properties properties = new Properties();

    public PropertiesCfgUtil() throws IOException {
        InputStream inputStream;
        inputStream = PropertiesCfgUtil.class.getClassLoader().getResourceAsStream(
                CfgFile);
        try {
            properties.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public PropertiesCfgUtil(String filePath) throws IOException {
        InputStream inputStream;
        inputStream = PropertiesCfgUtil.class.getClassLoader().getResourceAsStream(
                filePath);
        try {
            properties.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getValue(String key) throws IOException {
        new PropertiesCfgUtil();
        return properties.getProperty(key);
    }

    public static String getValueInfilePath(String key, String filePath) throws IOException {
        new PropertiesCfgUtil(filePath);
        return properties.getProperty(key);
    }
}
