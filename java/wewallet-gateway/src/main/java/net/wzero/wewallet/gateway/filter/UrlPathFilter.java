package net.wzero.wewallet.gateway.filter;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import net.wzero.wewallet.WalletException;
import net.wzero.wewallet.gateway.domain.ApiData;
import net.wzero.wewallet.gateway.serv.ApiService;
import net.wzero.wewallet.gateway.serv.OutAccessApiService;
import net.wzero.wewallet.utils.AppConstants;

public class UrlPathFilter extends ZuulFilter {
	private static Logger log = LoggerFactory.getLogger(UrlPathFilter.class);

	@Autowired
	private ApiService apiService;
	@Autowired
	private OutAccessApiService outAccessApiService;
	
	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() {
		// 过滤方法
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest req = ctx.getRequest();
		log.info("api：" + req.getRequestURI());
		
		if(this.outAccessApiService.getOutAccessApi(req.getRequestURI()) != null) return null; // 判断是否为外部可访问的api
		
		ApiData ad = this.apiService.getApi(req.getRequestURI());
		if(ad == null) {
			ctx.setSendZuulResponse(false);
//			this.sender.sendSysLog(req.getHeader("x-real-ip"), req.getRequestURI(), req.getParameterMap(), req.getHeaderNames(), req, "找不到api："+req.getRequestURI(), "urlPath");
			throw new WalletException("api_not_found","API找不到!");
		}
		if(!ad.getIsEnable()) {
			ctx.setSendZuulResponse(false);
//			this.sender.sendSysLog(req.getHeader("x-real-ip"), req.getRequestURI(), req.getParameterMap(), req.getHeaderNames(), req, "过时api："+req.getRequestURI(), "urlPath");
			throw new WalletException("api_not_enable","API不可用!");
		}
		ctx.set(AppConstants.CURRENT_API_DATA_KEY, ad);
		return null;
	}

	@Override
	public String filterType() {
		return FilterConstants.PRE_TYPE;
	}

	@Override
	public int filterOrder() {
		return 0;
	}

}