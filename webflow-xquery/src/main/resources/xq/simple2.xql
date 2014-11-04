declare variable $dburl := "http://localhost:8080/jersey/db/";

declare function local:query($sql as xs:string*) as element()*
{
    let $url := concat($dburl, 'query/', $sql) return
    let $uri := iri-to-uri($url) return doc($uri)/produs/*
};

declare function local:insert($sql as xs:string*)
{
    doc(iri-to-uri(concat($dburl, 'update/', $sql)))
};

<html>
<head><title>simple</title></head>
<body>
{
    local:insert("INSERT INTO produs (denumire) VALUES ('Fignea')")
}
</body>
</html>