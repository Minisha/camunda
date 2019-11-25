package com.test.bpmn.demo;

import com.test.bpmn.demo.dto.TaskResponse;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstanceQuery;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public List<HistoricProcessInstance> getAllCompletedProcessInstance() {
        List<HistoricProcessInstance> processInstances =
                camunda.getHistoryService().createHistoricProcessInstanceQuery()
                        .finished() // we only want the finished process instances
                        .list();

        return processInstances;
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

    public List<HistoricTaskInstance> getCompletedTask(String processInstanceId) {
        return camunda.getHistoryService().createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).finished().list();
    }

    public void claimATask(String taskId, String staffId) {
        TaskService taskService = camunda.getTaskService();
        taskService.claim(taskId, staffId);
    }

    public void completeTask(String taskId) {
        TaskService taskService = camunda.getTaskService();
        taskService.complete(taskId);
    }

}