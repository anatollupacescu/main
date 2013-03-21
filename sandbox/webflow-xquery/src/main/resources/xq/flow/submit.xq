(: XQuery main module :)

let $document := <root>
<request>
    <name>anatol</name>
    <pass>1243</pass>
</request>
</root>
return
<response>
<errors>
    <error for="user">Insert a normal username</error>
    <error for="pass">Insert a normal password</error>
</errors>
     <_next>
    {
        let $action := string-length($document/request/name)
        return if($action gt 4)
        then ''
        else
            <param xpath="/errors/*[@for='user']">error_username</param>
    }
    
    {
        let $action := string-length($document/request/pass)
        return if($action gt 4)
        then ''
        else <param xpath="/errors/*[@for='pass']">error_password</param>
    }
    
    {
    let $errors := count($document/request/param)
    return if ($errors gt 0)
    then
        <node>CREATE</node>
    else
        <page>user/page</page>
    }
     </_next>
{
let $ispage := count($document/request/page)
return if ($ispage gt 0)
then
    <user action='retrieve' columns="age">
        <name condition="EQ">{$document/request/name}</name>
        <pass condition="EQ">{$document/request/pass}</pass>
    </user>
else ''
}
</response>
