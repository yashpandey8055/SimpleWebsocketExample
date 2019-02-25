package com.application.authentication.configuration;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.application.authentication.TokenAuthenticationFilter;
import com.application.authentication.TokenAutheticationProvider;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	TokenAutheticationProvider provider ;
	
	private static final RequestMatcher PUBLIC_URLS = new OrRequestMatcher(
		    new AntPathRequestMatcher("/webjars/**"),
		    new AntPathRequestMatcher("/favicon.ico"),
		    new AntPathRequestMatcher("/"),
		    new AntPathRequestMatcher("/secure/**")
		    );

	private static final RequestMatcher PROTECTED_URLS = new NegatedRequestMatcher(PUBLIC_URLS);
	
	@Override
	public void configure(WebSecurity web) {
		web.ignoring().requestMatchers(PUBLIC_URLS);
	}
	
	  @Bean
	  TokenAuthenticationFilter restAuthenticationFilter() throws Exception {
	    final TokenAuthenticationFilter filter = new TokenAuthenticationFilter(PROTECTED_URLS);
	    filter.setAuthenticationManager(authenticationManager());
	    return filter;
	  }
	 
	  @Bean
	  SimpleUrlAuthenticationSuccessHandler successHandler() {
	    return new SimpleUrlAuthenticationSuccessHandler();
	  }
	  
	  /**
	   * Disable Spring boot automatic filter registration.
	   */
	  @Bean
	  FilterRegistrationBean<TokenAuthenticationFilter> disableAutoRegistration(final TokenAuthenticationFilter filter) {
	    final FilterRegistrationBean<TokenAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
	    registration.setEnabled(false);
	    return registration;
	  }

	  @Bean
	  AuthenticationEntryPoint forbiddenEntryPoint() {
	    return new HttpStatusEntryPoint(FORBIDDEN);
	  }
	  
	 
		@Override
		public void configure(HttpSecurity http) throws Exception {
			http
		      .sessionManagement()
		      .sessionCreationPolicy(STATELESS)
		      .and()
		      .exceptionHandling()
		      // this entry point handles when you request a protected page and you are not yet
		      // authenticated
		      .defaultAuthenticationEntryPointFor(forbiddenEntryPoint(),PROTECTED_URLS)
		      .and()
		      .authenticationProvider(provider)
		      .addFilterBefore(restAuthenticationFilter(), AnonymousAuthenticationFilter.class)
		      .authorizeRequests()
		      .anyRequest()
		      .authenticated()
		      .and()
		      .csrf().disable()
		      .formLogin().loginPage("/secure/login").permitAll().and()
		      .httpBasic().disable()
		      .logout().disable();
			}
	
	
}
