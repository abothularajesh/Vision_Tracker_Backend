package com.rajesh.Vision_Tracker_GoalOS.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rajesh.Vision_Tracker_GoalOS.auth.dto.AiResponseDTO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class AiService {

    private ChatClient chatClient;

    public AiService(ChatClient.Builder builder){
        this.chatClient=builder.build();
    }

    public AiResponseDTO generateRoadmap(String title, String description, LocalDateTime targetDate, String currentLevel, Integer conversationId) {

        String temp= """
                Create a roadmap for this goal.
                
                        Goal: {title}
                        Description: {description}
                        Current Level: {currentLevel}
                        Target Date: {targetDate}
               
                        Generate exactly 3 milestones.
                        Generate exactly 2 tasks per milestone.
                
                        Return only valid JSON.
                """;
        PromptTemplate tamplate=new PromptTemplate(temp);
        Prompt prompt=tamplate.create(Map.of("title",title, "description", description, "currentLevel", currentLevel, "targetDate", targetDate));

        AiResponseDTO response = chatClient
                .prompt(prompt)
                .call()
                .entity(AiResponseDTO.class);

        return response;

    }
}
