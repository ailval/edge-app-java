package local.test;

import com.qingcloud.iot.core.config.PropertiesCfgUtil;

import java.io.IOException;

public class TestPropertiesCfg {

    public static void main(String[] args) throws Exception {
        testPropertiesCfg();
    }

    private static void testPropertiesCfg() throws IOException {
        String value = PropertiesCfgUtil.getValue("protocol");
        System.out.println("protocol:" + value);

        PropertiesCfgUtil.getValue("hubAddr");
        System.out.println("hubAddr:" + PropertiesCfgUtil.getValue("hubAddr"));

        PropertiesCfgUtil.getValue("hubPort");
        System.out.println("hubPort:" + PropertiesCfgUtil.getValue("hubPort").toString());

        PropertiesCfgUtil.getValue("appId");
        System.out.println("appId:" + PropertiesCfgUtil.getValue("appId"));

        PropertiesCfgUtil.getValue("deviceId");
        System.out.println("deviceId:" + PropertiesCfgUtil.getValue("deviceId"));

        PropertiesCfgUtil.getValue("thingId");
        System.out.println("thingId:" + PropertiesCfgUtil.getValue("thingId"));

    }

}
