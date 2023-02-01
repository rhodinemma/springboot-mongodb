package com.example.demo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.example.demo.controller.TutorialController;
import com.example.demo.model.Tutorial;
import com.example.demo.repository.TutorialRepository;

@WebMvcTest(TutorialController.class)
public class TutorialControllerTests {
    @MockBean
    private TutorialRepository tutorialRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateTutorial() throws Exception {
        Tutorial tutorial = new Tutorial("Spring Boot @WebMvcTest", "Description", true);

        mockMvc.perform(post("/api/tutorials").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tutorial)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void shouldReturnTutorial() throws Exception {
        String id = "1";
        Tutorial tutorial = new Tutorial("Spring Boot @WebMvcTest", "Description", true);

        when(tutorialRepository.findById(id)).thenReturn(Optional.of(tutorial));
        mockMvc.perform(get("/api/tutorials/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(tutorial.getTitle()))
                .andExpect(jsonPath("$.description").value(tutorial.getDescription()))
                .andExpect(jsonPath("$.published").value(tutorial.isPublished()))
                .andDo(print());
    }

    @Test
    void shouldReturnNotFoundTutorial() throws Exception {
        String id = "4";

        when(tutorialRepository.findById(id)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/tutorials/{id}", id))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void shouldReturnListOfTutorials() throws Exception {
        List<Tutorial> tutorials = new ArrayList<>(
                Arrays.asList(new Tutorial("Spring Boot @WebMvcTest 1",  "Description", true),
                        new Tutorial("Spring Boot @WebMvcTest 2", "Description", true),
                        new Tutorial("Spring Boot @WebMvcTest 3", "Description 3", true)));

        when(tutorialRepository.findAll()).thenReturn(tutorials);
        mockMvc.perform(get("/api/tutorials"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(tutorials.size()))
                .andDo(print());
    }

    @Test
    void shouldReturnListOfTutorialsWithFilter() throws Exception {
        List<Tutorial> tutorials = new ArrayList<>(
                Arrays.asList(new Tutorial("Spring Boot @WebMvcTest", "Description 1", true),
                        new Tutorial("Spring Boot Web MVC", "Description 3", true)));

        String title = "Boot";
        MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
        paramsMap.add("title", title);

        when(tutorialRepository.findByTitleContaining(title)).thenReturn(tutorials);
        mockMvc.perform(get("/api/tutorials").params(paramsMap))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(tutorials.size()))
                .andDo(print());

        tutorials = Collections.emptyList();

        when(tutorialRepository.findByTitleContaining(title)).thenReturn(tutorials);
        mockMvc.perform(get("/api/tutorials").params(paramsMap))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void shouldReturnNoContentWhenFilter() throws Exception {
        String title = "BezKoder";
        MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
        paramsMap.add("title", title);

        List<Tutorial> tutorials = Collections.emptyList();

        when(tutorialRepository.findByTitleContaining(title)).thenReturn(tutorials);
        mockMvc.perform(get("/api/tutorials").params(paramsMap))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void shouldUpdateTutorial() throws Exception {
        String id = "1";

        Tutorial tutorial = new Tutorial("Description", "Description", false);
        Tutorial updatedtutorial = new Tutorial("Updated", "Updated", true);

        when(tutorialRepository.findById(id)).thenReturn(Optional.of(tutorial));
        when(tutorialRepository.save(any(Tutorial.class))).thenReturn(updatedtutorial);

        mockMvc.perform(put("/api/tutorials/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedtutorial)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(updatedtutorial.getTitle()))
                .andExpect(jsonPath("$.description").value(updatedtutorial.getDescription()))
                .andExpect(jsonPath("$.published").value(updatedtutorial.isPublished()))
                .andDo(print());
    }

    @Test
    void shouldReturnNotFoundUpdateTutorial() throws Exception {
        String id = "8";

        Tutorial updatedtutorial = new Tutorial("Updated", "Updated", true);

        when(tutorialRepository.findById(id)).thenReturn(Optional.empty());
        when(tutorialRepository.save(any(Tutorial.class))).thenReturn(updatedtutorial);

        mockMvc.perform(put("/api/tutorials/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedtutorial)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void shouldDeleteTutorial() throws Exception {
        String id = "2";

        doNothing().when(tutorialRepository).deleteById(id);
        mockMvc.perform(delete("/api/tutorials/{id}", id))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void shouldDeleteAllTutorials() throws Exception {
        doNothing().when(tutorialRepository).deleteAll();
        mockMvc.perform(delete("/api/tutorials"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }
}
