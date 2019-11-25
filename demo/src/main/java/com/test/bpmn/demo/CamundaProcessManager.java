package com.test.bpmn.demo;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstanceQuery;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CamundaProcessManager {

    @Autowired
    private ProcessEngine camunda;

    public ProcessInstance placeCase() {
        return camunda.getRuntimeService().startProcessInstanceByKey(//
                ProcessConstants.PROCESS_KEY_loanApproval);
    }

    public List<ProcessInstance> getAllProcessInstance() {
        ProcessInstanceQuery loanApproval = camunda.getRuntimeService()
                .createProcessInstanceQuery()
                .processDefinitionKey("loanApproval");

        List<ProcessInstance> list = loanApproval.list();
        return list;
    }

    public List<HistoricProcessInstance> getAllCompletedProcessInstance() {
        List<HistoricProcessInstance> processInstances =
                camunda.getHistoryService().createHistoricProcessInstanceQuery()
                        .finished() // we only want the finished process instances
                        .list();

        return processInstances;
    }

    public List<Task> getTask(String processInstanceId) {
        TaskService taskService = camunda.getTaskService();
        List<Task> list = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        return list;
    }

    public List<HistoricTaskInstance> getCompletedTask(String processInstanceId) {
        return camunda.getHistoryService().createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).finished().list();
    }
}
