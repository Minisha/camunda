package com.test.bpmn.demo.rest;

import com.test.bpmn.demo.CamundaProcessManager;
import com.test.bpmn.demo.dto.TaskResponse;
import com.test.bpmn.demo.dto.UserRequest;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/case_management")
public class CaseRestController {

  @Autowired
  private CamundaProcessManager camundaProcessManager;

  @RequestMapping(method= RequestMethod.POST)
  public void placeCase() {
    ProcessInstance processInstance = camundaProcessManager.placeCase();
    //Tie the process instance to staffId
    String caseInstanceId = processInstance.getCaseInstanceId();
    System.out.println("CaseInstanceId is: "+ caseInstanceId);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/process")
  public ResponseEntity<List<String>> getAllProcess() {
    List<String> allProcessInstance = camundaProcessManager.getAllProcessInstance();
    return new ResponseEntity<>(allProcessInstance, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/completed-process")
  public ResponseEntity<List<String>> getAllCompletedProcess() {
    return new ResponseEntity<>(camundaProcessManager.getAllCompletedProcessInstance(), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/task/{processId}")
  public List<TaskResponse> getAllTasks(@PathVariable String processId) {
    return camundaProcessManager.getTask(processId); //Map to task object before returning to UI
  }

  @RequestMapping(method = RequestMethod.GET, value = "/historyTask/{processId}")
  public List<TaskResponse> getAllHistoryTasks(@PathVariable String processId) {
    return camundaProcessManager.getCompletedTask(processId);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/claimTask/{staffId}/{taskId}")
  public void claimTask(@PathVariable String staffId, @PathVariable String taskId) {
     camundaProcessManager.claimATask(taskId, staffId);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/completeTask/{taskId}")
  public void claimTask(@PathVariable String taskId) {
    camundaProcessManager.completeTask(taskId);
  }

  @RequestMapping(method = RequestMethod.POST, value = "/submitForm/{taskId}")
  public void submitForm(@PathVariable String taskId, @RequestBody UserRequest userRequest) {
    camundaProcessManager.submitForm(taskId, userRequest);
  }

}
