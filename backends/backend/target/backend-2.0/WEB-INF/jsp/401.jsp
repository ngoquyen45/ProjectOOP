<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isErrorPage="true" session="false"
    trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!DOCTYPE html>
<html lang="en-us"
	class=" js flexbox flexboxlegacy canvas canvastext webgl no-touch geolocation postmessage websqldatabase indexeddb hashchange history draganddrop websockets rgba hsla multiplebgs backgroundsize borderimage borderradius boxshadow textshadow opacity cssanimations csscolumns cssgradients cssreflections csstransforms csstransforms3d csstransitions fontface generatedcontent video audio localstorage sessionstorage webworkers applicationcache svg inlinesvg smil svgclippaths">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<meta charset="utf-8">
<title>SalesQuick</title>

<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="author" content="trungkh">

<link rel="shortcut icon" href="<c:url value='/assets/favicon2.png' />">
<link rel="stylesheet" href="<c:url value='/assets/styles/font.css' />">
<link rel="stylesheet" href="<c:url value='/assets/styles/error.css' />">

<!-- Modernizr runs quickly on page load to detect features -->
<script src="<c:url value='/assets/js/modernizr.js' />" id="logo"></script>

</head>

<body class="vegas-container" cz-shortcut-listen="true">

	<div class="vegas-slide vegas-transition-fade vegas-transition-fade-in"
		style="transition: all 1000ms;">
		<div class="vegas-slide-inner"
			style="background-image: url(<c:url value='/assets/background.jpg' />); 
				background-color: rgb(51, 51, 51); 
				background-size: cover; 
				background-position: 50% 50%;">
		</div>
	</div>

	<div id="container">

		<div class="overlay"></div>

		<div class="item-title">

			<div id="message">
				<p>ERROR 401</p>
				<p>UNAUTHORIZED</p>
				<p>Access is denied.</p>
				<p>You do not have permission to view this page.</p>
				<p>Click on the links below to do something, Thanks!</p>
			</div>

			<div class="link-bottom">
				<a class="link-icon" href="<c:url value="/" />"> <i
					class="icon ion-ios-home"></i> Home
				</a> <a class="link-icon" href="mailto:salesquick.vt@gmail.com"> <i
					class="icon ion-ios-compose"></i> Write us
				</a>
			</div>

		</div>

	</div>

	<script src="<c:url value='/assets/js/jquery.min.js' />"></script>
	<script src="<c:url value='/assets/js/jquery.easings.min.js' />"></script>
	<script src="<c:url value='/assets/js/bootstrap.min.js' />"></script>
	<script src="<c:url value='/assets/js/401.js' />"></script>
	<!--[if lt IE 10]><script type="text/javascript" src="<c:url value='/assets/js/placeholder.js' />"></script><![endif]-->
</body>
</html>