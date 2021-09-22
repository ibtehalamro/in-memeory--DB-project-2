<h2 class="tableName">${tableName }</h2>
<table>
    <thead>
    <tr>
        <c:forEach items="${tableColumns}" var="column">
            <th><c:out value="${column}"/></th>
        </c:forEach>
    </tr>
    </thead>
    <tbody>

    <c:forEach items="${queryResult}" var="tableList">
        <tr>
            <c:forEach items="${tableList.tableRowMap}" var="dataRow">
                <td><c:out value="${dataRow.value}"/></td>
            </c:forEach>
        </tr>
    </c:forEach>
    </tbody>
</table>