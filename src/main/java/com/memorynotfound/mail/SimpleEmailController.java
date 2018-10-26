package com.memorynotfound.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class SimpleEmailController {

    @Autowired
    private JavaMailSender sender;

    @PostMapping("/sendmail")
    public String home(@RequestBody User user) throws Exception {
        System.out.println("Send mail");

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Runnable task2 = () -> {
            try {
                sendEmail(user);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Executing Task2 inside : " + Thread.currentThread().getName());

        };
        executorService.submit(task2);

        return "OK";
    }

    private void sendEmail(User user) throws Exception{
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        String inlineImage = "<img src=\"cid:logo.png\"></img><br/>";
        helper.setText("<p style=color:blue>Poštovani, Vaš račun za mesec avgust se nalazi u prilogu.</p>" + inlineImage, true);
        helper.setTo(user.getEmail());
        helper.setSubject("Racun subject");
        helper.addAttachment("globaltel.png", new ClassPathResource("globaltel.png"));


        sender.send(message);
    }
}