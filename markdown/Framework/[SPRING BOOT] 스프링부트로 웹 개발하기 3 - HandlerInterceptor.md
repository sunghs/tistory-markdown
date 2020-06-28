## [SPRING BOOT] 스프링부트로 웹 개발하기 3 - HandlerInterceptor

클라이언트에서 들어온 요청을 서블릿이 처리하기 전 후에 각각 전처리, 후처리를 할 수 있다.
모든 요청에 대해 공통으로 들어가는 로직을 구현하는 부분에 사용할 수 있다.

### HandlerInterceptor Interface 구현
_JAVA8 부터 Interface에 존재하는 default method는 구현체에서 반드시 구현하지 않아도 된다.
HandlerInterceptor에 존재하는 preHandle, postHandle, afterCompletion 메소드가 전부 default method 이므로 필요하면 직접 구현해야 한다._
<br/>
HandlerInterceptor 는 이렇게 생겼다.
```java
/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;

/**
 * Workflow interface that allows for customized handler execution chains.
 * Applications can register any number of existing or custom interceptors
 * for certain groups of handlers, to add common preprocessing behavior
 * without needing to modify each handler implementation.
 *
 * <p>A HandlerInterceptor gets called before the appropriate HandlerAdapter
 * triggers the execution of the handler itself. This mechanism can be used
 * for a large field of preprocessing aspects, e.g. for authorization checks,
 * or common handler behavior like locale or theme changes. Its main purpose
 * is to allow for factoring out repetitive handler code.
 *
 * <p>In an asynchronous processing scenario, the handler may be executed in a
 * separate thread while the main thread exits without rendering or invoking the
 * {@code postHandle} and {@code afterCompletion} callbacks. When concurrent
 * handler execution completes, the request is dispatched back in order to
 * proceed with rendering the model and all methods of this contract are invoked
 * again. For further options and details see
 * {@code org.springframework.web.servlet.AsyncHandlerInterceptor}
 *
 * <p>Typically an interceptor chain is defined per HandlerMapping bean,
 * sharing its granularity. To be able to apply a certain interceptor chain
 * to a group of handlers, one needs to map the desired handlers via one
 * HandlerMapping bean. The interceptors themselves are defined as beans
 * in the application context, referenced by the mapping bean definition
 * via its "interceptors" property (in XML: a &lt;list&gt; of &lt;ref&gt;).
 *
 * <p>HandlerInterceptor is basically similar to a Servlet Filter, but in
 * contrast to the latter it just allows custom pre-processing with the option
 * of prohibiting the execution of the handler itself, and custom post-processing.
 * Filters are more powerful, for example they allow for exchanging the request
 * and response objects that are handed down the chain. Note that a filter
 * gets configured in web.xml, a HandlerInterceptor in the application context.
 *
 * <p>As a basic guideline, fine-grained handler-related preprocessing tasks are
 * candidates for HandlerInterceptor implementations, especially factored-out
 * common handler code and authorization checks. On the other hand, a Filter
 * is well-suited for request content and view content handling, like multipart
 * forms and GZIP compression. This typically shows when one needs to map the
 * filter to certain content types (e.g. images), or to all requests.
 *
 * @author Juergen Hoeller
 * @since 20.06.2003
 * @see HandlerExecutionChain#getInterceptors
 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter
 * @see org.springframework.web.servlet.handler.AbstractHandlerMapping#setInterceptors
 * @see org.springframework.web.servlet.handler.UserRoleAuthorizationInterceptor
 * @see org.springframework.web.servlet.i18n.LocaleChangeInterceptor
 * @see org.springframework.web.servlet.theme.ThemeChangeInterceptor
 * @see javax.servlet.Filter
 */
public interface HandlerInterceptor {

	/**
	 * Intercept the execution of a handler. Called after HandlerMapping determined
	 * an appropriate handler object, but before HandlerAdapter invokes the handler.
	 * <p>DispatcherServlet processes a handler in an execution chain, consisting
	 * of any number of interceptors, with the handler itself at the end.
	 * With this method, each interceptor can decide to abort the execution chain,
	 * typically sending a HTTP error or writing a custom response.
	 * <p><strong>Note:</strong> special considerations apply for asynchronous
	 * request processing. For more details see
	 * {@link org.springframework.web.servlet.AsyncHandlerInterceptor}.
	 * <p>The default implementation returns {@code true}.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler chosen handler to execute, for type and/or instance evaluation
	 * @return {@code true} if the execution chain should proceed with the
	 * next interceptor or the handler itself. Else, DispatcherServlet assumes
	 * that this interceptor has already dealt with the response itself.
	 * @throws Exception in case of errors
	 */
	default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		return true;
	}

	/**
	 * Intercept the execution of a handler. Called after HandlerAdapter actually
	 * invoked the handler, but before the DispatcherServlet renders the view.
	 * Can expose additional model objects to the view via the given ModelAndView.
	 * <p>DispatcherServlet processes a handler in an execution chain, consisting
	 * of any number of interceptors, with the handler itself at the end.
	 * With this method, each interceptor can post-process an execution,
	 * getting applied in inverse order of the execution chain.
	 * <p><strong>Note:</strong> special considerations apply for asynchronous
	 * request processing. For more details see
	 * {@link org.springframework.web.servlet.AsyncHandlerInterceptor}.
	 * <p>The default implementation is empty.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler handler (or {@link HandlerMethod}) that started asynchronous
	 * execution, for type and/or instance examination
	 * @param modelAndView the {@code ModelAndView} that the handler returned
	 * (can also be {@code null})
	 * @throws Exception in case of errors
	 */
	default void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable ModelAndView modelAndView) throws Exception {
	}

	/**
	 * Callback after completion of request processing, that is, after rendering
	 * the view. Will be called on any outcome of handler execution, thus allows
	 * for proper resource cleanup.
	 * <p>Note: Will only be called if this interceptor's {@code preHandle}
	 * method has successfully completed and returned {@code true}!
	 * <p>As with the {@code postHandle} method, the method will be invoked on each
	 * interceptor in the chain in reverse order, so the first interceptor will be
	 * the last to be invoked.
	 * <p><strong>Note:</strong> special considerations apply for asynchronous
	 * request processing. For more details see
	 * {@link org.springframework.web.servlet.AsyncHandlerInterceptor}.
	 * <p>The default implementation is empty.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler handler (or {@link HandlerMethod}) that started asynchronous
	 * execution, for type and/or instance examination
	 * @param ex exception thrown on handler execution, if any
	 * @throws Exception in case of errors
	 */
	default void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable Exception ex) throws Exception {
	}

}
```
<br/>

