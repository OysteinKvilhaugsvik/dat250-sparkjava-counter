package no.hvl.dat250.rest.todos;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import static spark.Spark.*;


import java.util.*;
import java.util.regex.Pattern;


/**
 * Rest-Endpoint.
 */
public class TodoAPI {

    static Set<Todo> todos = new HashSet<>();
    static Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    public static void main(String[] args) {
        if (args.length > 0) {
            port(Integer.parseInt(args[0]));
        } else {
            port(8080);
        }


        after((req, res) -> res.type("application/json"));

        post("/todos", (req, res) -> {
            Gson gson = new Gson();

            Todo todo = gson.fromJson(req.body(), Todo.class);

            if(todo.getId() == null) {
                Todo createdTodo = new Todo(
                        new Random().nextLong(), todo.getSummary(), todo.getDescription());
                todos.add(createdTodo);
                return gson.toJson(createdTodo);
            }
            else {
                todos.add(todo);
                return gson.toJson(todo);
            }
        });

        get("/todos/:id", (req, res) -> {
            Gson gson = new Gson();
            Todo createdTodo;

            if(!pattern.matcher(req.params(":id")).matches()) {
                return String.format("The id \"%s\" is not a number!", req.params(":id"));
            }
            for(Todo todo : todos) {
                if(req.params(":id").equals(todo.getId().toString())) {
                    createdTodo = todo;
                    return gson.toJson(createdTodo);
                }
            }
            return String.format("Todo with the id \"%s\" not found!", req.params(":id"));
        });

        get("/todos", (req, res) -> {
            Gson gson = new Gson();

            return gson.toJson(todos);
        });

        put("/todos/:id", (req, res) -> {
            Gson gson = new Gson();
            Todo updateTodo = gson.fromJson(req.body(), Todo.class);

            if(!pattern.matcher(req.params(":id")).matches()) {
                return String.format("The id \"%s\" is not a number!", req.params(":id"));
            }

            for(Todo todo : todos) {
                if(req.params(":id").equals(todo.getId().toString())) {
                    todos.remove(todo);
                    todos.add(updateTodo);
                }
            }
            return null;
        });

        delete("/todos/:id", (req, res) -> {

            if(!pattern.matcher(req.params(":id")).matches()) {
                return String.format("The id \"%s\" is not a number!", req.params(":id"));
            }
            
            for(Todo todo : todos) {
                if(req.params(":id").equals(todo.getId().toString())) {
                    todos.remove(todo);
                    return null;
                }
            }
            return String.format("Todo with the id \"%s\" not found!", req.params(":id"));
        });
    }
}
