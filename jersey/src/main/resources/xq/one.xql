declare variable $dburl := "http://localhost:8080/jersey/db/";

declare function local:validate($prev as xs:string*) as xs:boolean {
    string-length($prev) > 0
};

declare function local:insert() as element()
{
    let $res := concat($dburl,'update/INSERT INTO dummy(id,response) VALUES(', $prev/id, ",'" , $prev/response , "')")
    return if(local:validate($prev/id) and local:validate($prev/response))
    then if(doc-available(iri-to-uri($res))) 
        then <message>Item added</message>
        else <message>Could not save</message>
    else
        <message>Please provide a valid id and name</message>
};

declare function local:delete() as element()
{
    let $delete := concat($dburl,'update/DELETE FROM dummy WHERE id=', $prev/delete)
    return if(string-length($prev/delete) > 0)
    then if(doc-available(iri-to-uri($delete)))
        then <message>Item deleted</message>
        else ''
    else ''
};

declare function local:select() as element()*
{
    let $url := concat($dburl, 'query/SELECT id,response FROM dummy') return
    let $uri := iri-to-uri($url) return doc($uri)/root/dummy
};

<root>
    {
        if(false() eq local:validate($prev/id/text()) and $prev/submit eq 'add') 
        then <id_error>Please enter a valid id</id_error> 
        else ''
    }
    
    {
        if(false() eq local:validate($prev/response/text()) and $prev/submit eq 'add') 
        then <response_error>Please enter a valid response</response_error> 
        else ''
    }
    
    {
        if((false() eq local:validate($prev/id/text()) or false() eq local:validate($prev/response/text())) and $prev/submit eq 'add') 
        then
            <saved>
             <id>{$prev/id/text()}</id>
             <response>{$prev/response/text()}</response>
            </saved>
            else ''
    }
    
    {
       if($prev/submit eq 'delete') then local:delete()
       else if ($prev/submit eq 'add') then local:insert()
       else ''
    }
    
    {
        local:select()
    }
    
    <_code>two</_code>
</root>
