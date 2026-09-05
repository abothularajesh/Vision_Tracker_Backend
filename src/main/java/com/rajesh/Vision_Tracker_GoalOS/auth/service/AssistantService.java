package com.rajesh.Vision_Tracker_GoalOS.auth.service;

import com.rajesh.Vision_Tracker_GoalOS.auth.dto.*;
import com.rajesh.Vision_Tracker_GoalOS.auth.entity.Goal;
import com.rajesh.Vision_Tracker_GoalOS.auth.entity.MileStones;
import com.rajesh.Vision_Tracker_GoalOS.auth.entity.Tasks;
import com.rajesh.Vision_Tracker_GoalOS.auth.entity.TodoList;
import com.rajesh.Vision_Tracker_GoalOS.auth.repository.GoalRepo;
import com.rajesh.Vision_Tracker_GoalOS.auth.repository.TodoListRepo;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssistantService {

    @Autowired
    private GoalRepo goalRepo;

    @Autowired
    private TodoListRepo todoListRepo;


    private final ChatClient chatClient;

    ChatMemory chatMemory= MessageWindowChatMemory.builder().build();

    public AssistantService(ChatClient.Builder chatClient) {
        this.chatClient = chatClient
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }


    public AssistantResponseDTO getResponse(AssistantRequestDTO assistantRequestDTO, int userId, int conversationId) {

        String message=getThePrompt(assistantRequestDTO, userId);

        ChatResponse chatResponse=chatClient
                .prompt(message)
                .advisors(a->a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .chatResponse();

        String response=chatResponse
                .getResult()
                .getOutput()
                .getText();
        AssistantResponseDTO responseDTO=new AssistantResponseDTO(response);

        //AssistantResponseDTO responseDTO=new AssistantResponseDTO(message);

        return responseDTO;

    }

    private String getThePrompt(AssistantRequestDTO assistantRequestDTO, int userId) {
        List<Goal> goals=goalRepo.findByUserId(userId);
        List<TodoList>todoLists=todoListRepo.findByUserId(userId);

        List<AssistantGoalDTO> assistantGoalDTOList=new ArrayList<>();

        int totalTasks=0;
        int completedTasks=0;

        for(Goal goal:goals){

            AssistantGoalDTO assistantGoalDTO=new AssistantGoalDTO();
            assistantGoalDTO.setTitle(goal.getTitle());
            assistantGoalDTO.setStatus(String.valueOf(goal.getStatus()));
            assistantGoalDTO.setCurrentLevel(String.valueOf(goal.getCurrentLevel()));
            assistantGoalDTO.setDescription(goal.getDescription());

            List<AssistantMilestoneDTO>assistantMilestoneDTOS=new ArrayList<>();
            for(MileStones mileStones: goal.getMileStones()){
                AssistantMilestoneDTO assistantMilestoneDTO=new AssistantMilestoneDTO();
                assistantMilestoneDTO.setTitle(mileStones.getTitle());

                totalTasks+=mileStones.getTasks().size();
                completedTasks+=(int)mileStones.getTasks().stream().
                        filter(task->"COMPLETED".equals(task.getStatus())).count();

                List<AssistantTaskDTO>assistantTaskDTOS=new ArrayList<>();
                for(Tasks tasks: mileStones.getTasks()){
                    AssistantTaskDTO assistantTaskDTO=new AssistantTaskDTO();
                    assistantTaskDTO.setTitle(tasks.getTitle());
                    assistantTaskDTO.setStatus(tasks.getStatus());

                    assistantTaskDTOS.add(assistantTaskDTO);
                }
                assistantMilestoneDTO.setTasks(assistantTaskDTOS);

                assistantMilestoneDTOS.add(assistantMilestoneDTO);
            }
            assistantGoalDTO.setMilestones(assistantMilestoneDTOS);

            assistantGoalDTOList.add(assistantGoalDTO);
        }

        List<AssistantTodosDTO> todosDTOList=new ArrayList<>();
        for(TodoList todo:todoLists){
            AssistantTodosDTO todosDTO=new AssistantTodosDTO();
            todosDTO.setTitle(todo.getTitle());
            todosDTO.setStatus(todo.getStatus());
            todosDTOList.add(todosDTO);
        }
        AssistantContextDTO context = new AssistantContextDTO();

        double overallProgress = totalTasks==0 ? 0 :(completedTasks * 100.0)/totalTasks;

        context.setGoals(assistantGoalDTOList);
        context.setTodoList(todosDTOList);
        context.setOverAllProgress(overallProgress);

        //Convert these Data into Prompt
        StringBuilder goalsText = new StringBuilder();

        for (AssistantGoalDTO goal : context.getGoals()) {

            goalsText.append("Goal: ")
                    .append(goal.getTitle())
                    .append("\n");

            goalsText.append("Status: ")
                    .append(goal.getStatus())
                    .append("\n");

            for (AssistantMilestoneDTO milestone : goal.getMilestones()) {

                goalsText.append("  Milestone: ")
                        .append(milestone.getTitle())
                        .append("\n");

                for (AssistantTaskDTO task : milestone.getTasks()) {

                    goalsText.append("    Task: ")
                            .append(task.getTitle())
                            .append(" → ")
                            .append(task.getStatus())
                            .append("\n");
                }
            }

            goalsText.append("\n");
        }

        StringBuilder todoTasksText = new StringBuilder();

        for (AssistantTodosDTO todo : context.getTodoList()) {

            todoTasksText.append("- ")
                    .append(todo.getTitle())
                    .append(" → ")
                    .append(todo.getStatus())
                    .append("\n");
        }

        //Final prompt
        String prompt = """
        You are Vision Tracker Assistant, a personal productivity assistant.

        Use the user's Vision Tracker data to provide personalized
        and practical answers.

        USER PROGRESS:
        Overall Progress: %s%%

        USER GOALS:
        %s

        TODO TASKS:
        %s

        USER QUESTION:
        %s

        INSTRUCTIONS:
        - Use the user's goals, milestones, tasks, todo tasks and progress.
        - Prioritize pending and relevant tasks.
        - Do not invent goals or tasks.
        - Give a concise and practical response.
        """.formatted(
                context.getOverAllProgress(),
                goalsText,
                todoTasksText,
                assistantRequestDTO
        );

        return prompt;
    }
}
