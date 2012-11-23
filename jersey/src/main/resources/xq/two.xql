<html>

<body>
    
    <form action="one" method="get">
        <p>
            <input type="text" name="id" value="{$prev/saved/id}" />
            <h2 style="color:red">{$prev/id_error}</h2>
        </p>
        
        <p>
            <input type="text" name="response" value="{$prev/saved/response}" />
            <h2 style="color:red">{$prev/response_error}</h2>
        </p>
        
        <input type="submit" name="submit" value="add"/>
    
    <hr/>
        <h2 style="color:red">{$prev/message}</h2>
    <hr/>
    
    <select name="delete">
    {
        for $value in $prev/dummy
        return <option value="{$value/id/text()}">{$value/response/text()}</option>
    }
    </select>
    
    <input type="submit" name="submit" value="delete"/>
    
</form>
</body>
</html>