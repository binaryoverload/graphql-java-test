package uk.co.binaryoverload.graphqltester.data.services;

import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import uk.co.binaryoverload.graphqltester.data.objects.User;

public class UserService {

    @GraphQLQuery
    public User user(@GraphQLNonNull String id) {
        return new User(id, "test name", 12);
    }

}
