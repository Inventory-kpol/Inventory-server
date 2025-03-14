package kpol.Inventory.domain.member.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kpol.Inventory.domain.member.dto.res.EmailResponseDto;
import kpol.Inventory.domain.member.util.RedisUtil;
import kpol.Inventory.global.exception.CustomException;
import kpol.Inventory.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service

public class EmailService {
    private final JavaMailSender mailSender;

    @Autowired
    private RedisUtil redisUtil;
    private int authNumber;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void makeRandomNumber() {
        Random random = new Random();
        String randomNumber = "";
        for (int i = 0; i < 6; i++) {
            randomNumber += Integer.toString(random.nextInt(10));
        }

        authNumber = Integer.parseInt(randomNumber);
    }

    public EmailResponseDto mailAuthCheck(String email, String authNum) {
        EmailResponseDto emailResponseDto = new EmailResponseDto();

        emailResponseDto.setAuthNumber(Integer.toString(authNumber));
        emailResponseDto.setEmail(email);

        if (redisUtil.getData(authNum) == null){
            throw new CustomException(ErrorCode.EMAIL_AUTH_NUMBER_NOT_FOUND);
        }

        if (!redisUtil.getData(authNum).equals(email)){
            throw new CustomException(ErrorCode.EMAIL_MISMATCH);
        }

        return emailResponseDto;
    }


    public EmailResponseDto joinEmail(String email){
        makeRandomNumber();
        String setFrom = fromEmail;
        String toMail = email;
        String title = "회원 가입 인증 이메일 입니다.";
        String content = "<html>"
                + "<body>"
                + "<h1>ImgForest 인증 코드: " + authNumber + "</h1>"
                + "<p>해당 코드를 홈페이지에 입력하세요.</p>"
                + "<footer style='color: grey; font-size: small;'>"
                + "<p>※본 메일은 자동응답 메일이므로 본 메일에 회신하지 마시기 바랍니다.</p>"
                + "</footer>"
                + "</body>"
                + "</html>";

        EmailResponseDto emailResponseDto = new EmailResponseDto();

        try {
            sendEmail(toMail, title, content);
            emailResponseDto.setEmail(email);
            emailResponseDto.setAuthNumber(Integer.toString(authNumber));
        } catch (MessagingException e) {
            throw new CustomException(ErrorCode.EMAIL_VERIFICATION_FAILED);
        }

        return emailResponseDto;
    }

    public void sendEmail(String toEmail,String title, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(toEmail);
        helper.setSubject(title);
        helper.setText(content,true);
        try {
            mailSender.send(message);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        }

        // 5분 동안 인증번호가 생존
        redisUtil.setDataExpire(Integer.toString(authNumber), toEmail, 60 * 10L);
    }
    public SimpleMailMessage createEmailForm(String toEmail, String title, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(title);
        message.setText(text);
        return message;
    }
}