**HandlerInterceptor 구현**
```java
package sunghs.boot.web.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

public @Slf4j class CustomInterceptor implements HandlerInterceptor {

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		log.info("PRE_HANDLE");
		return false;
	}
	
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
		log.info("POST_HANDLE");
	}
	
	
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
		log.info("AFTER_COMPLETION");
	}
}
```
수행 순서는 다음과 같다.
1. Client Request
2. preHandle
3. Servlet 비즈니스 로직 수행
4. postHandle
5. Client Response
6. afterCompletion

<br/>

- preHandle의 return 값이 false 인경우 servlet 수행으로 넘어가지 않는다. (client response도 당연히 일어나지 않음.) return true 인 경우 servlet 로직을 수행하게 된다.
- postHandle의 request, response, modelAndView에 데이터를 세팅 할 수 있다. modelAndView에 값 세팅을 하면 html 페이지에서 값을 불러올 수 있다.

### 만들어진 CustomInterceptor를 WebMvcConfigurer에 등록해야 한다.
CustomInterceptorConfigurer를 따로 구현하면 된다.
```java
package sunghs.boot.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public @Configuration class CustomInterceptorConfigurer implements WebMvcConfigurer {

	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(customInterceptor());
	}
	
	@Bean
	public CustomInterceptor customInterceptor() {
		return new CustomInterceptor();
	}
}

```

---

2019-07-31 10:19:01.957  INFO 36044 --- [nio-9071-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 14 ms
**2019-07-31 10:19:01.979  INFO 36044 --- [nio-9071-exec-1] s.boot.web.config.CustomInterceptor      : PRE_HANDLE**
2019-07-31 10:19:02.023  INFO 36044 --- [nio-9071-exec-1] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-07-31 10:19:02.031  WARN 36044 --- [nio-9071-exec-1] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-07-31 10:19:02.224  INFO 36044 --- [nio-9071-exec-1] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2019-07-31 10:19:02.271  INFO 36044 --- [nio-9071-exec-1] s.boot.web.controller.MainController     : PACKET_FROM : PACKET_FROM\~\~, PACKET_TO : PACKET_TO\~\~, PACKET_STREAM_COUNT : 4
2019-07-31 10:19:02.271  INFO 36044 --- [nio-9071-exec-1] s.boot.web.controller.MainController     : PACKET_FROM : PACKET_FROM\~\~, PACKET_TO : PACKET_TO\~\~, PACKET_STREAM_COUNT : 3
2019-07-31 10:19:02.271  INFO 36044 --- [nio-9071-exec-1] s.boot.web.controller.MainController     : PACKET_FROM : PACKET_FROM\~\~, PACKET_TO : PACKET_TO\~\~, PACKET_STREAM_COUNT : 2
**2019-07-31 10:19:02.276  INFO 36044 --- [nio-9071-exec-1] s.boot.web.config.CustomInterceptor      : POST_HANDLE
2019-07-31 10:19:02.525  INFO 36044 --- [nio-9071-exec-1] s.boot.web.config.CustomInterceptor      : AFTER_COMPLETION**

---

_출처 : 스프링5 레시피_