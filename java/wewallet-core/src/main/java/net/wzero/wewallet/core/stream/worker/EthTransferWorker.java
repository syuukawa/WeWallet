package net.wzero.wewallet.core.stream.worker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import net.wzero.wewallet.core.domain.Transaction;
import net.wzero.wewallet.core.serv.EthService;
import net.wzero.wewallet.core.stream.WorkerMessage;

@Component
public class EthTransferWorker {

	@Autowired
	private EthService ethService;
	
	@StreamListener(WorkerMessage.TRANSFER_JOB_INPUT)
	@SendTo(WorkerMessage.TRANSFER_JOB_CALLBACK_OUTPUT)
	public Transaction doWork(Transaction msg,@Header(name="p1")String pwd) {
		//区分 ether 还是 token 交易
		if(msg.getContractAddr() == null)
			return this.ethService.sendTransaction(msg,pwd);
		else
			return this.ethService.sendTokenTransaction(msg, pwd);
	}
}