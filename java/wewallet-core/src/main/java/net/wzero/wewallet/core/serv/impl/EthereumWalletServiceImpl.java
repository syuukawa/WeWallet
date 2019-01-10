package net.wzero.wewallet.core.serv.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.quincysx.crypto.ECKeyPair;
import com.quincysx.crypto.bip32.ExtendedKey;
import com.quincysx.crypto.bip32.ValidationException;
import com.quincysx.crypto.bip39.SeedCalculator;
import com.quincysx.crypto.bip44.AddressIndex;
import com.quincysx.crypto.bip44.CoinPairDerive;
import com.quincysx.crypto.ethereum.EthECKeyPair;

import lombok.extern.slf4j.Slf4j;
import net.wzero.wewallet.WalletException;
import net.wzero.wewallet.controller.SysParamSupport;
import net.wzero.wewallet.core.domain.Account;
import net.wzero.wewallet.core.domain.AccountType;
import net.wzero.wewallet.core.domain.EthereumAccount;
import net.wzero.wewallet.core.domain.Token;
import net.wzero.wewallet.core.repo.AccountRepository;
import net.wzero.wewallet.core.repo.AccountTypeRepository;
import net.wzero.wewallet.core.repo.TokenRepository;
import net.wzero.wewallet.core.serv.CryptoService;
import net.wzero.wewallet.core.serv.WalletService;
import net.wzero.wewallet.core.stream.CoreMessage;
import net.wzero.wewallet.utils.AppConstants;
import net.wzero.wewallet.utils.AppConstants.EthEnv;

@Slf4j
@Service("walletService")
public class EthereumWalletServiceImpl extends SysParamSupport implements WalletService {

	@Autowired
	private CryptoService cryptoService;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private AccountTypeRepository accountTypeRepository;
	@Autowired
	private TokenRepository tokenRepository;
	@Autowired
	private CoreMessage coreMessage;

	/**
	 * 默认遵循 BIP44
	 */
	@Override
	public Account createAccount(Integer memberId,String pwd, String mark) {
		try {
			// 取得账户类型
			AccountType ct = this.accountTypeRepository.getOne(AppConstants.ETHEREUM_ACCOUNT_TYPE);
			// 直接创建一个key
			ExtendedKey ek = ExtendedKey.createNew();
			EthECKeyPair kp = this.makeKeyPairByPath(ek, "m/44'/60'/0'/0/0");
			// 得到一个keystore
			String keystore = this.cryptoService.encryptKeystore(kp, pwd);// password 需要外部提供
			log.debug("keystore->\t" + keystore);
			EthereumAccount account =  (EthereumAccount)this.accountRepository.findByMemberIdAndAddr(memberId, kp.getAddress());
			if(account != null) throw new WalletException("account_exist","账户已经存在");
			// 创建 一个account
			account = new EthereumAccount();
			account.setAddr(kp.getAddress());
			account.setBalance("0");
			account.setAccountType(ct);// 需要传递参数
			account.setKeystore(keystore);//
			account.setMemberId(memberId);
			account.setPath("m/44'/60'/0'/0/0");
			if(mark == null) {
				account.setMark("account"+(this.findByMemberId(memberId).size()+1));
			} else 
				account.setMark(mark);
			// 保存
			return this.accountRepository.save(account);
		} catch (ValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException("validation_exception","编码错误");
		}
	}

	@Override
	public Account createAccount(Integer memberId,List<String> words,String pwd, String mark) {
		// 取得账户类型
		AccountType ct = this.accountTypeRepository.getOne(AppConstants.ETHEREUM_ACCOUNT_TYPE);
		try {
			// 生产 seed 如果内容一样 种子就会一样
			byte[] seed = new SeedCalculator().calculateSeed(words, "");//密码就不要了吧
			// 通过seed 生产公私钥 master
			ExtendedKey extendedKey = ExtendedKey.create(seed);
			// 提供的方法来操作 这是 BIP44 标准的子私钥获取方式 m/44'/60'/0'/0/0 以太坊获取第一个就好了
			EthECKeyPair eKey = this.makeKeyPairByPath(extendedKey, "m/44'/60'/0'/0/0");
			String keystore = this.cryptoService.encryptKeystore(eKey,pwd);// password 需要外部提供

			log.info("address->\t"+eKey.getAddress());
			//检查 此账户用户是否已经绑定过
			EthereumAccount account =  (EthereumAccount)this.accountRepository.findByMemberIdAndAddr(memberId, eKey.getAddress());
			if(account != null) throw new WalletException("account_exist","账户已经存在");
			account = new EthereumAccount();
			account.setAddr(eKey.getAddress());
			account.setBalance("0");
			account.setAccountType(ct);// 需要传递参数
			account.setKeystore(keystore);//
			account.setMemberId(memberId);
			account.setPath("m/44'/60'/0'/0/0");//以太坊现在默认
			if(mark == null) {
				account.setMark("account"+(this.findByMemberId(memberId).size()+1));
			} else 
				account.setMark(mark);
			// 保存
			return this.accountRepository.save(account);
		} catch (ValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException("validation_exception","编码错误");
		}
	}
	
