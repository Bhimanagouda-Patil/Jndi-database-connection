package com.unisys.scheduler;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.unisys.dao.UserDao;
import com.unisys.service.EmailService;

@Component
public class StudentScheduler {

    private static final Logger logger = LoggerFactory.getLogger(StudentScheduler.class);

    private final UserDao userDao = new UserDao(); // DAO for database operations

    @Autowired
    private EmailService emailService; // Service for sending emails

    private static final int STUDENT_THRESHOLD = 7;
    /**
     * Task 1: Log the number of students every minute.
     */
    @Scheduled(fixedRate = 60000) // Every 60 seconds
    public void logStudentCount() {
        try {
            int studentCount = userDao.getAllUsers().size();
            logger.info("Current number of students: {}", studentCount);
        } catch (Exception e) {
            logger.error("Error while retrieving student count: {}", e.getMessage(), e);
        }
    }

    /**
     * Task 2: Send an email notification if student count exceeds a threshold.
     */
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void notifyOnThreshold() {
        try {
            int studentCount = userDao.getAllUsers().size();
            if (studentCount > STUDENT_THRESHOLD) {
                String subject = "Student Threshold Alert";
                String message = "The number of students has exceeded the threshold of " + STUDENT_THRESHOLD +
                        ". Current count: " + studentCount;

                emailService.sendEmail("puttupatil49294929@gmail.com", subject, message);
                logger.info("Threshold alert email sent.");
            }
        } catch (Exception e) {
            logger.error("Error while sending threshold alert: {}", e.getMessage(), e);
        }
    }

}
