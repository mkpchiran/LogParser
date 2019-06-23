package com.paser;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.paser.model.AccessLogResult;

import java.util.List;

@Component
public class JobCompletionListener extends JobExecutionListenerSupport {

    @Value("${startDate}")
    private String startDate;

    @Value("${duration}")
    private String duration;

    @Value("${threshold}")
    private int threshold;

    @Value("${queryDate.format}")
    private String queryDateFormat;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JobCompletionListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            List<AccessLogResult> results = jdbcTemplate.query("SELECT ip, count(1) as request_count FROM access_log WHERE " +
                            "request_date >= STR_TO_DATE(?, ?) AND " +
                            "request_date <= DATE_ADD(STR_TO_DATE(?, ?), INTERVAL 1 " +
                            (duration.equals(Duration.HOURLY.desc) ? Duration.HOURLY.interval : Duration.DAILY.interval) + ") " +
                            "GROUP BY ip HAVING request_count > ?",
                    new Object[]{startDate, queryDateFormat, startDate, queryDateFormat, threshold},
                    (rs, rownum) -> {
                        AccessLogResult accessLogResult = new AccessLogResult();
                        accessLogResult.setIp(rs.getString(1));
                        accessLogResult.setComment("Made more than " + threshold + " requests (" + rs.getInt(2) + ") in 1 " +
                                (duration.equals(Duration.HOURLY.desc) ? "hour" : "day") + " starting from " + startDate);
                        return accessLogResult;
                    });

            for (AccessLogResult result : results) {
                System.out.println(result.toString());
                jdbcTemplate.update("INSERT INTO access_result (ip, comment) VALUES (?, ?)",
                        new Object[]{result.getIp(), result.getComment()});
            }
        }
    }

}