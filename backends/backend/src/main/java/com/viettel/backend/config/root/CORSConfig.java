package com.viettel.backend.config.root;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Configuration
public class CORSConfig {
	
	@Autowired
	private FrontendProperties frontendProperties;

	@Bean
	public SimpleCORSFilter corsFilter() {
		return new SimpleCORSFilter(frontendProperties.getAllowedOrigins());
	}
	
	public static class SimpleCORSFilter implements Filter {
		
		private final Logger logger = LoggerFactory.getLogger(this.getClass());
		
		private List<String> allowedOrigins = Collections.emptyList();
		
		/** Default does not allow CORS for any origin */
		private boolean sameDomainOnly = true;
		
		public SimpleCORSFilter(List<String> allowedOrigins) {
			if (!CollectionUtils.isEmpty(allowedOrigins)) {
				this.sameDomainOnly = false;
				this.allowedOrigins = new ArrayList<String>(allowedOrigins.size());
				for (String origin : allowedOrigins) {
					this.allowedOrigins.add(getOrigin(origin));
				}
			}
		}

		public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
			// If does not allow CORS for any origin, do not add any CORS header
			if (this.sameDomainOnly) {
				chain.doFilter(req, res);
				return;
			}
			// Add CORS headers for allowed origins
			HttpServletResponse httpResponse = (HttpServletResponse) res;
			HttpServletRequest httpRequest = ((HttpServletRequest) req);
			String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
			if (isPathAllowedForCors(path)) {
				String origin = httpRequest.getHeader("Origin");
				if (isOriginAllowed(origin)) {
					httpResponse.setHeader("Access-Control-Allow-Origin", origin);
				}
				httpResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
				httpResponse.setHeader("Access-Control-Max-Age", "3600");
				httpResponse.setHeader("Access-Control-Allow-Headers", "x-requested-with, accept, authorization, content-type");
				httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
			}
			if (httpRequest.getMethod().equals("OPTIONS")) {
	            try {
	                httpResponse.getWriter().print("OK");
	                httpResponse.getWriter().flush();
	            } catch (IOException e) {
	            	;
	            }
	        } else{
	    		chain.doFilter(req, res);
	        }
		}

		public void init(FilterConfig filterConfig) {}

		public void destroy() {}
		
		private boolean isOriginAllowed(String origin) {
			if (StringUtils.isEmpty(origin)) {
				return false;
			}
			
			for (String allowedOrigin : allowedOrigins) {
				if (allowedOrigin.equalsIgnoreCase(origin)) {
					return true;
				}
			}
			return false;
		}
		
		private boolean isPathAllowedForCors(String path) {
			if (path.startsWith("/account")) {
				// Allow cross-domain ping
				if (path.startsWith("/account/ping")) {
					return true;
				}
				return false;
			}
			return true;
		}

		private String getOrigin(String url) {
			if (!url.startsWith("http:") && !url.startsWith("https:")) {
				return url;
			}

			String origin = null;

			try {
				URI uri = new URI(url);
				origin = uri.getScheme() + "://" + uri.getAuthority();
			} catch (URISyntaxException e) {
				logger.error("Cannot parse URI from " + url, e);
			}

			return origin;
		}

	}
}
