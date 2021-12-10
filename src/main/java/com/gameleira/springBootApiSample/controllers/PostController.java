package com.gameleira.springBootApiSample.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gameleira.springBootApiSample.models.Post;
import com.gameleira.springBootApiSample.models.Posts;
import com.gameleira.springBootApiSample.services.HttpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.time.temporal.ChronoUnit.SECONDS;

@RestController
public class PostController {
    String url = "https://api.hatchways.io/assessment/blog/posts/?tag=";
    HttpService httpService = new HttpService();
    ObjectMapper objectMapper = new ObjectMapper();
    List<Post> result = new ArrayList<>();

    @RequestMapping("/post/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Post> getPosts(@RequestParam(value = "tag") String tag
            , @RequestParam(value = "sortby", required = false) String sortBy
            , @RequestParam(value = "direction", required = false) String direction) {

        System.out.println("sortBy:" + sortBy);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + tag))
                .GET()
                .timeout(Duration.of(10, SECONDS))
                .build();

        CompletableFuture<HttpResponse<String>> responseFuture =
                HttpClient.newBuilder()
                        .build()
                        .sendAsync(request, HttpResponse.BodyHandlers.ofString());

        responseFuture.whenComplete((resp, error) -> {
            if (error != null) {
                error.printStackTrace();
            } else {
                try {
                    result = processPosts(resp, sortBy, direction);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        });

        CompletableFuture.allOf(responseFuture).join();

        return result;

    }

    private void sortByLikes(List<Post> listOfPosts, String direction) {
        Comparator<Post> comparatorByLikes = Comparator.comparingInt(Post::getLikes);
        if ("desc".equals(direction)) {
            listOfPosts.sort(comparatorByLikes.reversed());
        } else {
            listOfPosts.sort(comparatorByLikes);
        }
    }

    private void sortByPopularity(List<Post> listOfPosts, String direction) {
        Comparator<Post> comparatorByPopularity = Comparator.comparingDouble(Post::getPopularity);
        listOfPosts.sort(comparatorByPopularity);
    }

    private void sortByReads(List<Post> listOfPosts, String direction) {
        Comparator<Post> comparatorByPopularity = Comparator.comparing(Post::getPopularity);
        listOfPosts.sort(comparatorByPopularity.reversed());
    }

    private void sortByAuthorId(List<Post> listOfPosts, String direction) {
        Comparator<Post> comparatorByAuthorId = Comparator.comparing(Post::getAuthorId);
        if ( "desc".equals(direction) ) {
            listOfPosts.sort(comparatorByAuthorId.reversed());
        } else {
            listOfPosts.sort(comparatorByAuthorId);
        }
    }

    private void sortByAuthorIdAndLikes(List<Post> listOfPosts, String direction) {
        Comparator<Post> comparatorAuthorIdAndLikes = Comparator.comparing(Post::getAuthorId).thenComparing(Post::getLikes).reversed();
        listOfPosts.sort(comparatorAuthorIdAndLikes);
    }

    private List<Post> processPosts(HttpResponse<String> resp, String orderBy, String direction) throws JsonProcessingException {
        Posts posts = objectMapper.readValue(resp.body(), new TypeReference<>() {});
        List<Post> listOfPosts = posts.getPosts();

        switch (orderBy) {
            case "likes" -> sortByLikes(listOfPosts, direction);
            case "popularity" -> sortByPopularity(listOfPosts, direction);
            case "reads" -> sortByReads(listOfPosts, direction);
            case "authorId" -> sortByAuthorId(listOfPosts, direction);
            case "authorIdAndLikes" -> sortByAuthorIdAndLikes(listOfPosts, direction);
        }

//        for (Post post : listOfPosts){
//            System.out.println(post.getAuthor());
//        }
        return listOfPosts;
    }


}
