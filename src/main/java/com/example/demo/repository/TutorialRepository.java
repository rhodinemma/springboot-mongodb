package com.example.demo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.demo.model.Tutorial;

// We should now be able to use MongoRepository's methods:
// save(), findOne(), findById(), findAll(), count(),
// delete(), deleteById()

public interface TutorialRepository extends MongoRepository<Tutorial, String> {
    // define custom finder methods
    // 1. findByTitleContaining - returns all tutorials which title
    // contains input "title"
    List<Tutorial> findByTitleContaining(String title);
    // 2. findByPublished - returns all tutorials with "published"
    // having value as input "published"
    List<Tutorial> findByPublished(boolean published);
}