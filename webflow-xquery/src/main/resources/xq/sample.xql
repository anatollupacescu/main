declare variable $dburl := "http://localhost:8080/jersey/db/";

declare function local:insert($id as xs:unsignedInt, $column as xs:string*, $value as xs:string*) as element()
{
    let $res := concat($dburl,'update/INSERT INTO entity(entity_id,column,value) VALUES(1,"', $column, '","', $value, '")')
    return if(doc-available(iri-to-uri($res))) 
        then <message>Row added</message>
        else <message>Could not insert</message>
};

declare function local:select($sql as xs:string*) as element()*
{
    let $url := concat($dburl, 'smart-query/', $sql) return
    let $uri := iri-to-uri($url) return doc($uri)/root/*
};

<html>
<head>
<title>Hello, World</title>
</head>
<body>

<hr/>
{
    for $res in local:select("column='user_age' and value<'27'")
    return <p>{node-name($res)} : {data($res)}</p>
}

</body>
</html>