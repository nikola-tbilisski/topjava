<%@ page import="ru.javawebinar.topjava.util.TimeUtil" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html lang="ru">
<head>
    <title>Meals List</title>
    <style>
        .normal {color: green;}

        .exceeded {color: red;}

        table, th, td {
            border: 2px solid black;
        }

        th, td {
            padding: 10px;
        }
    </style>
</head>
<body>
<section>
    <h2><a href="index.html">Home</a></h2>
    <h3>Meal List</h3>
    <hr>
    <table>
        <thead>
        <tr>
            <th>Date</th>
            <th>Description</th>
            <th>Calories</th>
        </tr>
        </thead>
        <jsp:useBean id="mealList" scope="request" type="java.util.List"/>
        <c:forEach items="${mealList}" var="meal">
            <jsp:useBean id="meal" type="ru.javawebinar.topjava.model.MealTo"/>
            <tr class="${meal.excess ? 'exceeded' : 'normal'}">
                <td>
                    <%=TimeUtil.toString(meal.getDateTime())%>
                </td>
                <td>${meal.description}</td>
                <td>${meal.calories}</td>
            </tr>
        </c:forEach>
    </table>
</section>
</body>
</html>