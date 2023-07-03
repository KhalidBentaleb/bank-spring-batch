package org.id.bankspringbatch.controller;

import org.id.bankspringbatch.dao.BankTransaction;
import org.id.bankspringbatch.dao.BankTransactionRepository;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class JobRestController {
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job job;
    @Autowired
    private BankTransactionRepository bankTransactionRepository;

    @GetMapping("/startJob")
    public BatchStatus load() throws Exception{
        Map<String, JobParameter> parameterMap = new HashMap<>();
        parameterMap.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters jobParameters = new JobParameters(parameterMap);
        JobExecution jobExecution = jobLauncher.run(job, jobParameters);
        while (jobExecution.isRunning()){
            System.out.println(".......");
        }
        return jobExecution.getStatus();
    }
}
