package com.apddemo.core.services.impl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.apddemo.core.configuration.CaptchaConfiguration;
import com.apddemo.core.services.CaptchaService;
import com.apddemo.core.utils.StringUtils;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

@Component(immediate = true, service = CaptchaService.class
        , configurationPid = "com.apddemo.core.services.impl.CaptchaServiceImpl")
public class CaptchaServiceImpl implements CaptchaService {

    public static final String SITE_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
    private static final Logger LOG = LoggerFactory.getLogger(CaptchaServiceImpl.class);

    @Reference
    private CaptchaConfiguration captchaConfiguration;

    @Override
    public Boolean validateCaptcha(String gRecaptchaResponse) {
        Boolean isValid = false;
        String secretKey = captchaConfiguration.getClientSecretKey();
        try {
            if (StringUtils.isNotBlank(gRecaptchaResponse) && StringUtils.isNotBlank(secretKey)) {
                //recaptcha verify
                URL verifyUrl = new URL(SITE_VERIFY_URL);
                // Open Connection to URL
                HttpsURLConnection conn = (HttpsURLConnection) verifyUrl.openConnection();

                // Add Request Header
                conn.setRequestMethod("POST");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");


                // Data will be sent to the server.
                String postParams = "secret=" + secretKey + "&response=" + gRecaptchaResponse;

                // Send Request
                conn.setDoOutput(true);

                // Get the output stream of Connection
                // Write data in this stream, which means to send data to Server.
                OutputStream outStream = conn.getOutputStream();
                outStream.write(postParams.getBytes());

                outStream.flush();
                outStream.close();

                // Response code return from server.
                int responseCode = conn.getResponseCode();

                if (LOG.isDebugEnabled()) {
                    LOG.debug("verify: got response code:" + responseCode);
                }

                if (responseCode == 200 || responseCode == 201) {
                    // Get the InputStream from Connection to read data sent from the server.
                    InputStream is = conn.getInputStream();
                    String jsonResponseStr = StringUtils.getStringFromInputStream(is);
                    LOG.info("verify: response string:" + jsonResponseStr);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("verify: response string:" + jsonResponseStr);
                    }
                    Gson gson = new Gson();
                    JsonElement element = gson.fromJson (jsonResponseStr, JsonElement.class);
                    if(element != null){
                        JsonObject jsonObj = element.getAsJsonObject();
                        if(jsonObj != null){
                            isValid = jsonObj.get("success").getAsBoolean();
                            LOG.info("jsonOBj:"+jsonObj);
                        }
                    }
                }
            }

        } catch (Exception e) {
            LOG.error("verify: encountered Exception", e);
        }
        return isValid;
    }
}
