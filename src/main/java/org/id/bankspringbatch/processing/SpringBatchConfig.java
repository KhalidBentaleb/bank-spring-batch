package org.id.bankspringbatch.processing;

import lombok.Getter;
import org.id.bankspringbatch.dao.BankTransaction;
import org.id.bankspringbatch.dao.BankTransactionRepository;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.text.SimpleDateFormat;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {
    @Bean
    public FlatFileItemReader<BankTransaction> flatFileItemReader(@Value("${inputFile}") Resource inputFile){
        FlatFileItemReader<BankTransaction> fileItemReader = new FlatFileItemReader<>();
        fileItemReader.setName("CSV-READER");
        fileItemReader.setLinesToSkip(1);
        fileItemReader.setResource(inputFile);
        fileItemReader.setLineMapper(lineMapper());
        return fileItemReader;
    }

    @Bean
    public LineMapper<BankTransaction> lineMapper(){
        DefaultLineMapper<BankTransaction> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "accountId", "strTransactionDate", "transactionType", "amount");
        lineMapper.setLineTokenizer(lineTokenizer);
        BeanWrapperFieldSetMapper fieldSetMapper = new BeanWrapperFieldSetMapper();
        fieldSetMapper.setTargetType(BankTransaction.class);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public ItemProcessor<BankTransaction, BankTransaction> bankTransactionItemProcessor(){
        return new ItemProcessor<BankTransaction, BankTransaction>() {
            private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            @Override
            public BankTransaction process(BankTransaction bankTransaction) throws Exception {
                    bankTransaction.setTransactionDate(dateFormat.parse(bankTransaction.getStrTransactionDate()));
                    return bankTransaction;
            }
        };
    }

    @Bean
    public ItemWriter<BankTransaction> bankTransactionItemWriter(){
        return new ItemWriter<BankTransaction>() {
            @Autowired
            private BankTransactionRepository bankTransactionRepository;
            @Override
            public void write(List<? extends BankTransaction> list) throws Exception {
                bankTransactionRepository.saveAll(list);
            }
        };
    }
}
