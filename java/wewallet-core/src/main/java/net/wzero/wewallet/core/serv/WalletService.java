package net.wzero.wewallet.core.serv;

import java.util.List;

import net.wzero.wewallet.core.domain.Card;
import net.wzero.wewallet.core.domain.Token;
import net.wzero.wewallet.utils.AppConstants.EthEnv;

public interface WalletService {

	/**
	 * 随机创建一个卡片（卡片也就是虚拟币账号）
	 * 用BIP44 方式创建，这个作为统一项
	 * @return
	 */
	Card createCard(Integer memberId,String pwd);
	/**
	 * 通过助记词创建一个卡片
	 * 卡应该是统一的，默认使用根地址，比特币不知道是否也是那个根地址
	 * @param mnemonic
	 * @return
	 */
	Card createCard(Integer memberId,List<String> words,String pwd);
	/**
	 * 刷新账户以太坊余额
	 * @param cardId
	 * @return
	 */
	Card refreshBalance(Integer memberId,Integer cardId);
	/**
	 * 加载一个token 到卡号
	 * @param memberId
	 * @param cardId
	 * @param contractAddr
	 * @param standard
	 * @return
	 */
	Token addToken(Integer memberId,Integer cardId,EthEnv env,String contractAddr,String standard);
	/**
	 * 刷新token的余额
	 * @param memberId
	 * @param tokenId
	 * @return
	 */
	Token refreshTokenBalance(Integer memberId,Integer tokenId);
}
