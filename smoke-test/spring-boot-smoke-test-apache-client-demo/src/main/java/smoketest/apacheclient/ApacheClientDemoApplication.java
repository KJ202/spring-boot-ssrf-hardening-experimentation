package smoketest.apacheclient;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApacheClientDemoApplication implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ApacheClientDemoApplication.class);

    @Autowired
    private CloseableHttpClient httpClient;

    @Value("${spring.httpclient.my-feature-enabled}")
    private boolean myFeatureEnabled;

    public static void main(String[] args) {
        SpringApplication.run(ApacheClientDemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Custom feature enabled: {}", myFeatureEnabled);

        HttpGet request = new HttpGet("https://httpbin.org/get");
        try {
            var response = httpClient.execute(request, response -> {
                int statusCode = response.getCode();
                String body = EntityUtils.toString(response.getEntity());
                logger.info("Response status code: {}", statusCode);
                logger.info("Response body: {}", body);
                return null;
            });
        }
        catch (Exception e) {
            logger.error("Error executing request", e);
        }
    }
}
