package uk.co.binaryoverload.graphqltester;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.StaticDataFetcher;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import spark.Route;
import spark.Spark;

import java.util.Collection;

public class Main {

    public static void main(String[] args) {
        Spark.port(8080);
        Spark.options("/*", (request,response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if(accessControlRequestMethod != null){
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });
        Spark.before((request,response) -> {
            response.header("Access-Control-Allow-Origin", "*");
        });
        Spark.post("/graphql", graphqlRoute);
    }

    public static final Gson GSON = new GsonBuilder().create();

    private static Route graphqlRoute = (req, res) -> {
        String schema = "type Query{hello: String}";

        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);

        RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .type("Query", builder -> builder.dataFetcher("hello", new StaticDataFetcher("world")))
                .build();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

        GraphQLRequest request = GSON.fromJson(req.body(), GraphQLRequest.class);

        GraphQL build = GraphQL.newGraphQL(graphQLSchema).build();
        res.type("application/json");

        ExecutionResult executionResult = build.execute(request.getQuery(), request.getOperationName(), (Object) null);
        JsonObject graphQlResponse = new JsonObject();
        if (!executionResult.getErrors().isEmpty() || executionResult.getData() == null) {
            graphQlResponse.add("errors", GSON.toJsonTree(executionResult.getErrors()));
        }
        graphQlResponse.add("data", GSON.toJsonTree(executionResult.getData()));

        return GSON.toJson(graphQlResponse);
    };

}