	@Override
	public Account findByAccountId(Integer accountId) {
		return this.accountRepository.findOne(accountId);
	}
	
	@Override
	public Account updateAccount(Account account, String mark) {
		if(mark != null) account.setMark(mark);
		return this.accountRepository.save(account);
	}
	
	private EthECKeyPair makeKeyPairByPath(ExtendedKey extendedKey,String path) throws ValidationException {
		AddressIndex address = this.cryptoService.paseAddress(path);
		return this.makeKeyPairByPath(extendedKey, address);
	}
	private EthECKeyPair makeKeyPairByPath(ExtendedKey extendedKey,AddressIndex address) throws ValidationException {
		CoinPairDerive coinKeyPair = new CoinPairDerive(extendedKey);
		ECKeyPair master = coinKeyPair.derive(address);
		return EthECKeyPair.parse(master);
	}

	@Override
	public Account refreshBalance(Integer memberId,Integer accountId) {
		// 判断环境是否已经配置
		if(this.getMember().getCurrEnv() == null) throw new WalletException("env_not_set","请先设置当前环境");
		EthEnv env = EthEnv.fromString(this.getMember().getCurrEnv());
		
		//获取账户
		Account account = this.accountRepository.findOne(accountId);
		if(account == null) throw new WalletException("account_not_exist","账户不存在");
		if(!memberId.equals(account.getMemberId())) throw new WalletException("op_failed","只能操作本账户下的账户");
		account.setIsRefreshing(true);
		account = this.accountRepository.save(account);
		// 发送更新请求消息 
		this.coreMessage.refreshJob().send(MessageBuilder.withPayload(account)
				.setHeader("jobType", AppConstants.JOB_TYPE_REFRESH_ACCOUNT)
				.setHeader("env", env.getName()).build());
		return account;
	}

	@Override
	public Token addToken(Integer memberId, Integer accountId, EthEnv env, String contractAddr, String standard, String icon) {
		//先获取account
		Account account = this.accountRepository.findOne(accountId);
		if(account == null) throw new WalletException("account_not_exist","指定的AccountID不存在");
		if(account.getMemberId() != memberId) throw new WalletException("session_error","本账户不包含此账户");
		// 看下token是否存在
		Token token = this.tokenRepository.findByAccountIdAndContractAddrAndEnv(accountId, contractAddr,env.getName());
		if(token != null) throw new WalletException("token_exist","此token已经存在，请勿重复添加");
		token = new Token();
		token.setAccount(account);
		token.setEnv(env.getName());
		token.setContractAddr(contractAddr);
		token.setStandard(standard);
		token.setIcon(icon);
		token.setBalance("0");
		return this.tokenRepository.save(token);
	}
	
	@Override
	public Token findByTokenId(Integer tokenId) {
		return this.tokenRepository.findOne(tokenId);
	}
	
	@Override
	public Token updateTokn(Token token, String contractAddr, String standard, String icon) {
		if(contractAddr != null) token.setContractAddr(contractAddr);
		if(standard != null) token.setStandard(standard);
		if(icon != null) token.setIcon(icon);
		return this.tokenRepository.save(token);
	}

	@Override
	public Token refreshTokenBalance(Integer memberId, Integer tokenId) {
		// 判断环境是否已经配置
//		if(this.getMember().getCurrEnv() == null) throw new WalletException("env_not_set","请先设置当前环境");
//		EthEnv env = EthEnv.fromString(this.getMember().getCurrEnv());
		Token token = this.tokenRepository.findOne(tokenId);
		// token 应该自带 env
		//EthEnv env = EthEnv.fromString(token.getEnv());
		// 获取token信息
		if(token == null) throw new WalletException("token_not_exist","指定的id不存在");
		
		if(!token.getAccount().getMemberId().equals(memberId)) throw new WalletException("op_failed","只能操作本账户下的token");
		token.setIsRefreshing(true);
		token = this.tokenRepository.save(token);
		// 发送一个消息 到rabbitmq
		this.coreMessage.refreshJob().send(MessageBuilder.withPayload(token)
				.setHeader("jobType", AppConstants.JOB_TYPE_REFRESH_TOKEN)
				/*.setHeader("env", token.getEnv())*/.build()); // 理论上 env header 不需要了
		return token;
	}
	
	@Override
	public List<Account> findByMemberId(Integer memberId) {
		return this.accountRepository.findByMemberId(this.getMember().getId());
	}
	
}
