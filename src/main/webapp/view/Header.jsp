<div class="header">
    <nav class="header__nav">
        <c:forEach items="${tables}" var="tablesList">
            <a class="header__nav__link" href="/query/${tablesList.tableName }">${tablesList.tableName }</a>
        </c:forEach>

        <a class="header__nav__link" href="/perform_logout">log out</a>
    </nav>
</div>