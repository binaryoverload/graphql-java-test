package uk.co.binaryoverload.graphqltester.data.objects;

import io.leangen.graphql.annotations.GraphQLNonNull;

public class User {

    @GraphQLNonNull
    private final String id;

    @GraphQLNonNull
    private final String name;

    @GraphQLNonNull
    private final int age;

    public User(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

}
