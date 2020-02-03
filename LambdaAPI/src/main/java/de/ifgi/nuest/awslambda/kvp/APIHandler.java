package de.ifgi.nuest.awslambda.kvp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author Daniel Nüst
 */
public class APIHandler implements RequestStreamHandler {

    private JSONParser parser = new JSONParser();

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        JSONObject responseJson = new JSONObject();

        try {
            JSONObject event = (JSONObject) parser.parse(reader);
            // JSONObject responseBody = new JSONObject();
            String responseBody = "Hello, thanks for getting in touch!";

            JSONObject pathParams = new JSONObject();
            JSONObject headerJson = new JSONObject();

            if (event.get("pathParameters") != null) {
                pathParams = (JSONObject) event.get("pathParameters");
                context.getLogger().log(String.format("Path parameters: %s", pathParams));
                responseBody = responseBody + "\n" + pathParams.toString();
            }

            if (event.get("queryStringParameters") != null) {
                JSONObject queryParams = (JSONObject) event.get("queryStringParameters");
                context.getLogger().log(String.format("Query string parameters: %s", queryParams));
                responseBody = responseBody + "\n" + queryParams.toString();
            }

            if (pathParams.values().contains("api/v1/svg")) {
                headerJson.put("x-handled-by", "SVG creator");
                headerJson.put("Content-Type", "image/svg+xml");

                URL url = new URL("http://worldtimeapi.org/api/ip");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                JSONObject time = (JSONObject) parser.parse(in);

                String template = "<svg xmlns=\"http://www.w3.org/2000/svg\" height=\"30\" width=\"400\">"
                        + "<text x=\"0\" y=\"15\" fill=\"red\">It is now %s in %s</text>" + "</svg>";

                responseBody = String.format(template, time.get("datetime"), time.get("timezone"));
            } else {
                headerJson.put("Content-Type", "plain/text");
            }

            responseJson.put("statusCode", 200);
            responseJson.put("headers", headerJson);
            responseJson.put("body", responseBody.toString());

        } catch (ParseException pex) {
            responseJson.put("statusCode", 400);
            responseJson.put("exception", pex.toString());
        }

        OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
        writer.write(responseJson.toString());
        writer.close();
    }
}
