package com.test.bpmn.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.bpmn.demo.dto.TaskResponse;
import com.test.bpmn.demo.dto.UserRequest;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstanceQuery;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CamundaProcessManager {

    @Autowired
    private ProcessEngine camunda;

    public ProcessInstance placeCase() {
        return camunda.getRuntimeService().startProcessInstanceByKey(//
                ProcessConstants.PROCESS_KEY_loanApproval);
    }

    public List<String> getAllProcessInstance() {
        ProcessInstanceQuery loanApproval = camunda.getRuntimeService()
                .createProcessInstanceQuery()
                .processDefinitionKey("loanApproval");

        List<ProcessInstance> list = loanApproval.list();
        List<String> processId = list.stream().map(r -> r.getProcessInstanceId()).collect(Collectors.toList());
        return processId;
    }

    public List<String> getAllCompletedProcessInstance() {
        List<HistoricProcessInstance> processInstances =
                camunda.getHistoryService().createHistoricProcessInstanceQuery()
                        .finished() // we only want the finished process instances
                        .list();

       return processInstances.stream().map(HistoricProcessInstance::getRootProcessInstanceId).collect(Collectors.toList());

    }

    public List<TaskResponse> getTask(String processInstanceId) {
        TaskService taskService = camunda.getTaskService();
        List<Task> list = taskService.createTaskQuery().processInstanceId(processInstanceId).list();

        List<TaskResponse> collect = list.stream().map(r -> {
            TaskResponse response = new TaskResponse();
            response.setAssignee(r.getAssignee());
            response.setName(r.getName());
            response.setTaskId(r.getId());
            return response;
        }).collect(Collectors.toList());

        return collect;
    }

    public List<TaskResponse> getCompletedTask(String processInstanceId) {
        List<HistoricTaskInstance> list = camunda.getHistoryService().createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId).finished()
                        .list();

        return list.stream().map(r -> {
            TaskFormData formData = camunda.getFormService().getTaskFormData(r.getId());
            TaskResponse response = new TaskResponse();
            response.setAssignee(r.getAssignee());
            response.setName(r.getName());
            response.setTaskId(r.getId());
            response.setData(null);
            return response;
        }).collect(Collectors.toList());
    }

    public void claimATask(String taskId, String staffId) {
        TaskService taskService = camunda.getTaskService();
        taskService.claim(taskId, staffId);
    }

    public void completeTask(String taskId) {
        TaskService taskService = camunda.getTaskService();
        taskService.complete(taskId);
    }

    public void submitForm(String taskId, UserRequest userRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> stringStringMap = objectMapper.convertValue(userRequest, new TypeReference<Map<String, Object>>() {
        });
        FormService formService = camunda.getFormService();
        formService.submitTaskForm(taskId, stringStringMap);
    }

}