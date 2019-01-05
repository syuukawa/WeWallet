package net.wzero.wewallet.utils;

public final class AppConstants {
	//-----------线程数据键-----------------
	public static final String CURRENT_API_DATA_KEY = "current_api_data";

	//-----------URL 参数名 ----------------------
	public static final String URL_TOKEN_KEY="token";
	public static final String URL_CLIENT_ID_KEY="clientid";
	
	//---------------虚拟币类型-------------------
	public final static Integer BITCOIN_CARD_TYPE = 0;
	public final static Integer ETHEREUM_CARD_TYPE = 60;
	//---------------微信相关---------------------
	public static final String WX_APP_ID_KEY="wx_app_id";
	public static final int WX_APP_TYPE_MP = 1;
	public static final int WX_APP_TYPE_MINIAPP = 2;
	public static final int WX_APP_TYPE_WEB = 3;	

	//---------------------登陆类型---------------------------
	public static final int LOGIN_TYPE_ID = 0;//账号登陆
	public static final int LOGIN_TYPE_ACCOUNT = 1;//账号登陆
	public static final int LOGIN_TYPE_PHONE = 2;//手机登陆
	public static final int LOGIN_TYPE_EMAIL = 3;//邮件登陆
	public static final int LOGIN_TYPE_WEIXIN = 10;//微信登陆
	public static final int LOGIN_TYPE_ALIPAY = 11;//支付宝？
	
	//-------------------Infura.io 的钱包节点 WeWallet 项目--------------------
	public static final String WEWALLET_PROJECT_ID = "3cef33da8db74456945739de85d48457";
	public static final String WEWALLET_PROJECT_SECRET = "b928943494b242148ec23669d4c7e4c8";
	/** 
	 * Main Ethereum Network
	 */
	public static final String MAINNET = "https://mainnet.infura.io/v3/3cef33da8db74456945739de85d48457";
	/** 
	 * Ropsten Test Network
	 */
	public static final String POPSTEN = "https://ropsten.infura.io/v3/3cef33da8db74456945739de85d48457";
	/**
	 * Kovan Test Network
	 */
	public static final String KOVAN = "https://kovan.infura.io/v3/3cef33da8db74456945739de85d48457";
	/**
	 * Rinkeby Test Network
	 */
	public static final String PINKEBY = "https://rinkeby.infura.io/v3/3cef33da8db74456945739de85d48457";
	/**
	 * localhost 8545 
	 */
	public static final String LOCALHOST = "http://localhost:8545";
	
}
