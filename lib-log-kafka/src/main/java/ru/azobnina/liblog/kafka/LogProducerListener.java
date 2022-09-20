package ru.azobnina.liblog.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.ProducerListener;

@Slf4j
public class LogProducerListener implements ProducerListener<String, String> {

    @Override
    public void onSuccess(ProducerRecord<String, String> producerRecord, RecordMetadata recordMetadata) {

        log.info(infoMessage(producerRecord));
    }

    @Override
    public void onError(ProducerRecord<String, String> producerRecord,
                        RecordMetadata recordMetadata,
                        Exception exception) {

        log.info(infoMessage(producerRecord));
        log.error("{} = {}", exception.getClass().getName(), ExceptionUtils.getStackTrace(exception));
    }

    private String infoMessage(ProducerRecord<String, String> producerRecord) {

        return StringUtils.LF +
                "KAFKA: Produce: " + StringUtils.LF +
                ">> Topic: " + producerRecord.topic() + StringUtils.LF +
                ">> Partition: " + producerRecord.partition() + StringUtils.LF +
                ">> Key: " + producerRecord.key() + StringUtils.LF +
                ">> Headers: " + producerRecord.headers() + StringUtils.LF +
                ">> Value: " + producerRecord.value();
    }
}
