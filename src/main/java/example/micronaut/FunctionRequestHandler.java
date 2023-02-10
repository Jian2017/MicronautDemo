package example.micronaut;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.micronaut.function.aws.MicronautRequestHandler;
import jakarta.inject.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionRequestHandler extends MicronautRequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final String ATTRIBUTE = "string";
    @Inject
    ObjectMapper objectMapper;

    @Override
    public APIGatewayProxyResponseEvent execute(APIGatewayProxyRequestEvent input) {
        final String inputBody = input.getBody();

        if (inputBody == null) {
            return error();
        }

        final TypeFactory typeFactory = objectMapper.getTypeFactory();
        final MapType mapType = typeFactory.constructMapType(HashMap.class, String.class, String.class);

        Map<String, String> request = null;
        try {
            request = objectMapper.readValue(inputBody, mapType);
        } catch (JsonProcessingException jpe) {
            return error();
        }

        if (!request.containsKey(ATTRIBUTE)) {
            return error();
        }

        final APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);
        response.setBody(inputBody);
        return response;
    }

    private APIGatewayProxyResponseEvent error() {
        final APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setBody("{\"message\":\"Invalid input!\"}");
        response.setStatusCode(400);
        response.setHeaders(Map.of(
                        "x-amzn-ErrorType", "InvalidInputException:http://internal.amazon.com/coral/com.amazon.raphackdemo/",
                        "Content-Type", "application/json"
                )
        );
        response.setMultiValueHeaders(Map.of(
                        "x-amzn-ErrorType", List.of("InvalidInputException:http://internal.amazon.com/coral/com.amazon.raphackdemo/"),
                        "Content-Type", List.of("application/json")
                )
        );
        response.setIsBase64Encoded(false);
        return response;
    }
}

