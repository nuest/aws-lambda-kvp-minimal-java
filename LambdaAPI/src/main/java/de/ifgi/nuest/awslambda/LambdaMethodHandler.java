package de.ifgi.nuest.awslambda;

import com.amazonaws.services.lambda.runtime.Context;

/**
 * @author Daniel Nüst
 */
public class LambdaMethodHandler {

    public String handleRequest(String input, Context context) {
        context.getLogger().log("Input: " + input);
        return "Hello World - " + input;
    }
    
}