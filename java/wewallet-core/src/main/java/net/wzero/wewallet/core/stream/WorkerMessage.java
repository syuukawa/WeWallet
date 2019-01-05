package net.wzero.wewallet.core.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * 在Spring cloud stream binder 注册一个订阅通道
 * 
 * @author yjjie
 *
 */
public interface WorkerMessage {
	String TRANSFER_JOB_INPUT = "transferJobInput";
	String TRANSFER_JOB_CALLBACK_OUTPUT = "transferJobCallbackOutput";
	String SMS_JOB_INPUT = "smsJobInput";

	@Input(WorkerMessage.TRANSFER_JOB_INPUT)
	SubscribableChannel transferJob();
	
	@Output(WorkerMessage.TRANSFER_JOB_CALLBACK_OUTPUT)
	MessageChannel transferJobCallback();

	@Input(WorkerMessage.SMS_JOB_INPUT)
	SubscribableChannel smsJob();
}
