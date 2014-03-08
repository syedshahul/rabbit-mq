package com.rabbit.mq.amqp;

/**
 * @author: Syed Shahul
 */

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * http://www.rabbitmq.com/tutorials/tutorial-five-java.html
 */
public class EmitLogTopic {

	private static final String EXCHANGE_NAME = "topic_logs";

	public static void main(String[] argv) {
		Connection connection = null;
		Channel channel = null;
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost(System.getenv("HOST"));
			factory.setVirtualHost(System.getenv("VHOST"));
			factory.setUsername(System.getenv("UNAME"));
			factory.setPassword(System.getenv("PASSCODE"));

			connection = factory.newConnection();
			channel = connection.createChannel();

			channel.exchangeDeclare(EXCHANGE_NAME, "topic");

			String routingKey = getRouting(argv);
			String message = getMessage(argv);

			channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
			System.out.println(" [x] Sent '" + routingKey + "':'" + message + "'");

		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(connection != null) {
				try {
					connection.close();
				} catch(Exception ignore) {
				}
			}
		}
	}

	private static String getRouting(String[] strings) {
		if(strings.length < 1) {
			return "kern.sdfgsdg";
		}
		return strings[0];
	}

	private static String getMessage(String[] strings) {
		if(strings.length < 2) {
			return "Hello World!";
		}
		return joinStrings(strings, " ", 1);
	}

	private static String joinStrings(String[] strings, String delimiter,
	                                  int startIndex) {
		int length = strings.length;
		if(length == 0) {
			return "";
		}
		if(length < startIndex) {
			return "";
		}
		StringBuilder words = new StringBuilder(strings[startIndex]);
		for(int i = startIndex + 1; i < length; i++) {
			words.append(delimiter).append(strings[i]);
		}
		return words.toString();
	}
}


