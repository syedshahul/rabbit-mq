package com.rabbit.mq.amqp;

/**
 * @author: Syed Shahul
 */
import com.google.common.collect.Lists;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

/**
 * http://www.rabbitmq.com/tutorials/tutorial-five-java.html
 */
public class ReceiveLogsTopic {

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
			String queueName = channel.queueDeclare().getQueue();

		/*	if (argv.length < 1){
				System.err.println("Usage: ReceiveLogsTopic [binding_key]...");
				System.exit(1);
			}
*/
			for(String bindingKey : Lists.newArrayList( "kern.*")){
				channel.queueBind(queueName, EXCHANGE_NAME, bindingKey);
			}

			System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

			QueueingConsumer consumer = new QueueingConsumer(channel);
			channel.basicConsume(queueName, true, consumer);

			while (true) {
				QueueingConsumer.Delivery delivery = consumer.nextDelivery();
				String message = new String(delivery.getBody());
				String routingKey = delivery.getEnvelope().getRoutingKey();

				System.out.println(" [x] Received '" + routingKey + "':'" + message + "'");
			}
		}
		catch  (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (connection != null) {
				try {
					connection.close();
				}
				catch (Exception ignore) {}
			}
		}
	}
}


