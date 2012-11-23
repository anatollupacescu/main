declare variable $dburl := "http://localhost:8080/jersey/db/";

declare function local:query($sql as xs:string*) as element()*
{
    let $url := concat($dburl, 'query/', $sql) return
    let $uri := iri-to-uri($url) return doc($uri)/root/*
};

declare function local:insert($id as xs:unsignedInt, $column as xs:string*, $value as xs:string*) as xs:string*
{
    let $res := concat($dburl,'insert/', $id,"|", $column, "|", $value)
    return doc(iri-to-uri($res))
};

<html>
<head><title>simple</title></head>
<body>
{
    for $res in local:query("col='log_enter'")
    return <p>{node-name($res)} : {data($res)}</p>
}

{
    local:insert($prev/id, "log_enter", string(current-time()))
}

</body>
</html>