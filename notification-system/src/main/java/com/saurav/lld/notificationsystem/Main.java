package com.saurav.lld.notificationsystem;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Entry point for notification-system. Add domain types in this package (or
 * subpackages), not in the default package. Clean
 */
enum Channel {
    EMAIL, SMS, IN_APP
}

enum Priority {
    HIGH, MEDIUM, LOW
}

class Template {

    public String id;
    String name;
    String content;
    String subject;
    Channel channel;
    long createdAt;
    Priority type;

    public Template(String name, String subject, String content, Channel channel, Priority type) {
        this.name = name;
        this.subject = subject;
        this.content = content;
        this.channel = channel;
        this.type = type;
        this.createdAt = System.currentTimeMillis();
    }

}

class UserPreference {

    String userId;
    Set<Channel> enabledChannels;

    public UserPreference(String userId, Set<Channel> enabledChannels) {
        this.userId = userId;
        this.enabledChannels = enabledChannels;
    }
}

interface NotificationChannel {

    String getType();

    boolean send(String recipient, String subject, String content);
}

class EmailChannel implements NotificationChannel {

    @Override
    public String getType() {
        return "email";
    }

    @Override
    public boolean send(String recipient, String subject, String content) {
        System.out.println(" [EMAIL] To: " + recipient + " | Subject: " + subject + " | Body: " + content);
        return true;
    }
}

class SMSChannel implements NotificationChannel {

    @Override
    public String getType() {
        return "sms";
    }

    @Override
    public boolean send(String recipient, String subject, String content) {
        System.out.println(" [SMS] To: " + recipient + " | Subject: " + subject + " | Body: " + content);
        return true;
    }
}

class INAPPChannel implements NotificationChannel {

    @Override
    public String getType() {
        return "in_app";
    }

    @Override
    public boolean send(String recipient, String subject, String content) {
        System.out.println(" [IN_APP] To: " + recipient + " | Subject: " + subject + " | Body: " + content);
        return true;
    }
}

public class NotificationSystem {

    private Map<String, NotificationChannel> channels = new LinkedHashMap<>();
    private final Map<String, Template> templates = new HashMap<>();
    private final Map<String, UserPreference> preferences = new HashMap<>();

    public void registerChannel(NotificationChannel channel) {
        channels.put(channel.getType(), channel);
    }

    public void registerTemplate(Template template) {
        templates.put(template.id, template);
    }

    public void setPreference()
}

public class Main {

    public static void main(String[] args) {
        NotificationSystem service = new NotificationSystem();

        service.registerChannel(new EmailChannel());
        service.registerChannel(new SMSChannel());
        service.registerChannel(new INAPPChannel());

        Template t1 = new Template("order_confirmation",
                "Order {{order_id}} has confirmed", "Hi {{username}}, your order {{order_id}} has confirmed",
                Channel.EMAIL,
                Priority.HIGH);

        Template t2 = new Template("welcome",
                "Welcome to Amazon", "Hi {{username}}, your account is successfully registered",
                Channel.EMAIL,
                Priority.MEDIUM);

        service.registerTemplate(t1);
        service.registerTemplate(t2);

    }
}
