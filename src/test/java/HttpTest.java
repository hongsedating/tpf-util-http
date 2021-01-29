import com.tpf.util.http.HttpUtil;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class HttpTest {
    private static Logger log = LoggerFactory.getLogger(HttpTest.class);
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
            log.info(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGet() throws NoSuchAlgorithmException, KeyManagementException, IOException, HttpException {
        String url = "https://www.baidu.com/";
        String result = HttpUtil.get(url, null, null);
        log.info(result);
    }

    @Test
    public void testCharset() {
        log.info(Charsets.UTF_8.displayName());
    }


}
