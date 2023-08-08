package com.rest.webservices.restfulwebservices.user;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
public class UserResource {
    private UserRepository userRepository;
    private PostRepository postRepository;

    public UserResource(UserRepository userRepository,
                        PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @GetMapping(path="/users")
    public List<User> retrieveAllUsers(){
        return userRepository.findAll();
    }

    @GetMapping(path = "/users/{id}")
    public Optional<User> retrieveUser(@PathVariable int id){
        Optional<User> user = userRepository.findById(id);
        if(user == null){
            throw new UserNotFoundException("id:"+id);
        }
        return user;
    }

    @PostMapping(path="/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user){
        final User savedUser= userRepository.save(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping(path="/users/{id}")
    public void deleteUser(@PathVariable int id){
        userRepository.deleteById(id);
    }

    @GetMapping("/users/{id}/posts")
    public List<Post> retrievePostsByUserId(@PathVariable int id){
        Optional<User> user = userRepository.findById(id);
        if(user == null){
            throw new UserNotFoundException("id:"+id);
        }
        return user.get().getPosts();
    }

    @PostMapping("/users/{id}/posts")
    public ResponseEntity<Post> createPostForUser(@PathVariable int id, @RequestBody Post post){
        Optional<User> user = userRepository.findById(id);
        if(user == null){
            throw new UserNotFoundException("id:"+id);
        }

        post.setUser(user.get());
        Post savedPost = postRepository.save(post);

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(savedPost.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }
}
