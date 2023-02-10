package example.micronaut;
import io.micronaut.function.aws.MicronautRequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import jakarta.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FunctionRequestHandler extends MicronautRequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Inject
    ObjectMapper objectMapper;

    @Override
    public APIGatewayProxyResponseEvent execute(APIGatewayProxyRequestEvent input) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
 

        final String inputBody = input.getBody();

        try {

            if( inputBody != null ){
                response.setBody(inputBody);
                response.setStatusCode(200);
                return response;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        response.setBody("{\"message\":\"Invalid input!\"}");
        response.setStatusCode(400);
        response.setHeaders(Map.of(
                        "x-amzn-ErrorType", "InvalidInputException:http://internal.amazon.com/coral/com.amazon.raphackdemo/",
                        "Content-Type","application/json"
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

