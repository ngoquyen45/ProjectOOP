<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    
    <title>DMSPlus</title>
    
    <c:set var="lang">${pageContext.response.locale.language}</c:set>
    <c:choose>
    	<c:when test="${lang == 'zh'}">
    		<c:set var="backgroundImage" value="/assets/background_zh.jpg" />
    	</c:when>
    	<c:when test="${lang == 'en'}">
    		<c:set var="backgroundImage" value="/assets/background_en.jpg" />
    	</c:when>
    	<c:otherwise>
    		<c:set var="backgroundImage" value="/assets/background.jpg" />
    	</c:otherwise>
    </c:choose>
    
    <link rel="stylesheet" href="<c:url value='/assets/styles/font.css' />">
    <link rel="icon" type="img/ico" href="<c:url value='/assets/favicon2.png' />">
    
    <style>
        body {
            background: url('<c:url value='${backgroundImage}' />') no-repeat fixed center center;
            background-size: cover;
            font-family: Roboto;
            font-weight: 300;
        }

        .login-block {
            width: 320px;
            padding: 20px;
            background: #fff;
            border-radius: 5px;
            border-top: 5px solid #37464F;
            margin: 90px auto 0 auto;
        }

        .login-block img#logo {
            width: 214px; 
            height: 55px; 
            margin: 0 53px 20px 53px;
        }
        
        .login-block .error-block {
        	display: block;
        	margin-bottom: 20px;
        }
        
        .login-block .error-block p {
        	margin: 0;
        	color: #37464f;
        }

        .login-block .input-container {
            position: relative;
        }

        .login-block .input-container input {
            width: 100%;
            height: 42px;
            box-sizing: border-box;
            border-radius: 5px;
            border: 1px solid #78909C;
            margin-bottom: 20px;
            font-size: 14px;
            padding: 0 20px 0 50px;
            outline: none;
            background: #fff;
        }
        
        .login-block .input-container input:active, .login-block .input-container input:focus {
            border: 1px solid #37464F;
        }

        .login-block .input-container .icon-container img {
            width: 14px;
            height: 14px;
            position: absolute;
            top: 0;
            left: 0;
            margin: 13px 18px;
        }

        .login-block .input-container .icon-container .icon-focus {
            display: none;
        }

        .login-block .input-container input:active + .icon-container .icon-normal, 
        .login-block .input-container input:focus + .icon-container .icon-normal {
            display: none;
        }

        .login-block .input-container input:active + .icon-container .icon-focus, 
        .login-block .input-container input:focus + .icon-container .icon-focus {
            display: block;
        }

        .login-block .submit {
            width: 100%;
            height: 40px;
            background: #37464F;
            box-sizing: border-box;
            border-radius: 5px;
            border: 1px solid #263238;
            color: #fff;
            font-weight: bold;
            text-transform: uppercase;
            font-size: 14px;
            outline: none;
            cursor: pointer;
        }

        .login-block .submit:hover {
            background: #455A64;
        }
        
        .language-container {
        	margin-top: 20px;
			text-align: center;
		}

		.language-container img.flag {
			width: auto;
			height: auto;
			padding: 1px;
			max-height: 36px;
			-webkit-filter: grayscale(80%);
			-moz-filter: grayscale(80%);
			-o-filter: grayscale(80%);
			filter: grayscale(80%);
		}

		.language-container img.flag:hover, 
		.language-container img.flag.active {
			-webkit-filter: grayscale(0);
			-moz-filter: grayscale(0);
			-o-filter: grayscale(0);
			filter: grayscale(0);
			cursor: pointer;
		}
		
		.input-container .g-recaptcha {
		    margin-bottom: 20px;
		}

    </style>

</head>

<body>

    <form class="login-block" action="<c:url value="/account/login"/>" method="post">
    	<%-- <img src="<c:url value='/assets/salesquick.png' />" id="logo"> --%>
        <img src="<c:url value='/assets/dmsplus.png' />" id="logo">
        
        <c:if test="${not empty param.authentication_error}">
			<div class="error-block" style="display: block !important;">
				<p><spring:message code="login.message.not.success" text="Your login attempt was not successful." /></p>
			</div>
		</c:if>
		<c:if test="${not empty param.authorization_error}">
			<div class="error-block" style="display: block !important;">
				<p><spring:message code="login.message.not.permitted" text="You are not permitted to access that resource." /></p>
			</div>
		</c:if>
        
        <div class="input-container">
            <input type="text" value="" placeholder="<spring:message code="login.hint.account" text="account" />" id="username" name="j_username"/>
            <div class="icon-container">
                <img class="icon-normal" src="<c:url value='/assets/user.svg' />">
                <img class="icon-focus" src="<c:url value='/assets/user-focus.svg' />">
            </div>
        </div>
        
        <div class="input-container">
            <input type="password" value="" placeholder="<spring:message code="login.hint.password" text="password" />" id="password" name="j_password"/>
            <div class="icon-container">
                <img class="icon-normal" src="<c:url value='/assets/lock.svg' />">
                <img class="icon-focus" src="<c:url value='/assets/lock-focus.svg' />">
            </div>
        </div>
        
        <c:if test="${requireCAPTCHA}">
	        <div class="input-container">
	            <div class="g-recaptcha" data-sitekey="${siteKey}"></div>
	        </div>
        </c:if>
        
        <input class="submit" type="submit" value="<spring:message code="login.button" text="Login" />"/>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
    </form>
    
	<div class="language-container">
		<%
			List<String> languages = (List<String>)request.getAttribute("languages");
			if (languages.contains("zh")) {
			    %>
				<a href="?lang=zh"> <img
					src="<c:url value='/assets/language_chinese_inactive.png' />"
					alt="中文" class="flag <c:if test="${lang == 'zh'}">active</c:if>" />
				</a> 
			    <%
			}
			
			if (languages.contains("vi")) {
			    %>
				<a href="?lang=vi"> <img
					src="<c:url value='/assets/language_vietnamese_inactive.png' />"
					alt="Tiếng Việt" class="flag <c:if test="${lang == 'vi'}">active</c:if>" />
				</a> 
			    <%
			}
			
			if (languages.contains("en")) {
			    %>
				<a href="?lang=en"> <img
					src="<c:url value='/assets/language_english_inactive.png' />"
					alt="English" class="flag <c:if test="${lang == 'en'}">active</c:if>" />
				</a>
			    <%
			}
		%>
	</div>
	
	<script src='https://www.google.com/recaptcha/api.js?hl=${lang}'></script>
	
</body>

</html>