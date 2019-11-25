package com.test.bpmn.demo.rest;

import com.test.bpmn.demo.CamundaProcessManager;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
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
  public List<ProcessInstance> getAllProcess() {
    return camundaProcessManager.getAllProcessInstance();
  }

  @RequestMapping(method = RequestMethod.GET, value = "/completed-process")
  public List<HistoricProcessInstance> getAllCompletedProcess() {
    return camundaProcessManager.getAllCompletedProcessInstance();
  }

  @RequestMapping(method = RequestMethod.GET, value = "/task/{processId}")
  public List<Task> getAllTasks(@PathVariable String processId) {
    return camundaProcessManager.getTask(processId); //Map to task object before returning to UI
  }

  @RequestMapping(method = RequestMethod.GET, value = "/historyTask/{processId}")
  public List<HistoricTaskInstance> getAllHistoryTasks(@PathVariable String processId) {
    return camundaProcessManager.getCompletedTask(processId);
  }


}
