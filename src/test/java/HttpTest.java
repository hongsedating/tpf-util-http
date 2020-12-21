import com.tpf.util.http.HttpUtil;
import org.junit.Test;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class HttpTest {
    @Test
    public void testPost() {
        final String SERVER_PORT = "https://ydgd3.zhizhangyi.com:3000";
        final String OPERATE_MESS_AND_RING_URL = "/emm/api/device/operateMessAndRing";
        final String TENANT = "uusafe999";
        try {
            String url = SERVER_PORT + OPERATE_MESS_AND_RING_URL;
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("param","{"
                    + "\"event\": \"9\","
                    + "\"companyCode\": \""+TENANT+"\","
                    + "\"deviceIds\": \"960379237636939776,1147,1143"
                    + "\"}");
            String result = HttpUtil.postForm(url, null, params);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGet() throws NoSuchAlgorithmException, KeyManagementException, IOException {
        String url = "https://www.baidu.com/";
        String result = HttpUtil.get(url, null, null);
        System.out.println(result);
    }


}
